package com.newland.tianyan.auth.service;

import com.newland.tianyan.auth.dao.AppInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppInfoService {

    @Autowired
    private AppInfoMapper appInfoMapper;

    public void createTable(String tableName) {
        appInfoMapper.createTable(tableName);
    }

}
