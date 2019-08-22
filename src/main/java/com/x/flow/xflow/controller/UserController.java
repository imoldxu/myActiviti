package com.x.flow.xflow.controller;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.x.flow.xflow.context.HandleException;
import com.x.flow.xflow.context.Response;
import com.x.flow.xflow.entity.Role;
import com.x.flow.xflow.entity.User;
import com.x.flow.xflow.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;

@RestController
@RequestMapping("/user")
@Api("用户接口")
public class UserController {

	@Autowired
	UserService userService;
	
	@RequiresRoles({"manager"})
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ApiOperation(value = "用户注册账户", notes = "用户注册接口")
	public Response register(@ApiParam(name="phone",value="电话") @RequestParam(name="phone") String phone,
			@ApiParam(name="password",value="密码") @RequestParam(name="password") String password,
			@ApiParam(name="roleIds", value="角色") @RequestParam(name="roleIds") List<Integer> roleIds) {
		try {	
			userService.register(phone, password, roleIds);
			
			return Response.OK(null);
		}catch(HandleException e) {		
			return Response.Error(e.getCode(), e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			return Response.SystemError();
		}
	}
	
	@RequiresRoles({"manager"})
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@RequestMapping(value = "/updateUser", method = RequestMethod.POST)
	@ApiOperation(value = "更新用户信息", notes = "更新用户信息")
	@ApiResponse(response=User.class, code=200, message="")
	public Response updateUser(@ApiParam(name="user",value="用户json数据") @RequestBody User user) {
		try {	
			user = userService.updateUser(user);
			
			return Response.OK(user);
		}catch(HandleException e) {		
			return Response.Error(e.getCode(), e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			return Response.SystemError();
		}
	}
	
	@RequiresRoles({"manager"})
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@RequestMapping(value = "/addRole", method = RequestMethod.POST)
	@ApiOperation(value = "添加角色", notes = "添加角色")
	public Response addRole(@ApiParam(name="name",value="角色名称") @RequestParam(name="name") String name) {
		try {	
			userService.addRole(name);
			return Response.OK(null);
		}catch(HandleException e) {		
			return Response.Error(e.getCode(), e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			return Response.SystemError();
		}
	}
	
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@RequestMapping(value = "/listRole", method = RequestMethod.GET)
	@ApiOperation(value = "查询角色", notes = "查询角色")
	public Response listRole() {
		try {	
			List<Role> roleList = userService.listRole();
			return Response.OK(roleList);
		}catch(HandleException e) {		
			return Response.Error(e.getCode(), e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			return Response.SystemError();
		}
	}
	
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ApiOperation(value = "用户登陆", notes = "用户登陆接口")
	public Response login(@ApiParam(name="phone",value="电话") @RequestParam(name="phone") String phone,
			@ApiParam(name="password",value="密码") @RequestParam(name="password") String password) {
		try {	
			Subject subject = SecurityUtils.getSubject();
			AuthenticationToken token = new UsernamePasswordToken(phone, password);
			subject.login(token);
			User user = (User) subject.getPrincipal();
			return Response.OK(user);
		}catch(HandleException e) {		
			return Response.Error(e.getCode(), e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			return Response.SystemError();
		}
	}
}
