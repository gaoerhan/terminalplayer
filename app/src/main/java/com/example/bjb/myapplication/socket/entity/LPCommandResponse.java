package com.example.bjb.myapplication.socket.entity;

public class LPCommandResponse {

    private int command;
    private int instructionId;
    private String result;
    private int screenDeviceId;
    private int screenDeviceStatus;

    public int getScreenDeviceId() {
        return screenDeviceId;
    }

    public void setScreenDeviceId(int screenDeviceId) {
        this.screenDeviceId = screenDeviceId;
    }

    public int getScreenDeviceStatus() {
        return screenDeviceStatus;
    }

    public void setScreenDeviceStatus(int screenDeviceStatus) {
        this.screenDeviceStatus = screenDeviceStatus;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public int getInstructionId() {
        return instructionId;
    }

    public void setInstructionId(int instructionId) {
        this.instructionId = instructionId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "CommandResponse{" +
                "command=" + command +
                ", instructionId=" + instructionId +
                ", result='" + result + '\'' +
                '}';
    }
}
