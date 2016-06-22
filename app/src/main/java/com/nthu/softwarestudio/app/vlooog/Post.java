package com.nthu.softwarestudio.app.vlooog;

import android.graphics.Bitmap;

/**
 * Created by Ywuan on 21/06/2016.
 */
public class Post {
    private int postId;
    private int postBy;
    private Bitmap profilePicture;
    private String profileName;
    private Bitmap contentImage;
    private int ratingBar;
    private String ratingValue;
    private String content;
    private String comments;
    private String date;

    public Post(Bitmap profilePicture, String profileName, Bitmap contentImage, int ratingBar, String ratingValue, String content, String comments, int postId, String date, int postBy) {
        this.profilePicture = profilePicture;
        this.profileName = profileName;
        this.contentImage = contentImage;
        if(ratingBar < 0) this.ratingBar = 0;
        else this.ratingBar = ratingBar;
        this.ratingValue = ratingValue;
        this.content = content;
        this.comments = comments;
        this.postId = postId;
        this.date = date;
        this.postBy = postBy;
    }

    public Post(Bitmap profilePicture, String profileName, Bitmap contentImage, int ratingBar, int ratingValue, String content, int comments, int postId, String date, int postBy) {
        this.profilePicture = profilePicture;
        this.profileName = profileName;
        this.contentImage = contentImage;
        if(ratingBar < 0) this.ratingBar = 0;
        else this.ratingBar = ratingBar;
        this.ratingValue = Integer.toString(ratingValue);
        this.content = content;
        this.comments = Integer.toString(comments);
        this.postId = postId;
        this.date = date;
        this.postBy = postBy;
    }

    public Post(Bitmap profilePicture, String profileName, Bitmap contentImage, int ratingBar, int ratingValue, String content, String comments, int postId, String date, int postBy) {
        this.profilePicture = profilePicture;
        this.profileName = profileName;
        this.contentImage = contentImage;
        if(ratingBar < 0) this.ratingBar = 0;
        else this.ratingBar = ratingBar;
        this.ratingValue = Integer.toString(ratingValue);
        this.content = content;
        this.comments = comments;
        this.postId = postId;
        this.date = date;
        this.postBy = postBy;
    }

    public Post(Bitmap profilePicture, String profileName, Bitmap contentImage, int ratingBar, String ratingValue, String content, int comments, int postId, String date, int postBy) {
        this.profilePicture = profilePicture;
        this.profileName = profileName;
        this.contentImage = contentImage;
        if(ratingBar < 0) this.ratingBar = 0;
        else this.ratingBar = ratingBar;
        this.ratingValue = ratingValue;
        this.content = content;
        this.comments = Integer.toString(comments);
        this.postId = postId;
        this.date = date;
        this.postBy = postBy;
    }

    public void setProfilePicture(Bitmap profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public void setContentImage(Bitmap contentImage) {
        this.contentImage = contentImage;
    }

    public void setRatingBar(int ratingBar) {
        if(ratingBar < 0) this.ratingBar = 0;
        else this.ratingBar = ratingBar;
    }

    public void setRatingValue(String ratingValue) {
        this.ratingValue = ratingValue;
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = Integer.toString(ratingValue);
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setComments(int comments) {
        this.comments = Integer.toString(comments);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPostBy() {
        return postBy;
    }

    public void setPostBy(int postBy) {
        this.postBy = postBy;
    }


    public Bitmap getProfilePicture() {
        return profilePicture;
    }

    public String getProfileName() {
        return profileName;
    }

    public Bitmap getContentImage() {
        return contentImage;
    }

    public int getRatingBar() {
        return ratingBar;
    }

    public String getRatingValue() {
        return ratingValue + " rated";
    }

    public String getRatingValue_raw() {
        return ratingValue;
    }

    public String getContent() {
        return content;
    }

    public String getComments() {
        return comments + " comments";
    }

    public String getComments_raw() {
        return comments;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }
}
