package com.newland.tianya.commons.base.constants;

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
}
