package org.im.entity;

public class UserInfo {

    private String userName;

    public UserInfo(String userName) {
        this.userName = userName;
    }

    public UserInfo() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                ", userName='" + userName + '\'' +
                '}';
    }
}
