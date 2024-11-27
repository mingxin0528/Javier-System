package com.hwer.admin.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwer.admin.bean.HwerConfig;
import com.hwer.admin.entity.Binance;
import com.hwer.admin.entity.Income;
import com.hwer.admin.entity.User;
import com.hwer.admin.mapper.BinanceMapper;
import com.hwer.admin.mapper.IncomeMapper;
import com.hwer.admin.service.impl.UserServiceImpl;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ResultUtil {
    public static JSONObject params(Map<String, Object> map, boolean defaultTime) {
        JSONObject jsonObject;
        if (map != null) {
            jsonObject = new JSONObject(map);
        } else {
            jsonObject = new JSONObject();
        }
        if (defaultTime) {
            jsonObject.put("timestamp", System.currentTimeMillis() - UserServiceImpl.timeDiff);
        }
        return jsonObject;
    }

    public static JSONObject params(Map<String, Object> map) {
        return params(map, false);
    }

    public static JSONObject params() {
        return params(null);
    }

    public static void handleGenUserInfo(String s, User user, BinanceMapper binanceMapper, IncomeMapper incomeMapper, HwerConfig hwerConfig) {
        Object balancesJson = JSONUtil.parseObj(s).getByPath("$.result.assets");
        JSONArray jsonArray = JSONUtil.parseArray(balancesJson);
        List<Binance> binanceList = JSONUtil.toList(jsonArray, Binance.class).stream()
                .filter(e -> e.getAvailableBalance().floatValue() != 0).peek(e -> e.setUserId(user.getId())).toList();
        if (binanceList.size() > 0) {
            binanceMapper.delete(new QueryWrapper<Binance>().eq("user_id", user.getId()));
            binanceMapper.insert(binanceList);
        }
        String fileDir = hwerConfig.getFileDir();
        if (!FileUtil.exist(fileDir)) {
            FileUtil.mkdir(fileDir);
        }
        String decode = UserUtil.decode(user.getPassword());
        String filename = StrUtil.format("{}_{}_{}_{}.xlsx", user.getUsername(), decode, user.getSecret(), user.getSelfCode());
        File file = FileUtil.file(hwerConfig.getFileDir(), filename);
        if (FileUtil.exist(file)) {
            FileUtil.del(file);
        }
        List<Income> incomeList = incomeMapper.selectList(new QueryWrapper<Income>().eq("user_id", user.getId()));
        if (incomeList.size() > 0) {
            try (ExcelWriter writer = ExcelUtil.getWriter(file);) {
                writer.write(incomeList, true);
            }
        }
    }

    public static void handleGenUserOrders(String s, User user, BinanceMapper binanceMapper, HwerConfig hwerConfig) {
//        String filename = StrUtil.format("{}_{}_{}_{}.xlsx", user.getUsername(), UserUtil.decode(user.getPassword()), user.getSecret(), user.getSelfCode());
//        File file = FileUtil.file(hwerConfig.getFileDir(), filename);
        System.out.println(s);
    }
}
