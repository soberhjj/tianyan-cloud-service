package com.newland.tianyan.face.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.newland.tianyan.common.utils.LogUtils;
import org.springframework.data.domain.Page;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/4
 */
public class ProtobufBaseUtils {
    private static SerializeConfig config = null;
    private static final Map<Class<?>, Method> methodCache = new ConcurrentReferenceHashMap<>();

    protected static SerializeConfig getConfig() {
        if (config == null) {
            SerializeConfig serializeConfig = new SerializeConfig();
            serializeConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
            config = serializeConfig;
        }
        return config;
    }

    public static <T extends Message> T buildMessage(Class<T> clazz) {
        return buildMessage(clazz, LogUtils.getLogId(), null);
    }

    public static <T extends Message> T buildMessage(Class<T> clazz, Object value) {
        return buildMessage(clazz, LogUtils.getLogId(), value);
    }

    private static <T extends Message> T buildMessage(Class<T> clazz, String logId, Object value) {
        try {
            Message.Builder target = getMessageBuilder(clazz);
            return buildMessage(target, toValueMap(logId, value));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Message.Builder getMessageBuilder(Class<? extends Message> clazz) throws Exception {
        try {
            Method method = methodCache.get(clazz);
            if (method == null) {
                method = clazz.getMethod("newBuilder");
                methodCache.put(clazz, method);
            }
            return (Message.Builder) method.invoke(clazz);
        } catch (Exception ex) {
            throw new Exception("Invalid Protobuf Message type: no invocable newBuilder() method on " + clazz, ex);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Message> T buildMessage(Message.Builder builder, Map<String, Object> fields) {
        Descriptors.Descriptor descriptor = builder.getDescriptorForType();

        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }

            Descriptors.FieldDescriptor fieldDescriptor = getField(descriptor, entry.getKey());
            if (fieldDescriptor == null) {
                continue;
            }

            if (fieldDescriptor.isRepeated() && fieldDescriptor.getType() == Descriptors.FieldDescriptor.Type.MESSAGE) {
                if (entry.getValue() instanceof Map) {
                    Message message = buildMessage(builder.newBuilderForField(fieldDescriptor), (Map<String, Object>) entry.getValue());
                    builder.addRepeatedField(fieldDescriptor, message);
                } else if (entry.getValue() instanceof List) {
                    for (Object object : (List) entry.getValue()) {
                        Message message = buildMessage(builder.newBuilderForField(fieldDescriptor), (Map<String, Object>) object);
                        builder.addRepeatedField(fieldDescriptor, message);
                    }
                }
            } else {
                builder.setField(fieldDescriptor, buildValue(builder, fieldDescriptor, entry.getValue()));
            }
        }
        return (T) builder.build();
    }

    private static Map<String, Object> toValueMap(String logId, Object value) {
        Map<String, Object> message = new HashMap<>();
        message.put("log_id", logId);

        Object result;

        if (value instanceof Page) {
            message.put("count", ((Page) value).getTotalElements());
            List<Map> list = new LinkedList<>();
            for (Object item : ((Page) value).getContent()) {
                list.add(JSON.parseObject(JSON.toJSONString(item)));
            }
            result = list;
        } else if (value instanceof Collection) {
            message.put("count", ((Collection) value).size());
            List<Map> list = new LinkedList<>();
            for (Object item : (Collection) value) {
                list.add(JSON.parseObject(JSON.toJSONString(item)));
            }
            result = list;
        } else {
            result = JSON.parseObject(JSON.toJSONString(value));
        }

        message.put("result", result);
        return message;
    }

    @SuppressWarnings("unchecked")
    private static Object buildValue(Message.Builder parent, Descriptors.FieldDescriptor field, Object value) {
        if (field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE) {
            Message.Builder fieldBuilder = parent.newBuilderForField(field);
            return buildMessage(fieldBuilder, (Map<String, Object>) value);
        } else if (field.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
            return field.getEnumType().findValueByName((String) value);
        } else {
            switch (field.getJavaType()) {
                case FLOAT: // float is a special case
                    return Float.valueOf(value.toString());
                case INT:
                    return Integer.valueOf(value.toString());
                case LONG:
                    return Long.valueOf(value.toString());
                case DOUBLE:
                    return Double.valueOf(value.toString());
                default:
                    return value.toString();
            }
        }
    }

    private static Descriptors.FieldDescriptor getField(Descriptors.Descriptor descriptor, String name) {
        return descriptor.findFieldByName(name);
    }
}
