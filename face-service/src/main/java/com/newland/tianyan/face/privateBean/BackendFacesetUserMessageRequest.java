package com.newland.tianyan.face.privateBean;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Setter
public class BackendFacesetUserMessageRequest {

    @NotBlank
    private String account;

    @NotNull
    private Long appId;

    // 指定时为搜索,不指定则返回全部
    private String groupId;

    @NotBlank
    private String userId;

}