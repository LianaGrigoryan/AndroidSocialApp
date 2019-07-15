package com.example.mypc.a8;


public class getComments {

    private String Date, CommentText, Fullname, ProfileImage, Time, userId ;

    public getComments() {
    }

    public getComments(String date, String commentText, String fullname, String profileImage, String time) {
        Date = date;
        CommentText = commentText;
        Fullname = fullname;
        ProfileImage = profileImage;
        Time = time;
    }

    public String getUserId() {
        return userId;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getCommentText() {
        return CommentText;
    }

    public void setCommentText(String commentText) {
        CommentText = commentText;
    }

    public String getFullname() {
        return Fullname;
    }

    public void setFullname(String fullname) {
        Fullname = fullname;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
