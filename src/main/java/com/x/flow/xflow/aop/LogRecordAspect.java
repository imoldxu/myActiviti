package com.x.flow.xflow.aop;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;

@Aspect
@Component
public class LogRecordAspect {

	private static final Logger logger = LoggerFactory.getLogger(LogRecordAspect.class);
	 
    // 定义切点Pointcut
    @Pointcut("execution(* com.x.flow.xflow.controller..*.*(..))")
    public void webLog() {
    }
 
    // 定义advise
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {

        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
 
        //String url = request.getRequestURL().toString();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        //Object[] args = pjp.getArgs();
        String paramStr = "";
        //获取请求参数集合并进行遍历拼接
        if("POST".equals(method)){
        	Enumeration<String> enums = request.getParameterNames();
            List<String> params = new ArrayList<String>();
            while (enums.hasMoreElements()) {
                String paraName = enums.nextElement();
                String param = paraName + ":" + request.getParameter(paraName);
                params.add(param);
            }
            paramStr = params.toString();
        }else if("GET".equals(method)){
        	paramStr = queryString;
        }
        // result的值就是被拦截方法的返回值
        long startTime = System.currentTimeMillis();
        Object result = pjp.proceed();
        logger.info("请求=====>"+method+" "+uri);
        logger.info("请求参数==>:"+paramStr);
        logger.info("响应=====>:" + JSON.toJSONString(result));
        logger.info("耗时=====>"+ (System.currentTimeMillis() - startTime));
        return result;
    }

    @After("webLog()")
    public void doAfter() throws Throwable {
    	logger.info("after");
    	return;
    }
}
