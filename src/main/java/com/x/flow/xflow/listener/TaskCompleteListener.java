package com.x.flow.xflow.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.x.flow.xflow.service.NotifyService;
import com.x.flow.xflow.util.SpringContextUtil;

import tk.mybatis.mapper.util.StringUtil;

public class TaskCompleteListener implements TaskListener{


	/**
	 * 
	 */
	private static final long serialVersionUID = -8835768537541189947L;
	
	private NotifyService notifyService;
	
	public TaskCompleteListener() {
		notifyService = SpringContextUtil.getBean(NotifyService.class);
	}
	
	@Override
	public void notify(DelegateTask delegateTask) {
		String name = delegateTask.getName();
		String pid = delegateTask.getProcessInstanceId();
		
		Long cid = (Long) delegateTask.getVariable("customerId");
		if(null != cid) {
			//通知客户
			notifyService.sendCustromMsg(cid, "你关注的"+pid+"号流程"+name+"完毕");
		}
		
		String starterId = (String) delegateTask.getVariable("starterId");
		if(StringUtil.isNotEmpty(starterId)) {
			//通知流程发起人
			notifyService.sendUserMsg(Integer.valueOf(starterId), "你发起的"+pid+"号流程"+name+"处理完毕");	
		}
	}

}
