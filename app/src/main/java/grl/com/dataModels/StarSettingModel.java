package grl.com.dataModels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 6/11/2016.
 */
public class StarSettingModel {
    @SerializedName("user_id")
    public String userId;

    @SerializedName("user_name")
    public String userName;

    @SerializedName("user_phone")
    public String userPhone;

    @SerializedName("star_num")
    public Integer starNum;

    public StarSettingModel () {
        this.userId = "";
        this.userName = "";
        this.starNum = 0;
    }
}
