package com.example.bjb.myapplication.socket.entity;

public class HeartbeatRequest {

    private String command;
    private String cpu;

    private String disk;

    private String ip;
    private String mac;

    private String machine_code;

    private String name;

    private String online;

    private String ram;

    private String token;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getDisk() {
        return disk;
    }

    public void setDisk(String disk) {
        this.disk = disk;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMachine_code() {
        return machine_code;
    }

    public void setMachine_code(String machine_code) {
        this.machine_code = machine_code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "HeartbeatRequest{" +
                "command='" + command + '\'' +
                ", cpu='" + cpu + '\'' +
                ", disk='" + disk + '\'' +
                ", ip='" + ip + '\'' +
                ", mac='" + mac + '\'' +
                ", machine_code='" + machine_code + '\'' +
                ", name='" + name + '\'' +
                ", online='" + online + '\'' +
                ", ram='" + ram + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
