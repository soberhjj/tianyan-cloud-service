package com.newland.tianyan.face.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.googlecode.protobuf.format.JsonFormat;
import com.newland.tianyan.common.feign.ImageStoreFeignService;
import com.newland.tianyan.common.feign.vo.image.DownloadReq;
import com.newland.tianyan.common.feign.vo.image.UploadReq;
import com.newland.tianyan.common.utils.exception.CommonException;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.common.utils.utils.ProtobufUtils;
import com.newland.tianyan.face.cache.FaceCacheHelperImpl;
import com.newland.tianyan.face.cache.MilvusKey;
import com.newland.tianyan.face.config.RabbitMQSender;
import com.newland.tianyan.face.constant.RabbitMqQueueName;
import com.newland.tianyan.face.constant.StatusConstants;
import com.newland.tianyan.face.dao.FaceMapper;
import com.newland.tianyan.face.dao.GroupInfoMapper;
import com.newland.tianyan.face.dao.UserInfoMapper;
import com.newland.tianyan.face.domain.Face;
import com.newland.tianyan.face.domain.GroupInfo;
import com.newland.tianyan.face.domain.UserInfo;
import com.newland.tianyan.face.event.face.FaceCreateEvent;
import com.newland.tianyan.face.event.face.FaceDeleteEvent;
import com.newland.tianyan.face.event.group.GroupCreateEvent;
import com.newland.tianyan.face.event.user.UserCreateEvent;
import com.newland.tianyan.face.exception.ApiReturnErrorCode;
import com.newland.tianyan.face.service.FacesetUserFaceService;
import com.newland.tianyan.face.utils.FeaturesTool;
import lombok.extern.slf4j.Slf4j;
import newlandFace.NLFace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.*;

import static java.lang.Math.abs;

/**
 * @Author: huangJunJie  2020-11-04 09:15
 */
@Service
@Slf4j
public class FacesetUserFaceServiceImpl implements FacesetUserFaceService {

    @Autowired
    private ImageStoreFeignService imageStorageService;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private FaceCacheHelperImpl<Face> faceCacheHelper;
    @Autowired
    private FaceMapper faceMapper;
    @Autowired
    private GroupInfoMapper groupInfoMapper;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Override
    //@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Face create(NLBackend.BackendAllRequest receive) {
        int qualityControl = receive.getQualityControl();
        if (qualityControl != 0) {
            this.handleImageQualityControl(qualityControl, receive.getImage());
        }
        Face query = ProtobufUtils.parseTo(receive, Face.class);
        Face insertFace = new Face();
        //图片提交至服务器
        this.uploadImage(insertFace, receive.getImage());
        //note 处理特征值
        this.handleFeatures(insertFace, receive.getImage());
        insertFace.setAppId(receive.getAppId());
        insertFace.setUserId(receive.getUserId());

        String[] groups = receive.getGroupId().split(",");
        //去重
        Set<String> groupIdSet = new HashSet<>(Arrays.asList(groups));
        for (String groupId : groupIdSet) {
            query.setGroupId(groupId);
            //如果group_info表中不存在该用户组，则往group_info表中插入一条新记录表示新增了该用户组
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setAppId(query.getAppId());
            groupInfo.setGroupId(query.getGroupId());
            groupInfo.setIsDelete(StatusConstants.NOT_DELETE);
            groupInfo = groupInfoMapper.selectOne(groupInfo);
            if (groupInfo == null) {
                GroupInfo insertGroup = new GroupInfo();
                insertGroup.setAppId(query.getAppId());
                insertGroup.setGroupId(query.getGroupId());
                insertGroup.setIsDelete(StatusConstants.NOT_DELETE);
                insertGroup.setUserNumber(0);
                insertGroup.setFaceNumber(0);
                groupInfoMapper.insertGetId(insertGroup);
                publisher.publishEvent(new GroupCreateEvent(query.getAppId(), query.getGroupId()));
                groupInfo = insertGroup;
            }
            insertFace.setGroupId(groupId);

            //如果user_info表中不存在该用户，那么在添加人脸后，往user_info表中插入一条新记录表示新增了该用户。
            UserInfo queryUser = new UserInfo();
            queryUser.setAppId(query.getAppId());
            queryUser.setGid(groupInfo.getId());
            queryUser.setGroupId(groupId);
            queryUser.setUserId(receive.getUserId());
            UserInfo sourceUser = userInfoMapper.selectOne(queryUser);
            if (sourceUser == null) {
                //往user_info表中插入一条新记录
                UserInfo userInfo = new UserInfo();
                userInfo.setAppId(query.getAppId());
                userInfo.setGid(groupInfo.getId());
                userInfo.setGroupId(query.getGroupId());
                userInfo.setUserId(receive.getUserId());
                if (!receive.getUserName().isEmpty()) {
                    userInfo.setUserName(receive.getUserName());
                } else {
                    userInfo.setUserName(receive.getUserId());
                }
                userInfo.setUserInfo(receive.getUserInfo());
                //此时就是在进行添加人脸的操作，所以直接将人脸数的初始值设置为1
                userInfo.setFaceNumber(1);
                try {
                    userInfoMapper.insertGetId(userInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new EntityNotFoundException("add user wrong: user_id" + receive.getUserId() + "!");
                }

                //添加人脸
                insertFace.setUid(userInfo.getId());
                insertFace.setGid(groupInfo.getId());
                insertFace.setId(MilvusKey.generatedKey(insertFace.getGid(), insertFace.getUid(), userInfo.getFaceNumber() + 1));
                //note 缓存中添加用户的人脸
                if (faceCacheHelper.add(insertFace) < 0) {
                    log.info("[人脸新增向量失败],参数{}", "AppId:" + insertFace.getAppId() + "GroupId" + insertFace.getGroupId() + "userId" + insertFace.getUserId());
                    throw ApiReturnErrorCode.CACHE_INSERT_ERROR.toException("[人脸新增]");
                }
                if (faceMapper.insertSelective(insertFace) <= 0) {
                    log.info("[人脸新增DBMS失败],参数{}", "AppId:" + insertFace.getAppId() + "GroupId" + insertFace.getGroupId() + "userId" + insertFace.getUserId());
                    throw ApiReturnErrorCode.DB_INSERT_ERROR.toException("[人脸新增]");
                }
                //发布事件。由于新增了用户，所以要在group_info表中将该用户对应的那个用户组的记录进行更新（更新的字段是user_number和face_number）
                publisher.publishEvent(new UserCreateEvent(query.getAppId(), query.getGroupId(), query.getUserId(), 1, 1));
            }
            //如果user表中已存在该用户，那么根据action_type的值来进行相应操作。action_type取值有两种("append"和"replace")。
            //下面只实现了当action_type取值为"append"时的执行逻辑，而当action_type取值为"replace"时的执行逻辑这里暂不实现。
            else {
                insertFace.setUid(sourceUser.getId());
                insertFace.setGid(sourceUser.getGid());
                if ("append".equals(receive.getActionType())) {
                    //note 缓存中添加用户的人脸
                    insertFace.setId(MilvusKey.generatedKey(insertFace.getGid(), insertFace.getUid(), sourceUser.getFaceNumber() + 1));
                    if (faceCacheHelper.add(insertFace) < 0) {
                        log.info("[人脸新增向量失败],参数{}", "AppId:" + insertFace.getAppId() + "GroupId" + insertFace.getGroupId() + "userId" + insertFace.getUserId());
                        throw ApiReturnErrorCode.CACHE_INSERT_ERROR.toException("[人脸新增]");
                    }
                    //添加人脸
                    if (faceMapper.insertSelective(insertFace) <= 0) {
                        log.info("[人脸新增DBMS失败],参数{}", "AppId:" + insertFace.getAppId() + "GroupId" + insertFace.getGroupId() + "userId" + insertFace.getUserId());
                        throw ApiReturnErrorCode.DB_INSERT_ERROR.toException("[人脸新增]");
                    }
                    //发布事件。已存在的用户添加了人脸，所以要在user_info中将该用户对应的那条记录进行更新（更新的字段是face_number）,也要在group_info表中将该用户对应的那个用户组的记录进行更新（更新的字段同样是face_number）
                    publisher.publishEvent(new FaceCreateEvent(query.getAppId(), query.getGroupId(), query.getUserId()));
                } else if ("replace".equals(receive.getActionType())) {
                    insertFace.setId(MilvusKey.generatedKey(insertFace.getGid(), insertFace.getUid(), 1));
                    //删除face表中该用户原本的人脸（用户在一个组中可能有多张人脸）
                    Face face = new Face();
                    face.setGroupId(groupId);
                    face.setUserId(query.getUserId());
                    face.setAppId(query.getAppId());
                    List<Long> faceIdList = faceMapper.selectIdByGroupId(groupId);
                    //note 删除原本缓存中的人脸，添加新的人脸
                    if ((!CollectionUtils.isEmpty(faceIdList)) && faceCacheHelper.deleteBatch(query.getAppId(), faceIdList) < 0) {
                        log.info("[人脸删除向量失败],参数{}", "AppId:" + insertFace.getAppId() + "GroupId" + insertFace.getGroupId() + "userId" + insertFace.getUserId());
                        throw ApiReturnErrorCode.CACHE_DELETE_ERROR.toException("[人脸新增]");
                    }
                    int deleteCount;
                    try {
                        deleteCount = faceMapper.delete(face);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw ApiReturnErrorCode.DB_DELETE_ERROR.toException("[人脸新增]");
                    }
                    //添加该用户新的人脸（只有一张）
                    if (faceCacheHelper.add(insertFace) < 0) {
                        log.info("[人脸新增向量失败],参数{}", "AppId:" + insertFace.getAppId() + "GroupId" + insertFace.getGroupId() + "userId" + insertFace.getUserId());
                        throw ApiReturnErrorCode.CACHE_INSERT_ERROR.toException("[人脸新增]");
                    }
                    if (faceMapper.insertSelective(insertFace) <= 0) {
                        log.info("[人脸新增DBMS失败],参数{}", "AppId:" + insertFace.getAppId() + "GroupId" + insertFace.getGroupId() + "userId" + insertFace.getUserId());
                        throw ApiReturnErrorCode.DB_INSERT_ERROR.toException("[人脸新增]");
                    }
                    //人脸替换后更新user_info表中该用户的face_number,也要更新group_info表中该用户对在的用户组对应的那条记录中的face_number
                    userInfoMapper.faceNumberIncrease(receive.getAppId(), groupId, receive.getUserId(), 1 - deleteCount);
                    groupInfoMapper.faceNumberIncrease(receive.getAppId(), groupId, 1 - deleteCount);
                } else {
                    throw new EntityNotFoundException("user_id" + receive.getUserId() + " exists!");
                }
            }
        }
        return insertFace;
    }

    private void handleImageQualityControl(int qualityControl, String image) {
        NLFace.CloudFaceSendMessage.Builder detectBuilder = NLFace.CloudFaceSendMessage.newBuilder();
        NLFace.CloudFaceSendMessage detectDef = amqpHelper(image, 1, 1);
        detectBuilder.mergeFrom(detectDef);
        NLFace.CloudFaceSendMessage detectRe = detectBuilder.build();
        double eyeDistance = Math.sqrt(Math.pow(detectRe.getFaceInfos(0).getPtx(0) - detectRe.getFaceInfos(0).getPtx(1), 2)
                + Math.pow(detectRe.getFaceInfos(0).getPty(0) - detectRe.getFaceInfos(0).getPty(1), 2));
        if (qualityControl == 1 && eyeDistance <= 20) {
            throw new CommonException(6200, "low quality control fail! eye distance < 20 pixels");
        }
        if (qualityControl == 2 && eyeDistance <= 40) {
            throw new CommonException(6200, "median quality control fail! eye distance < 40 pixels");
        }
        if (qualityControl == 3 && eyeDistance <= 60) {
            throw new CommonException(6200, "high quality control fail! eye distance < 60 pixels");
        }
        NLFace.CloudFaceSendMessage.Builder builder = NLFace.CloudFaceSendMessage.newBuilder();

        NLFace.CloudFaceSendMessage def =
                amqpHelper(image, 1, 4);
        builder.mergeFrom(def);
        NLFace.CloudFaceSendMessage qualityRe = builder.build();
        if (qualityControl == 1) {
            if (abs(qualityRe.getFaceAttributes(0).getPitch()) >= 40) {
                throw new CommonException(6200, "low quality control fail! abs(pitch) >= 40");
            }
            if (abs(qualityRe.getFaceAttributes(0).getYaw()) >= 40) {
                throw new CommonException(6200, "low quality control fail! abs(yaw) >= 40");
            }
            if (abs(qualityRe.getFaceAttributes(0).getRoll()) >= 40) {
                throw new CommonException(6200, "low quality control fail! abs(roll) >= 40");
            }
            if (qualityRe.getFaceAttributes(0).getBlur() >= 0.8) {
                throw new CommonException(6200, "low quality control fail! blur >= 0.8");
            }
            if (qualityRe.getFaceAttributes(0).getOcclusion() >= 0.8) {
                throw new CommonException(6200, "low quality control fail! occlusion >= 0.8");
            }
            if (qualityRe.getFaceAttributes(0).getBrightness() <= 0.15 || qualityRe.getFaceAttributes(0).getBrightness() >= 0.95) {
                throw new CommonException(6200, "low quality control fail! brightness is not in (0.15, 0.95)");
            }
            if (qualityRe.getFaceAttributes(0).getBrightnessSideDiff() >= 0.8) {
                throw new CommonException(6200, "low quality control fail! brightness side diff >= 0.8");
            }
            if (qualityRe.getFaceAttributes(0).getBrightnessUpdownDiff() >= 0.8) {
                throw new CommonException(6200, "low quality control fail! brightness updown diff >= 0.8");
            }
            if (qualityRe.getFaceAttributes(0).getToneOffCenter() >= 0.8) {
                throw new CommonException(6200, "low quality control fail! tone off center >= 0.8");
            }
        }
        if (qualityControl == 2) {
            if (abs(qualityRe.getFaceAttributes(0).getPitch()) >= 30) {
                throw new CommonException(6200, "median quality control fail! abs(pitch) >= 30");
            }
            if (abs(qualityRe.getFaceAttributes(0).getYaw()) >= 30) {
                throw new CommonException(6200, "median quality control fail! abs(yaw) >= 30");
            }
            if (abs(qualityRe.getFaceAttributes(0).getRoll()) >= 30) {
                throw new CommonException(6200, "median quality control fail! abs(roll) >= 30");
            }
            if (qualityRe.getFaceAttributes(0).getBlur() >= 0.6) {
                throw new CommonException(6200, "median quality control fail! blur >= 0.6");
            }
            if (qualityRe.getFaceAttributes(0).getOcclusion() >= 0.6) {
                throw new CommonException(6200, "median quality control fail! occlusion >= 0.6");
            }
            if (qualityRe.getFaceAttributes(0).getBrightness() <= 0.2 || qualityRe.getFaceAttributes(0).getBrightness() >= 0.9) {
                throw new CommonException(6200, "median quality control fail! brightness is not in (0.2, 0.9)");
            }
            if (qualityRe.getFaceAttributes(0).getBrightnessSideDiff() >= 0.6) {
                throw new CommonException(6200, "median quality control fail! brightness side diff >= 0.6");
            }
            if (qualityRe.getFaceAttributes(0).getBrightnessUpdownDiff() >= 0.6) {
                throw new CommonException(6200, "median quality control fail! brightness updown diff >= 0.6");
            }
            if (qualityRe.getFaceAttributes(0).getToneOffCenter() >= 0.6) {
                throw new CommonException(6200, "median quality control fail! tone off center >= 0.6");
            }
        }
        if (qualityControl == 3) {
            if (abs(qualityRe.getFaceAttributes(0).getPitch()) >= 20) {
                throw new CommonException(6200, "high quality control fail! abs(pitch) >= 20");
            }
            if (abs(qualityRe.getFaceAttributes(0).getYaw()) >= 20) {
                throw new CommonException(6200, "high quality control fail! abs(yaw) >= 20");
            }
            if (abs(qualityRe.getFaceAttributes(0).getRoll()) >= 20) {
                throw new CommonException(6200, "high quality control fail! abs(roll) >= 20");
            }
            if (qualityRe.getFaceAttributes(0).getBlur() >= 0.4) {
                throw new CommonException(6200, "high quality control fail! blur >= 0.4");
            }
            if (qualityRe.getFaceAttributes(0).getOcclusion() >= 0.4) {
                throw new CommonException(6200, "high quality control fail! occlusion >= 0.4");
            }
            if (qualityRe.getFaceAttributes(0).getBrightness() <= 0.3 || qualityRe.getFaceAttributes(0).getBrightness() >= 0.8) {
                throw new CommonException(6200, "high quality control fail! brightness is not in (0.3, 0.8)");
            }
            if (qualityRe.getFaceAttributes(0).getBrightnessSideDiff() >= 0.4) {
                throw new CommonException(6200, "high quality control fail! brightness side diff >= 0.4");
            }
            if (qualityRe.getFaceAttributes(0).getBrightnessUpdownDiff() >= 0.4) {
                throw new CommonException(6200, "high quality control fail! brightness updown diff >= 0.4");
            }
            if (qualityRe.getFaceAttributes(0).getToneOffCenter() >= 0.4) {
                throw new CommonException(6200, "high quality control fail! tone off center >= 0.4");
            }
        }
    }


    public NLFace.CloudFaceSendMessage amqpHelper(String fileName, int maxFaceNum, Integer taskType) {
        //封装请求
        NLFace.CloudFaceAllRequest.Builder amqpRequest = NLFace.CloudFaceAllRequest.newBuilder();
        amqpRequest.setLogId(UUID.randomUUID().toString());
        amqpRequest.setTaskType(taskType);
        amqpRequest.setImage(fileName);
        amqpRequest.setMaxFaceNum(maxFaceNum);
        byte[] message = amqpRequest.build().toByteArray();
        //请求MQ
        String json = new String(rabbitMQSender.send(RabbitMqQueueName.FACE_DETECT_QUEUE, message));
        //处理结果
        NLFace.CloudFaceSendMessage.Builder result = NLFace.CloudFaceSendMessage.newBuilder();
        try {
            JsonFormat.merge(json, result);
        } catch (JsonFormat.ParseException e) {
            e.printStackTrace();
            throw new CommonException(6400, "proto parse exception");
        }
        NLFace.CloudFaceSendMessage build = result.build();
        if (!StringUtils.isEmpty(build.getErrorMsg())) {
            throw new CommonException(build.getErrorCode(), build.getErrorMsg());
        }
        return build;
    }

    private void uploadImage(Face face, String image) {
        //提交至指定服务器路径
        UploadReq uploadReq = UploadReq.builder().image(image).build();
        String imagePath = imageStorageService.uploadImageV2(uploadReq).getImagePath();
        face.setImagePath(imagePath);
    }

    private void handleFeatures(Face face, String image) {
        NLFace.CloudFaceSendMessage feature =
                amqpHelper(image, 1, 2);
        NLFace.CloudFaceSendMessage.Builder builder = feature.toBuilder();
        List<Float> featureList = builder.getFeatureResult(0).getFeaturesList();
        face.setFeatures(FeaturesTool.normalizeConvertToByte(featureList));
    }

    @Override
    public List<Face> getList(NLBackend.BackendAllRequest receive) {
        Face query = ProtobufUtils.parseTo(receive, Face.class);
        if (!StringUtils.isEmpty(query.getGroupId())) {
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setAppId(query.getAppId());
            groupInfo.setGroupId(query.getGroupId());
            groupInfo.setIsDelete(StatusConstants.NOT_DELETE);
            if (groupInfoMapper.selectCount(groupInfo) <= 0) {
                return new ArrayList<>();
            }
        }

        PageInfo<Face> facePageInfo = PageHelper.offsetPage(query.getStartIndex(), query.getLength())
                .setOrderBy("create_time desc")
                .doSelectPageInfo(
                        () -> faceMapper.select(query));

        List<Face> faces = facePageInfo.getList();
        if (CollectionUtils.isEmpty(faces)) {
            return faces;
        }
        for (Face face : faces) {
            if (face.getImagePath() != null) {
                DownloadReq downloadReq = DownloadReq.builder().imagePath(face.getImagePath()).build();
                face.setImage(imageStorageService.downloadImage(downloadReq).getImage());
            }
            face.setFaceId(face.getId().toString());
        }
        return faces;
    }

    /**
     * 删除人脸
     */
    @Override
    public void delete(NLBackend.BackendAllRequest receive) {
        Face query = ProtobufUtils.parseTo(receive, Face.class);
        query.setId(Long.parseLong(query.getFaceId()));

        //检查group_info表中是否存在该group_id
        if (!StringUtils.isEmpty(receive.getGroupId())) {
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setAppId(groupInfo.getAppId());
            groupInfo.setGroupId(groupInfo.getGroupId());
            groupInfo.setIsDelete(StatusConstants.NOT_DELETE);
            if (groupInfoMapper.selectCount(groupInfo) <= 0) {
                throw new EntityNotFoundException("group_id " + query.getGroupId() + " doesn't exist!");
            }
        }

        //检查user_info表中是否存在该user_id
        UserInfo userInfo = new UserInfo();
        userInfo.setAppId(receive.getAppId());
        userInfo.setGroupId(receive.getGroupId());
        userInfo.setUserId(receive.getUserId());
        if (userInfoMapper.selectCount(userInfo) <= 0) {
            throw new EntityNotFoundException("user_id " + query.getUserId() + " doesn't exist!");
        }

        //然后直接去face表中查询是否存在这张人脸图片的记录，若不存在则抛出异常，存在则删除该人脸
        Face face = faceMapper.selectOne(query);
        if (face == null) {
            return;
        }

        //note 缓存中删除用户指定的人脸
        if (faceCacheHelper.delete(query.getAppId(), face.getId()) < 0) {
            log.info("[人脸删除],参数{}", "AppId:" + face.getAppId() + "GroupId" + face.getGroupId() + "userId" + face.getUserId());
            throw ApiReturnErrorCode.CACHE_DELETE_ERROR.toException("[人脸删除]", "faceId:" + face.getId());
        }
        //物理删除人脸
        try {
            faceMapper.delete(query);
        } catch (Exception e) {
            e.printStackTrace();
            throw ApiReturnErrorCode.DB_DELETE_ERROR.toException("[人脸删除]");
        }
        publisher.publishEvent(new FaceDeleteEvent(query.getAppId(), query.getGroupId(), query.getUserId()));
    }
}
