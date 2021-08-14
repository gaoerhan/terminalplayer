package com.example.bjb.myapplication.socket.entity;

import java.util.List;

public class LiandongReceive {

    /**
     * command : 9
     * devices : ["172.16.10.45"]
     * id : 267
     * name : 0ea716f4d7710f7981bb7a610d78f84a.mp4
     * path : /ffmpeg/screen/b62140352080425fac475fa93668370b.mp4
     * screenDeviceId : 137
     * time : 1588226186247
     */

    private int command;
    private int id;
    private String name;
    private String path;
    private int screenDeviceId;
    private long time;
    private List<String> devices;

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getScreenDeviceId() {
        return screenDeviceId;
    }

    public void setScreenDeviceId(int screenDeviceId) {
        this.screenDeviceId = screenDeviceId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public List<String> getDevices() {
        return devices;
    }

    public void setDevices(List<String> devices) {
        this.devices = devices;
    }
}
