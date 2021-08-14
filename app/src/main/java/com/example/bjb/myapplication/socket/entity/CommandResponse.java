package com.example.bjb.myapplication.socket.entity;

public class CommandResponse {

    private int command;
    private int instructionId;
    private String result;
    private long materialId;
    private String downloadProgress;

    public long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(long materialId) {
        this.materialId = materialId;
    }

    public String getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(String downloadProgress) {
        this.downloadProgress = downloadProgress;
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
                ", materialId=" + materialId +
                ", downloadProgress='" + downloadProgress + '\'' +
                '}';
    }
}
