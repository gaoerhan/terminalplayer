package com.example.bjb.myapplication.socket.entity;

import java.util.List;

public class CommandReceive {


    /**
     * command : 5
     * deviceInstructionList : [{"instructionId":34,"instructionCode":"A006-12","terminalKey":"","code":"","name":"开始轮播","shareTerminalKey":"","materialIds":"10","type":"1","value":""}]
     * time : 1573193605103
     */

    private int command;
    private long time;
    private List<DeviceInstructionListBean> deviceInstructionList;

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public List<DeviceInstructionListBean> getDeviceInstructionList() {
        return deviceInstructionList;
    }

    public void setDeviceInstructionList(List<DeviceInstructionListBean> deviceInstructionList) {
        this.deviceInstructionList = deviceInstructionList;
    }

    @Override
    public String toString() {
        return "CommandReceive{" +
                "command=" + command +
                ", time=" + time +
                ", deviceInstructionList=" + deviceInstructionList +
                '}';
    }

    public static class DeviceInstructionListBean {
        /**
         * instructionId : 34
         * instructionCode : A006-12
         * terminalKey :
         * code :
         * name : 开始轮播
         * shareTerminalKey :
         * materialIds : 10
         * type : 1
         * value :
         */

        private int instructionId;
        private String instructionCode;
        private String terminalKey;
        private String code;
        private String name;
        private List<String> shareTerminalKey;
        private String materialIds;
        private String type;
        private String value;

        public int getInstructionId() {
            return instructionId;
        }

        public void setInstructionId(int instructionId) {
            this.instructionId = instructionId;
        }

        public String getInstructionCode() {
            return instructionCode;
        }

        public void setInstructionCode(String instructionCode) {
            this.instructionCode = instructionCode;
        }

        public String getTerminalKey() {
            return terminalKey;
        }

        public void setTerminalKey(String terminalKey) {
            this.terminalKey = terminalKey;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getShareTerminalKey() {
            return shareTerminalKey;
        }

        public void setShareTerminalKey(List<String> shareTerminalKey) {
            this.shareTerminalKey = shareTerminalKey;
        }

        public String getMaterialIds() {
            return materialIds;
        }

        public void setMaterialIds(String materialIds) {
            this.materialIds = materialIds;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "DeviceInstructionListBean{" +
                    "instructionId=" + instructionId +
                    ", instructionCode='" + instructionCode + '\'' +
                    ", terminalKey='" + terminalKey + '\'' +
                    ", code='" + code + '\'' +
                    ", name='" + name + '\'' +
                    ", shareTerminalKey='" + shareTerminalKey + '\'' +
                    ", materialIds='" + materialIds + '\'' +
                    ", type='" + type + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
}
