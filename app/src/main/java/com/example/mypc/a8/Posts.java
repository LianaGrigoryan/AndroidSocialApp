package com.example.mypc.a8;


public class Posts {

    String userId, date, time, userFullName, postImage, profileImage, description;

    public Posts(){

    }


    public Posts(String userId, String date, String time, String userFullName, String postImage, String profileImage,  String description)  {

        this.userId = userId;
        this.date = date;
        this.time = time;
        this.userFullName = userFullName;
        this.postImage = postImage;
        this.profileImage = profileImage;
        this.description=description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

}
