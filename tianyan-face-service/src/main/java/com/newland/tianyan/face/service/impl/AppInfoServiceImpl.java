package com.newland.tianyan.face.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.newland.tianyan.common.exception.BaseException;
import com.newland.tianyan.common.model.auth.AuthClientReqDTO;
import com.newland.tianyan.common.utils.AppUtils;
import com.newland.tianyan.common.utils.JsonUtils;
import com.newland.tianyan.common.utils.ProtobufUtils;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.face.constant.StatusConstants;
import com.newland.tianyan.face.dao.AppInfoMapper;
import com.newland.tianyan.face.domain.entity.AppInfoDO;
import com.newland.tianyan.face.domain.entity.FaceDO;
import com.newland.tianyan.face.exception.BusinessErrorEnums;
import com.newland.tianyan.face.exception.SysErrorEnums;
import com.newland.tianyan.face.feign.client.AuthClientFeignService;
import com.newland.tianyan.face.service.AppInfoService;
import com.newland.tianyan.face.service.cache.FaceCacheHelperImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

/**
 * @description: 用户接口
 **/
@Service
public class AppInfoServiceImpl implements AppInfoService {

    @Autowired
    private AuthClientFeignService clientService;
    @Autowired
    private AppInfoMapper appInfoMapper;
    @Autowired
    private FaceCacheHelperImpl<FaceDO> faceFaceCacheHelper;

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
    public void insert(NLBackend.BackendAllRequest receive) throws BaseException {
        AppInfoDO appInfoDO = ProtobufUtils.parseTo(receive, AppInfoDO.class);
        // (未逻辑删除的数据集)检查account和appNames是否已经存在
        appInfoDO.setIsDelete(StatusConstants.NOT_DELETE);
        if (appInfoMapper.selectCount(appInfoDO) > 0) {
            throw BusinessErrorEnums.ALREADY_EXISTS.toException(receive.getAccount());
        }
        // 数据插入
        appInfoDO.setApiKey(AppUtils.generateApiKey());
        appInfoDO.setSecretKey(AppUtils.generateSecretKey());
        appInfoMapper.insertSelective(appInfoDO);
        // 建立缓存库,milvus创建collection
        faceFaceCacheHelper.createCollection(appInfoMapper.selectOne(appInfoDO).getAppId());
        // 查询app的主键
        Long appId = appInfoMapper.select(appInfoDO).get(0).getAppId();
        appInfoDO.setAppId(appId);

        // 远程调用
        AuthClientReqDTO clientRequest = new AuthClientReqDTO(receive.getAccount(), appInfoDO.getAppId(),
                appInfoDO.getApiKey(), appInfoDO.getSecretKey());
        clientService.addClient(clientRequest);
    }

    @Override
    public AppInfoDO getInfo(NLBackend.BackendAllRequest receive) throws BaseException {
        AppInfoDO appInfoDO = ProtobufUtils.parseTo(receive, AppInfoDO.class);

        appInfoDO.setIsDelete(StatusConstants.NOT_DELETE);
        AppInfoDO query = appInfoMapper.selectOne(appInfoDO);
        if (query == null) {
            throw BusinessErrorEnums.NOT_EXISTS.toException(receive.getAppId());
        }
        return query;
    }

    @Override
    public PageInfo<AppInfoDO> getList(NLBackend.BackendAllRequest receive) throws BaseException {
        AppInfoDO appInfoDO = ProtobufUtils.parseTo(receive, AppInfoDO.class);
        return PageHelper.offsetPage(appInfoDO.getStartIndex(), appInfoDO.getLength())
                .doSelectPageInfo(
                        () -> {
                            Example example = new Example(AppInfoDO.class);
                            Example.Criteria criteria = example.createCriteria();
                            // set dynamic table name
                            example.setTableName(AppInfoDO.TABLE_NAME);

                            criteria.andEqualTo("account", appInfoDO.getAccount());
                            criteria.andEqualTo("isDelete", StatusConstants.NOT_DELETE);
                            // dynamic sql
                            if (StringUtils.isNotBlank(appInfoDO.getAppName())) {
                                criteria.andLike("appName", "%" + appInfoDO.getAppName() + "%");
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
    public void update(NLBackend.BackendAllRequest receive) throws BaseException {
        AppInfoDO appInfoDO = ProtobufUtils.parseTo(receive, AppInfoDO.class);
        //是否存在且状态有效
        AppInfoDO queryAppInfoDO = new AppInfoDO();
        queryAppInfoDO.setAppId(appInfoDO.getAppId());
        queryAppInfoDO.setIsDelete(StatusConstants.NOT_DELETE);
        if (appInfoMapper.selectCount(queryAppInfoDO) <= 0) {
            throw BusinessErrorEnums.NOT_EXISTS.toException(receive.getAppId());
        }
        //是否存在同名应用
        Example example = new Example(AppInfoDO.class);
        Example.Criteria criteria = example.createCriteria();
        example.setTableName(AppInfoDO.TABLE_NAME);
        criteria.andEqualTo("account", appInfoDO.getAccount());
        if (StringUtils.isNotBlank(appInfoDO.getAppName())) {
            criteria.andEqualTo("appName", appInfoDO.getAppName());
        }
        criteria.andEqualTo("isDelete", StatusConstants.NOT_DELETE);
        criteria.andNotEqualTo("appId", appInfoDO.getAppId());
        if (appInfoMapper.selectCountByExample(example) > 0) {
            throw BusinessErrorEnums.NOT_EXISTS.toException(receive.getAppName());
        }
        try {
            appInfoMapper.update(appInfoDO);
        } catch (Exception e) {
            throw SysErrorEnums.DB_UPDATE_ERROR.toException(JsonUtils.toJson(appInfoDO));
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
    public void delete(NLBackend.BackendAllRequest receive) throws BaseException {
        AppInfoDO appInfoDO = ProtobufUtils.parseTo(receive, AppInfoDO.class);
        if (appInfoDO == null) {
            throw BusinessErrorEnums.NOT_EXISTS.toException(receive.getAppId());
        }

        appInfoDO.setIsDelete(StatusConstants.NOT_DELETE);
        AppInfoDO appToDelete = appInfoMapper.selectOne(appInfoDO);
        if (appToDelete == null) {
            throw BusinessErrorEnums.NOT_EXISTS.toException(receive.getAppId());
        }
        //milvus删除人脸集合
        faceFaceCacheHelper.deleteCollection(appInfoDO.getAppId());
        // app逻辑删除
        try {
            appInfoMapper.updateToDelete(StatusConstants.DELETE, appInfoDO.getAppId());
        } catch (Exception e) {
            throw SysErrorEnums.DB_UPDATE_ERROR.toException(JsonUtils.toJson(appInfoDO));
        }

        // 远程调用
        AuthClientReqDTO clientRequest = new AuthClientReqDTO(receive.getAccount(), appToDelete.getAppId(),
                appToDelete.getApiKey(), appToDelete.getSecretKey());
        clientService.deleteClient(clientRequest);
    }

}
