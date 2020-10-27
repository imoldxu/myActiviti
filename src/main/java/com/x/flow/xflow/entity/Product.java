package com.x.flow.xflow.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.ibatis.type.JdbcType;

import tk.mybatis.mapper.annotation.ColumnType;

@Table(name="t_product")
public class Product {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ColumnType(jdbcType = JdbcType.INTEGER)
	private Integer id;
	
	@Column(name = "name")
	@ColumnType(jdbcType = JdbcType.VARCHAR)
	private String name;
	

	@Column(name = "bankid")
	@ColumnType(jdbcType = JdbcType.VARCHAR)
	private Integer bankid;
	
	@Column(name = "condition")
	@ColumnType(jdbcType = JdbcType.VARCHAR)
	private String condition;
	
	@Column(name = "info")
	@ColumnType(jdbcType = JdbcType.VARCHAR)
	private String info;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getBankid() {
		return bankid;
	}

	public void setBankid(Integer bankid) {
		this.bankid = bankid;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	
}
