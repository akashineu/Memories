package com.akash.memories.model;

public class PostModel {
    private  String image;
    private String caption;

    private String description;
    private String postOwnerName;
    private Long timeAdded;

    public Long getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Long timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getUserName() {
        return postOwnerName;
    }

    public void setUserName(String postOwnerName) {
        this.postOwnerName = postOwnerName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PostModel(String image, String caption, String description, String postOwnerName, Long timeAdded) {
        this.image = image;
        this.caption = caption;
        this.description = description;
        this.postOwnerName = postOwnerName;
        this.timeAdded = timeAdded;
    }

    public PostModel() {
    }
}
