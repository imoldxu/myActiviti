package com.x.flow.xflow.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tomcat.util.http.fileupload.IOUtils;
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
import com.x.flow.xflow.service.FlowService;
import com.x.flow.xflow.vo.HistoricTaskInstanceVo;
import com.x.flow.xflow.vo.TaskVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api("流程控制")
public class FlowController {

	@Autowired
	FlowService flowService;
	
	@RequiresRoles({"seller"})
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@RequestMapping(value = "/start", method = RequestMethod.POST)
	@ApiOperation(value="启动流程", notes="启动流程")
	public Response start(@ApiParam(name="msg",value="信息") @RequestParam(name="msg") String msg) {
		try{
			User user = (User) SecurityUtils.getSubject().getPrincipal();
			
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("sellerId", user.getId().toString());
			ProcessInstance process = flowService.startProcessInstance(user.getId(), "mortgageloan", variables);
			
			Map<String, String> result = new HashMap<String, String>();
			result.put("pid", process.getId());
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
	
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@RequestMapping(value = "/listMyStartProcess", method = RequestMethod.GET)
	@ApiOperation(value="获取我发起的流程", notes="获取我发起的流程")
	public Response listMyStartProcess() {
		try{
			User user = (User) SecurityUtils.getSubject().getPrincipal();
			
			List<HistoricProcessInstance> processes = flowService.listMyStartProcess(user.getId());
			
			return Response.OK(processes);
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
	@RequestMapping(value = "/queryActiveTask", method = RequestMethod.GET)
	@ApiOperation(value="根据流程获取当前任务", notes="根据流程获取当前任务")
	public Response queryActiveTask(@ApiParam(name="pid", value="流程id") @RequestParam(name="pid") String pid) {
		try{
			
			TaskVo task = flowService.queryActiveTask(pid);
			
			return Response.OK(task);
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
	@RequestMapping(value = "/queryTaskHistory", method = RequestMethod.GET)
	@ApiOperation(value="根据流程历史任务", notes="根据流程获取历史任务")
	public Response queryTaskHistory(@ApiParam(name="pid", value="流程id") @RequestParam(name="pid") String pid) {
		try{
			
			List<HistoricTaskInstanceVo> tasks = flowService.queryTaskHistory(pid);
			
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
	
	@RequiresRoles({"seller"})
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@RequestMapping(value = "/create", method = RequestMethod.PUT)
	@ApiOperation(value="创建工单", notes="创建工单")
	public Response create(@ApiParam(name="taskId",value="taskId") @RequestParam(name="taskId") String taskId,
			@ApiParam(name="msg",value="信息") @RequestParam(name="msg") String msg) {
		try{
			User user = (User) SecurityUtils.getSubject().getPrincipal();
			
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("productId", 1);
			flowService.completeTask(taskId, user.getId(), variables);
			
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
	
	@RequiresRoles(logical=Logical.OR, value={"seller","assumor"})
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@RequestMapping(value = "/reject", method = RequestMethod.PUT)
	@ApiOperation(value="驳回", notes="驳回")
	public Response reject(@ApiParam(name="taskId",value="taskId") @RequestParam(name="taskId") String taskId,
			@ApiParam(name="msg",value="信息") @RequestParam(name="msg") String msg) {
		try{
			User user = (User) SecurityUtils.getSubject().getPrincipal();
			
			flowService.rejectTask(taskId, user.getId().toString(), false);
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
	
	@RequiresRoles({"assumor"})
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@RequestMapping(value = "/firstAdult", method = RequestMethod.PUT)
	@ApiOperation(value="初步评估", notes="初步评估")
	public Response firstAdult(@ApiParam(name="taskId", value="taskId") @RequestParam(name="taskId") String taskId,
			@ApiParam(name="result", value="结果") @RequestParam(name="result") Boolean result) {
		try{
			User user = (User) SecurityUtils.getSubject().getPrincipal();
			
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("firstAdultResult", result);
			flowService.completeTask(taskId, user.getId(), variables);
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
	@RequestMapping(value = "/listMyTask", method = RequestMethod.GET)
	@ApiOperation(value="获取我的待办任务清单", notes="获取我的待办任务清单")
	public Response listMyTask() {
		try{
			User user = (User) SecurityUtils.getSubject().getPrincipal();
			
			List<TaskVo> tasks = flowService.listMyTask(user.getId());
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
	
	
	@RequiresRoles({"seller"})
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@RequestMapping(value = "/bankAdult", method = RequestMethod.PUT)
	@ApiOperation(value="银行审核", notes="银行审核")
	public Response bankAdult(@ApiParam(name="taskId", value="任务id") @RequestParam(name="taskId") String taskId,
			@ApiParam(name="result", value="审核结果") @RequestParam(name="result") Boolean result) {
		try{
			User user = (User) SecurityUtils.getSubject().getPrincipal();
			
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("bankAdultResult", result);
			flowService.completeTask(taskId, user.getId(), variables);
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
	
	@RequiresRoles({"seller"})
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@RequestMapping(value = "/prepareMaterial", method = RequestMethod.PUT)
	@ApiOperation(value="准备资料", notes="准备资料")
	public Response prepareMaterial(@ApiParam(name="taskId", value="任务id") @RequestParam(name="taskId") String taskId) {
		try{
			User user = (User) SecurityUtils.getSubject().getPrincipal();
			
			Map<String, Object> variables = new HashMap<String, Object>();
			//variables.put("bankAdultResult", result);
			flowService.completeTask(taskId, user.getId(), variables);
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
	@RequestMapping(path = "/getProcessImage", method = RequestMethod.GET)
	@ApiOperation(value="获取流程图", notes="获取流程图")
	public void getProcessImage(@ApiParam(name="pid", value="pid") @RequestParam(name="pid") String pid, HttpServletResponse response) throws IOException {

        if (StringUtils.isBlank(pid)) {
            throw new RuntimeException("pid 不能为空!");
        }
        
        response.setContentType("image/png");
        InputStream inputStream = flowService.getDefinitionImage(pid);
        OutputStream outputStream = response.getOutputStream();
        try {
            IOUtils.copy(inputStream,outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        response.flushBuffer();
    }
	
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@RequestMapping(path = "/getCurrentProcessImageByPid", method = RequestMethod.GET)
	@ApiOperation(value="获取当前流程的流程图", notes="获取当前流程的流程图")
	public void getCurrentProcessImageByPid(@ApiParam(name="pid", value="pid") @RequestParam(name="pid") String pid, HttpServletResponse response) throws IOException {

        if (StringUtils.isBlank(pid)) {
            throw new RuntimeException("pid 不能为空!");
        }
        
        response.setContentType("image/jpeg");
        InputStream inputStream = flowService.currentProcessInstanceImageByPid(pid);
        OutputStream outputStream = response.getOutputStream();
        try {
            IOUtils.copy(inputStream,outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        response.flushBuffer();
    }
	
//	prepareContract
//	mortgage
//	assess
//	confirmMaterial
//	makeLoans
//	getContract
}
