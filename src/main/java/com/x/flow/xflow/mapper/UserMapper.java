package com.x.flow.xflow.mapper;

import org.apache.ibatis.annotations.Param;

import com.x.flow.xflow.BaseMapper;
import com.x.flow.xflow.entity.User;

public interface UserMapper extends BaseMapper<User>{

	public User selectUserByPhone(@Param(value = "phone") String phone);
	
	public User selectUserById(@Param(value = "uid") Integer uid);
}
