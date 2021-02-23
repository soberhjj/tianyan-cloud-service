package com.newland.tianyan.face.event.user;

public class UserCopyEvent extends UserEvent {

    private int faceNumber;
    private int userNumber;

    public UserCopyEvent(Long appId, String groupId, String userId, int faceNumber, int usernumber) {
        super(appId, groupId, userId);
        this.faceNumber = faceNumber;
        this.userNumber = usernumber;
    }

    public int getFaceNumber() {
        return faceNumber;
    }

    public void setFaceNumber(int faceNumber) {
        this.faceNumber = faceNumber;
    }

    public int getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(int userNumber) {
        this.userNumber = userNumber;
    }
}
