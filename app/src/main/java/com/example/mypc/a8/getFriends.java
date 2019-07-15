package com.example.mypc.a8;


public class getFriends {

    private String Country, Fullname, Profileimage;

    public getFriends() {
    }

    public getFriends(String country, String fullname, String profileimage) {
        Country = country;
        Fullname = fullname;
        Profileimage = profileimage;
}

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getFullname() {
        return Fullname;
    }

    public void setFullname(String fullname) {
        Fullname = fullname;
    }

    public String getProfileimage() {
        return Profileimage;
    }

    public void setProfileimage(String profileimage) {
        Profileimage = profileimage;
    }
}
