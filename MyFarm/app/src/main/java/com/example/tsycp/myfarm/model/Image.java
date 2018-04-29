package com.example.tsycp.myfarm.model;

public class Image extends BaseResponse {

    private ImageData data;

    public Image() {
    }

    public ImageData getData() {
        return data;
    }

    public void setData(ImageData data) {
        this.data = data;
    }
}
