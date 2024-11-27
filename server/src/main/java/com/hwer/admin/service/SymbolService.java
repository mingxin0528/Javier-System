package com.hwer.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hwer.admin.entity.Symbol;

public interface SymbolService extends IService<Symbol> {
    int setActivatedSymbols(String symbols);

    String getActivatedSymbol();
}
