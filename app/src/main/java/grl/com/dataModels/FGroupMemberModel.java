package grl.com.dataModels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 6/9/2016.
 */
public class FGroupMemberModel {
    @SerializedName("user_id")
    public String userID;

    @SerializedName("user_name")
    public String userName;

    @SerializedName("user_photo")
    public String userPhoto;

    public FGroupMemberModel() {
        this.userID = "";
        this.userName = "";
        this.userPhoto = "";
    }
}
