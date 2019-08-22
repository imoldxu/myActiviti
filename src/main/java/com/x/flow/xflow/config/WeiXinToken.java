package com.x.flow.xflow.config;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.HostAuthenticationToken;
import org.apache.shiro.authc.RememberMeAuthenticationToken;

public class WeiXinToken implements AuthenticationToken{

	private String wxCode;
	
	public WeiXinToken(String wxCode) {
		this.wxCode = wxCode;
	}
	
	
	@Override
	public Object getPrincipal() {
		return wxCode;
	}

	@Override
	public Object getCredentials() {
		return wxCode;
	}

}
