package grl.com.activities.order;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grl.com.adapters.energy.NewEnergyContactsAdapter;
import grl.com.adapters.order.ContactsAdapter;
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
public class ContactsSelectActivity extends Activity implements View.OnClickListener, ContactsAdapter.OnItemOnClickListener{

    private RecyclerView recyclerView;
    private ContactsAdapter recyclerAdaper;

    private ContactsModel contactsModel;

    String numberArr = "";
    EditText numberEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_select);

        Intent intent = getIntent();
        numberArr = intent.getStringExtra(Constant.PHONE_CONTACT);
        if (numberArr == null)
            numberArr = "";


        this.getViewByID();
        this.initNavBar();
        this.initializeData();
        this.setOnListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcast();
        initializeData();
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
        numberEdit = (EditText) findViewById(R.id.et_phone_number);
    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText("");
        ((TextView)findViewById(R.id.txt_right)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_right)).setText(getString(R.string.save));
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.txt_right).setOnClickListener(this);
    }

    public void initializeData() {
        // set up adapters
        numberEdit.setText(numberArr);
        numberEdit.setSelection(numberEdit.getText().length());

        recyclerAdaper = new ContactsAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdaper);
        recyclerAdaper.setItemOnClickListener(this);

        // init data
        GlobalVars.showWaitDialog(this);
        contactsModel = new ContactsModel(this);
    }


    private void refreshData() {
        recyclerAdaper.myList = contactsModel.contactsList;
        recyclerAdaper.notifyDataSetChanged();
    }

    private void selectFinish() {
        numberArr = numberEdit.getText().toString();
        Intent intent = new Intent();
        intent.putExtra(Constant.PHONE_CONTACT, numberArr);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.txt_right:
                selectFinish();
                break;
        }
    }

    @Override
    public void onItemClick(int position) {
        ContactsModel.Contacts contact = contactsModel.contactsList.get(position);
        if (contact.contactPhone.size() == 0)
            return;
        numberArr = numberEdit.getText().toString();
        String phone = contact.contactPhone.get(0);
        if (numberArr.contains(phone)) {
            numberArr.replace(phone + ";", "");
            numberArr.replace(phone, "");
        } else {
            if (numberArr.isEmpty())
                numberArr = contact.contactPhone.get(0) + ";";
            else
                numberArr = numberArr + contact.contactPhone.get(0) + ";";
        }
        numberEdit.setText(numberArr);
        numberEdit.setSelection(numberEdit.getText().length());
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
        refreshData();
        GlobalVars.hideWaitDialog();
        }
    };
}
