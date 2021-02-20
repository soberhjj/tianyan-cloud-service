package com.newland.tianyan.face.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.newland.tianyan.common.model.authService.dto.AuthClientReqDO;
import com.newland.tianyan.common.utils.exception.CommonException;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.common.utils.utils.AppUtils;
import com.newland.tianyan.common.utils.utils.ProtobufUtils;

import com.newland.tianyan.face.constant.StatusConstants;
import com.newland.tianyan.face.dao.AppInfoMapper;
import com.newland.tianyan.face.entity.AppInfo;
import com.newland.tianyan.face.entity.Face;
import com.newland.tianyan.face.exception.ApiReturnErrorCode;
import com.newland.tianyan.face.remote.AuthFeignService;
import com.newland.tianyan.face.service.AppInfoService;
import com.newland.tianyan.face.service.cache.FaceCacheHelperImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.persistence.EntityNotFoundException;

/**
 * @description: 用户接口
 **/
@Service
public class AppInfoServiceImpl implements AppInfoService {

    @Autowired
    private AuthFeignService clientService;
    @Autowired
    private AppInfoMapper appInfoMapper;
    @Autowired
    private FaceCacheHelperImpl<Face> faceFaceCacheHelper;

    /**
     * 新增一条appinfo
     *
     * @param receive
     * @return void
     * @Author Ljh
     * @Date 2020/10/21 17:51
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void insert(NLBackend.BackendAllRequest receive) {
        AppInfo appInfo = ProtobufUtils.parseTo(receive, AppInfo.class);
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
            throw ApiReturnErrorCode.CACHE_CREATE_ERROR.toException();
        }
        // 查询app的主键
        Long appId = appInfoMapper.select(appInfo).get(0).getAppId();
        appInfo.setAppId(appId);

        // 远程调用
        AuthClientReqDO clientRequest = new AuthClientReqDO(receive.getAccount(), appInfo.getAppId(),
                appInfo.getApiKey(), appInfo.getSecretKey());
        clientService.addClient(clientRequest);
    }

    @Override
    public AppInfo getInfo(NLBackend.BackendAllRequest receive) {
        AppInfo appInfo = ProtobufUtils.parseTo(receive, AppInfo.class);
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
    public PageInfo<AppInfo> getList(NLBackend.BackendAllRequest receive) {
        AppInfo appInfo = ProtobufUtils.parseTo(receive, AppInfo.class);
        return PageHelper.offsetPage(appInfo.getStartIndex(), appInfo.getLength())
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

    /**
     * 更新一条数据
     *
     * @param receive
     * @return void
     * @Author Ljh
     * @Date 2020/10/21 18:32
     */
    @Override
    public void update(NLBackend.BackendAllRequest receive) {
        AppInfo appInfo = ProtobufUtils.parseTo(receive, AppInfo.class);
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


    /**
     * 删除一条数据
     *
     * @param receive
     * @return void
     * @Author Ljh
     * @Date 2020/10/21 18:13
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(NLBackend.BackendAllRequest receive) {
        AppInfo appInfo = ProtobufUtils.parseTo(receive, AppInfo.class);
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
            throw ApiReturnErrorCode.CACHE_DROP_ERROR.toException();
        }
        // app逻辑删除
        try {
            appInfoMapper.updateToDelete(StatusConstants.DELETE, appInfo.getAppId());
        } catch (Exception e) {
            throw new CommonException(6100, "invalid param");
        }

        // 远程调用
        AuthClientReqDO clientRequest = new AuthClientReqDO(receive.getAccount(), appToDelete.getAppId(),
                appToDelete.getApiKey(), appToDelete.getSecretKey());
        clientService.deleteClient(clientRequest);
    }

}
