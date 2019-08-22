package com.x.flow.xflow.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import com.x.flow.xflow.BaseMapper;
import com.x.flow.xflow.entity.UserRole;

public interface UserRoleMapper extends BaseMapper<UserRole>{

	@Delete("DELETE FROM t_user_role WHERE uid = #{uid, jdbcType=INTEGER}")
	public int deleteAllRoleByUser(@Param("uid") Integer uid);
	
}
