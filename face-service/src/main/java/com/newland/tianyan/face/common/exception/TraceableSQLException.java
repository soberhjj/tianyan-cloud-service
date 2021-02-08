package com.newland.tianyan.face.common.exception;

import com.newland.face.message.NLBackend;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/8
 */
public class TraceableSQLException extends RuntimeException {

    private NLBackend.BackendAllRequest request;

    public TraceableSQLException(NLBackend.BackendAllRequest request) {
        this.request = request;
    }

    public NLBackend.BackendAllRequest getRequest() {
        return request;
    }

    public void setRequest(NLBackend.BackendAllRequest request) {
        this.request = request;
    }
}

