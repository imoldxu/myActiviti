package com.x.flow.xflow.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.x.flow.xflow.context.ErrorCode;
import com.x.flow.xflow.context.Response;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/error")
public class ErrorController {

	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@RequestMapping(value = "/unlogin")
	@ApiOperation(value = "未登陆", notes = "未登陆")
	public Response unlogin() {
		return Response.Error(ErrorCode.UNLOGIN.getCode(), ErrorCode.UNLOGIN.getMsg());
	}
	
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@RequestMapping(value = "/unAuth")
	@ApiOperation(value = "权限不足", notes = "权限不足")
	public Response unAuth() {
		return Response.Error(ErrorCode.DOMAIN_ERROR.getCode(), ErrorCode.DOMAIN_ERROR.getMsg());
	}
}
