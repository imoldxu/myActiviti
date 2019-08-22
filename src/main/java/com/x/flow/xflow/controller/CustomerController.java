package com.x.flow.xflow.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.x.flow.xflow.context.ErrorCode;
import com.x.flow.xflow.context.HandleException;
import com.x.flow.xflow.context.Response;
import com.x.flow.xflow.entity.Customer;
import com.x.flow.xflow.entity.User;
import com.x.flow.xflow.service.CustomerService;
import com.x.flow.xflow.service.FlowService;
import com.x.flow.xflow.vo.TaskVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api("客户接口")
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	private CustomerService customerService;
	@Autowired
	private FlowService flowService;
	
	
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ApiOperation(value="客户登陆", notes="客户登陆")
	public Response login(@ApiParam(name="phone",value="手机号") @RequestParam(name="phone") String phone, HttpServletRequest request) {
		try{
			
			Customer customer= customerService.login();
	
			HttpSession session = request.getSession();
			session.setAttribute("customer", customer);
			
			return Response.OK(customer);
		}catch(HandleException e) {
			return Response.Error(e.getCode(), e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			return Response.SystemError();
		}
	}
	
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@RequestMapping(path = "/associateProcess", method = RequestMethod.PUT)
	@ApiOperation(value="客户关注流程", notes="客户关注流程")
	public Response associateProcess(@ApiParam(name="pid", value="pid") @RequestParam(name="pid") String pid,
			HttpServletRequest request) {

		try {
	        if (StringUtils.isBlank(pid)) {
	            return Response.Error(ErrorCode.ARG_ERROR.getCode(), "pid 不能为空!");
	        }
	        
	        Customer customer = (Customer) request.getSession().getAttribute("customer");
			
	        Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("customerId", customer.getId());
			
	        flowService.setVariables(pid, variables);
	        
	        return Response.OK(null);
		}catch(HandleException e) {
			return Response.Error(e.getCode(), e.getMessage());
		}catch (ActivitiException e) {
			return Response.NormalError(e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			return Response.SystemError();
		}
    }
	
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@RequestMapping(path = "/listCustomerTask", method = RequestMethod.GET)
	@ApiOperation(value="获取客户关注流程", notes="获取客户关注流程")
	public Response listCustomerTask(HttpServletRequest request) {

		try {
			Customer customer = (Customer) request.getSession().getAttribute("customer");
		
	        List<TaskVo> tasks = flowService.listTaskByCustomer(customer.getId());
	        
	        return Response.OK(tasks);
		}catch(HandleException e) {
			return Response.Error(e.getCode(), e.getMessage());
		}catch (ActivitiException e) {
			return Response.NormalError(e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			return Response.SystemError();
		}
    }
	
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@RequestMapping(path = "/listCustomerHistoryTask", method = RequestMethod.GET)
	@ApiOperation(value="获取客户关注流程", notes="获取客户关注流程")
	public Response listCustomerHistoryTask(HttpServletRequest request) {

		try {
			Customer customer = (Customer) request.getSession().getAttribute("customer");
		
	        //List<HistoricProcessInstance> result = flowService.listHistoricProcessInstanceByVar(customer.getId());
			List<HistoricTaskInstance> result = flowService.listHistoricTaskByVar(customer.getId());
			//List<HistoricVariableInstance> result = flowService.listHistoricVariableInstanceByVar(customer.getId());
	        
	        return Response.OK(result);
		}catch(HandleException e) {
			return Response.Error(e.getCode(), e.getMessage());
		}catch (ActivitiException e) {
			return Response.NormalError(e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			return Response.SystemError();
		}
    }
}
