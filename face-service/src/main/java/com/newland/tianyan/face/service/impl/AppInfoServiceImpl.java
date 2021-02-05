package com.newland.tianyan.face.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.newland.tianyan.common.exception.CommonException;
import com.newland.tianyan.common.feign.AuthFeignService;
import com.newland.tianyan.common.feign.dto.ClientRequest;
import com.newland.tianyan.face.cache.FaceCacheHelperImpl;
import com.newland.tianyan.face.common.constant.StatusConstants;
import com.newland.tianyan.face.common.exception.FaceServiceErrorEnum;
import com.newland.tianyan.face.common.utils.AppUtils;
import com.newland.tianyan.face.dao.AppInfoMapper;
import com.newland.tianyan.face.domain.AppInfo;
import com.newland.tianyan.face.domain.FaceInfo;
import com.newland.tianyan.face.dto.app.*;
import com.newland.tianyan.face.service.AppInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.persistence.EntityNotFoundException;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/4
 */
@Service
public class AppInfoServiceImpl implements AppInfoService {
    @Autowired
    private AuthFeignService authFeignService;
    @Autowired
    private AppInfoMapper appInfoMapper;
    @Autowired
    private FaceCacheHelperImpl<FaceInfo> faceFaceCacheHelper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void insert(BackendAppCreateRequest receive) {
        AppInfo appInfo = new AppInfo();
        BeanUtils.copyProperties(receive, appInfo);
        // (未逻辑删除的数据集)检查account和appNames是否已经存在
        appInfo.setIsDelete(StatusConstants.NOT_DELETE);
        if (appInfoMapper.selectCount(appInfo) > 0) {
            throw new EntityNotFoundException("account: " + receive.getAccount() + " exits app with name: " + receive.getAppName());
        }
        // 数据插入
        appInfo.setApiKey(AppUtils.generateApiKey());
        appInfo.setSecretKey(AppUtils.generateSecretKey());
        appInfoMapper.insertSelective(appInfo);
        // 建立缓存库,milvus创建collection
        if (faceFaceCacheHelper.createCollection(appInfoMapper.selectOne(appInfo).getAppId()) < 0) {
            throw FaceServiceErrorEnum.CACHE_CREATE_ERROR.toException();
        }
        // 查询app的主键
        Long appId = appInfoMapper.select(appInfo).get(0).getAppId();
        appInfo.setAppId(appId);

        // 远程调用
        ClientRequest addClientRequest = new ClientRequest(receive.getAccount(), appInfo.getAppId(),
                appInfo.getApiKey(), appInfo.getSecretKey());
        authFeignService.addClient(addClientRequest);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(BackendAppDeleteRequest receive) {
        AppInfo appInfo = new AppInfo();
        BeanUtils.copyProperties(receive,appInfo);
        if (appInfo.getAppId() == null) {
            throw new CommonException(6101, "not enough param");
        }

        appInfo.setIsDelete(StatusConstants.NOT_DELETE);
        AppInfo appToDelete = appInfoMapper.selectOne(appInfo);
        if (appToDelete == null) {
            throw new EntityNotFoundException("app_id doesn't exists!");
        }
        //milvus删除人脸集合
        if (faceFaceCacheHelper.deleteCollection(appInfo.getAppId()) < 0) {
            throw FaceServiceErrorEnum.CACHE_DROP_ERROR.toException();
        }
        // app逻辑删除
        try {
            appInfoMapper.updateToDelete(StatusConstants.DELETE, appInfo.getAppId());
        } catch (Exception e) {
            throw new CommonException(6100, "invalid param");
        }

        // 远程调用
        ClientRequest addClientRequest = new ClientRequest(receive.getAccount(), appToDelete.getAppId(),
                appToDelete.getApiKey(), appToDelete.getSecretKey());
        authFeignService.deleteClient(addClientRequest);
    }

    @Override
    public void update(BackendAppUpdateRequest receive) {
        AppInfo appInfo = new AppInfo();
        BeanUtils.copyProperties(receive,appInfo);
        //是否存在且状态有效
        AppInfo queryAppInfo = new AppInfo();
        queryAppInfo.setAppId(appInfo.getAppId());
        queryAppInfo.setIsDelete(StatusConstants.NOT_DELETE);
        if (appInfoMapper.selectCount(queryAppInfo) <= 0) {
            throw new EntityNotFoundException("app_id doesn't exists!");
        }
        //是否存在同名应用
        Example example = new Example(AppInfo.class);
        Example.Criteria criteria = example.createCriteria();
        example.setTableName(AppInfo.TABLE_NAME);
        criteria.andEqualTo("account", appInfo.getAccount());
        if (StringUtils.isNotBlank(appInfo.getAppName())) {
            criteria.andEqualTo("appName", appInfo.getAppName());
        }
        criteria.andEqualTo("isDelete", StatusConstants.NOT_DELETE);
        criteria.andNotEqualTo("appId", appInfo.getAppId());
        if (appInfoMapper.selectCountByExample(example) > 0) {
            throw new EntityNotFoundException("exits app with name: " + receive.getAppName());
        }
        try {
            appInfoMapper.update(appInfo);
        } catch (Exception e) {
            throw new CommonException(6100, "invalid param");
        }
    }

    @Override
    public AppInfo getInfo(BackendAppGetInfoRequest receive) {
        AppInfo appInfo = new AppInfo();
        BeanUtils.copyProperties(receive,appInfo);
        if (appInfo.getAppId() == null) {
            throw new CommonException(36, "not enough param");
        }
        appInfo.setIsDelete(StatusConstants.NOT_DELETE);
        AppInfo query = appInfoMapper.selectOne(appInfo);
        if (query == null) {
            throw new CommonException(6110, "app_id doesn't exist");
        }
        return query;
    }

    @Override
    public PageInfo<AppInfo> getList(BackendAppGetListRequest receive) {
        AppInfo appInfo = new AppInfo();
        BeanUtils.copyProperties(receive,appInfo);
        return PageHelper.offsetPage(receive.getStartIndex(), receive.getLength())
                .doSelectPageInfo(
                        () -> {
                            Example example = new Example(AppInfo.class);
                            Example.Criteria criteria = example.createCriteria();
                            // set dynamic table name
                            example.setTableName(AppInfo.TABLE_NAME);

                            criteria.andEqualTo("account", appInfo.getAccount());
                            criteria.andEqualTo("isDelete", StatusConstants.NOT_DELETE);
                            // dynamic sql
                            if (StringUtils.isNotBlank(appInfo.getAppName())) {
                                criteria.andLike("appName", "%" + appInfo.getAppName() + "%");
                            }

                            // execute select
                            appInfoMapper.selectByExample(example);
                        }
                );
    }
}
