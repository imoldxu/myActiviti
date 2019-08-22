package com.x.flow.xflow.context;

public class HandleException extends RuntimeException{

	private static final long serialVersionUID = -7604792672088123562L;
	
	private int code;
	
	private String msg;
	
	public HandleException(ErrorCode errorCode) {
		this.msg = msg;
		this.code = errorCode.getCode();
	}
	
	public HandleException(int errorCode, String msg){
		this.msg = msg;
		this.code = errorCode;
	}

	public HandleException(String msg) {
		this.msg = msg;
		this.code = ErrorCode.NORMAL_ERROR.getCode();
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
