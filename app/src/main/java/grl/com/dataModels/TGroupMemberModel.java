package grl.com.dataModels;

import android.app.Activity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;

import grl.com.activities.discovery.crash.CrashActivity;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.httpRequestTask.tGroup.TeacherDiscipleTask;
import grl.com.network.HttpCallback;

/**
 * Created by Administrator on 6/7/2016.
 */
public class TGroupMemberModel extends Object{

    // require
    Activity context;
    String curTeacherID;

    // tasks
    TeacherDiscipleTask teacherDiscipleTask;

    // response
    public  JsonArray disciples;
    public  JsonArray teachers;

    public TGroupMemberModel (Activity context, String curTeacherID) {
        this.context = context;
        this.curTeacherID = curTeacherID;

        InitProcess();
    }

    public void InitProcess () {
        // init data
        disciples = new JsonArray();
        teachers = new JsonArray();
        // send request
        teacherDiscipleTask = new TeacherDiscipleTask(context, curTeacherID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(context);
                    return;
                }
                JsonObject result = (JsonObject) Response;
                disciples = result.get("disciple_group").getAsJsonArray();
                teachers = result .get("teacher_group").getAsJsonArray();
                if(context instanceof CrashActivity) {
                    ((CrashActivity)context).getCrashLst();
                }
            }
        });
    }

    public String getUserNameFromDiscipleID (String discipleID) {
        for(int i = 0; i < disciples.size(); i ++) {
            JsonObject item = disciples.get(i).getAsJsonObject();
            if(discipleID.equals(item.get("user_id").getAsString()))
                return item.get("user_name").getAsString();
        }
        return "";
    }

    public String getUserPhotoFromDiscipleID (String discipleID) {
        for(int i = 0; i < disciples.size(); i ++) {
            JsonObject item = disciples.get(i).getAsJsonObject();
            if(discipleID.equals(item.get("user_id").getAsString()))
                return item.get("user_photo").getAsString();
        }
        return "";
    }

    public Boolean isMemberofGroup (String userID) {
        int i;
        for(i = 0; i < disciples.size(); i ++) {
            JsonObject item = disciples.get(i).getAsJsonObject();
            if(userID.equals(item.get("user_id").getAsString()))
                return true;
        }
        for(i = 0; i< teachers.size(); i ++) {
            JsonObject item = teachers.get(i).getAsJsonObject();
            if(userID.equals(item.get("user_id").getAsString()))
                return true;
        }
        return false;
    }

    public Boolean areYouBoss () {
        if(curTeacherID.equals(SelfInfoModel.userID))
            return true;
        return  false;
    }
}
