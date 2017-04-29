package grl.com.dataModels;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import grl.com.configuratoin.GlobalVars;

/**
 * Created by Administrator on 6/7/2016.
 */
public class TwitterRelModel {
    @SerializedName("user_id")
    public String userID;

    @SerializedName("user_name")
    public String userName;

    @SerializedName("user_Photo")
    public String userPhoto;

    @SerializedName("user_job")
    public String userJob;

    @SerializedName("user_skill")
    public String userSkill;

    @SerializedName("user_sex")
    public Integer userGender;

    @SerializedName("index")
    public int index;

    @SerializedName("state")
    public String state;

    public TwitterRelModel() {
        this.userID = "";
        this.userName = "";
        this.userPhoto = "";
        this.userJob = "";
        this.userSkill = "";
        this.userGender = 0;
        this.state = "";
    }

    public void parseFromJson(JSONObject jsonObject) {

        try {
            this.userID = GlobalVars.getIdFromJson(jsonObject, "user_id");
            this.userName = GlobalVars.getStringFromJson(jsonObject, "user_name");
            this.userPhoto = GlobalVars.getStringFromJson(jsonObject, "user_photo");
            this.userJob = GlobalVars.getStringFromJson(jsonObject, "user_job");
            this.userSkill = GlobalVars.getStringFromJson(jsonObject, "user_skill");
            this.userGender = GlobalVars.getIntFromJson(jsonObject, "user_sex");
            this.index = GlobalVars.getIntFromJson(jsonObject, "index");

        } catch (Exception ex) {
            Log.e("Json Parse", ex.getMessage());

        }
    }
}
