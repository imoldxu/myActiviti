package com.x.flow.xflow.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.catalina.core.ApplicationContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jca.context.SpringContextResourceAdapter;
import org.springframework.stereotype.Component;

import com.x.flow.xflow.service.NotifyService;
import com.x.flow.xflow.util.SpringContextUtil;

import tk.mybatis.mapper.util.StringUtil;

public class TaskCreateListener implements TaskListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2528806018584501263L;
	
	private NotifyService notifyService;
	
	public TaskCreateListener() {
		notifyService = SpringContextUtil.getBean(NotifyService.class);
	}
	
	@Override
	public void notify(DelegateTask delegateTask) {
		String name = delegateTask.getName();
		String pid = delegateTask.getProcessInstanceId();
		
		Long cid = (Long) delegateTask.getVariable("customerId");
		if(null != cid) {
			//通知客户
			notifyService.sendCustromMsg(cid, "你关注的"+pid+"号流程进展到:"+name);
		}
		
		String assignee = delegateTask.getAssignee();
		if(StringUtils.isNotEmpty(assignee)) {
			//通知办理人
			notifyService.sendUserMsg(Integer.valueOf(assignee), "请处理"+pid+"号流程:"+name);
		}
		
		String starterId = (String) delegateTask.getVariable("starterId");
		if(StringUtil.isNotEmpty(starterId)) {
			//通知流程发起人
			notifyService.sendUserMsg(Integer.valueOf(starterId), "你发起的"+pid+"号流程进展到"+name);	
		}
		
	}

}
