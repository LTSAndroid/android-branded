package com.pixelmags.android.bean;

import java.util.ArrayList;

/**
 * Created by sejeeth on 31/7/17.
 */

public class RegionBean {
    String title,url,type,id;
    ArrayList<String> region;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getRegion() {
        return region;
    }

    public void setRegion(ArrayList<String> region) {
        this.region = region;
    }
}
