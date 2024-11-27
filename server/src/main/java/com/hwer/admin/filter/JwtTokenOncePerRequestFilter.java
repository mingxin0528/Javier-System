package com.hwer.admin.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwer.admin.entity.Token;
import com.hwer.admin.entity.User;
import com.hwer.admin.properties.LoginProperties;
import com.hwer.admin.service.TokenService;
import com.hwer.admin.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenOncePerRequestFilter extends OncePerRequestFilter {
    @Resource
    private TokenService tokenService;
    @Resource
    private UserService userService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = request.getHeader(LoginProperties.HTTP_HEADER);
        if (StrUtil.isEmpty(tokenHeader)) {
            tokenHeader = request.getParameter(LoginProperties.HTTP_HEADER);
        }
        if (StrUtil.isEmpty(tokenHeader)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            UsernamePasswordAuthenticationToken token = getUsernamePasswordAuthenticationToken(tokenHeader, response);
            SecurityContextHolder.getContext().setAuthentication(token);
        } catch (Exception e) {
            log.warn("自定义权限过滤失败" + e);
        }
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(String token, HttpServletResponse response) throws IOException {
        User user = userService.getOne(new QueryWrapper<User>().eq("token", token));
        String username = null;
        if (null == user) {
            Map<String, String> result = new HashMap<>(4);
            result.put("code", "401");
            result.put("status", "No");
            result.put("data", "null");
            result.put("msg", "登录失败，请重新登录");
            response.getWriter().write(JSONUtil.toJsonStr(result));
            return null;
        }
        username = user.getUsername();
        Token tokenUser = tokenService.getOne(new QueryWrapper<Token>().eq("username", username));
        if (tokenUser.getEx().getTime() > System.currentTimeMillis()) {
            tokenUser.setEx(new Date(System.currentTimeMillis() + (tokenUser.getSaveLogin() > 0 ? 30 * 24 * 60 : 60) * 60 * 1000));
            tokenService.updateById(tokenUser);
            return new UsernamePasswordAuthenticationToken(user, null, null);
        }
        return null;
    }


}
