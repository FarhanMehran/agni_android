package com.capcorp.utils.facebook;

/**
 * Created by cbluser3 on 27/6/17.
 */
public class FBFriendData {
    public String facebookId = "";
    public String name = "";
    public String friendImageUrl = "";

    public FBFriendData(String facebookId, String name, String friendImageUrl) {
        this.facebookId = facebookId;
        this.name = name;
        this.friendImageUrl = friendImageUrl;
    }

    public FBFriendData() {
    }
}
