package com.example.WebLogin.otherClasses;

public class DirFilePathClass {

    private String id, name,src;
    private boolean file;

    public DirFilePathClass(String id, String name, String src) {
        this.id = id;
        this.name = name;
        this.src = src;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public boolean isFile() {
        return file;
    }

    public void setFile(boolean file) {
        this.file = file;
    }
  
}