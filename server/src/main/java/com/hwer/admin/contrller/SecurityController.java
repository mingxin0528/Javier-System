package com.hwer.admin.contrller;

import com.hwer.admin.entity.R;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/common")
@Transactional(rollbackFor = Exception.class)
public class SecurityController {
    @RequestMapping(value = "/401", method = RequestMethod.GET)
    public R needLogin() {
        return R.no(401, "登录失效");
    }
}
