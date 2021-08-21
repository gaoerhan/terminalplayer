package com.example.bjb.myapplication.entity;

/**
 * @author: geh
 * @date: 2021/8/14
 */

public class DefaultPlayBean {


    private int code;
    private String message;
    private BodyBean body;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "DefaultPlayBean{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", body=" + body +
                '}';
    }

    public static class BodyBean {
        private String materialIds;
        private String type;

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

        @Override
        public String toString() {
            return "BodyBean{" +
                    "materialIds='" + materialIds + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}
