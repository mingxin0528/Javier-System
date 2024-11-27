package com.hwer.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwer.admin.bean.HwerConfig;
import com.hwer.admin.entity.Symbol;
import com.hwer.admin.entity.User;
import com.hwer.admin.mapper.SymbolMapper;
import com.hwer.admin.mapper.UserMapper;
import com.hwer.admin.service.SymbolService;
import com.hwer.admin.util.Request;
import com.hwer.admin.util.URLConstant;
import jakarta.annotation.Resource;
import org.apache.ibatis.executor.BatchResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SymbolServiceImpl extends ServiceImpl<SymbolMapper, Symbol> implements SymbolService {
    @Resource
    private SymbolMapper symbolMapper;
    public static String SYMBOL = "";

    @Resource
    private UserMapper userMapper;

    @Resource
    private HwerConfig hwerConfig;

    @Override
    public int setActivatedSymbols(String symbols) {
        symbolMapper.update(new UpdateWrapper<Symbol>().set("activated", 0));
        String[] s = symbols.split(",");
        List<Symbol> symbolList = new ArrayList<>();
        for (String type : s) {
            SYMBOL = type;
            symbolList.add(new Symbol(type, 1));
            break;
        }
        List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("status", 1).eq("allow", 1).eq("role", 0));
        try {
            for (User user : users) {
                Map<String, Object> map = new HashMap<>();
                map.put("symbol", SYMBOL);
                map.put("recvWindow", 10000);
                Request request = new Request(URLConstant.U_PROD_URL, user.getApiKey(), user.getSecretKey(), hwerConfig);
                String res = request.sendSignedRequest(map, "/fapi/v1/symbolConfig", "GET");
                System.out.println(res);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<BatchResult> batchResults = symbolMapper.updateById(symbolList);
        return batchResults.size();
    }

    @Override
    public String getActivatedSymbol() {
        if (StrUtil.isEmpty(SYMBOL)) {
            Symbol activated = getOne(new QueryWrapper<Symbol>().eq("activated", 1));
            SYMBOL = activated.getType();
        }
        return SYMBOL;
    }

}
