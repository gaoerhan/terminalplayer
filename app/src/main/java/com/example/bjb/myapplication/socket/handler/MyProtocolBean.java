package com.example.bjb.myapplication.socket.handler;

public class MyProtocolBean {
    //类型  接口类型
    private int type;

    //内容长度
    private int length;

    //内容
    private String content;

    public int getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }


    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MyProtocolBean(int type, int length, String content) {
        this.type = type;
        this.length = length;
        this.content = content;
    }

    @Override
    public String toString() {
        return "MyProtocolBean{" +
                "type=" + type +
                ", length=" + length +
                ", content='" + content + '\'' +
                '}';
    }
}

