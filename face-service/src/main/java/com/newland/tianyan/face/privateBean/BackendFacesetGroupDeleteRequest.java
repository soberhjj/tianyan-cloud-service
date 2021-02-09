package com.newland.tianyan.face.privateBean;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class BackendFacesetGroupDeleteRequest {

    @NotBlank
    private String account;

    @Min(0)
    private Long appId;

    @NotBlank
    private String groupId;
}
