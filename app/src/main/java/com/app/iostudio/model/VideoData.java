package com.app.iostudio.model;

import java.util.ArrayList;

/**
 * Created by anantshah on 26/06/18.
 */

public class VideoData {
    private String success;
    private ArrayList<VideoDictionary>data;

    public VideoData() {
    }

    public ArrayList<VideoDictionary> getData() {
        return data;
    }

    public void setData(ArrayList<VideoDictionary> data) {
        this.data = data;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}
