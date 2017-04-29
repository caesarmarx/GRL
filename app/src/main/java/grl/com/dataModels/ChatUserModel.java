package grl.com.dataModels;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import grl.com.configuratoin.GlobalVars;

/**
 * Created by Administrator on 6/7/2016.
 */
public class ChatUserModel {
    @SerializedName("user_id")
    public String userID;

    @SerializedName("user_name")
    public String userName;

    @SerializedName("user_photo")
    public String userPhoto;


    public ChatUserModel() {
        this.userID = "";
        this.userName = "";
        this.userPhoto = "";
    }

    public void parseFromJson(JSONObject object) {
        this.userID = GlobalVars.getIdFromJson(object, "user_id");
        this.userName = GlobalVars.getStringFromJson(object, "user_name");
        this.userPhoto = GlobalVars.getStringFromJson(object, "user_photo");
    }
}
