package com.x.flow.xflow.entity;

import javax.persistence.Column;
import javax.persistence.Table;

import org.apache.ibatis.type.JdbcType;

import tk.mybatis.mapper.annotation.ColumnType;

@Table(name="t_role_permission")
public class RolePermission {
	
	@Column(name = "uid")
	@ColumnType(column="uid", jdbcType=JdbcType.INTEGER)
	public Integer rid;
	
	@Column(name = "pid")
	@ColumnType(column="pid", jdbcType=JdbcType.INTEGER)
	public Integer pid;

	public Integer getRid() {
		return rid;
	}

	public void setRid(Integer rid) {
		this.rid = rid;
	}

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

}
