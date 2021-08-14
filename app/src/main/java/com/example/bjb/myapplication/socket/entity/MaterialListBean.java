package com.example.bjb.myapplication.socket.entity;

public class MaterialListBean {

    /**
     * id : 509
     * name : 66.jpeg
     * path : /static/image/aa56a512-9cef-445f-b749-bb27a7c26b0d66.jpeg
     * type : 0
     * size : 92241
     * format : jpeg
     * label : null
     */

    private int id;
    private String name;
    private String path;
    private int type;
    private int size;
    private String format;
    private Object label;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Object getLabel() {
        return label;
    }

    public void setLabel(Object label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "MaterialListBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", type=" + type +
                ", size=" + size +
                ", format='" + format + '\'' +
                ", label=" + label +
                '}';
    }
}
