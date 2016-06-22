package com.nthu.softwarestudio.app.vlooog;

import android.graphics.Bitmap;
import android.widget.RatingBar;

import java.io.Serializable;

/**
 * Created by Ywuan on 21/06/2016.
 */
public class Post implements Serializable {
    private int postId;
    private Bitmap profilePicture;
    private String profileName;
    private Bitmap contentImage;
    private int ratingBar;
    private String ratingValue;
    private String content;
    private String comments;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;

    public Post(Bitmap profilePicture, String profileName, Bitmap contentImage, int ratingBar, String ratingValue, String content, String comments, int postId, String date) {
        this.profilePicture = profilePicture;
        this.profileName = profileName;
        this.contentImage = contentImage;
        this.ratingBar = ratingBar;
        this.ratingValue = ratingValue + " rated";
        this.content = content;
        this.comments = comments + " comments";
        this.postId = postId;
        this.date = date;
    }

    public Post(Bitmap profilePicture, String profileName, Bitmap contentImage, int ratingBar, int ratingValue, String content, int comments, int postId, String date) {
        this.profilePicture = profilePicture;
        this.profileName = profileName;
        this.contentImage = contentImage;
        this.ratingBar = ratingBar;
        this.ratingValue = ratingValue + " rated";
        this.content = content;
        this.comments = comments + " comments";
        this.postId = postId;
        this.date = date;
    }

    public Post(Bitmap profilePicture, String profileName, Bitmap contentImage, int ratingBar, int ratingValue, String content, String comments, int postId, String date) {
        this.profilePicture = profilePicture;
        this.profileName = profileName;
        this.contentImage = contentImage;
        this.ratingBar = ratingBar;
        this.ratingValue = ratingValue + " rated";
        this.content = content;
        this.comments = comments + " comments";
        this.postId = postId;
        this.date = date;
    }

    public Post(Bitmap profilePicture, String profileName, Bitmap contentImage, int ratingBar, String ratingValue, String content, int comments, int postId, String date) {
        this.profilePicture = profilePicture;
        this.profileName = profileName;
        this.contentImage = contentImage;
        this.ratingBar = ratingBar;
        this.ratingValue = ratingValue + " rated";
        this.content = content;
        this.comments = comments + " comments";
        this.postId = postId;
        this.date = date;
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
        this.ratingBar = ratingBar;
    }

    public void setRatingValue(String ratingValue) {
        this.ratingValue = ratingValue + " rated";
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue + " rated";
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setComments(String comments) {
        this.comments = comments + " comments";
    }

    public void setComments(int comments) {
        this.comments = comments + " comments";
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
        return ratingValue;
    }

    public String getContent() {
        return content;
    }

    public String getComments() {
        return comments;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }
}
