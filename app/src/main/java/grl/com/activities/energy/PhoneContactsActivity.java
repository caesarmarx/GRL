package grl.com.activities.energy;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grl.com.adapters.energy.NewEnergyContactsAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.ContactsModel;
import grl.com.httpRequestTask.newEnergy.EnergyTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */
public class PhoneContactsActivity extends Activity implements View.OnClickListener{

    private RecyclerView recyclerView;
    private NewEnergyContactsAdapter recyclerAdaper;

    private EnergyTask energyTask;

    private JSONArray resultData;
    private ContactsModel contactsModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonecontacts);

        this.getViewByID();
        this.initNavBar();
        this.initializeData();
        this.setOnListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcast();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterBroadcast();
    }

    @Override
    protected void onDestroy() {
        if(contactsModel.thread != null)
        {
            contactsModel.isRunning = false;
            try {
                contactsModel.thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        GlobalVars.hideWaitDialog();
        super.onDestroy();
    }

    public void getViewByID () {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.new_energy_title));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.my_contacts_title));
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
    }

    public void initializeData() {
        // set up adapters
        recyclerAdaper = new NewEnergyContactsAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdaper);

        // init data
        GlobalVars.showWaitDialog(this);
        contactsModel = new ContactsModel(PhoneContactsActivity.this);
    }


    public void getDataFromServer () {
        // get data from
        JSONObject params = new JSONObject();
        try {
            params.put("session_id", SelfInfoModel.sessionID);
            params.put("user_id", SelfInfoModel.userID);
            params.put("con_type", 0);
            params.put("type", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        energyTask = new EnergyTask(this, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) {
                if(!flag || Response == null) {                 //failure
                    GlobalVars.showErrAlert(PhoneContactsActivity.this);
                    return;
                }
                try {
                    JSONArray jsonArray = new JSONArray();
                    JSONArray results = (JSONArray)Response;
                    for(int i = 0; i < results.length(); i ++) {
                        JSONObject data = results.getJSONObject(i);
                        String user_id = data.getString("user_id");
                        JSONObject temp = new JSONObject();
                        if(user_id.equals("-1")) {
                            temp.put("user_phone", "-1");
                            temp.put("user_id", "");
                            temp.put("user_photo", "");
                            temp.put("user_name", "");
                            temp.put("energy_tyep", -1);
                            temp.put("request_teacher", -1);
                            temp.put("request_disciple", -1);
                            temp.put("request_grl", -1);
                            temp.put("fgroup_id", "");
                            temp.put("fgroup_name", "");
                            temp.put("energy_quality", 0);
                            temp.put("request_flags", 1);
                        } else {
                            temp.put("user_phone", data.getString("user_phone"));
                            temp.put("user_id", data.getString("user_id"));
                            temp.put("user_photo", data.getString("user_photo"));
                            temp.put("user_name", data.getString("user_name"));
                            temp.put("request_teacher", data.getInt("request_teacher"));
                            temp.put("request_disciple", data.getInt("request_disciple"));
                            temp.put("request_grl", data.getInt("request_grl"));
                            temp.put("fgroup_id", data.getString("fgroup_id"));
                            temp.put("fgroup_name", data.getString("fgroup_name"));
                            temp.put("energy_quality", data.getInt("energy_quality"));
                            temp.put("request_flags", data.getInt("request_type"));
                            int energy_type = data.getInt("energy_type");
                            switch (energy_type) {
                                case 0:
                                    energy_type = Constant.ENERGY_ACCEPT;
                                    break;
                                case 1:
                                    energy_type = Constant.ENERGY_ACCEPTED;
                                    break;
                                case 2:
                                    energy_type = Constant.ENERGY_REQUIRED;
                                    break;
                            }
                            temp.put("energy_type", energy_type);
                        }

                        jsonArray.put(temp);
                    }
                    reparsingData(jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
        }
    }

    public void reparsingData(JSONArray jsonArray) {
        resultData = new JSONArray();
        int cnt = contactsModel.contactsList.size();
        for(int i = 0; i < cnt; i ++) {
            ContactsModel.Contacts contactItem = contactsModel.contactsList.get(i);
            String otherPhone = "";
            if(contactItem.contactPhone.size() > 0)
                otherPhone = contactItem.contactPhone.get(0);
            Integer requestType = Constant.ENERGY_REQUIR;
            Integer request_teacher = -1;
            Integer request_disciple = -1;
            Integer request_grl = -1;
            String group_id = "";
            String group_name = "";
            String user_id = "";
            int contactPhoneCnt = contactItem.contactPhone.size();
            for(int k = 0; k < contactItem.contactPhone.size(); k ++) {
                String contactPhone = contactItem.contactPhone.get(k);
                Boolean isExist = false;
                for(int j = 0; j < jsonArray.length(); j ++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(j);
                        if (jsonObject.getString("user_phone").equals(contactPhone)) {
                            isExist = true;
                            requestType = jsonObject.getInt("energy_type");
                            request_teacher = jsonObject.getInt("request_teacher");
                            request_disciple = jsonObject.getInt("request_disciple");
                            request_grl = jsonObject.getInt("request_grl");
                            group_id = jsonObject.getString("fgroup_id");
                            group_name = jsonObject.getString("fgroup_name");
                            otherPhone = jsonObject.getString("user_phone");
                            user_id = jsonObject.getString("user_id");
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(isExist)
                        break;
                }
            }
              if(user_id.equals("")) {
                user_id = otherPhone;
            }
            JSONObject jsonDic = new JSONObject();
            try {
                jsonDic.put("user_name", contactItem.contactName);
                jsonDic.put("user_phone", otherPhone);
                jsonDic.put("energy_type", requestType);
                jsonDic.put("user_photo", contactItem.contactPhoto);
                jsonDic.put("request_teacher", request_teacher);
                jsonDic.put("request_disciple", request_disciple);
                jsonDic.put("request_grl", request_grl);
                jsonDic.put("fgroup_id", group_id);
                jsonDic.put("fgroup_name", group_name);
                jsonDic.put("user_id", user_id);

                resultData.put(jsonDic);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        // refresh recyclerView
        recyclerAdaper.myList = resultData;
        recyclerAdaper.notifyDataSetChanged();
    }

    // recieve broadcast
    // broadcast receiver
    public void registerBroadcast() {
        // 새로운 능량요청이 들어온 경우
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mNewenergyContact, new IntentFilter(Constant.NEW_ENERGY_CONTACT));
    }
    public void unregisterBroadcast () {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mNewenergyContact, new IntentFilter(Constant.NEW_ENERGY_CONTACT));
    }
    private BroadcastReceiver mNewenergyContact = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            GlobalVars.hideWaitDialog();
            getDataFromServer();
        }
    };
}
