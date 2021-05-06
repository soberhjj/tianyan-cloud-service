package com.newland.tianyan.face.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.newland.tianya.commons.base.exception.BaseException;
import com.newland.tianya.commons.base.model.auth.AuthClientReqDTO;
import com.newland.tianya.commons.base.model.proto.NLBackend;
import com.newland.tianya.commons.base.support.ExceptionSupport;
import com.newland.tianya.commons.base.utils.AppUtils;
import com.newland.tianya.commons.base.utils.ProtobufUtils;
import com.newland.tianyan.face.constant.EntityStatusConstants;
import com.newland.tianyan.face.constant.ExceptionEnum;
import com.newland.tianyan.face.dao.AppInfoMapper;
import com.newland.tianyan.face.domain.entity.AppInfoDO;
import com.newland.tianyan.face.domain.entity.FaceDO;
import com.newland.tianyan.face.feign.client.AuthClientFeignService;
import com.newland.tianyan.face.service.AppInfoService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AppInfoServiceImpl implements AppInfoService {

    @Autowired
    private AuthClientFeignService clientService;
    @Autowired
    private AppInfoMapper appInfoMapper;
    @Autowired
    private VectorSearchServiceImpl<FaceDO> faceFaceCacheHelper;

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
        appInfoDO.setIsDelete(EntityStatusConstants.NOT_DELETE);
        if (appInfoMapper.selectCount(appInfoDO) > 0) {
            throw ExceptionSupport.toException(ExceptionEnum.APP_ALREADY_EXISTS, appInfoDO.getAppName());
        }
        appInfoDO.setApiKey(AppUtils.generateApiKey());
        appInfoDO.setSecretKey(AppUtils.generateSecretKey());

        appInfoMapper.insertSelective(appInfoDO);

        log.info("请求向量搜索服务服务创建向量集合");
        faceFaceCacheHelper.createCollection(appInfoMapper.selectOne(appInfoDO).getAppId());

        log.info("请求授权服务增加客户端");
        // 查询app的主键
        Long appId = appInfoMapper.select(appInfoDO).get(0).getAppId();
        appInfoDO.setAppId(appId);
        AuthClientReqDTO clientRequest = new AuthClientReqDTO(receive.getAccount(), appInfoDO.getAppId(),
                appInfoDO.getApiKey(), appInfoDO.getSecretKey());
        clientService.addClient(clientRequest);
    }

    @Override
    public AppInfoDO getInfo(NLBackend.BackendAllRequest receive) throws BaseException {
        AppInfoDO appInfoDO = ProtobufUtils.parseTo(receive, AppInfoDO.class);

        appInfoDO.setIsDelete(EntityStatusConstants.NOT_DELETE);
        AppInfoDO result = appInfoMapper.selectOne(appInfoDO);
        if (result == null) {
            throw ExceptionSupport.toException(ExceptionEnum.APP_NOT_FOUND, receive.getAppId());
        }
        return result;
    }

    @Override
    public PageInfo<AppInfoDO> getList(NLBackend.BackendAllRequest receive) throws BaseException {
        AppInfoDO appInfoDO = ProtobufUtils.parseTo(receive, AppInfoDO.class);
        return PageHelper.startPage(receive.getStartIndex(), receive.getLength())
                .doSelectPageInfo(
                        () -> {
                            Example example = new Example(AppInfoDO.class);
                            Example.Criteria criteria = example.createCriteria();
                            // set dynamic table name
                            example.setTableName(AppInfoDO.TABLE_NAME);

                            criteria.andEqualTo("account", appInfoDO.getAccount());
                            criteria.andEqualTo("isDelete", EntityStatusConstants.NOT_DELETE);
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
        queryAppInfoDO.setIsDelete(EntityStatusConstants.NOT_DELETE);
        if (appInfoMapper.selectCount(queryAppInfoDO) <= 0) {
            throw ExceptionSupport.toException(ExceptionEnum.APP_NOT_FOUND, appInfoDO.getAppId());
        }
        //是否存在同名应用
        Example example = new Example(AppInfoDO.class);
        Example.Criteria criteria = example.createCriteria();
        example.setTableName(AppInfoDO.TABLE_NAME);
        criteria.andEqualTo("account", appInfoDO.getAccount());
        if (StringUtils.isNotBlank(appInfoDO.getAppName())) {
            criteria.andEqualTo("appName", appInfoDO.getAppName());
        }
        criteria.andEqualTo("isDelete", EntityStatusConstants.NOT_DELETE);
        criteria.andNotEqualTo("appId", appInfoDO.getAppId());
        if (appInfoMapper.selectCountByExample(example) > 0) {
            throw ExceptionSupport.toException(ExceptionEnum.APP_NOT_FOUND);
        }
        appInfoMapper.update(appInfoDO);
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
        appInfoDO.setIsDelete(EntityStatusConstants.NOT_DELETE);
        AppInfoDO appToDelete = appInfoMapper.selectOne(appInfoDO);
        if (appToDelete == null) {
            throw ExceptionSupport.toException(ExceptionEnum.APP_NOT_FOUND, appInfoDO.getAppId());

        }
        //milvus删除人脸集合
        faceFaceCacheHelper.deleteCollection(appInfoDO.getAppId());
        // app逻辑删除
        appInfoMapper.updateToDelete(EntityStatusConstants.DELETE, appInfoDO.getAppId());

        // 远程调用
        AuthClientReqDTO clientRequest = new AuthClientReqDTO(receive.getAccount(), appToDelete.getAppId(),
                appToDelete.getApiKey(), appToDelete.getSecretKey());
        clientService.deleteClient(clientRequest);
    }

}
