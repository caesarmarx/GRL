package grl.com.dataModels;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import grl.com.configuratoin.GlobalVars;

/**
 * Created by Administrator on 6/7/2016.
 */
public class ConsultUserModel {
    @SerializedName("user_id")
    public String userID;

    @SerializedName("user_name")
    public String userName;

    @SerializedName("user_photo")
    public String userPhoto;


    @SerializedName("last_msg")
    public String lastMsg;

    @SerializedName("last_date")
    public String lastDate;

    @SerializedName("msg_count")
    public Integer msgCount;

    @SerializedName("is_teacher")
    public Boolean bTeacher;

    public ConsultUserModel() {
        this.userID = "";
        this.userName = "";
        this.userPhoto = "";
        this.lastMsg = "";
        this.lastDate = "";
        this.msgCount = 0;
    }

    public void parseFromJson(JSONObject object) {
        this.userID = GlobalVars.getIdFromJson(object, "user_id");
        this.userName = GlobalVars.getStringFromJson(object, "user_name");
        this.userPhoto = GlobalVars.getStringFromJson(object, "user_photo");
        this.bTeacher = GlobalVars.getBooleanFromJson(object, "is_teacher");
    }
}
