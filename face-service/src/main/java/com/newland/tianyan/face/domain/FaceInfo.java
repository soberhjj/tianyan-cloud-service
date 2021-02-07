package com.newland.tianyan.face.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaceInfo extends BaseEntity {
    private Long id;
    private Long appId;
    private Long gid;
    private Long uid;
    private String groupId;
    private String userId;
    private String imagePath;
    private byte[] features;
    private String createTime;
    private String modifyTime;

    @Transient
    private int version;

    @Transient
    private String featuresNew;

    @Transient
    private String faceId;

    @Transient
    private String image;

    @Transient
    private float distance;
}
