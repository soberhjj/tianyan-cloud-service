package com.newland.tianyan.face.dao;


import com.newland.tianyan.face.domain.entity.UserInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @Author: huangJunJie  2020-11-05 14:34
 */
@Mapper
@Component
public interface UserInfoMapper extends CommonMapper<UserInfoDO> {

    int faceNumberIncrease(@Param("appId") Long appId, @Param("groupId") String groupId, @Param("userId") String userId, @Param("count") int count, @Param("faceIdSlot") String faceIdSlot);

    int faceNumberDecrease(@Param("appId") Long appId, @Param("groupId") String groupId, @Param("userId") String userId, @Param("count") int count, @Param("faceIdSlot") String faceIdSlot);

    /**
     * (从未逻辑删除数据集)通过用户业务标识符获取组业务标识符
     *
     * @param userId 用户业务标识
     * @param appId  应用databaseId
     * @return 用户组业务标识符
     */
    Set<String> getGroupId(@Param("appId") Long appId, @Param("groupId") String groupId, @Param("userId") String userId);

    /**
     * 插入userInfo并且userInfo.id被赋予自增Id
     */
    void insertGetId(UserInfoDO userInfoDO);

    List<UserInfoDO> queryBatch(@Param("appId") Long appId, Set<Long> gidSet, Set<Long> uidSet, String userId);
}
