package com.newland.tianyan.face.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @Author: huangJunJie  2020-11-04 09:20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "face")
public class FaceDO extends BaseEntity {
    @Id
    private Long id;
    private Long appId;
    private Long gid;
    private Long uid;
    private String groupId;
    private String userId;
    private String imagePath;
    private String photoSign;
    private byte[] features;
    private String createTime;
    private String modifyTime;

    public Long getGid() {
        return gid;
    }

    public void setGid(Long gid) {
        this.gid = gid;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

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
