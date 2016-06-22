package com.nthu.softwarestudio.app.vlooog;

import android.graphics.Bitmap;

/**
 * Created by Ywuan on 23/06/2016.
 */
public class Comment {
    private String profile_name;
    private Bitmap profile_image;
    private String comment_content;

    public Comment(String profile_name, Bitmap profile_image, String comment_content) {
        this.profile_name = profile_name;
        this.profile_image = profile_image;
        this.comment_content = comment_content;
    }

    public String getProfile_name() {
        return profile_name;
    }

    public void setProfile_name(String profile_name) {
        this.profile_name = profile_name;
    }

    public Bitmap getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(Bitmap profile_image) {
        this.profile_image = profile_image;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }
}
