package com.newland.tianyan.common.exception.others;


import com.newland.tianyan.common.utils.message.NLBackend;

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
