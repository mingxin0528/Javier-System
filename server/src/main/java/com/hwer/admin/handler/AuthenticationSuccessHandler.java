package com.hwer.admin.handler;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwer.admin.entity.Token;
import com.hwer.admin.entity.User;
import com.hwer.admin.properties.LoginProperties;
import com.hwer.admin.service.TokenService;
import com.hwer.admin.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

@Slf4j
@Component
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Resource
    private UserService userService;
    @Resource
    private TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication ac) throws IOException, ServletException {
        String saveLogin = request.getParameter(LoginProperties.SAVE_LOGIN);
        boolean saveLoginFlag = StrUtil.equalsIgnoreCase("true", StrUtil.emptyToDefault(saveLogin, "false"));
        String username = ((UserDetails) ac.getPrincipal()).getUsername();
        User user = userService.getOne(new QueryWrapper<User>().eq("username", username));
        Assert.isTrue(user != null, username + " is null");
        String token = IdUtil.fastSimpleUUID();
        Token tokenUser = new Token(username, new Date(System.currentTimeMillis() + (saveLoginFlag ? 30 * 24 * 60 : 60) * 60 * 1000), 1, saveLoginFlag ? 1 : 0);
        user.setToken(token);
        userService.updateById(user);
        tokenService.saveOrUpdate(tokenUser);
        HashMap<String, String> map = MapUtil.of("code", "200");
        map.put("data", token);
        map.put("status", "Yes");
        map.put("msg", "success");
        response.setCharacterEncoding(CharsetUtil.UTF_8);
        response.setContentType(ContentType.JSON.getValue());
        response.getWriter().write(JSONUtil.toJsonStr(map));
    }
}
