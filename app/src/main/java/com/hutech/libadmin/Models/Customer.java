package com.hutech.libadmin.Models;

import com.google.gson.annotations.SerializedName;

public class Customer {

    @SerializedName("id")
    public int customer_id;

    @SerializedName("name")
    public String customer_name;

    @SerializedName("email")
    public String customer_email;

    @SerializedName("code")
    public String customer_code;

    @SerializedName("rule")
    public int customer_rule;

    @SerializedName("created_at")
    public String created_at;

    public Customer(int customer_id, String customer_name, String customer_email, String customer_code, int customer_rule, String created_at) {
        this.customer_id = customer_id;
        this.customer_name = customer_name;
        this.customer_email = customer_email;
        this.customer_code = customer_code;
        this.customer_rule = customer_rule;
        this.created_at = created_at;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public String getCustomer_code() {
        return customer_code;
    }

    public void setCustomer_code(String customer_code) {
        this.customer_code = customer_code;
    }

    public int getCustomer_rule() {
        return customer_rule;
    }

    public void setCustomer_rule(int customer_rule) {
        this.customer_rule = customer_rule;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}