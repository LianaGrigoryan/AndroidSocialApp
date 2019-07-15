package com.example.mypc.a8;

public class SearchedFriends {

     private  String profileImage, Fullanme, Country;

    public SearchedFriends(){

    }

    public SearchedFriends(String profileImage, String fullanme, String country) {
        this.profileImage = profileImage;
        this.Fullanme = fullanme;
        this.Country = country;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getFullanme() {
        return Fullanme;
    }

    public void setFullanme(String fullanme) {
        Fullanme = fullanme;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }
}
