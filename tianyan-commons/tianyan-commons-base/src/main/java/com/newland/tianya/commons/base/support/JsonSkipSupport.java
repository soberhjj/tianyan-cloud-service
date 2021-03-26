package com.newland.tianya.commons.base.support;

import com.alibaba.fastjson.JSON;
import com.newland.tianya.commons.base.model.auth.AuthClientReqDTO;
import com.newland.tianya.commons.base.model.imagestrore.UploadReqDTO;
import com.newland.tianya.commons.base.utils.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/24
 */
public class JsonSkipSupport {

    public static List<String> skipFields = SkipFieldEnums.getAllField();

    public static String toJson(Class aClass, Object object) {
        //空对象直接返回
        if (object == null) {
            return null;
        }

        //对应字段含有过滤字段的继续执行逻辑
        boolean keepGoing = false;
        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().contains("image") || field.getName().contains("Image")) {
                keepGoing = true;
                break;
            }
        }
        if (!keepGoing) {
            return object.toString();
        }

        return toJson(object);
    }

    public static String toJson(Object object) {
        //空对象直接返回
        if (object == null) {
            return null;
        }

        //非json对象直接返回（下划线）
        if (!JsonUtils.isValidJson(object)) {
            return object.toString();
        }

        //string反序列化
        if (object instanceof String) {
            String string = (String) object;
            object = JSON.parseObject(string);
        }
        return replace(object);
    }

    private static String replace(Object object) {
        Map<String, Object> objectMap = JsonUtils.toMap(object);
        if (objectMap == null) {
            return null;
        }
        for (String key : objectMap.keySet()) {
            for (String skipItem : skipFields) {
                if (key.equals(skipItem)) {
                    objectMap.replace(key, SkipFieldEnums.getPrintMsg(skipItem));
                }
            }
        }
        return JsonUtils.toJson(objectMap);
    }

    @Getter
    @AllArgsConstructor
    private enum SkipFieldEnums {
        /**
         * 省略打印图片字段
         */
        IMAGE("image", "(base转码图片，省略不打印)"),
        IMAGE2("Image", "(base转码图片，省略不打印)"),
        IMAGE3("first_image", "(base转码图片，省略不打印)"),
        IMAGE4("second_image", "(base转码图片，省略不打印)"),
        ;
        private final String field;
        private final String printMsg;

        public static List<String> getAllField() {
            List<String> fields = new ArrayList<>();
            for (SkipFieldEnums item : values()) {
                fields.add(item.getField());
            }
            return fields;
        }

        public static String getPrintMsg(String field) {
            for (SkipFieldEnums item : values()) {
                if (item.getField().equals(field)) {
                    return item.getPrintMsg();
                }
            }
            return null;
        }
    }

    public static void main(String[] args) {
        String result = JsonSkipSupport.toJson(UploadReqDTO.class, "{\n" +
                "    \"log_id\": \"824957646699233280\"\n" +
                "}");
//        String result = JsonSkipSupport.toJson("ok");
        System.out.println(result);
    }
}
