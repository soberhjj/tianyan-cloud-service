package com.newland.tianyan.face.common.aop;

import com.newland.face.message.NLBackend;
import com.newland.tianyan.face.common.constant.TaskType;
import com.newland.tianyan.face.common.exception.TraceableSQLException;
import com.newland.tianyan.face.common.utils.ProtobufConvertUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Component;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/8
 */
@Aspect
@Component
public class ControllerAspect {

    @Around("execution(* com.newland.tianyan.face.controller.*.*(..)) && args(request)")
    public Object throwTraceableSQLException(ProceedingJoinPoint joinPoint, Object request) throws Throwable {
        Object result;
        NLBackend.BackendAllRequest receive = NLBackend.BackendAllRequest.newBuilder().build();
        try {
            receive = ProtobufConvertUtils.toBackendAllRequest(request, TaskType.BACKEND_APP_GET_INFO);
            result = joinPoint.proceed();
        } catch (BadSqlGrammarException e) {
            e.printStackTrace();
            //todo ???
            throw new TraceableSQLException(receive);
        }
        return result;
    }
}