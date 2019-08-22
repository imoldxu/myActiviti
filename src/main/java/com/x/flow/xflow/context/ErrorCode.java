package com.x.flow.xflow.context;

public enum ErrorCode {
	
	/**
	 * showdoc
	 * @catalog 接口文档/全局错误码
	 * @title 全局错误码
	 * @remark 1 成功
	 */
	OK(1,"成功"),
	
	NORMAL_ERROR(2,""),
	
	SYSTEM_ERROR(3,"系统异常"),
	
	SESSION_ERROR(4,"session已经失效"),
	
	LOGIN_ERROR(5,"登陆失败"),
	
	ARG_ERROR(6,"参数错误"),

	MODULE_ERROR(7,"服务模块异常"),

	RECOMMIT_ERROR(8,"重复提交"),

	DATA_ERROR(9,"内部数据异常"), 

	LOCK_ERROR(10,"锁异常"),

	WX_NET_ERROR(11,"微信网络异常"),

	WX_CRYPTO_ERROR(12,"微信加密异常"),

	WX_ERROR(13,"微信api异常"),
	
	DOMAIN_ERROR(14,"权限错误"),

	UNLOGIN(15,"你还未登陆");
	
	private int code;
	
	private String msg;
	
	ErrorCode(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
