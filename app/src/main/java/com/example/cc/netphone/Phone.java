package com.example.cc.netphone;


import java.io.File;

public class Phone {
    private int ImageId;
    private String Name;
    private File file;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Phone(int ImageId, String Name) {
        this.ImageId = ImageId;
        this.Name = Name;
    }
    public Phone(File file, String Name) {
        this.file = file;
        this.Name = Name;
    }
    public void setName(String name) {
        Name = name;
    }

    public void setImageId(int imageId) {
        ImageId = imageId;
    }

    public String getName() {
        return Name;
    }

    public int getImageId() {
        return ImageId;
    }
}
