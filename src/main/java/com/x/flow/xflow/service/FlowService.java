package com.x.flow.xflow.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiTaskAlreadyClaimedException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManagerImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.x.flow.xflow.context.HandleException;
import com.x.flow.xflow.util.ViewObjectConverter;
import com.x.flow.xflow.vo.HistoricTaskInstanceVo;
import com.x.flow.xflow.vo.ProcessDefinitionVo;
import com.x.flow.xflow.vo.TaskVo;

/**
 * 流程服务
 * @author 老徐
 *
 */
@Service
public class FlowService {

	@Autowired
	ProcessEngine processEngine;
	@Autowired
	RuntimeService runtimeService;
	@Autowired
	TaskService taskService;
	@Autowired
	IdentityService identityService;
	@Autowired
	HistoryService historyService;
	@Autowired
	RepositoryService repositoryService;
	@Autowired
    ManagementService managementService;
	
	
	/***
     *  开始流程
     * @param instanceKey 流程实例key
     * @param variables 参数
     */
    public ProcessInstance startProcessInstance(Integer uid, String instanceKey, Map<String, Object> variables) {
    	identityService.setAuthenticatedUserId(uid.toString());//设置流程发起人，配合startEvent中的init变量使用
    	ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(instanceKey, variables);
//      log.debug(String.format("id:%s,activitiId:%s", processInstance.getId(), processInstance.getActivityId()));
        return processInstance;
    }
	

    /**
     * 根据流程id查询当前任务
     * @param pid
     * @return
     */
	public TaskVo queryActiveTask(String pid) {
		Task task = taskService.createTaskQuery().processInstanceId(pid).active().singleResult();
		TaskVo taskVo = ViewObjectConverter.objToBeanVo(task, TaskVo.class, "variables");
		
		Map<String, Object> variables = taskService.getVariables(task.getId());
		taskVo.setVariables(variables);

		return taskVo;
	}
    
	/**
	 * 获取我发起的任务列表
	 * @param uid
	 * @return
	 */
	public List<HistoricProcessInstance> listMyStartProcess(Integer uid){
		List<HistoricProcessInstance> historicProcessInstanceList = historyService
				.createHistoricProcessInstanceQuery()
				.startedBy(uid.toString())
				.list();
		return historicProcessInstanceList;
	}
	
    /**
     * 查询我代办的任务
     * @param uid
     * @return
     */
	public List<TaskVo> listMyTask(Integer uid) {
		List<Task> resultTask = taskService.createTaskQuery()
				//.processDefinitionKey("mortgageloan")
				.taskCandidateOrAssigned(uid.toString()).list();
		
		List<TaskVo> tasks = new ArrayList<TaskVo>();
        if (resultTask != null && resultTask.size() > 0) {

            tasks = ViewObjectConverter.listToBeanVo(resultTask, TaskVo.class, "variables");

            for (TaskVo task : tasks) {

                Map<String, Object> variables = taskService.getVariables(task.getId());
                task.setVariables(variables);

                //log.debug("ID:" + task.getId() + ",姓名:" + task.getName() + ",接收人:" + task.getAssignee() + ",开始时间:" + task.getCreateTime());
            }
        }
		return tasks;
	}

	/**
	 * 查询任务的变量
	 * @param taskId
	 * @return
	 */
	public Map<String, Object> queryVariables(String taskId) {
        return taskService.getVariables(taskId);
    }
	
	/**
	 * 查询流程定义
	 * @param instanceKey
	 * @return
	 */
	public List<ProcessDefinitionVo> processDefinitionQuery(String instanceKey) {

        //创建查询对象
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();

        ProcessDefinitionQuery processDefinitionQuery = query.latestVersion();
        if (StringUtils.isBlank(instanceKey)) {
            processDefinitionQuery.list();
        } else {
            query.processDefinitionKey(instanceKey);
        }

        //添加查询条件
        // .processDefinitionName("My process")//通过name获取
        // .orderByProcessDefinitionId()//根据ID排序
        //执行查询获取流程定义明细
        List<ProcessDefinition> pds = query.list();
        //log.debug("queryProcdef query list:" + pds);
        //if (pds != null && pds.size() > 0) {
            //for (ProcessDefinition pd : pds) {
                //log.debug("ID:" + pd.getId() + ",NAME:" + pd.getName() + ",KEY:" + pd.getKey() + ",VERSION:" + pd.getVersion() + ",RESOURCE_NAME:" + pd.getResourceName() + ",DGRM_RESOURCE_NAME:" + pd.getDiagramResourceName());
            //}
        //}


        return ViewObjectConverter.listToBeanVo(pds, ProcessDefinitionVo.class);
    }

	
	/**
	 * 认领任务
	 * @param uid
	 * @param taskid
	 */
	public void claimTask(Integer uid, String taskid) {
		try {
			taskService.claim(taskid, uid.toString());
		}catch (ActivitiTaskAlreadyClaimedException e) {
			throw new HandleException("该任务已经被他人领取");
		}
	}

	/**
	 * 让他人代替处理任务
	 * @param uid
	 * @param taskid
	 */
	public void assignTask(Integer owner, Integer assignee, String taskId) {
		taskService.setOwner(taskId, owner.toString());
		taskService.setAssignee(taskId, assignee.toString());
	}
	
	/**
	 * 指派任务
	 * @param uid
	 * @param taskid
	 */
	public void assignTask(Integer assignee, String taskId) {
		taskService.setAssignee(taskId, assignee.toString());
	}
	
	/**
	 * 释放任务
	 * @param uid
	 * @param taskid
	 */
	public void release(Integer uid, String taskid) {
		Task task = taskService.createTaskQuery().taskId(taskid).singleResult();
		if (null != task.getAssignee() && task.getAssignee().equals(uid.toString())) {
			taskService.resolveTask(taskid);
		} else {
			throw new HandleException("你无权释放该任务");
		}
	}

	/**
	 * 完成任务
	 * @param taskId
	 * @param uid
	 * @param variables
	 * @return
	 */
	public boolean completeTask(String taskId, Integer uid, Map<String, Object> variables) {

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        taskService.setVariablesLocal(taskId, variables);
        
//      taskService.setAssignee(taskId, uid.toString()); 使用setAssignee不会判断是否任务已经被别人领取
		claimTask(uid, taskId);
        
        taskService.complete(taskId, variables);

        return isFinishProcess(task.getProcessInstanceId());
    }

	/**
	 * 删除任务
	 * @param taskId
	 */
    public void deleteTask(String taskId) {
        taskService.deleteTask(taskId);
    }
	
    /**
     * 判断流程是否结束
     * @param processInstanceId
     * @return
     */
	public boolean isFinishProcess(String pid) {

        /**判断流程是否结束，查询正在执行的执行对象表*/
        ProcessInstance rpi = processEngine.getRuntimeService()//
                .createProcessInstanceQuery()//创建流程实例查询对象
                .processInstanceId(pid)
                .singleResult();

        return rpi == null;
    }
	
	/**
	 * 驳回任务
	 * @param taskId
	 * @param assignee
	 * @param returnStart
	 */
	public void rejectTask(String taskId, String assignee, boolean returnStart) {
        //当前任务
        Task currentTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        //获取流程定义
//        Process process = activitiService.repositoryService.getBpmnModel(currentTask.getProcessDefinitionId()).getMainProcess();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(currentTask.getProcessDefinitionId());

        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().processInstanceId(currentTask.getProcessInstanceId()).activityType("userTask").finished().orderByHistoricActivityInstanceEndTime().asc().list();
        if (list == null || list.size() == 0) {
            throw new ActivitiException("操作历史流程不存在");
        }

        //获取目标节点定义
        FlowNode targetNode = null;

        if (returnStart) {//驳回到发起点

            targetNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(list.get(0).getActivityId());
        } else {//驳回到上一个节点

            FlowNode currNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(currentTask.getTaskDefinitionKey());

            for (int i = 0; i < list.size(); i++) {//倒序审核任务列表，最后一个不与当前节点相同的节点设置为目标节点
                FlowNode lastNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(list.get(i).getActivityId());
                if (list.size() > 0 && currNode.getId().equals(lastNode.getId())) {
                    targetNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(list.get(i - 1).getActivityId());
                    break;
                }
            }

            if (targetNode == null && list.size() > 0) {
                targetNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(list.get(list.size() - 1).getActivityId());
            }


//            Map<String, Object> flowElementMap = new TreeMap<>();
//            Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();
//            for (FlowElement flowElement : flowElements) {
//
//                flowElementMap.put(flowElement.getId(), flowElement);
//            }
//
//            targetNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(tmplist.get(tmplist.size() - 1).getActivityId());

        }

        if (targetNode == null) {
            throw new ActivitiException("开始节点不存在");
        }


        //删除当前运行任务
        String executionEntityId = managementService.executeCommand(new DeleteTaskCmd(currentTask.getId()));
        //流程执行到来源节点
        managementService.executeCommand(new SetFLowNodeAndGoCmd(targetNode, executionEntityId));
    }

	/**
	 * 查询流程的历史任务
	 * @param pid
	 * @return
	 */
	public List<HistoricTaskInstanceVo> queryTaskHistory(String pid) {

        List<HistoricTaskInstance> list = historyService
                .createHistoricTaskInstanceQuery()//创建历史任务实例查询
                .processInstanceId(pid)//
                .orderByHistoricTaskInstanceStartTime().asc()
                .list();


        List<HistoricTaskInstanceVo> list1 = null;
        if (list != null && list.size() > 0) {
            list1 = new ArrayList<>();
            for (HistoricTaskInstance hti : list) {
                //log.debug(hti.getId() + "    " + hti.getName() + "    " + hti.getProcessInstanceId() + "   " + hti.getStartTime() + "   " + hti.getEndTime() + "   " + hti.getDurationInMillis());
                //log.debug("################################");

                HistoricTaskInstanceVo historicTaskInstanceVo = ViewObjectConverter.objToBeanVo(hti, HistoricTaskInstanceVo.class);
                if (historicTaskInstanceVo != null) {

                    List<HistoricVariableInstance> list2 = historyService.createHistoricVariableInstanceQuery().taskId(hti.getId()).list();
                    if (list2 != null && list2.size() > 0) {

                        Map<String, Object> variables = new HashMap<>();
                        for (HistoricVariableInstance historicVariableInstance : list2) {
                            variables.put(historicVariableInstance.getVariableName(), historicVariableInstance.getValue());
                        }
                        historicTaskInstanceVo.setVariables(variables);
                    }


                    list1.add(historicTaskInstanceVo);
                }

            }
        }

        return list1;
    }

	
	public class DeleteTaskCmd extends NeedsActiveTaskCmd<String> {
        /**
		 * 
		 */
		private static final long serialVersionUID = -1932091859638816639L;

		public DeleteTaskCmd(String taskId) {
            super(taskId);
        }

        public String execute(CommandContext commandContext, TaskEntity currentTask) {
            //获取所需服务
            TaskEntityManagerImpl taskEntityManager = (TaskEntityManagerImpl) commandContext.getTaskEntityManager();
            //获取当前任务的来源任务及来源节点信息
            ExecutionEntity executionEntity = currentTask.getExecution();
            //删除当前任务,来源任务
            taskEntityManager.deleteTask(currentTask, "jumpReason", false, false);
            return executionEntity.getId();
        }

        public String getSuspendedTaskException() {
            return "挂起的任务不能跳转";
        }
    }

    //根据提供节点和执行对象id，进行跳转命令
    public class SetFLowNodeAndGoCmd implements Command<Void> {
        private FlowNode flowElement;
        private String executionId;

        public SetFLowNodeAndGoCmd(FlowNode flowElement, String executionId) {
            this.flowElement = flowElement;
            this.executionId = executionId;
        }

        public Void execute(CommandContext commandContext) {

            ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findById(executionId);

            //获取目标节点的来源连线
            List<SequenceFlow> flows = flowElement.getIncomingFlows();
            if (flows == null || flows.size() < 1) {

                executionEntity.setCurrentFlowElement(flowElement);
                commandContext.getAgenda().planTakeOutgoingSequenceFlowsOperation(executionEntity, true);

            } else {
                //随便选一条连线来执行，时当前执行计划为，从连线流转到目标节点，实现跳转
                executionEntity.setCurrentFlowElement(flows.get(0));
            }

            commandContext.getAgenda().planTakeOutgoingSequenceFlowsOperation(executionEntity, true);

            return null;
        }
    }

    /**
              * 获取流程定义图片
     * @param pid
     * @return
     */
	public InputStream getDefinitionImage(String pid) {
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(pid).singleResult();
        InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getDiagramResourceName());
        return inputStream;
	}

	/**
	 * 获取当前流程的进度图
	 * @param pid
	 * @return
	 */
	public InputStream currentProcessInstanceImageByPid(String pid) {
		TaskVo task = queryActiveTask(pid);
		return currentProcessInstanceImageByTaskId(task.getId());
	}
	
	/**
     * 获取当前任务流程图
     *
     * @param taskId
     * @return
     */
    public InputStream currentProcessInstanceImageByTaskId(String taskId) {

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(task.getProcessDefinitionId());
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        // ID 为 流程定义Key
        Process process = bpmnModel.getProcessById(processDefinition.getKey());

//        UserTask userTask = (UserTask) process.getFlowElement(task.getTaskDefinitionKey());
        // 流程节点ID
        FlowElement flowElement = process.getFlowElement(task.getTaskDefinitionKey());

        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();


        List<String> highLightedActivities = new ArrayList<>();
        highLightedActivities.add(flowElement.getId());

        // 生成流程图
        // InputStream inputStream = generator.generateJpgDiagram(bpmnModel);
        // InputStream inputStream = generator.generatePngDiagram(bpmnModel);
        // InputStream inputStream = generator.generateDiagram(bpmnModel, "jpg", highLightedActivities);

        // 生成图片
        InputStream inputStream = generator.generateDiagram(bpmnModel, "jpg", highLightedActivities, Collections.emptyList(), "宋体", "宋体", "宋体", null, 2.0);
        return inputStream;
    }


	public void setVariables(String pid, Map<String, Object> variables) {
		TaskVo task = queryActiveTask(pid);
		taskService.setVariables(task.getId(), variables);
	}
	
	/**
	 * 查询客户相关的任务
	 * @param cid
	 * @return
	 */
	public List<TaskVo> listTaskByCustomer(Long cid) {
		//List<Task> resultTask = taskService.createTaskQuery().processVariableValueEquals("customerId", cid).list();
		List<Task> resultTask = taskService.createTaskQuery().list();
		
		List<TaskVo> tasks = new ArrayList<TaskVo>();
        if (resultTask != null && resultTask.size() > 0) {

            tasks = ViewObjectConverter.listToBeanVo(resultTask, TaskVo.class, "variables");

            for (TaskVo task : tasks) {

                Map<String, Object> variables = taskService.getVariables(task.getId());
                task.setVariables(variables);

                //log.debug("ID:" + task.getId() + ",姓名:" + task.getName() + ",接收人:" + task.getAssignee() + ",开始时间:" + task.getCreateTime());
            }
        }
		return tasks;

	}
	
	/**
	 * 查询变量查询历史任务
	 * @param cid
	 * @return
	 */
	public List<HistoricTaskInstance> listHistoricTaskByVar(Long cid) {
		List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().list();
		List<HistoricTaskInstanceVo> hiTasks = new ArrayList<HistoricTaskInstanceVo>();
		
		if (list != null && list.size() > 0) {
            for (HistoricTaskInstance hti : list) {
                //log.debug(hti.getId() + "    " + hti.getName() + "    " + hti.getProcessInstanceId() + "   " + hti.getStartTime() + "   " + hti.getEndTime() + "   " + hti.getDurationInMillis());
                //log.debug("################################");

                HistoricTaskInstanceVo historicTaskInstanceVo = ViewObjectConverter.objToBeanVo(hti, HistoricTaskInstanceVo.class);
                if (historicTaskInstanceVo != null) {

                    List<HistoricVariableInstance> list2 = historyService.createHistoricVariableInstanceQuery().taskId(hti.getId()).list();
                    if (list2 != null && list2.size() > 0) {

                        Map<String, Object> variables = new HashMap<>();
                        for (HistoricVariableInstance historicVariableInstance : list2) {
                            variables.put(historicVariableInstance.getVariableName(), historicVariableInstance.getValue());
                        }
                        historicTaskInstanceVo.setVariables(variables);
                    }


                    hiTasks.add(historicTaskInstanceVo);
                }

            }
        }
		
		
		return list;
	}
	
	public List<HistoricVariableInstance> listHistoricVariableInstanceByVar(Long cid) {
		List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().list();
		return list;
	}
	
	public List<HistoricProcessInstance> listHistoricProcessInstanceByVar(Long cid) {
		List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().list();
		return list;
	}
}
