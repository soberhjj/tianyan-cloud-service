package com.newland.tianya.commons.base.support;

import com.alibaba.fastjson.JSON;
import com.newland.tianya.commons.base.model.imagestrore.UploadReqDTO;
import com.newland.tianya.commons.base.utils.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Field;
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

    /**
     * 通过class进行字段筛选，如果存在过滤字段则通过反射简略内容
     */
    public static String toJson(Object object) {
        //空对象直接返回
        if (object == null) {
            return null;
        }

        if (basicClass(object.getClass())||!JsonUtils.isValidJson(object)) {
            return object.toString();
        }

        String objectJson = JsonUtils.toJson(object);
        Object copy = JSON.parseObject(objectJson, object.getClass());

        //对应字段含有过滤字段的继续执行逻辑
        Field[] fields = copy.getClass().getDeclaredFields();
        for (Field field : fields) {
            for (String skipItem : skipFields) {
                if (field.getName().equals(skipItem)) {
                    field.setAccessible(true);
                    try {
                        if (field.get(copy) != null) {
                            String newMsg = SkipFieldEnums.getPrintMsg(skipItem);
                            field.set(copy, newMsg);
                        }
                    } catch (IllegalArgumentException | IllegalAccessException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }

        return JsonUtils.toJson(copy);
    }

    private static boolean basicClass(Class tClass) {
        if (String.class.equals(tClass)) {
            return true;
        } else if (Integer.class.equals(tClass) || int.class.equals(tClass)) {
            return true;
        } else if (Long.class.equals(tClass) || long.class.equals(tClass)) {
            return true;
        } else if (Double.class.equals(tClass) || double.class.equals(tClass)) {
            return true;
        } else if (Character.class.equals(tClass) || char.class.equals(tClass)) {
            return true;
        } else if (List.class.equals(tClass) || Map.class.equals(tClass)) {
            return true;
        } else if (tClass.getName().contains("protobuf")){
            return true;
        }
        return false;
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
        IMAGE5("firstImage","(base转码图片，省略不打印)"),
        IMAGE6("secondImage","(base转码图片，省略不打印)")
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
        String result = JsonSkipSupport.toJson("hello");
        System.out.println(result);
        System.out.println(result.getClass());
        String result2 = JsonSkipSupport.toJson(UploadReqDTO.builder().image("testImage.......").build());
        System.out.println(result2);
    }
}
