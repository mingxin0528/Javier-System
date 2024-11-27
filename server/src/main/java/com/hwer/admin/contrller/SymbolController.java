package com.hwer.admin.contrller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hwer.admin.entity.R;
import com.hwer.admin.entity.Symbol;
import com.hwer.admin.entity.vo.PageVO;
import com.hwer.admin.service.SymbolService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/symbol")
public class SymbolController {
    @Resource
    private SymbolService symbolService;

    @GetMapping("/index")
    public ModelAndView index() {
        return new ModelAndView("symbol/index");
    }

    @GetMapping("/page")
    @ResponseBody
    public R page(PageVO vo) {
        return R.ok(symbolService.page(new Page<>(vo.getPage(), vo.getSize())));
    }

    @GetMapping("/list")
    @ResponseBody
    public R list() {
        return R.ok(symbolService.list());
    }

    @PostMapping("/add")
    @ResponseBody
    public R list(Symbol symbol) {
        return R.ok(symbolService.saveOrUpdate(symbol));
    }

    @PostMapping("/remove")
    @ResponseBody
    public R remove(Symbol symbol) {
        return R.ok(symbolService.removeById(symbol));
    }

    @PostMapping("/set")
    @ResponseBody
    public R set(String symbols) {
        return R.ok(symbolService.setActivatedSymbols(symbols));
    }
}
