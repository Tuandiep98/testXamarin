package com.hutech.libadmin.Models;

import com.google.gson.annotations.SerializedName;

public class Statistic {
    @SerializedName("book")
    private int book;

    @SerializedName("author")
    private int author;

    @SerializedName("hiring")
    private int hiring;

    @SerializedName("order")
    private int order;

    @SerializedName("user")
    private int user;

    @SerializedName("profit")
    private float profit;

    @SerializedName("library")
    private int library;

    public Statistic(int book, int author, int hiring, int order, int user, float profit, int library) {
        this.book = book;
        this.author = author;
        this.hiring = hiring;
        this.order = order;
        this.user = user;
        this.profit = profit;
        this.library = library;
    }

    public int getBook() {
        return book;
    }

    public void setBook(int book) {
        this.book = book;
    }

    public int getAuthor() {
        return author;
    }

    public void setAuthor(int author) {
        this.author = author;
    }

    public int getHiring() {
        return hiring;
    }

    public void setHiring(int hiring) {
        this.hiring = hiring;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public float getProfit() {
        return profit;
    }

    public void setProfit(float profit) {
        this.profit = profit;
    }

    public int getLibrary() {
        return library;
    }

    public void setLibrary(int library) {
        this.library = library;
    }
}
