package com.newland.tianyan.face.dao;


import com.newland.tianyan.face.domain.entity.GroupInfoDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @Author: huangJunJie  2020-11-02 14:37
 */
@Mapper
@Component
public interface GroupInfoMapper extends CommonMapper<GroupInfoDO> {

    int userNumberIncrease(@Param("appId") Long appId, @Param("groupId") String groupId, @Param("count") int count);

    int faceNumberIncrease(@Param("appId") Long appId, @Param("groupId") String groupId, @Param("count") int count);

    int userNumberDecrease(@Param("appId") Long appId, @Param("groupId") String groupId, @Param("userCount") int userCount,@Param("faceCount") int faceCount);

    int faceNumberDecrease(@Param("appId") Long appId, @Param("groupId") String groupId, @Param("faceCount") int faceCount);

    /**
     * @param isDelete 逻辑删除状态位
     * @param id       删除用户组标识符
     * @return 影响行数
     */
    @Delete("UPDATE group_info SET is_delete = ${isDelete} WHERE id = ${id}")
    int updateToDelete(@Param("isDelete") Byte isDelete, @Param("id") Long id);

    /**
     * 插入groupInfo并且groupInfo.id被赋予自增Id
     */
    int insertGetId(GroupInfoDO groupInfoDO);

    List<GroupInfoDO> queryBatch(@Param("appId") Long appId, Set<String> groupIdSet);
}
