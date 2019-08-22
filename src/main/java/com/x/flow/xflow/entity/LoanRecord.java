package com.x.flow.xflow.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.ibatis.type.JdbcType;

import tk.mybatis.mapper.annotation.ColumnType;

@Table(name="t_loan_record")
public class LoanRecord {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ColumnType(jdbcType = JdbcType.BIGINT)
	private Long id;
	
	@Column(name = "uid")
	@ColumnType(jdbcType = JdbcType.INTEGER)
	private Integer uid;

	@Column(name = "cid")
	@ColumnType(jdbcType = JdbcType.INTEGER)
	private Integer cid;
	
	@Column(name = "money")
	@ColumnType(jdbcType = JdbcType.INTEGER)
	private Integer money;
	
	@Column(name = "premonth")
	@ColumnType(jdbcType = JdbcType.INTEGER)
	private Integer premonth;
}
