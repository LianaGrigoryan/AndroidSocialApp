package com.example.mypc.a8;

public class getUsers {

    private String Country, Fullanme, profileImage;

    public getUsers() {
    }

    public getUsers(String country, String fullanme, String profileImage) {
        Country = country;
        Fullanme = fullanme;
        this.profileImage = profileImage;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getFullanme() {
        return Fullanme;
    }

    public void setFullanme(String fullanme) {
        Fullanme = fullanme;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
