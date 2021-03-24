package com.newland.tianya.commons.base.support;

import com.newland.tianya.commons.base.model.imagestrore.UploadResDTO;
import com.newland.tianya.commons.base.utils.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/24
 */
public class JsonSkipSupport {
    public static List<String> skipFields = SkipFieldEnums.getAllField();

    public static String toJson(Object object) {
        if(object == null){
            return null;
        }
        if (!JsonUtils.isValidJson(object)) {
            return JsonUtils.toSnakeCaseJsonString(object);
        }
        return replace(object);
    }

    private static String replace(Object object) {
        Map<String, Object> fields = JsonUtils.toMap(object);
        for (String field : fields.keySet()) {
            for (String skipItem : skipFields) {
                if (field.equals(skipItem)) {
                    fields.replace(field, SkipFieldEnums.getPrintMsg(skipItem));
                }
            }
        }
        return JsonUtils.toJson(fields);
    }

    @Getter
    @AllArgsConstructor
    private enum SkipFieldEnums {
        /**
         * 省略打印图片字段
         */
        IMAGE("image", "(base转码图片，省略不打印)"),
        IMAGE2("Image", "(base转码图片，省略不打印)"),
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
        String result = JsonSkipSupport.toJson("ok");
        System.out.println(result);
    }
}
