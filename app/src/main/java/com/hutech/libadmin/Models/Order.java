package com.hutech.libadmin.Models;

import com.google.gson.annotations.SerializedName;

public class Order {
    @SerializedName("id")
    private int order_id;

    @SerializedName("order_code")
    private String order_code;

    @SerializedName("user_id")
    private int user_id;

    @SerializedName("author_id")
    private int author_id;

    @SerializedName("name_book")
    private String name_book;

    @SerializedName("image_book")
    private String image_book;

    @SerializedName("description_book")
    private String description_book;

    @SerializedName("publish_date")
    private String publish_date;

    @SerializedName("price_book")
    private float price_book;

    @SerializedName("author_name")
    private String author_name;

    @SerializedName("user_name")
    private String user_name;

    @SerializedName("user_email")
    private String user_email;

    @SerializedName("date_hire")
    private String date_hire;

    @SerializedName("date_return")
    private String date_return;

    public Order(int order_id, String order_code, int user_id, int author_id, String name_book, String image_book, String description_book, String publish_date, float price_book, String author_name, String user_name, String user_email, String date_hire, String date_return) {
        this.order_id = order_id;
        this.order_code = order_code;
        this.user_id = user_id;
        this.author_id = author_id;
        this.name_book = name_book;
        this.image_book = image_book;
        this.description_book = description_book;
        this.publish_date = publish_date;
        this.price_book = price_book;
        this.author_name = author_name;
        this.user_name = user_name;
        this.user_email = user_email;
        this.date_hire = date_hire;
        this.date_return = date_return;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public String getName_book() {
        return name_book;
    }

    public void setName_book(String name_book) {
        this.name_book = name_book;
    }

    public String getImage_book() {
        return image_book;
    }

    public void setImage_book(String image_book) {
        this.image_book = image_book;
    }

    public String getDescription_book() {
        return description_book;
    }

    public void setDescription_book(String description_book) {
        this.description_book = description_book;
    }

    public String getPublish_date() {
        return publish_date;
    }

    public void setPublish_date(String publish_date) {
        this.publish_date = publish_date;
    }

    public float getPrice_book() {
        return price_book;
    }

    public void setPrice_book(float price_book) {
        this.price_book = price_book;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getDate_hire() {
        return date_hire;
    }

    public void setDate_hire(String date_hire) {
        this.date_hire = date_hire;
    }

    public String getDate_return() {
        return date_return;
    }

    public void setDate_return(String date_return) {
        this.date_return = date_return;
    }
}
