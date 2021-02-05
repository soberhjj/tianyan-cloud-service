package com.newland.tianyan.face.domain;

import lombok.*;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo extends BaseEntity{
    private Long id;
    private Long appId;
    private Long gid;
    private String groupId;
    private String userId;
    private String userName;
    private Integer faceNumber;
    private String userInfo;
    private String createTime;
    private String modifyTime;
}
