package grl.com.configuratoin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Point;
import android.location.Location;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.widget.ImageView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import grl.com.App;
import grl.com.dataModels.BackgroundOrderModel;
import grl.com.dataModels.EmoticonModel;
import grl.com.dataModels.FactorModel;
import grl.com.dataModels.OrderContentModel;
import grl.com.dataModels.OrderEntireModel;
import grl.com.dataModels.OrderModel;
import grl.com.dataModels.UserModel;
import grl.com.httpRequestTask.order.RealTimeTask;
import grl.com.network.HttpCallback;
import grl.com.subViews.dialogues.WaitingDialog;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/6/2016.
 */
public final class GlobalVars {

    public static String SEVER_ADDR = "http://120.24.183.9/GRL";
//    public static String SEVER_ADDR = "http://192.168.1.101:8088/grl";
    private static String SERVER_URL_PREFIX;
    private static WaitingDialog waitDlg;

    private static final String PRE_PASSWORD = "GRL_PASSWORD";
    private static final String PRE_PHONENUMBER = "GRL_PHONENUMBER";

    public static ArrayList<FactorModel> factorList;
    public static ArrayList<BackgroundOrderModel> bkOrderList;
    public static ArrayList<OrderEntireModel> realTimeOrder;
    public static boolean bFirstNotify = false;

    static {
        SERVER_URL_PREFIX = "/index.php/";
    }
    // 사용자 관련
    public static String LOGIN_URL = GlobalVars.getServerUrl() + "user/login";
    public static String REGISTER_URL = GlobalVars.getServerUrl() + "user/register";
    public static String SET_PASSWORD_URL = GlobalVars.getServerUrl() + "user/set_password";
    public static String USER_RANK_URL = GlobalVars.getServerUrl() + "user/grl_ranking";
    public static String USER_INFO_URL = GlobalVars.getServerUrl() + "user/get_user_info";
    public static String REGISTER_INFO_URL = GlobalVars.getServerUrl() + "user/register_info";

    public static String CROSS_USER_URL = GlobalVars.getServerUrl() + "userstars/grcross_list";
    public static String PLANET_USER_URL = GlobalVars.getServerUrl() + "userstars/grstar_list";

    public static String UPDATE_LOCATION_URL = GlobalVars.getServerUrl() + "user/update_location";
    // Search 관련
    public static String GPS_SEARCH_URL = GlobalVars.getServerUrl() + "user/search_by_gps";

    // 령 관령
    public static String GIVE_ORDER_URL = GlobalVars.getServerUrl() + "order/give_order";
    public static String ORDER_REACT_URL = GlobalVars.getServerUrl() + "order/order_react";
    public static String REAL_TIME_URL = GlobalVars.getServerUrl() + "order/realtime_order";
    public static String TGROUP_ORDER_URL = GlobalVars.getServerUrl() + "order/tgroup_order";
    public static String MY_ORDER_URL = GlobalVars.getServerUrl() + "order/my_order_given";
    public static String MY_SOLVE_URL = GlobalVars.getServerUrl() + "order/my_order_sol";
    public static String TGROUP_ORDER_DETAIL_URL = GlobalVars.getServerUrl() + "order/get_tgrouporder_detail";
    public static String MY_ORDER_DETAIL_URL = GlobalVars.getServerUrl() + "order/get_order_detail";
    public static String ORDER_ACCEPT_URL = GlobalVars.getServerUrl() + "order/order_accept";
    public static String ORDER_SOLVE_URL = GlobalVars.getServerUrl() + "order/order_solve";
    public static String ORDER_RESEND_URL = GlobalVars.getServerUrl() + "order/order_resend";
    public static String ORDER_DECLLINE_URL = GlobalVars.getServerUrl() + "order/order_decline";
    public static String GET_ACCEPT_USER_URL = GlobalVars.getServerUrl() + "order/get_accepted_order";
    public static String SOLVE_SELECT_URL = GlobalVars.getServerUrl() + "order/solution_sel";
    public static String OTHER_ORDER_DETAIL_URL = GlobalVars.getServerUrl() + "order/get_other_order_detail";
    public static String ORDER_INSERT_ACCEPT_URL = GlobalVars.getServerUrl() + "order/order_insert_accept";
    public static String GET_ORDER_STATE_URL = GlobalVars.getServerUrl() + "order/get_order_state";
    public static String SET_ORDER_STATE_URL = GlobalVars.getServerUrl() + "order/order_state_edit";
    public static String REPORT_REASON_URL = GlobalVars.getServerUrl() + "user/get_report_reason";
    // 인기창 관련
    public static String TGROUP_FACTOR_URL = GlobalVars.getServerUrl() + "tgroup/est_list";
    public static String TGROUP_POPULARITY_URL = GlobalVars.getServerUrl() + "twitter/get_all_twitter";
    public static String MY_POPULARITY_URL = GlobalVars.getServerUrl() + "twitter/get_my_twitter";
    public static String NEW_POPULARITY_URL = GlobalVars.getServerUrl() + "twitter/twitter_publish";
    public static String POPULARITY_ESTIMATE_URL = GlobalVars.getServerUrl() + "twitter/insert_comment";
    public static String POPULARITY_PRAISE_URL = GlobalVars.getServerUrl() + "twitter/praise_twitter";

    public static String PK_LIST_URL = GlobalVars.getServerUrl() + "twitter/get_pk_list";
    public static String PK_REQUEST_URL = GlobalVars.getServerUrl() + "twitter/request_pk";
    public static String PK_ACCEPT_URL = GlobalVars.getServerUrl() + "twitter/accept_pk";

    public static String EXAMPLE_LIST_URL = GlobalVars.getServerUrl() + "twitter/get_example_list";
    public static String MY_EXAMPLE_LIST_URL = GlobalVars.getServerUrl() + "twitter/get_user_to_me";
    public static String EXAMPLE_REQUEST_URL = GlobalVars.getServerUrl() + "twitter/request_example";
    public static String EXAMPLE_ACCEPT_URL = GlobalVars.getServerUrl() + "twitter/accept_example";

    public static String SURE_ACCEPT_URL = GlobalVars.getServerUrl() + "twitter/accept_sure";

    public static String POPULARITY_USER_RANK_URL = GlobalVars.getServerUrl() + "twitter/get_user_rank";

    public static String POPULARITY_AlLL_REQUEST_URL = GlobalVars.getServerUrl() + "twitter/get_all_request";

    //  Challenge 관련
    public static String CHALLENGE_REQUEST_URL = GlobalVars.getServerUrl() + "challenge/give_challenge";
    public static String CHALLENGE_LOG_URL = GlobalVars.getServerUrl() + "challenge/get_challenge_log";
    public static String CHALLENGE_LIST_URL = GlobalVars.getServerUrl() + "challenge/get_challenge_list";
    public static String CHALLENGE_NOTIFY_URL = GlobalVars.getServerUrl() + "challenge/get_notify_list";
    public static String CHALLENGE_INFO_URL = GlobalVars.getServerUrl() + "challenge/get_challenge_info";
    public static String CHALLENGE_ACCEPT_URL = GlobalVars.getServerUrl() + "challenge/accept_challenge";
    public static String CHALLENGE_ATTEND_URL = GlobalVars.getServerUrl() + "challenge/attend_challenge";

    //  Chat 관련
    public static String CHAT_INFO_URL = GlobalVars.getServerUrl() + "chat/get_chat_info";
    public static String CHAT_RULE_URL = GlobalVars.getServerUrl() + "chat/get_chat_rule";
    public static String USER_LIST_URL = GlobalVars.getServerUrl() + "user/user_list";
    public static String GIFT_ADD_URL = GlobalVars.getServerUrl() + "chat/add_gift";
    public static String GIFT_LIST_URL = GlobalVars.getServerUrl() + "chat/get_my_gift";
    public static String NEWS_LIST_URL = GlobalVars.getServerUrl() + "chat/get_news_list";

    // 신능량창 관련
    public static String TEACHER_DISCIPLE_DETAIL = GlobalVars.getServerUrl() + "tgroup/teacher_disciple_detail";
    public static String ENERGY_GET = GlobalVars.getServerUrl() + "newenergy/energy";
    public static String ENERGY_ACCEPT = GlobalVars.getServerUrl() + "newenergy/energy_accept";
    public static String GET_RELATIONSHIT = GlobalVars.getServerUrl() + "newenergy/get_relationship";
    public static String ENERGY_REQUEST = GlobalVars.getServerUrl() + "newenergy/energy_request";
    public static String GET_ORDER_PLATE_GROUP = GlobalVars.getServerUrl() + "order/get_order_plate_group";
    // 제3인자창관련
    public static String GET_USER_INFO = GlobalVars.getServerUrl() + "user/get_user_info";
    public static String USER_STATUS_GET = GlobalVars.getServerUrl() + "user/user_status_get";
    public static String USER_STATUS_SET = GlobalVars.getServerUrl() + "user/user_status_set";
    public static String USER_SETTING = GlobalVars.getServerUrl() + "user/user_setting";
    // 기부통관련
    public static String PHONE_VERIFY =  GlobalVars.getServerUrl() + "donatehelp/phone_verify";
    public static String MONEY_REQUEST = GlobalVars.getServerUrl() + "donatehelp/money_req";
    public static String DONATE_HELP_CHECK = GlobalVars.getServerUrl() + "donatehelp/help_check";
    public static String DONATE_CONFIRM = GlobalVars.getServerUrl() + "donatehelp/help_confirm";
    public static String DONATE_CANCEL_LIST = GlobalVars.getServerUrl() + "donatehelp/cancel_list";
    public static String DONATE_CONFIRM_LIST = GlobalVars.getServerUrl() + "donatehelp/confirm_list";
    public static String GET_USER_PAID = GlobalVars.getServerUrl() + "challenge/get_user_paid";
    public static String ACCEPTED_MONEY_LIST = GlobalVars.getServerUrl() + "donatehelp/accepted_money_list";

    // 친구계관련
    public static String FGROUP_LIST_GET = GlobalVars.getServerUrl() + "friendgroup/group_list";
    public static String FGROUP_CREATE = GlobalVars.getServerUrl() + "friendgroup/group_create";
    public static String FGROUP_ENERGY = GlobalVars.getServerUrl() + "friendgroup/group_energy";
    public static String FGROUP_EST_LIST = GlobalVars.getServerUrl() + "friendgroup/friend_est_list";
    public static String EST_LIST = GlobalVars.getServerUrl() + "friendgroup/est_list";
    public static String FRIEND_EST = GlobalVars.getServerUrl() + "friendgroup/friend_est";
    public static String FRIEND_LIST = GlobalVars.getServerUrl() + "friendgroup/friend_list";
    public static String FRIEND_HELP_LIST = GlobalVars.getServerUrl() + "friendgroup/friend_help_list";
    public static String FRIEND_HELP_SOLVE = GlobalVars.getServerUrl() + "friendgroup/friend_help_solve";
    public static String FRIEND_HELP_REQ = GlobalVars.getServerUrl() + "friendgroup/friend_help_req";
    public static String FGROUP_GET_INFO = GlobalVars.getServerUrl() + "friendgroup/group_getInfo";
    public static String FGROUP_SET_INFO = GlobalVars.getServerUrl() + "friendgroup/group_setInfo";
    public static String FGROUP_QUIT = GlobalVars.getServerUrl()  + "friendgroup/group_quit";

    // 스승제자계관련
    public static String LEAGUE_STATUS_GET = GlobalVars.getServerUrl() + "league/league_status_get";
    public static String TEACHER_DISCIPLE = GlobalVars.getServerUrl() + "tgroup/teacher_disciple";
    public static String LEAGUE_RESULT_SET = GlobalVars.getServerUrl() + "league/league_result_set";
    public static String LEAGUE_STATUS_SET = GlobalVars.getServerUrl() + "league/league_status_set";
    public static String GET_TGROUP_RULE = GlobalVars.getServerUrl() + "tgroup/get_tgroup_rule";
    public static String LEAGUE_GET_INFO = GlobalVars.getServerUrl() + "league/league_get_info";
    public static String LEAGUE_SETTING_UPDATE = GlobalVars.getServerUrl() + "league/round_setting_update";
    public static String LEAGUE_ROUND_LIST = GlobalVars.getServerUrl() + "league/round_list";
    public static String LEAGUE_AWARDING_INFO = GlobalVars.getServerUrl() + "league/league_awarding_info";
    public static String LEAGUE_AWARDING_SET = GlobalVars.getServerUrl() + "league/league_awarding_set";
    public static final String TEACHER_SET_JUDGE = GlobalVars.getServerUrl() + "tgroup/set_judge";
    public static final String LESSON_TIME_GET_URL = GlobalVars.getServerUrl() + "tgroup/timetable_get";
    public static final String LESSON_TIME_SET_URL = GlobalVars.getServerUrl() + "tgroup/timetable_set";
    public static final String LESSON_MATERIAL_GET_URL = GlobalVars.getServerUrl() + "tgroup/course_get";
    public static final String LESSON_MATERIAL_SET_URL = GlobalVars.getServerUrl() + "tgroup/course_set";
    public static final String LESSON_RULE_GET_URL = GlobalVars.getServerUrl() + "tgroup/lesson_rule_get";
    public static final String LESSON_RULE_SET_URL = GlobalVars.getServerUrl() + "tgroup/lesson_rule_set";
    public static final String LESSON_ESTIMATE_GET_URL = GlobalVars.getServerUrl() + "tgroup/estimate_info";
    public static final String LESSON_ESTIMATE_SET_URL = GlobalVars.getServerUrl() + "tgroup/estimate_publish";
    public static final String TGROUP_RELATION_CHECK_URL = GlobalVars.getServerUrl() + "tgroup/tgroup_exist_check";

    // 화일upload
    public static final String FILE_UPLOAD = GlobalVars.getServerUrl() + "upload/file_upload";

    // 귀성관련
    public static final String NINE_PLANETS_LIST = GlobalVars.getServerUrl() + "userstars/nineplanets_list";
    public static final String SET_PLANET = GlobalVars.getServerUrl() + "userstars/set_planet";

    // profile설정관련
    public static final String SET_USER_PROFILE = GlobalVars.getServerUrl() + "user/set_user_profile";
    public static final String GET_USER_PROFILE = GlobalVars.getServerUrl() + "user/get_user_profile";
    public static final String UPDATE_PROFILE_CERT = GlobalVars.getServerUrl() + "user/update_profile_cert";
    public static final String CERT_ENABLE_PROFILE = GlobalVars.getServerUrl() + "user/cert_enable_profile";

    // crash 관련
    public static final String CRASH_LIST = GlobalVars.getServerUrl() + "crash/crash_list";
    public static final String CRASH_REQUEST = GlobalVars.getServerUrl() + "crash/crash_request";
    public static final String CRASH_QUIT = GlobalVars.getServerUrl() + "crash/crash_quit";
    public static final String CRASH_RESULT = GlobalVars.getServerUrl() + "crash/crash_result";

    // 사용자추가(검색관련)
    public static final String SEARRCH_PHONE = GlobalVars.getServerUrl() + "user/search_phone";

    // 사용자와의 관계설정관련
    public static final String GET_COM_VALUE = GlobalVars.getServerUrl() + "user/get_com_value";
    public static final String IS_RELATION = GlobalVars.getServerUrl() + "user/is_relation";
    public static final String DISCONNECT_REL = GlobalVars.getServerUrl() + "user/disconnect_rel";
    public static final String SET_COM_VALUE = GlobalVars.getServerUrl() + "user/set_com_value";

    // 구좌관련
    public static final String GET_WALLET = GlobalVars.getServerUrl() + "/user/get_wallet";

    // push notification 관련
    public static final String INSERT_TOKEN = GlobalVars.getServerUrl() + "/pushnotification/insert_token";
    public static final String GET_NOTIFICATION_DATA = GlobalVars.getServerUrl() + "/pushnotification/get_notification_data";
    public static final String DELETE_NOTIFICATION = GlobalVars.getServerUrl() + "/pushnotification/delete_notifications";
    public static final String SEND_NOTIFICATION = GlobalVars.getServerUrl() + "/pushnotification/send_notification";

    public static String getServerUrl() {
        return GlobalVars.SEVER_ADDR + GlobalVars.SERVER_URL_PREFIX;
    }

    public static void showErrAlert(final Activity context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("网络错误!");

        // set dialog message
        alertDialogBuilder
                .setMessage("您的连接已失败")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        //context.finish();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public static JSONObject getJsonDataFromString(String data) throws JSONException {
        if(data == null)
            return new JSONObject();
        data = data.replace("\uFEFF", "");
        return new JSONObject(data);
    }


    public static JsonObject getGJsonDataFromString(String data){
        if(data == null)
            return new JsonObject();
        data = data.replace("\uFEFF", "");
        JsonObject result = new JsonObject();
        try {
            JsonElement jelement = new JsonParser().parse(data);
            result = jelement.getAsJsonObject();
            return result;
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        result.add("result_data", new JsonObject());
        result.addProperty("result_code", false);
        return result;
    }

    /**
     * Order
     */
    public static String getOrderString(String orderType, String orderContent) {

        if (orderContent == null)
            orderContent = "";

        if ((orderType == null) || (orderType.compareTo("") == 0))
            return orderContent;

        return String.format("%s+%s",orderType, orderContent);
    }

    /**
     * Waiting Dialog
     */
    public static void showWaitDialog(Activity activity) {
        if (waitDlg == null)
            waitDlg = WaitingDialog.newInstance();
        else {
            return;
        }
        waitDlg.setShowsDialog(true);
        waitDlg.show(activity.getFragmentManager(), "");
    }

    public static void hideWaitDialog() {
        try {
            if (waitDlg == null)
                return;
            if(waitDlg.getActivity().isDestroyed())
                    return;
            waitDlg.dismiss();
            waitDlg = null;
        } catch (Exception ex) {

        }
    }

    /**
     * User Login - Preference
     */

    public static String getPasswordFromPre(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PRE_PASSWORD, "");
    }
    public static void setPasswordToPre(Context context, String password) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PRE_PASSWORD, password).commit();
    }
    public static String getPhoneNumberFromPre(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PRE_PHONENUMBER, "");
    }
    public static void setPhoneNumberToPre(Context context, String phoneNumber) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PRE_PHONENUMBER, phoneNumber).commit();
    }

    // 봉사기로부터의 화상현시
    public static Boolean loadImage(ImageView imageView, String filePath) {
        if(filePath == null || filePath.equals("")) {
            imageView.setImageDrawable(App.getInstance().getResources().getDrawable(R.drawable.user_default));
            return false;
        }
        filePath = GlobalVars.SEVER_ADDR + "/" + filePath;
        Ion.with(imageView)
                .placeholder(R.drawable.user_default)
                .load(filePath);
        return true;
    }

    public static String getFullPath (String path) {
        if(path.equals(""))
            return "";
        return GlobalVars.SEVER_ADDR + "/" + path;
    }
    public static JSONArray insertObject(JSONArray data, JSONObject insert_data, int position) {
        JSONArray result = new JSONArray();
        ArrayList<JSONObject> arrayList = new ArrayList<JSONObject>();
        for(int i = 0 ; i < data.length(); i ++) {
            try {
                arrayList.add(data.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        arrayList.add(position, insert_data);
        try {
            result = new JSONArray(arrayList.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Get Date
     */
    public static String getDateStringFromMongoDate(JSONObject dataObj, String format) {
        String strDate = "";
        try {
            Long sec =  dataObj.getLong("sec");
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            strDate = formatter.format(new Date(sec * 1000));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return strDate;
    }
    public static String getDateStringFromMongoDate(JsonObject dataObj, String format) {
        String strDate = "";
        try {
            Long sec =  dataObj.get("sec").getAsLong();
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            strDate = formatter.format(new Date(sec * 1000));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }
    public static String getDateStringFromLong(Long sec, String format) {
        try {
            String strDate = "";
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            strDate = formatter.format(new Date(sec * 1000));
            return strDate;
        } catch (Exception ex) {

        }
        return "";
    }

    public static String getDateStringFromDate(Date date, String format) {
        String strDate = "";
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        strDate = formatter.format(date);
        return strDate;
    }

    public static String getRelativeDate(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date now = new Date();
        long offset = (now.getHours() * 3600 + now.getMinutes() * 60 + now.getSeconds()) * 1000;
        if (now.getTime() - date.getTime() < offset) {
            return "今天";
        }
        if (now.getTime() - date.getTime() < offset + 24 * 3600 * 1000) {
            return "今天";
        }
        return formatter.format(date);


    }
    /**
     * Get Value From Json
     */
    public static Long getDateFromJson(JSONObject jsonObject, String fieldName) {
        if (jsonObject.has(fieldName)) {
            try {
                Object object = jsonObject.get(fieldName);
                if (object.getClass() == JSONObject.class) {
                    return ((JSONObject)object).getLong("sec");
                }
                if (object.getClass() == String.class) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date date = format.parse((String)object);
                        System.out.println(date);
                        return (date.getTime() / 1000);
                    } catch (ParseException e) {
                        return Long.valueOf(0);
                    }
                }
                if (object.getClass() == Long.class) {
                    return (Long)object;
                }
            } catch (Exception ex) {

            }
        }
        return Long.valueOf(0);
    }

    public static String getIdFromJson(JSONObject jsonObject, String fieldName) {
        if (jsonObject.has(fieldName)) {
            try {
                Object object = jsonObject.get(fieldName);
                if (object.getClass() == JSONObject.class) {
                    return ((JSONObject)object).getString("$id");
                }
                if (object.getClass() == String.class) {
                    return (String)object;
                }
            } catch (Exception ex) {

            }
        }
        return "";
    }

    public static String getStringFromJson(JSONObject jsonObject, String fieldName) {
        if (jsonObject.has(fieldName)) {
            try {
                return jsonObject.getString(fieldName);
            } catch (Exception ex) {

            }
        }
        return "";
    }

    public static Integer getIntFromJson(JSONObject jsonObject, String fieldName) {
        if (jsonObject.has(fieldName)) {
            try {
                return jsonObject.getInt(fieldName);
            } catch (Exception ex) {

            }
        }
        return 0;
    }

    public static Long getLongFromJson(JSONObject jsonObject, String fieldName) {
        if (jsonObject.has(fieldName)) {
            try {
                return jsonObject.getLong(fieldName);
            } catch (Exception ex) {

            }
        }
        return Long.valueOf(0);
    }

    public static Double getDoubleFromJson(JSONObject jsonObject, String fieldName) {
        if (jsonObject.has(fieldName)) {
            try {
                return jsonObject.getDouble(fieldName);
            } catch (Exception ex) {

            }
        }
        return Double.valueOf(0);
    }

    public static Boolean getBooleanFromJson(JSONObject jsonObject, String fieldName) {
        if (jsonObject.has(fieldName)) {
            try {
                Object object = jsonObject.get(fieldName);
                if (object.getClass() == Integer.class) {
                    int result = (Integer)object;
                    if (result > 0) return true;
                    return false;
                }
                if (object.getClass() == Boolean.class) {
                    return jsonObject.getBoolean(fieldName);
                }
            } catch (Exception ex) {

            }
        }
        return false;
    }

    public static JSONObject getJSONObjectFromJson(JSONObject jsonObject, String fieldName) {
        if (jsonObject.has(fieldName)) {
            try {
                return jsonObject.getJSONObject(fieldName);
            } catch (Exception ex) {

            }
        }
        return null;
    }

    public static JSONArray getJSONArrayFromJson(JSONObject jsonObject, String fieldName) {
        if (jsonObject.has(fieldName)) {
            try {
                return jsonObject.getJSONArray(fieldName);
            } catch (Exception ex) {

            }
        }
        return new JSONArray();
    }

    public static void showCommonAlertDialog (Activity context, String title, String msg) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton(context.getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

//    String -> Integer Value
    public static int getIntFromString(String str) {
        if (str == null || str.isEmpty())
            return 0;
        int value = 0;
        try {
            value = Integer.parseInt(str);
        } catch (Exception ex) {
            value = 0;
        }
        return value;
    }

    // 현재시간 얻기
    public static String getCurrentTime () {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    // convert dp -> px
    public static float dp2px(Context context, int dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;
    }

    // convert px -> dp
    public int px2dp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static String getRealPathFromURI(Activity context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /**
     * Get Distance
     */
    public static float getDistanceDouble(float startLatitude, float startLongitude, float stopLatitude, float stopLongitude) {
        Location locationA = new Location("point A");

        locationA.setLatitude(startLatitude);
        locationA.setLongitude(startLongitude);

        Location locationB = new Location("point B");

        locationB.setLatitude(stopLatitude);
        locationB.setLongitude(stopLongitude);

        float distance = locationA.distanceTo(locationB);
        return distance;
    }

    public static String getDistanceString(double startLatitude, double startLongitude, double stopLatitude, double stopLongitude) {
        Location locationA = new Location("point A");

        locationA.setLatitude(startLatitude);
        locationA.setLongitude(startLongitude);

        Location locationB = new Location("point B");

        locationB.setLatitude(stopLatitude);
        locationB.setLongitude(stopLongitude);

        float distance = locationA.distanceTo(locationB);

        if (distance < 1000)
            return String.format("%d米以内", ((int)(distance / 100) + 1) * 100);
        else
            return String.format("%d公里以内", ((int)(distance / 1000) + 1) * 100);
    }


    // Sound Pool Play
    public static void playSound() {

    }
    public static void playSoundPool(Context context, int resId) {
        SoundPool mSoundPool =  new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        int soundId = mSoundPool.load(context, resId, 1);
        mSoundPool.play(soundId, 1, 1, 0, 0, 1);
    }

    public static List<EmoticonModel> getEmoticonList(boolean bConsult) {
        List<EmoticonModel> list = new ArrayList<EmoticonModel>();
        if (bConsult) {
            list.add(new EmoticonModel("[好]", 2, R.drawable.emoticon_est_good));
            list.add(new EmoticonModel("[中]", 1, R.drawable.emoticon_est_normal));
            list.add(new EmoticonModel("[差]", 2, R.drawable.emoticon_est_bad));
        }
        list.add(new EmoticonModel("[提问小手]", 0.1f, R.drawable.emoticon_question));
        list.add(new EmoticonModel("[金条]", 1, R.drawable.emoticon_gold_bar));
        list.add(new EmoticonModel("[鲜花]", 0.2f, R.drawable.emoticon_flower));
        list.add(new EmoticonModel("[红心]", 5, R.drawable.emoticon_heart));
        list.add(new EmoticonModel("[V587]", 2, R.drawable.emoticon_v587));
        list.add(new EmoticonModel("[掌声]", 0.2f, R.drawable.emoticon_cheer));
        list.add(new EmoticonModel("[最棒]", 5, R.drawable.emoticon_best));
        list.add(new EmoticonModel("[牛人]", 4, R.drawable.emoticon_cattle));
        list.add(new EmoticonModel("[蓝色妖姬]", 3, R.drawable.emoticon_blue_lover));
        list.add(new EmoticonModel("[钻戒]", 10, R.drawable.emoticon_diamond));
        return list;
    }

    public static EmoticonModel getEmoticon(String emoticonName) {
        List<EmoticonModel> list = getEmoticonList(true);
        for (int i = 0; i < list.size(); i ++) {
            EmoticonModel model = list.get(i);
            if (model.emoticonName.compareTo(emoticonName) == 0)
                return model;
        }
        return null;
    }
    public static int isGiftEmoticon(String emoticonName) {
        if (emoticonName == null)
            return -1;
        if (emoticonName.compareTo("[好]") == 0)
            return 0;
        if (emoticonName.compareTo("[中]") == 0)
            return 1;
        if (emoticonName.compareTo("[差]") == 0)
            return 2;
        return -1;
    }

    // Background Order Add
    public static void addBackgroundOrder(JsonObject object, boolean bBackground) {
        if (object == null)
            return;
        BackgroundOrderModel model = new BackgroundOrderModel();
        try {
            if (bBackground) {
                model.orderId = object.get("order_id").getAsString();
                model.backShow = true;
            } else {
                model.orderId = object.get("order_id").getAsString();
                model.backShow = false;
            }
        } catch (Exception ex) {

        }
        if (bkOrderList == null)
            bkOrderList = new ArrayList<BackgroundOrderModel>();
        bkOrderList.add(model);
    }
    public static void removeBKOrder(String orderId) {
        if (bkOrderList == null) return;
        for (int i = 0; i < bkOrderList.size(); i++) {
            try {
                BackgroundOrderModel model = bkOrderList.get(i);
                if (model.orderId.compareTo(orderId) == 0) {
                    bkOrderList.remove(i);
                    break;
                }
            } catch (Exception ex) {

            }

        }
    }
    public static int getNotifyCount() {
        int count = 0;
        if (bkOrderList == null)
            return 0;
        for (int i = 0; i < bkOrderList.size(); i++) {
            BackgroundOrderModel model = bkOrderList.get(i);
            if (model.backShow == true) {
                count ++;
            }
        }
        return  count;
    }
    public static BackgroundOrderModel getFirstNotify() {
        for (int i = 0; i < bkOrderList.size(); i++) {
            BackgroundOrderModel model = bkOrderList.get(i);
            if (model.backShow == true) {
                return model;
            }
        }
        return null;
    }
    public static void removeNotifyShow() {
        for (int i = 0; i < bkOrderList.size(); i++) {
            BackgroundOrderModel model = bkOrderList.get(i);
            model.backShow = false;
            bkOrderList.set(i, model);
        }
    }
    public static void refreshBackOrder() {
        if (bkOrderList == null)
            return;
        for (int i = bkOrderList.size() - 1; i >= 0; i--) {
            BackgroundOrderModel model = bkOrderList.get(i);
            boolean flag = false;
            for (int j = 0; j < GlobalVars.realTimeOrder.size(); j++) {
                OrderEntireModel order = GlobalVars.realTimeOrder.get(j);
                if (model.orderId.compareTo(order.orderModel.orderId) == 0) {
                    flag = true;
                }
            }
            if (!flag) {
                bkOrderList.remove(i);
            }
        }
    }
    public static void loadRealTimeOrder(final boolean bsound) {
        new RealTimeTask(App.getInstance(), SelfInfoModel.userID, 10, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    return;
                }
                if (bsound) {
                    GlobalVars.playSoundPool(App.curActivity, R.raw.order_send);
                }

                GlobalVars.realTimeOrder.clear();
                try {
                    JSONArray result = (JSONArray) response;

                    for (int i =0; i < result.length(); i++) {
                        JSONObject object = result.getJSONObject(i);

                        OrderModel orderModel = new OrderModel();
                        orderModel.parseFromJson(object.getJSONObject("order"));
                        OrderContentModel contentModel = new OrderContentModel();
                        contentModel.parseFromJson(object.getJSONObject("ord_content"));
                        UserModel userModel = new UserModel();
                        userModel.parseFromJson(object.getJSONObject("user_info"));

                        OrderEntireModel entireModel = new OrderEntireModel();
                        entireModel.orderModel = orderModel;
                        entireModel.userModel = userModel;
                        entireModel.contentModel = contentModel;

                        if (entireModel.orderModel.orderStatus >= 1)
                            entireModel.userModel.state = "已抢";

                        GlobalVars.realTimeOrder.add(entireModel);
                    }

                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }
                GlobalVars.refreshBackOrder();
                LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(App.getInstance());
                Intent sendIntent = new Intent(Constant.UPDATE_MAIN_UI);
                sendIntent.putExtra("Message",Constant.UPDATE_REAL_ORDER);
                broadcaster.sendBroadcast(sendIntent);
            }
        });
    }


    // Screen Resolution Init
    public static void initResolution(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        if (width >= 640) {
            Constant.IMAGE_WIDTH = Constant.BIG_IMAGE_WIDTH;
            Constant.IMAGE_HEIGHT = Constant.BIG_IMAGE_HEIGHT;
        } else {
            Constant.IMAGE_WIDTH = Constant.SMALL_IMAGE_WIDTH;
            Constant.IMAGE_HEIGHT = Constant.SMALL_IMAGE_HEIGHT;
        }
    }

}
