package grl.com.dataModels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 6/8/2016.
 */
public class OrderGroupUserModel {
    @SerializedName("user_id")
    public String userID;

    @SerializedName("user_name")
    public String userName;

    @SerializedName("user_Photo")
    public String userPhoto;

    public OrderGroupUserModel() {
        this.userID = "";
        this.userName = "";
        this.userPhoto = "";
    }
}
