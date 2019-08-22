package com.x.flow.xflow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.x.flow.xflow.entity.User;

@Service
public class NotifyService {

	private static final Logger logger = LoggerFactory.getLogger(NotifyService.class);
	
	@Autowired
	private UserService userService;
	
	public void sendCustromMsg(Long cid, String msg) {
		logger.info("客户("+cid+")收到消息："+msg);
	}

	public void sendUserMsg(Integer uid, String msg) {
		//User user = userService.getUserById(uid);
		logger.info("员工("+uid+")收到消息："+msg);
	}
}
