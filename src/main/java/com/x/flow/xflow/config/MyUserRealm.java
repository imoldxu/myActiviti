package com.x.flow.xflow.config;

import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.x.flow.xflow.entity.Role;
import com.x.flow.xflow.entity.User;
import com.x.flow.xflow.service.UserService;

public class MyUserRealm extends AuthorizingRealm{

	@Autowired
	private UserService userService;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		User user = (User) principals.fromRealm(this.getClass().getName()).iterator().next();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //获得该用户角色
        Set<Role> roles = user.getRoles();
        //需要将 role 封装到 Set 作为 info.setRoles() 的参数
        //设置该用户拥有的角色
        roles.forEach(role->{
        	info.addRole(role.getName());
        	role.getPermissions().forEach(permission->{
        		info.addStringPermission(permission.getName());
        	});
        });
        return info;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		if(token instanceof UsernamePasswordToken) {
			UsernamePasswordToken tk = (UsernamePasswordToken)token;
			User user = userService.login(tk.getUsername(), new String(tk.getPassword())); 
	        if(user == null){
	            throw new UnknownAccountException(); //没找到账号
	        }
	        
	//        if(Boolean.TRUE.equals(user.getLocked())){
	//            throw new LockedAccountException(); //账号被锁定
	//        }
	        
	        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
	                user,
	                user.getPassword(),
	                this.getClass().getName());
			return authenticationInfo;
		}else if(token instanceof WeiXinToken){
			WeiXinToken tk = (WeiXinToken)token;
			
			return null;
		}else {
			return null;
		}
	}

	@Override
	public boolean supports(AuthenticationToken token) {
		if(token instanceof WeiXinToken) {
			return true;
		}
		return super.supports(token);
	}
	
}
