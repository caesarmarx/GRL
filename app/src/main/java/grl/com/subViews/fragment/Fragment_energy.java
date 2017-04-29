package grl.com.subViews.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import grl.com.adapters.energy.EnergyTopSectionAdapter;
import grl.com.adapters.energy.TGroupSectionTitleAdapter;
import grl.com.adapters.energy.TGroupViewAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.httpRequestTask.newEnergy.TeacherDiscipleDetailTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * Created by Administrator on 6/7/2016.
 */
public class Fragment_energy extends Fragment {
    Activity ctx;
    View layout;

    public EnergyTopSectionAdapter customTopAdapter;
    TGroupSectionTitleAdapter tGroupSectionTitleAdapter;
    TGroupViewAdapter tGroupViewAdapter;

    // tasks
    TeacherDiscipleDetailTask teacherDiscipleDetailTask;

    // response parameters
    JSONArray resultData;
    JSONArray reparsingResultData;
    List<String> indexTitles;

    public SectionedRecyclerViewAdapter sectionAdapter;
    private RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (layout == null) {
            ctx = this.getActivity();
            layout = ctx.getLayoutInflater().inflate(R.layout.fragment_energy,
                    null);
            initViews();
            initData();
//            initializeData();
            setOnListener();
        } else {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        return layout;
    }


    private void initViews() {
        // TODO Auto-generated method stub
        this.findViewByID();
        this.setOnListener();

    }

    public void findViewByID() {
        this.recyclerView = (RecyclerView)layout.findViewById(R.id.recycler_view);

    }
    public void initializeData() {
        // create an instance of sectionrecylcerview
        sectionAdapter = new SectionedRecyclerViewAdapter();
        customTopAdapter = new EnergyTopSectionAdapter(this.ctx);
        tGroupSectionTitleAdapter = new TGroupSectionTitleAdapter(this.ctx);
        // add sections
        sectionAdapter.addSection(customTopAdapter);
        sectionAdapter.addSection(tGroupSectionTitleAdapter);
        // set up your recyclerview with sectinedrecyclerviewAdapter
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.recyclerView.setAdapter(sectionAdapter);

        // send request to server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("session_id", SelfInfoModel.sessionID);
        params.put("user_id", SelfInfoModel.userID);
        teacherDiscipleDetailTask = new TeacherDiscipleDetailTask(this.ctx, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) {
                if(!flag || Response == null) {                 //failure
                    GlobalVars.showErrAlert(ctx);
                    return;
                }
                JSONObject temp = (JSONObject)Response;
                resultData = new JSONArray();
                try {
                    resultData = temp.getJSONArray("teachers");
                    resultData.put(temp.getJSONObject("mine"));
                    reparsingData();
                    refreshAdapter();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setOnListener() {
        // TODO Auto-generated method stub
    }

    private void initData() {
        // TODO Auto-generated method stub


    }

    @SuppressLint("NewApi")
    public void reparsingData () {
        indexTitles = new ArrayList<String>();
        reparsingResultData = new JSONArray();
        String userID = SelfInfoModel.userID;
        for(int i = 0; i < resultData.length(); i ++) {
            try {
                JSONObject item = resultData.getJSONObject(i);

                ///////////
                JSONArray teacherTemp = new JSONArray();
                if(item.has("teacher_group")) {
                    teacherTemp = new JSONArray(item.getJSONArray("teacher_group").toString());
                }
                int j;
                JSONArray members = new JSONArray();
                for(j = 0; j < teacherTemp.length(); j ++) {
                    JSONObject info = teacherTemp.getJSONObject(j);
                    String othereUserID = info.getString("user_id");
                    if(userID.equals(othereUserID)) {
                        continue;
                    }
                    info.put("relation_type", Constant.REL_GRAND);
                    members.put(info);
                }
                ///////////
                JSONArray itemTemp = new JSONArray();
                if(item.has("disciple_group")) {
                    itemTemp = item.getJSONArray("disciple_group");
                }
                // 본인정보얻기 밑 자료에서 삭제
                JSONObject mineTempInfo = new JSONObject();
                for(j = 0; j < itemTemp.length(); j ++) {
                    JSONObject info = itemTemp.getJSONObject(j);
                    String otherUserID = info.getString("user_id");
                    if(userID.equals(otherUserID)) {
                        mineTempInfo = new JSONObject(info.toString());
                        itemTemp.remove(j);
                        break;
                    }
                }
                item.put("disciple_group", itemTemp);
                // 계의 매성원과 본인과의 관계를 해석한다.
                for(j = 0; j < itemTemp.length(); j ++ ) {
                    JSONObject info = itemTemp.getJSONObject(j);
                    String relationType;
                    if(mineTempInfo.length() == 0)          // 나의 계
                    {
                        relationType = Constant.REL_SON;
                        info.put("relation_type", relationType);
                        members.put(info);
                        JSONArray ddGroup = info.getJSONArray("dd_group");
                        for(int k = 0; k < ddGroup.length(); k ++) {
                            JSONObject ddItem = ddGroup.getJSONObject(k);
                            ddItem.put("relation_type", Constant.REL_GRAND_SON);
                            members.put(ddItem);
                        }
                        continue;
                    } else {                                // 스승 계
                        long mineTime = mineTempInfo.getJSONObject("reg_date").getLong("sec");
                        long otherTime = info.getJSONObject("reg_date").getLong("sec");
                        if(mineTime >= otherTime) {
                            relationType = Constant.REL_FATHER_YOUNG;
                        } else {
                            relationType = Constant.REL_FATHER_OLD;
                        }
                    }
                    info.put("relation_type", relationType);
                    members.put(info);
                }

                // 스승계인 경우 스승을 계의 민처음에 추가한다.
                if(mineTempInfo.length() > 0) {
                    JSONObject teacher = new JSONObject();
                    teacher.put("user_id", item.getString("user_id"));
                    teacher.put("user_name", item.getString("user_name"));
                    teacher.put("user_photo", item.getString("user_photo"));
                    teacher.put("relation_type", Constant.REL_FATHER);
                    teacher.put("type", item.getInt("type"));

                    itemTemp = insertObject(itemTemp, teacher, 0);
                    members = insertObject(members, teacher, 0);
                }
                item.put("disciple_group", itemTemp);
                // 자료 갱신
                reparsingResultData.put(members);
                // 문파이름 생성
                String title;
                title = resultData.getJSONObject(i).getString("user_name");
                if(i == resultData.length() - 1)                    // 나의 문파인 경우
                {
                    title = "我的";
                }
                title = title + "师门";
                indexTitles.add(title);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 해당사문에서 증복처리를 진행한다.
        for(int i = 0; i < reparsingResultData.length(); i ++) {
            try {
                JSONArray arrayData = reparsingResultData.getJSONArray(i);
                for(int j = 0; j < arrayData.length(); j ++) {
                    JSONObject dicData = arrayData.getJSONObject(j);
                    String relType = dicData.getString("relation_type");
                    String relID = dicData.getString("user_id");
                    if(relType.equals(Constant.REL_GRAND) || relType.equals(Constant.REL_GRAND_SON)) {
                        Boolean flag = false;
                        int k;
                        for(k = 0; k < arrayData.length(); k ++) {
                            if(j == k)
                                continue;
                            JSONObject otherDic = arrayData.getJSONObject(k);
                            String otherRelID = otherDic.getString("user_id");
                            if(relID.equals(otherRelID)) {
                                flag = true;
                                break;
                            }
                        }
                        if(flag) {                                  // 증복되는 자료이므로 삭제한다.
//                            if(!relType.equals(Constant.REL_FATHER))
//                                arrayData.remove(j);
//                            else
                            arrayData.remove(k);
                            j --;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

//    public JSONArray removeObject (JSONArray data, int position) {
//        ArrayList<JSONObject> list = new ArrayList<>();
//        int len = data.length();
//        if(data != null)
//        {
//            for(int s = 0; s < len; s ++) {
//                try {
//                    list.add(data.getJSONObject(s));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        list.remove(position);
//        JSONArray result = new JSONArray(list);
//        return result;
//    }
    public void refreshAdapter () {
        for(int i = 0; i < reparsingResultData.length(); i ++) {
            tGroupViewAdapter = new TGroupViewAdapter(this.ctx);
            try {
                tGroupViewAdapter.myList = reparsingResultData.getJSONArray(i);
                tGroupViewAdapter.groupTitle = this.indexTitles.get(i).toString();
                if(reparsingResultData.length() - 1  == i) {
                    tGroupViewAdapter.groupHeaderPhoto = SelfInfoModel.userPhoto;
                    tGroupViewAdapter.tgroupID = SelfInfoModel.userID;
                } else {
                    JSONObject jsonObject = tGroupViewAdapter.myList.getJSONObject(0);
                    tGroupViewAdapter.groupHeaderPhoto = jsonObject.getString("user_photo");
                    tGroupViewAdapter.tgroupID = jsonObject.getString("user_id");
                }
                sectionAdapter.addSection(tGroupViewAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        sectionAdapter.notifyDataSetChanged();
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
}
