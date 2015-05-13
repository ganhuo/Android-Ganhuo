package com.ganhuo.app.models;

import android.text.format.DateFormat;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;

import java.util.Date;

@AVClassName("Entry")
public class Entry extends AVObject {
    public String getTitle() {
        return getString("title");
    }

    public String getContent() {
        return getString("content");
    }

    public String getUrl() {
        return getString("url");
    }

    public AVFile getThumb() {
        return getAVFile("thumb");
    }


    public Date getPublishDate() {
        return getDate("publishDate");
    }

    public String getBeautyPublishDate() {
        return DateFormat.format("yyyy-MM-dd", getPublishDate()).toString();
    }

    public String getThumbUrl() {
        return getThumb().getUrl();
    }
}
