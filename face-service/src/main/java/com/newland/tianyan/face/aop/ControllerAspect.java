package com.newland.tianyan.face.aop;



import com.newland.tianyan.common.utils.constans.TaskType;
import com.newland.tianyan.common.utils.exception.TraceableSQLException;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.common.utils.utils.ProtobufUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerAspect {

    @Around("execution(* com.newland.tianyan.face.controller.*.*(..)) && args(request)")
    public Object throwTraceableSQLException(ProceedingJoinPoint joinPoint, Object request) throws Throwable {
        Object result;
        NLBackend.BackendAllRequest receive = NLBackend.BackendAllRequest.newBuilder().build();
        try {
            receive = ProtobufUtils.toBackendAllRequest(request, TaskType.BACKEND_APP_GET_INFO);
            result = joinPoint.proceed();
        } catch (BadSqlGrammarException e) {
            e.printStackTrace();
            //todo ???
            throw new TraceableSQLException(receive);
        }
        return result;
    }
}
