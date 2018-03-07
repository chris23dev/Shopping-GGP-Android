package com.ggp.theclub.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class HelloWorldProfileRequest {
    @Getter @Setter @SerializedName("first_name") String firstName;
    @Getter @Setter @SerializedName("last_name") String lastName;
    @Getter @Setter @SerializedName("email") String email;
    @Getter @Setter @SerializedName("mall_name") String mallName;

    public HelloWorldProfileRequest(String firstName, String lastName,  String email, String mallName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mallName = mallName;
    }
}
