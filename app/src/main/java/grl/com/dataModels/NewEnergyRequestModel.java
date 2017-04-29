package grl.com.dataModels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 6/8/2016.
 */
public class NewEnergyRequestModel {
    @SerializedName("user_id")
    public String userID;

    @SerializedName("user_name")
    public String userName;

    @SerializedName("user_photo")
    public String userPhoto;

    @SerializedName("energy_type")
    public Integer energyType;

    @SerializedName("request_teacher")
    public Integer requestTeacher;

    @SerializedName("request_disciple")
    public Integer requestDisciple;

    @SerializedName("request_grl")
    public Integer requestGrl;

    @SerializedName("fgroup_name")
    public String fGroupName;

    @SerializedName("energy_quality")
    public Integer energyQuality;

    public NewEnergyRequestModel() {
        this.userID = "";
        this.userPhoto = "";
        this.energyQuality = 0;
        this.requestDisciple = -1;
        this.requestGrl = -1;
        this.requestTeacher = -1;
        this.fGroupName = "";
        this.energyType = -1;
    }
}
