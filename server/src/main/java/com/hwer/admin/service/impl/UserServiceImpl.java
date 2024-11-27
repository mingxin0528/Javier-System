package com.hwer.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.binance.connector.client.SpotClient;
import com.binance.connector.client.WebSocketApiClient;
import com.binance.connector.client.impl.SpotClientImpl;
import com.binance.connector.client.impl.WebSocketApiClientImpl;
import com.binance.connector.client.utils.ProxyAuth;
import com.binance.connector.client.utils.signaturegenerator.HmacSignatureGenerator;
import com.binance.connector.client.utils.websocketcallback.WebSocketMessageCallback;
import com.hwer.admin.bean.HwerConfig;
import com.hwer.admin.entity.Binance;
import com.hwer.admin.entity.Income;
import com.hwer.admin.entity.Order;
import com.hwer.admin.entity.User;
import com.hwer.admin.entity.vo.OrderVO;
import com.hwer.admin.entity.vo.UserVO;
import com.hwer.admin.mapper.BinanceMapper;
import com.hwer.admin.mapper.IncomeMapper;
import com.hwer.admin.mapper.OrderMapper;
import com.hwer.admin.mapper.UserMapper;
import com.hwer.admin.service.SymbolService;
import com.hwer.admin.service.UserService;
import com.hwer.admin.util.Request;
import com.hwer.admin.util.ResultUtil;
import com.hwer.admin.util.URLConstant;
import com.hwer.admin.util.UserUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private static final Map<String, UserClient> CLIENT_MAP = new ConcurrentHashMap<>(4);
    private static final Map<String, RequestId.Type> REQ_MAP = new ConcurrentHashMap<>(8);
    public static Long timeDiff = 0L;
    @Resource
    private UserMapper userMapper;
    @Resource
    private BinanceMapper binanceMapper;

    @Resource
    private HwerConfig hwerConfig;

    @Resource
    private SymbolService symbolService;
    @Resource
    private OrderMapper orderMapper;

    @Resource
    private IncomeMapper incomeMapper;

    @Override
    public User getByUsername(UserVO user) {
        return userMapper.getByUsername(user);
    }

    @Override
    public User register(UserVO vo) {
        String un = vo.getUsername().trim();
        Assert.isTrue(un.length() <= 8, "用户名不得超过8个字符");
        User username = userMapper.selectOne(new QueryWrapper<User>().eq("username", un));
        Assert.isTrue(null == username, "该用户名已被注册");
        Assert.isTrue(vo.getPassword().trim().length() >= 6, "密码不得少于6位");
        Assert.isTrue(vo.getPassword().trim().length() <= 16, "密码不得多于16位");
        User user = new User();
        user.setUsername(un);
        BeanUtil.copyProperties(vo, user);
        user.setCreatedTime(new Date());
        user.setPassword(UserUtil.encode(user.getPassword()));
        user.setSelfCode(RandomUtil.randomString("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", 8));
        user.setSecret(RandomUtil.randomString("abcdef0123456789", 17));
        userMapper.insert(user);
        return user;
    }

    private ProxyAuth getProxy() {
        Proxy proxyConn = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hwerConfig.getProxy().getIp(), hwerConfig.getProxy().getPort()));
        Authenticator auth = (route, response) -> {
            if (response.request().header("Proxy-Authorization") != null) {
                return null;
            }
            String credential = Credentials.basic(hwerConfig.getProxy().getUser(), hwerConfig.getProxy().getAuth());
            return response.request().newBuilder().header("Proxy-Authorization", credential).build();

        };
        return new ProxyAuth(proxyConn, auth);
    }

    private List<Income> parseIncome(User user, int page) throws Exception {
        Map<String, Object> map = new HashMap<>();
        // 30 天
        map.put("startTime", String.valueOf(new Date(System.currentTimeMillis() - 2592000000L).getTime()));
        int limit = 1000;
        map.put("page", page);
        map.put("limit", limit);
        Request request = new Request(URLConstant.U_PROD_URL, user.getApiKey(), user.getSecretKey(), hwerConfig);
        String s = request.sendSignedRequest(map, "/fapi/v1/income", "GET");
        return JSONUtil.toList(s, Income.class).stream().peek(e -> e.setUserId(user.getId())).toList();
    }

    @Override
    public void generateUserInfo(User user) {
        try {
            incomeMapper.delete(new QueryWrapper<Income>().eq("user_id", user.getId()));
            int page = 1;
            List<Income> incomes = parseIncome(user, page);
            while (incomes.size() > 0) {
                incomeMapper.insert(incomes);
                incomes = parseIncome(user, ++page);
            }
            send(user, "account.accountStatus", ResultUtil.params(reqId(RequestId.Type.GEN_USER_INFO)));
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    private void accountStatus(User user, String symbol, String msgFlag) {
        try {
            send(user, "account.accountStatus", ResultUtil.params(reqId(RequestId.Type.GEN_USER_INFO)));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            log.error("{}:{}:{}", msgFlag, symbol, ex.toString());
        }
    }

    @Override
    public void closeAllOrder(User user) {
        try {
            // 先查询所有订单
//            List<Order> orders = orderMapper.selectList(new QueryWrapper<Order>().eq("user_id", user.getId()).and(q -> q.in("type", "LIMIT")));
//            for (Order order : orders) {
//                JSONObject param = ResultUtil.params(reqId(RequestId.Type.GET_OPEN_ORDERS)).put("orderId", order.getOrderId());
//                send(user, "trade.getOpenOrders", param);
//            }
            Map<String, Object> map = new HashMap<>();
            map.put("symbol",symbolService.getActivatedSymbol());
            Request request = new Request(URLConstant.U_PROD_URL, user.getApiKey(), user.getSecretKey(), hwerConfig);
            try {
                String res = request.sendSignedRequest(map, "/fapi/v1/allOpenOrders", "DELETE");
                log.info("Cancel all orders:{}", res);
            } catch (Exception e) {
                log.error("Cancel all orders:{}", e.toString());
            }
            map.clear();
            String s = request.sendSignedRequest(map, "/fapi/v3/positionRisk", "GET");
            JSONUtil.parseArray(s).forEach(e -> {
                cn.hutool.json.JSONObject obj = JSONUtil.parseObj(e);
                String symbol = obj.getStr("symbol");
                if (symbolService.getActivatedSymbol().equals(symbol)) {
                    String positionAmt = obj.getStr("positionAmt");
                    if (new BigDecimal(positionAmt).floatValue() <= 0) {
                        accountStatus(user,symbol,"No positionAmt");
                        return;
                    }
                    String positionSide = obj.getStr("positionSide");
                    Map<String, Object> p = new HashMap<>();
                    p.put("symbol", symbol);
                    p.put("side", "SELL");
                    p.put("positionSide", positionSide);
                    p.put("type", "MARKET");
                    p.put("quantity", positionAmt);
                    try {
                        String cp = request.sendSignedRequest(p, "/fapi/v1/order", "POST");
                    } catch (Exception ex) {
                        log.error("POST /fapi/v1/order:{}",ex.toString());
                    }
                    accountStatus(user,symbol,"close position error");
                }
            });

        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    @Override
    public Page<User> getUserList(UserVO vo) {
        return userMapper.getPagedUserList(new Page<>(vo.getPage(), vo.getSize()), vo);
    }

    @Override
    public User updateAllow(UserVO vo) {
        User byUsername = getByUsername(vo);
        Assert.isTrue(byUsername != null, "用户不存在");
        if (vo.getAllow() == 1) {
            if (StrUtil.isEmpty(vo.getApiKey()) || StrUtil.isEmpty(vo.getSecretKey())) {
                throw new IllegalArgumentException("未配置 API KEY 或 SECRET KEY");
            }
            generateUserInfo(byUsername);
        } else {
            closeAllOrder(byUsername);
        }
        userMapper.update(new UpdateWrapper<User>().set("allow", vo.getAllow()).eq("username", vo.getUsername()));
        return getByUsername(vo);
    }


    @Override
    public void connect(UserVO vo) {
    }

    private Map<String, Object> reqId(RequestId.Type type) {
        String requestId = UUID.fastUUID().toString();
        REQ_MAP.put(requestId, type);
        return JSONUtil.createObj().set("requestId", requestId);
    }

    @Override
    public void close(UserVO vo) {
        UserClient userClient = CLIENT_MAP.get(vo.getUsername());
//        userClient.client.account().accountBalance(ResultUtil.params());
        String id = UUID.fastUUID().toString();
    }

    private String checkUserPositionMode(User user) {
        try {
            Request request = new Request(URLConstant.U_PROD_URL, user.getApiKey(), user.getSecretKey(), hwerConfig);
            Map<String, Object> p = new HashMap<>();
            String cp = request.sendSignedRequest(p, "/fapi/v1/positionSide/dual", "GET");
            log.info(cp);
            return cp;
        } catch (Exception ex) {
            log.error("close position error:" + ex);
        }
        return "";
    }

    private String createOrderSingUser(User user, OrderVO vo) {
        Assert.isTrue(user != null, "用户不存在");
        BigDecimal availableBinance = new BigDecimal(0);
        for (Binance binance : user.getBinanceList()) {
            if (StrUtil.endWithIgnoreCase(symbolService.getActivatedSymbol(), binance.getAsset()) && binance.getAvailableBalance().floatValue() > 0) {
                availableBinance = binance.getAvailableBalance();
                break;
            }
        }
        if (availableBinance.floatValue() <= 0) {
            return user.getUsername() + ":无余额";
        }
        try {

            BigDecimal lossRate = new BigDecimal(vo.getStopLossRate());
            JSONObject params = ResultUtil.params(reqId(RequestId.Type.CREATE_ORDER));
            params.put("positionSide", vo.getPositionSide());
            BigDecimal quantity;
            if ("LONG".equals(vo.getPositionSide())) {
                BigDecimal diff = new BigDecimal(vo.getPrice()).subtract(new BigDecimal(vo.getStopLossPrice()));
                // 多单止损金额 = (开单价-止损价）* 开仓数量
                quantity = availableBinance.multiply(lossRate).divide(diff, 2, RoundingMode.HALF_UP);
            } else { // 空单
                // 空单止损金额=（止损价-开单价）* 开仓数量
                BigDecimal diff = new BigDecimal(vo.getStopLossPrice()).subtract(new BigDecimal(vo.getPrice()));
                quantity = availableBinance.multiply(lossRate).divide(diff, 2, RoundingMode.HALF_UP);
            }
            // 挂单
            params.put("price", vo.getPrice());
            params.put("quantity", quantity.toPlainString());
            params.put("recvWindow", 6000);
            params.put("timeinforce", "GTC");
            String id1 = params.getString("requestId");
            // 止损单
            JSONObject paramsLoss = ResultUtil.params(reqId(RequestId.Type.CREATE_ORDER));
            String id2 = paramsLoss.getString("requestId");
            paramsLoss.put("stopPrice", vo.getStopLossPrice());
            paramsLoss.put("closePosition", "true");
            paramsLoss.put("positionSide", vo.getPositionSide());
            paramsLoss.put("recvWindow", 6000);
            // 止盈单
            JSONObject paramsEarn = ResultUtil.params(reqId(RequestId.Type.CREATE_ORDER));
            String id3 = paramsEarn.getString("requestId");
            paramsEarn.put("stopPrice", vo.getStopEarnPrice());
            paramsEarn.put("closePosition", "true");
            paramsEarn.put("positionSide", vo.getPositionSide());
            paramsEarn.put("recvWindow", 6000);
            orderMapper.insert(new Order(user.getId(), id1, id3 + "," + id2, "BUY", vo.getPositionSide(), lossRate, false));
            send(user, "trade.newOrder", symbolService.getActivatedSymbol(), "BUY", "LIMIT", params);
            orderMapper.insert(new Order(user.getId(), id3, id1, "SELL", vo.getPositionSide(), lossRate, true));
            send(user, "trade.newOrder", symbolService.getActivatedSymbol(), "SELL", "STOP_MARKET", paramsLoss);
            orderMapper.insert(new Order(user.getId(), id2, id1, "SELL", vo.getPositionSide(), lossRate, true));
            send(user, "trade.newOrder", symbolService.getActivatedSymbol(), "SELL", "TAKE_PROFIT_MARKET", paramsEarn);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            log.error(e.toString());
            return e.getMessage();
        }
        return "";
    }

    @Override
    public List<String> createOrder(OrderVO vo) {
        List<String> result = new ArrayList<>();
        if (null == vo.getUser() || StrUtil.isEmpty(vo.getUser().getUsername())) {
            List<User> list = userMapper.getAllowedUsers();
            for (User user : list) {
                result.add(createOrderSingUser(user, vo));
            }
        } else {
            User user = getByUsername(vo.getUser());
            result.add(createOrderSingUser(user, vo));
        }
        return result.stream().filter(e -> e.length() > 0).toList();
    }

    @PostConstruct
    private void timerToPingClient() {
        SpotClient spotClient = new SpotClientImpl();
        String s = spotClient.createMarket().exchangeInfo(new JSONObject().put("symbol", symbolService.getActivatedSymbol()).toMap());
        System.out.println(JSONUtil.parseObj(s).getJSONArray("symbols"));
//        String time = spotClient.createMarket().time();
//        log.info(time);
//        Long serverTime = JSONUtil.parseObj(time).get("serverTime", Long.class);
//        timeDiff = serverTime - System.currentTimeMillis();
//        log.info(String.valueOf(timeDiff));
        List<User> list = list(new QueryWrapper<User>().eq("status", 1).eq("allow", 1).eq("role", 0));
        list.forEach(e -> initUserConnect(e, false));
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> CLIENT_MAP.forEach((username, userClient) -> {
            WebSocketApiClient client = userClient.client;
            if ((System.currentTimeMillis() - userClient.connectionTime) / 86400000 > 20) {
                client.close();
                initUserConnect(userClient.user, false);
            }
            try {
                client.general().ping(new JSONObject(JSONUtil.createObj().set("requestId", UUID.fastUUID().toString())));
            } catch (Exception e) {
                initUserConnect(userClient.user, true);
            }
        }), 0, 60, TimeUnit.SECONDS);
    }

    private void initUserConnect(User user, boolean reconnect) {
        ThreadUtil.sleep(500L);
        if (!CLIENT_MAP.containsKey(user.getUsername())) {
            HmacSignatureGenerator signatureGenerator = new HmacSignatureGenerator(user.getSecretKey());
            WebSocketApiClient client = new WebSocketApiClientImpl(user.getApiKey(), signatureGenerator, URLConstant.WS_API_URL);
            client.connect(new MsgCallback(user));
            CLIENT_MAP.put(user.getUsername(), new UserClient(client, user));
        }
        if (reconnect) {
            HmacSignatureGenerator signatureGenerator = new HmacSignatureGenerator(user.getSecretKey());
            WebSocketApiClient client = new WebSocketApiClientImpl(user.getApiKey(), signatureGenerator, URLConstant.WS_API_URL);
            client.connect(new MsgCallback(user));
            CLIENT_MAP.put(user.getUsername(), new UserClient(client, user));
        }
    }

    private void send(User user, String method, Object... params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Assert.isTrue(user != null, "用户不存在");
        initUserConnect(user, false);
        WebSocketApiClient client = CLIENT_MAP.get(user.getUsername()).client;
        String[] split = method.split("\\.");
        Method declaredMethod = client.getClass().getDeclaredMethod(split[0]);
        declaredMethod.setAccessible(true);
        Object clientType = declaredMethod.invoke(client);
        log.info(clientType.getClass().getSimpleName());
        Class<?>[] classes = Arrays.stream(params).map(Object::getClass).toArray(Class[]::new);
        Method targetMethod = clientType.getClass().getDeclaredMethod(split[1], classes);
        targetMethod.setAccessible(true);
        targetMethod.invoke(clientType, params);
    }


    public class MsgCallback implements WebSocketMessageCallback {
        private final User user;
        private final Long localTime;

        MsgCallback(User user) {
            this.user = user;
            this.localTime = System.currentTimeMillis();
        }

        @Override
        public void onMessage(String s) {
            String id = JSONUtil.parseObj(s).get("id").toString();
            // ping 消息，需要回复 pong 消息
            if (!REQ_MAP.containsKey(id)) {
                System.out.println("============> " + s);
                CLIENT_MAP.get(user.getUsername()).client.general().ping(ResultUtil.params(reqId(RequestId.Type.PING)));
            }
            RequestId.Type type = REQ_MAP.get(id);
            REQ_MAP.remove(id);
            if (type == RequestId.Type.GEN_USER_INFO) {
                ResultUtil.handleGenUserInfo(s, user, binanceMapper, incomeMapper, hwerConfig);
            } else if (type == RequestId.Type.GEN_USER_INFO_2) {
                ResultUtil.handleGenUserOrders(s, user, binanceMapper, hwerConfig);
            } else if (type == RequestId.Type.CREATE_ORDER) {
                System.out.println(s);
                Object error = JSONUtil.parseObj(s).get("error");
                if (null != error) {
                    log.error(error.toString());
                    return;
                }
                Order result = JSONUtil.parseObj(s).get("result", Order.class);
                System.out.println(result);
                Order orderId = orderMapper.selectOne(new QueryWrapper<Order>().eq("order_id", id));
                result.setUserId(user.getId());
                result.setId(orderId.getId());
                System.out.println(result);
                orderMapper.updateById(result);
            } else if (type == RequestId.Type.GET_OPEN_ORDERS) {
                System.out.println(s);

//                Order order = JSONUtil.toBean(s, Order.class);
//                Order order1 = orderMapper.selectOne(new QueryWrapper<Order>().eq("order_id", order.getOrderId()));
//                order1.setStatus(order.getStatus());
//                orderMapper.updateById(order1);
//                // 如果是 NEW 状态，则撤销订单
//                JSONObject params = ResultUtil.params(reqId(RequestId.Type.CANCEL_ORDER));
//                params.put("orderId", order.getOrderId());
//                try {
//                    send(user, "trade.cancelOrder", symbolService.getActivatedSymbol(), params);
//                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//                    log.error(e.toString());
//                }
            }
        }
    }

    static class UserClient {
        WebSocketApiClient client;
        User user;
        Long connectionTime;

        public UserClient(WebSocketApiClient client, User user) {
            this.client = client;
            this.user = user;
            this.connectionTime = System.currentTimeMillis();
        }
    }

    static class RequestId {
        final Type type;
        Long createdTime;

        public RequestId(Type type) {
            this.type = type;
            this.createdTime = System.currentTimeMillis();
        }

        enum Type {
            LOGON, SERVER_TIME, GEN_USER_INFO, GEN_USER_INFO_2, PING, CANCEL_ALL, CREATE_ORDER, CREATE_ORDER_STOP_EARN, CREATE_ORDER_STOP_LOSS, GET_OPEN_ORDERS, CANCEL_ORDER, ACCOUNT_STATUS
        }
    }
}
