package com.newland.tianyan.auth.dao;


import com.newland.tianyan.auth.entity.AppInfo;
import com.newland.tianyan.auth.dao.CommonMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface AppInfoMapper extends CommonMapper<AppInfo> {

    void createTable(@Param("tableName") String tableName);

}
