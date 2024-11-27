package com.hwer.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwer.admin.entity.Order;
import com.hwer.admin.mapper.OrderMapper;
import com.hwer.admin.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
}
