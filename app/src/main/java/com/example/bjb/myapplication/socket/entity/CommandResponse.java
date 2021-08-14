package com.example.bjb.myapplication.socket.entity;

public class CommandResponse {

    private int command;
    private int instructionId;
    private String result;

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
