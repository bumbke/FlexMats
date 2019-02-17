package com.example.l3umb.ver3.Object;

public class User {
    private String user_id;
    private String name;
    private String DoB;
    private int gender;
    private String jobs;
    private String bio;
    private String avatar_url;
    private String status;
    private String email;
    private int like_count;
    private int view_count;
    private int points;
    private String password;
    private String old_password;
    private String new_password;
    private String code;
    private String keyword;
    private String last_activity;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return DoB;
    }

    public void setDoB(String DoB) {
        this.DoB = DoB;
    }

    public String getGender() {
        if (gender == 1) {
            return "Male";
        } else {
            return "Female";
        }
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getJobs() {
        return jobs;
    }

    public void setJobs(String jobs) {
        this.jobs = jobs;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getStatus() {
        return status;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public int getView_count() {
        return view_count;
    }

    public void setView_count(int view_count) {
        this.view_count = view_count;
    }


    public int getPoints() {
        return points;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOld_password() {
        return old_password;
    }

    public void setOld_password(String old_password) {
        this.old_password = old_password;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getLast_activity() {
        return last_activity;
    }

    public void setLast_activity(String last_activity) {
        this.last_activity = last_activity;
    }
}
