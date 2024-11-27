package com.hwer.admin.contrller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hwer.admin.entity.R;
import com.hwer.admin.entity.User;
import com.hwer.admin.entity.vo.UserVO;
import com.hwer.admin.service.UserService;
import com.hwer.admin.util.UserUtil;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@AllArgsConstructor
public class UserCommonController {
    @Resource
    private UserService userService;


    @PostMapping("/common/user/register")
    @ResponseBody
    public R register(@RequestBody @Validated UserVO vo) {
        return R.ok(userService.register(vo));
    }

    @PostMapping("/common/user/login")
    @ResponseBody
    public R login(@RequestBody @Validated UserVO vo) {
        User username = userService.getOne(new QueryWrapper<User>().eq("username", vo.getUsername()).eq("status", 1));
        if (null == username) {
            return R.no("用户不存在");
        }
        if (!UserUtil.encode(vo.getPassword()).equals(username.getPassword())) {
            return R.no("用户名或密码错误");
        }
        return R.ok(username);
    }

    @PostMapping("/common/user/getByUsername")
    @ResponseBody
    public R getByUsername(@RequestBody @Validated UserVO vo) {
        User username = userService.getByUsername(vo);
        if (null == username) {
            return R.no("用户不存在");
        }
        if (username.getShowSecret() == 0) {
            username.setSecret("");
        }
        return R.ok(username);
    }

    @PostMapping("/common/user/updateAllow")
    @ResponseBody
    public R updateAllow(@RequestBody @Validated UserVO vo) {
        return R.ok(userService.updateAllow(vo));
    }

    @PostMapping("/common/user/updateKeys")
    @ResponseBody
    public R updateKeys(@RequestBody @Validated UserVO vo) {
        User username = userService.getOne(new QueryWrapper<User>().eq("username", vo.getUsername()).eq("status", 1));
        if (null == username) {
            return R.no("用户不存在");
        }
        if (username.getAllow() == 1) {
            return R.no("请先停止策略");
        }
        userService.update(new UpdateWrapper<User>().set("api_key", vo.getApiKey()).set("secret_key", vo.getSecretKey()).eq("username", vo.getUsername()));
        return R.ok(userService.getByUsername(vo));
    }

    @PostMapping("/common/user/hideSecret")
    @ResponseBody
    public R hideSecret(@RequestBody @Validated UserVO vo) {
        User username = userService.getOne(new QueryWrapper<User>().eq("username", vo.getUsername()).eq("status", 1));
        if (null == username) {
            return R.no("用户不存在");
        }
        userService.update(new UpdateWrapper<User>().set("show_secret", 0).eq("username", vo.getUsername()));
        return getByUsername(vo);
    }

    @PostMapping("/common/user/destroyAccount")
    @ResponseBody
    public R destroyAccount(@RequestBody @Validated UserVO vo) {
        User username = userService.getOne(new QueryWrapper<User>().eq("username", vo.getUsername()).eq("status", 1));
        if (null == username) {
            return R.no("用户不存在");
        }
        userService.update(new UpdateWrapper<User>().set("status", 0).eq("username", vo.getUsername()));
        return R.ok(userService.getOne(new QueryWrapper<User>().eq("username", vo.getUsername()).eq("status", 0)));
    }
}
