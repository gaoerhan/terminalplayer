package com.example.bjb.myapplication.socket.entity;

public class LoginResponse {
    private int code;
    private int command;
    private String msg;
    private String token;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    @Override
    public String toString() {
        return "LoginResponse{" +
                "code=" + code +
                ", command=" + command +
                ", msg='" + msg + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
