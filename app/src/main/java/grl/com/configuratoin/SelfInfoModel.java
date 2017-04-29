package grl.com.configuratoin;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 6/6/2016.
 */
public final class SelfInfoModel {

    public static String sessionID = "";
    public static String userID = "";
    public static String userName = "";
    public static String userPhoto = "";
    public static String userPass = "";
    public static String userPhone = "";
    public static String userNickName = "";
    public static String getUserPhoto = "";
    public static Integer userGender = 0;
    public static String userJob = "";
    public static String userArea = "";
    public static Integer energyQuality = 0;
    public static String userSkill = "";
    public static String chatServer = "";

    public static double latitude = 0;
    public static double longitude = 0;
    public static HashMap<String, String> posArea;

    public static void reset() {
        latitude = 0;
        longitude = 0;
        posArea = new HashMap<String, String>();
        posArea.put("citycode", "");
        posArea.put("adcode", "");
        posArea.put("province", "");
        posArea.put("city", "");
        posArea.put("district", "");
    }

    public static void parseFromJson(JSONObject jsonObject) {
        sessionID = GlobalVars.getIdFromJson(jsonObject, "session_id");
        userID = GlobalVars.getIdFromJson(jsonObject, "user_id");
        userName = GlobalVars.getStringFromJson(jsonObject, "user_name");
        userPass = GlobalVars.getStringFromJson(jsonObject, "user_pw");
        userGender = GlobalVars.getIntFromJson(jsonObject, "user_sex");
        userArea = GlobalVars.getStringFromJson(jsonObject, "user_area");
        userJob = GlobalVars.getStringFromJson(jsonObject, "user_job");
        userNickName = GlobalVars.getStringFromJson(jsonObject, "user_nickname");
        userPhoto = GlobalVars.getStringFromJson(jsonObject, "user_photo");
        chatServer = GlobalVars.getStringFromJson(jsonObject, "chat_server");
        latitude = GlobalVars.getDoubleFromJson(jsonObject, "latitude");
        longitude = GlobalVars.getDoubleFromJson(jsonObject, "longitude");
//        try {
//            JSONObject object = jsonObject.getJSONObject("pos_area");
//            posArea.clear();
//            posArea.put("citycode", GlobalVars.getStringFromJson(object, "citycode"));
//            posArea.put("adcode", GlobalVars.getStringFromJson(object, "adcode"));
//            posArea.put("province", GlobalVars.getStringFromJson(object, "province"));
//            posArea.put("city", GlobalVars.getStringFromJson(object, "city"));
//            posArea.put("district", GlobalVars.getStringFromJson(object, "district"));
//        } catch (Exception ex) {
//
//        }

    }
 }
