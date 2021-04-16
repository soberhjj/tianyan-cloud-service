package com.newland.tianyan.face.event.face;

import com.newland.tianyan.face.utils.FaceIdSlotHelper;
import com.newland.tianyan.face.utils.VectorSearchKeyUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class FaceEvent {

    private Long appId;

    private String groupId;

    private String userId;

    private Integer faceNumber;

    private String oldFaceIdSlot;

    private List<Long> faceIds;

    public String getNewFaceIdSlot(boolean isRollback) {
        FaceIdSlotHelper faceIdSlotHelper = new FaceIdSlotHelper(this.getOldFaceIdSlot());
        Set<Integer> faceIdSet = this.faceIds.stream().map(VectorSearchKeyUtils::splitFaceIndex).collect(Collectors.toSet());
        if (isRollback){
            faceIdSlotHelper.rollback(faceIdSet);
        }else {
            faceIdSlotHelper.refresh(faceIdSet);
        }
        return faceIdSlotHelper.getIdSlotStr();
    }
}
