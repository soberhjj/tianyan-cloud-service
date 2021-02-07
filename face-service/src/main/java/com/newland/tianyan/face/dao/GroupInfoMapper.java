package com.newland.tianyan.face.dao;

import com.newland.tianyan.face.domain.GroupInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @Author: huangJunJie  2020-11-02 14:37
 */
@Mapper
@Component
public interface GroupInfoMapper extends CommonMapper<GroupInfo> {

    int userNumberIncrease(@Param("appId") Long appId, @Param("groupId") String groupId, @Param("count") int count);

    int faceNumberIncrease(@Param("appId") Long appId, @Param("groupId") String groupId, @Param("count") int count);

    int userNumberDecrease(@Param("appId") Long appId, @Param("groupId") String groupId, @Param("count") int count);

    int faceNumberDecrease(@Param("appId") Long appId, @Param("groupId") String groupId, @Param("count") int count);

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
    int insertGetId(GroupInfo groupInfo);
}
