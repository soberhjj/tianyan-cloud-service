package com.newland.tianyan.face.common.exception;

import com.newland.tianyan.common.exception.CommonException;

public class FaceServiceException extends CommonException {
    public FaceServiceException(int errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }

    public FaceServiceException(Integer errorCode, String errorMsg, Throwable e) {
        super(errorCode, errorMsg, e);
    }

    public FaceServiceException(Integer errorCode, String errorMsg, Object... args) {
        super(errorCode, errorMsg, args);
    }
}
