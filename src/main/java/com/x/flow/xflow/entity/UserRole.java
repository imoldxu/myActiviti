package com.x.flow.xflow.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.ibatis.type.JdbcType;

import tk.mybatis.mapper.annotation.ColumnType;

@Table(name="t_user_role")
public class UserRole {

	@Column(name="uid")
	@ColumnType(column="uid", jdbcType=JdbcType.INTEGER)
	public Integer uid;

	@Column(name="rid")
	@ColumnType(column="rid", jdbcType=JdbcType.INTEGER)
	public Integer rid;

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public Integer getRid() {
		return rid;
	}

	public void setRid(Integer rid) {
		this.rid = rid;
	}
	
}
