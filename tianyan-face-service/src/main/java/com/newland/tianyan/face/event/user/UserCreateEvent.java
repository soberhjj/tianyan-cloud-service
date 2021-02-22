package com.newland.tianyan.face.event.user;

public class UserCreateEvent extends UserEvent {

    private int faceNumber;
    private int userNumber;

    public UserCreateEvent(Long appId, String groupId, String user_id, int faceNumber, int userNumber) {
        super(appId, groupId, user_id);
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
