package com.hwer.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hwer.admin.entity.User;
import com.hwer.admin.entity.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    List<User> getAllowedUsers();

    User getByUsername(UserVO user);

    Page<User> getPagedUserList(@Param("page") Page<User> page, @Param("vo") UserVO vo);
}
