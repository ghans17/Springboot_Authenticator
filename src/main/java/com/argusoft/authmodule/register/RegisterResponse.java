package com.argusoft.authmodule.register;

public class RegisterResponse {

    private String status;

    public RegisterResponse(String status) {
        this.status = status;
    }
    public String getStatus(){
        return status;
    }
}
