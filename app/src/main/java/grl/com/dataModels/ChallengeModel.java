package grl.com.dataModels;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import grl.com.configuratoin.GlobalVars;

/**
 * Created by Administrator on 6/7/2016.
 */
public class ChallengeModel {

    @SerializedName("challenge_id")
    public String challengeId;

    @SerializedName("from_userid")
    public String fromUserId;

    @SerializedName("from_username")
    public String fromUserName;

    @SerializedName("from_userphoto")
    public String fromUserPhoto;

    @SerializedName("to_userid")
    public String toUserId;

    @SerializedName("to_username")
    public String toUserName;

    @SerializedName("to_userphoto")
    public String toUserPhoto;

    @SerializedName("start_date")
    public Long startDate;

    @SerializedName("end_date")
    public Long endDate;

    @SerializedName("est_name")
    public String estName;

    @SerializedName("notify_range")
    public Integer notifyRange;

    @SerializedName("challenge_money")
    public Integer challengeMoney;

    @SerializedName("accept_state")
    public Integer acceptState;

    @SerializedName("challenge_state")
    public Integer challengeState;

    @SerializedName("challenge_result")
    public Integer challengeResult;

    @SerializedName("challenge_users")
    public ArrayList<ChallengeContributeModel> challengeUsers;

    @SerializedName("response_users")
    public ArrayList<ChallengeContributeModel> responseUsers;

    @SerializedName("reg_date")
    public Long regDate;

    public int challengeType;  // 0:Request 1:Response

    public ChallengeModel() {
        this.challengeId = "";
        this.fromUserId = "";
        this.fromUserName = "";
        this.fromUserPhoto = "";
        this.toUserId = "";
        this.toUserName = "";
        this.toUserPhoto = "";
        this.startDate = Long.valueOf(0);
        this.endDate = Long.valueOf(0);
        this.estName = "";
        this.notifyRange = Integer.valueOf(0);
        this.challengeMoney = Integer.valueOf(0);
        this.acceptState = Integer.valueOf(0);
        this.challengeState = Integer.valueOf(0);
        this.challengeResult = Integer.valueOf(0);
        this.challengeUsers = new ArrayList<ChallengeContributeModel>();
        this.responseUsers = new ArrayList<ChallengeContributeModel>();
        this.regDate = Long.valueOf(0);
        this.challengeType = 0;
    }

    public void parseFromJson(JSONObject object) {
        this.challengeId = GlobalVars.getIdFromJson(object, "challenge_id");
        this.fromUserId = GlobalVars.getIdFromJson(object, "from_userid");
        this.fromUserName = GlobalVars.getStringFromJson(object, "from_username");
        this.fromUserPhoto = GlobalVars.getStringFromJson(object, "from_userphoto");
        this.toUserId = GlobalVars.getIdFromJson(object, "to_userid");
        this.toUserName = GlobalVars.getStringFromJson(object, "to_username");
        this.toUserPhoto = GlobalVars.getStringFromJson(object, "to_userphoto");
        this.startDate = GlobalVars.getDateFromJson(object, "start_date");
        this.endDate = GlobalVars.getDateFromJson(object, "end_date");
        this.estName = GlobalVars.getStringFromJson(object, "est_name");
        this.notifyRange = GlobalVars.getIntFromJson(object, "notify_range");
        this.challengeMoney = GlobalVars.getIntFromJson(object, "challenge_money");
        this.acceptState = GlobalVars.getIntFromJson(object, "accept_state");
        this.challengeState = GlobalVars.getIntFromJson(object, "challenge_state");
        this.challengeResult = GlobalVars.getIntFromJson(object, "challenge_result");
        this.regDate = GlobalVars.getDateFromJson(object, "reg_date");
        if (object.has("challenge_users")) {
            try {
                JSONArray jsonArray = object.getJSONArray("challenge_users");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object1 = jsonArray.getJSONObject(i);
                    ChallengeContributeModel model = new ChallengeContributeModel();
                    model.parseFromJson(object);
                    this.challengeUsers.add(model);
                }
            } catch (Exception ex) {

            }
        }
        if (object.has("response_users")) {
            try {
                JSONArray jsonArray = object.getJSONArray("response_users");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object1 = jsonArray.getJSONObject(i);
                    ChallengeContributeModel model = new ChallengeContributeModel();
                    model.parseFromJson(object);
                    this.challengeUsers.add(model);
                }
            } catch (Exception ex) {

            }
        }
    }
}
