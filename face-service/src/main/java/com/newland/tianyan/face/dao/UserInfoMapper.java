package com.newland.tianyan.face.dao;


import com.newland.tianyan.face.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: huangJunJie  2020-11-05 14:34
 */
@Mapper
@Component
public interface UserInfoMapper extends CommonMapper<UserInfo> {

    int faceNumberIncrease(@Param("appId") Long appId, @Param("groupId") String groupId, @Param("userId") String userId, @Param("count") int count);

    int faceNumberDecrease(@Param("appId") Long appId, @Param("groupId") String groupId, @Param("userId") String userId, @Param("count") int count);

    /**
     * (从未逻辑删除数据集)通过用户业务标识符获取组业务标识符
     *
     * @param userId 用户业务标识
     * @param appId  应用databaseId
     * @return 用户组业务标识符
     */
    List<String> getGroupIdByUserId(@Param("userId") String userId, @Param("appId") Long appId);

    /**
     * 插入userInfo并且userInfo.id被赋予自增Id
     */
    void insertGetId(UserInfo userInfo);
}
