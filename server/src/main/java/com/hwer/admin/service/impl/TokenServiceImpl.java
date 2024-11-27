package com.hwer.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwer.admin.entity.Token;
import com.hwer.admin.mapper.TokenMapper;
import com.hwer.admin.service.TokenService;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl extends ServiceImpl<TokenMapper, Token> implements TokenService {
}
