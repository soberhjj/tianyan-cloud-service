package com.newland.tianyan.face.constant;

/**
 * @author: RojiaHuang
 * @description: 验证正则表达式
 * @date: 2021/3/3
 */
public class VerifyConstant {
    public static final String REGEX_NUMBER = "^\\s{0}|\\d+$";
    public static final String REGEX_NUMBER_LENGTH = "^\\d{%d}$";
    public static final String REGEX_YEAR = "^\\d{4}$";
    public static final String REGEX_MONTH= "^\\d{4}-\\d{1,2}";
    public static final String REGEX_DATE = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$";
    public static final String REGEX_TIME = "\\s{0}|(([01]\\d)|(2[0-3]))[0-5]\\d([0-5]\\d)?";
    public static final String REGEX_DATETIME = "^\\s{0}|\\d{4}((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3([0|1])))((0[0-9])|([1-2][0-9]))((0[0-9])|([1-5][0-9]))((0[0-9])|([1-5][0-9]))$";

    public static final String APP_LIST = "^[0-5,]*$";

    public static final String APP_NAME = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{0,32}$";

    public static final String GROUP_ID_OLD = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{0,32}$";
    /**
     * GROUP_ID 仅有数字、英文、下划线，长度32
     * */
    public static final String GROUP_ID = "^[\\dA-Za-z_]{0,32}$";

    public static final String GROUP_ID_LIST = "^[\\dA-Za-z\\u4e00-\\u9fa5_,]{0,329}$";

    public static final String USER_ID_OLD = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{0,32}$";
    /**
     * USER_ID 仅有数字、英文、下划线，长度32
     * */
    public static final String USER_ID = "^[\\dA-Za-z_]{0,32}$";

    public static final String USER_NAME = "[\\dA-Za-z_\\u4e00-\\u9fa5]{0,64}$";

    public static final String USER_INFO = "[\\dA-Za-z_\\u4e00-\\u9fa5]{0,64}$";
}
