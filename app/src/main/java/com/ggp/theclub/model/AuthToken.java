package com.ggp.theclub.model;

import com.google.gson.annotations.SerializedName;

public class AuthToken {
    @SerializedName("access_token") private String accessToken;
    @SerializedName("token_type") private String tokenType;

    public String getAuthToken() {
        return Character.toString(tokenType.charAt(0)).toUpperCase().concat(tokenType.substring(1)) + " " + accessToken;
    }
}