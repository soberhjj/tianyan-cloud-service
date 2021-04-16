package com.newland.tianyan.face.utils;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.MAX_FACE_NUMBER;

/**
 * @author: RojiaHuang
 * @description: 用户人脸标识位维护工具
 * @date: 2021/4/14
 */
public class FaceIdSlotHelper {

    private String idSlotStr;

    private List<IdObject> idSlotList = new ArrayList<>();

    private Queue<Integer> lastValidIndexQueue = new LinkedList<>();

    @Data
    private static class IdObject {
        private boolean isValid;
        private final int index;

        public IdObject(int index, char value) {
            char valid = '0';
            this.index = index;
            this.isValid = value == valid;
        }

        public void setAsInvalid() {
            isValid = false;
        }

        public void setAsValid() {
            isValid = true;
        }
    }

    public FaceIdSlotHelper(String idSlotStr) {
        if (StringUtils.isEmpty(idSlotStr)) {
            idSlotStr = "0000000000";
        }
        this.idSlotStr = idSlotStr.replaceAll("\'", "").trim();
        this.init(this.idSlotStr);
    }

    public String getPreIdSlotStr() {
        return this.idSlotStr;
    }

    public void reset() {
        this.init(this.idSlotStr);
    }

    private void init(String idSlotStr) {
        char[] charArray = idSlotStr.toCharArray();
        int charLength = charArray.length;
        for (int i = 0; i < MAX_FACE_NUMBER; i++) {
            IdObject object;
            if (i >= charLength) {
                object = new IdObject(i, '0');
            } else {
                object = new IdObject(i, charArray[i]);
            }
            if (object.isValid) {
                lastValidIndexQueue.offer(object.getIndex());
            }
            idSlotList.add(object);
        }
    }

    public synchronized Integer pollNextValidId() {
        Integer index = lastValidIndexQueue.poll();
        if (index != null) {
            idSlotList.get(index).setAsInvalid();
        }
        return index;
    }

    public synchronized void refresh(Set<Integer> indexSets) {
        this.idSlotList.forEach(idObjectItem -> {
            if (indexSets.contains(idObjectItem.getIndex())) {
                idObjectItem.setAsInvalid();
            }
        });
    }

    public synchronized void rollback(Set<Integer> indexSets) {
        this.idSlotList.forEach(idObjectItem -> {
            if (indexSets.contains(idObjectItem.getIndex())) {
                idObjectItem.setAsValid();
            }
        });
    }

    public synchronized String getIdSlotStr() {
        char[] charArr = new char[idSlotList.size()];
        for (int i = 0; i < idSlotList.size(); i++) {
            char c = idSlotList.get(i).isValid() ? '0' : '1';
            charArr[i] = c;
        }
        return new String(charArr);
    }

}
