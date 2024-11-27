package com.hwer.admin.config;

import com.hwer.admin.filter.JwtTokenOncePerRequestFilter;
import com.hwer.admin.handler.AuthenticationFailHandler;
import com.hwer.admin.handler.AuthenticationSuccessHandler;
import com.hwer.admin.service.impl.UserDetailServiceImpl;
import com.hwer.admin.util.UserUtil;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    public UserDetailsService userDetailService() {
        return new UserDetailServiceImpl();
    }


    @Bean
    PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return UserUtil.encode(rawPassword.toString());
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return UserUtil.encode(rawPassword.toString()).equals(encodedPassword);
            }
        };
    }

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailService());
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    SecurityFilterChain web(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                (authorize) -> authorize.requestMatchers("/static/**", "/user/logon", "/common/**").permitAll()
                        .anyRequest().authenticated()
        ).formLogin(
                (form) -> form.loginPage("/user/login").permitAll()
        ).logout(
                LogoutConfigurer::permitAll
        ).csrf(
                AbstractHttpConfigurer::disable
        ).cors(
                AbstractHttpConfigurer::disable
        );
        return http.build();
    }


    @Bean
    public JwtTokenOncePerRequestFilter authenticationJwtTokenFilter() throws Exception {
        return new JwtTokenOncePerRequestFilter();
    }
}
