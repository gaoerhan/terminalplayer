package com.example.bjb.myapplication.socket.entity;

public class HeartbeatResponse {

    private int command;

    private long serverTime;

    private String msg;

    private String code;


    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public long getServerTime() {
        return serverTime;
    }

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    @Override
    public String toString() {
        return "HeartbeatResponse{" +
                "command=" + command +
                ", serverTime=" + serverTime +
                ", msg='" + msg + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
