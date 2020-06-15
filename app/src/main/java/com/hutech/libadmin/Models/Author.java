package com.hutech.libadmin.Models;

import com.google.gson.annotations.SerializedName;

public class Author {

    @SerializedName("id")
    public int author_id;

    @SerializedName("user_id")
    public int user_id;

    @SerializedName("name")
    public String name;

    @SerializedName("avatar")
    public String avatar;

    @SerializedName("description")
    public String description;

    public Author(int author_id, int user_id, String name, String avatar, String description) {
        this.author_id = author_id;
        this.user_id = user_id;
        this.name = name;
        this.avatar = avatar;
        this.description = description;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
