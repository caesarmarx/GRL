package grl.com.dataModels;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import grl.com.configuratoin.GlobalVars;

/**
 * Created by Administrator on 6/7/2016.
 */
public class UserModel {
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

    @SerializedName("state")
    public String state;

    @SerializedName("latitude")
    public float latitude;

    @SerializedName("longitude")
    public float longitude;

    @SerializedName("user_status")
    public int userStatus;

    @SerializedName("order_status")
    public int orderStatus;

    public UserModel() {
        this.userID = "";
        this.userName = "";
        this.userPhoto = "";
        this.userJob = "";
        this.userSkill = "";
        this.userGender = 0;
        this.state = "";
        this.latitude = 0;
        this.longitude = 0;
    }

    public void parseFromJson(JSONObject jsonObject) {

        try {
            this.userID = GlobalVars.getIdFromJson(jsonObject, "user_id");
            this.userName = GlobalVars.getStringFromJson(jsonObject, "user_name");
            this.userPhoto = GlobalVars.getStringFromJson(jsonObject, "user_photo");
            this.userJob = GlobalVars.getStringFromJson(jsonObject, "user_job");
            this.userSkill = GlobalVars.getStringFromJson(jsonObject, "user_skill");
            this.userGender = GlobalVars.getIntFromJson(jsonObject, "user_sex");
            this.latitude = GlobalVars.getDoubleFromJson(jsonObject, "latitude").floatValue();
            this.longitude = GlobalVars.getDoubleFromJson(jsonObject, "longitude").floatValue();
            this.userStatus = GlobalVars.getIntFromJson(jsonObject, "user_status");
            this.orderStatus = GlobalVars.getIntFromJson(jsonObject, "order_status");
        } catch (Exception ex) {
            Log.e("Json Parse", ex.getMessage());

        }
    }
}
