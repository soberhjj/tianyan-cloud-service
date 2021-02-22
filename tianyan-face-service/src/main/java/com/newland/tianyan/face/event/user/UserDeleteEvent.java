package com.newland.tianyan.face.event.user;

public class UserDeleteEvent extends UserEvent {

    private int faceNumber;
    private int userNumber;

    public UserDeleteEvent(Long appId, String groupId, String userId, int faceNumber, int userNumber) {
        super(appId, groupId, userId);
        this.faceNumber = faceNumber;
        this.userNumber = userNumber;
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
