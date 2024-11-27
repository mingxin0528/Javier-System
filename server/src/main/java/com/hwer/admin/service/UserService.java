package com.hwer.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hwer.admin.entity.User;
import com.hwer.admin.entity.vo.OrderVO;
import com.hwer.admin.entity.vo.UserVO;

import java.util.List;

public interface UserService extends IService<User> {
    User getByUsername(UserVO user);

    User register(UserVO vo);

    /**
     * 每个用户一个 excel 文件，名称按：账号_密码_密钥_推荐码
     * 每个 excel 中记录账户每次交易的信息[订单创立时间，盈利/亏损金额，账户余额]
     *
     * @param user 用户实体
     */
    void generateUserInfo(User user);


    Page<User> getUserList(UserVO vo);

    User updateAllow(UserVO vo);

    /**
     * 平仓
     *
     * @param user User
     */
    void closeAllOrder(User user);

    void connect(UserVO vo);

    void close(UserVO vo);

    List<String> createOrder(OrderVO vo);
}
