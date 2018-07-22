package com.sayeedul.instaclone;

public class ItemDataUserList {

    private int image,image2;  private String username;

    public ItemDataUserList(int image, String username) {
        this.image = image;
        this.username = username;
    }

    public int getImage() {
        return image;
    }

    public String getUsername() {
        return username;
    }

    public void setNewImage(int image) {
        this.image = image;
    }

}
