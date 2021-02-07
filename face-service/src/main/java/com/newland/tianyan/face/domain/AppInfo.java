package com.newland.tianyan.face.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/4
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppInfo extends BaseEntity {
    @Column(name = "id")
    private Long appId;
    private String account;
    private String appName;
    private String apiKey;
    private String secretKey;
    private String appInfo;
    private String apiList;
    private Integer type;
    private Integer groupNumber;
    private String createTime;
    private String modifyTime;
    private Byte isDelete;

    public static final String TABLE_NAME = "app_info";
    public static final String APP_ID = "id";
    public static final String ACCOUNT = "account";
    public static final String APP_NAME = "app_name";
    public static final String API_KEY = "api_key";
    public static final String SECRET_KEY = "secret_key";
    public static final String APP_INFO = "app_info";
    public static final String API_LIST = "api_list";
    public static final String TYPE = "type";
    public static final String GROUP_NUMBER = "group_number";
    public static final String IS_DELETE = "is_delete";
    public static final String CREATE_TIME = "create_time";
    public static final String MODIFY_TIME = "modify_time";

}
