package grl.com.subViews.fragment.donate;

import android.app.Activity;
import android.app.Service;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.SoftKeyboard;
import grl.com.httpRequestTask.discovery.donate.DonateRequestTask;
import grl.com.httpRequestTask.discovery.donate.PhoneNumberVerifyTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/10/2016.
 */
public class Fragment_donate_request extends Fragment implements View.OnClickListener{
    Activity ctx;
    View layout;

    // views
    EditText etRequest;
    EditText firstPhone;
    EditText secondPhone;
    EditText thirdPhone;
    EditText fourthPhone;
    EditText fifthPhone;
    ImageView firstCheck;
    ImageView secondCheck;
    ImageView thirdCheck;
    ImageView fourthCheck;
    ImageView fifthCheck;
    Button btnReq;
    LinearLayout mainLayout;

    // Tasks
    PhoneNumberVerifyTask phoneNumberVerifyTask;
    DonateRequestTask donateRequestTask;

    // Response
    JSONArray phoneVierfiyResult;

    // values
    SoftKeyboard softKeyboard;
    List<EditText> etPhoneNumbers;
    List<ImageView> imgPhoneChecks;
    String donatePhoneID;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (layout == null) {
            ctx = this.getActivity();
            layout = ctx.getLayoutInflater().inflate(R.layout.fragment_donate_request,
                    null);
            initViews();
            initData();
            setOnListener();
        } else {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        return layout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        softKeyboard.unRegisterSoftKeyboardCallback();
    }

    private void initViews() {
        // TODO Auto-generated method stub
        etRequest = (EditText) layout.findViewById(R.id.etDonateReq);
        firstPhone = (EditText) layout.findViewById(R.id.etFifth);
        secondPhone = (EditText) layout.findViewById(R.id.etSecond);
        thirdPhone = (EditText) layout.findViewById(R.id.etThird);
        fourthPhone = (EditText) layout.findViewById(R.id.etFourth);
        fifthPhone = (EditText) layout.findViewById(R.id.etFifth);
        firstCheck = (ImageView) layout.findViewById(R.id.imgFirstCheck);
        secondCheck = (ImageView) layout.findViewById(R.id.imgSecondCheck);
        thirdCheck = (ImageView) layout.findViewById(R.id.imgThirdCheck);
        fourthCheck = (ImageView) layout.findViewById(R.id.imgFourthCheck);
        fifthCheck = (ImageView) layout.findViewById(R.id.imgFifthCheck);
        btnReq = (Button) layout.findViewById(R.id.btnRequest);
        mainLayout = (LinearLayout) layout.findViewById(R.id.main_layout);
    }

    private void setOnListener() {
        // TODO Auto-generated method stub
        layout.findViewById(R.id.btnRequest).setOnClickListener(this);

        // keyboard event
        InputMethodManager im = (InputMethodManager) ctx.getSystemService(Service.INPUT_METHOD_SERVICE);
        softKeyboard = new SoftKeyboard(mainLayout, im);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged()
        {

            @Override
            public void onSoftKeyboardHide()
            {
                // Code here
                checkPhoneNumbers();
            }

            @Override
            public void onSoftKeyboardShow()
            {
                // Code here
            }
        });
    }


    private void initData() {
        // TODO Auto-generated method stub
        etPhoneNumbers = Arrays.asList(firstPhone, secondPhone, thirdPhone, fourthPhone, fifthPhone);
        imgPhoneChecks = Arrays.asList(firstCheck, secondCheck, thirdCheck, fourthCheck, fifthCheck);
        for(int i = 0; i < etPhoneNumbers.size(); i ++ ){
            EditText etPhone = etPhoneNumbers.get(i);
            etPhone.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRequest:                   // 신청단추를 눌렀을때의 처리 진행
                try {
                    onRequestAction();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void checkPhoneNumbers () {
        JSONObject params = new JSONObject();
        JSONArray phoneNumbers = new JSONArray();
        for(int i = 0; i < etPhoneNumbers.size(); i ++) {
            EditText etPhone = etPhoneNumbers.get(i);
            phoneNumbers.put(etPhone.getText().toString());
        }
        try {
            params.put("session_id", SelfInfoModel.sessionID);
            params.put("user_id", SelfInfoModel.userID);
            params.put("phone_number", phoneNumbers);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        phoneNumberVerifyTask = new PhoneNumberVerifyTask(ctx, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(ctx);
                    return;
                }
                phoneVierfiyResult = (JSONArray) Response;
                verifyPhoneView();
            }
        });
    }

    public void verifyPhoneView () throws JSONException {
        btnReq.setEnabled(true);
        for(int i = 0; i < phoneVierfiyResult.length(); i ++) {
            JSONObject jsonObject = phoneVierfiyResult.getJSONObject(i);
            String userId = jsonObject.getString("user_id");
            String userPhone = jsonObject.getString("user_phone");
            EditText etPhone = etPhoneNumbers.get(i);
            ImageView imgCheck = imgPhoneChecks.get(i);
            etPhone.setText(userPhone);
            if(!userId.equals("")) {
                imgCheck.setVisibility(View.VISIBLE);
            } else {
                imgCheck.setVisibility(View.INVISIBLE);
            }
            if(userPhone.equals(""))
                btnReq.setEnabled(true);
        }
    }

    public void onRequestAction () throws JSONException {
        JSONObject params = new JSONObject();
        params.put("session_id", SelfInfoModel.sessionID);
        params.put("user_id", SelfInfoModel.userID);
        params.put("req_content", etRequest.getText().toString());
        params.put("phone_numbers", phoneVierfiyResult);
        donateRequestTask = new DonateRequestTask(ctx, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(ctx);
                    return;
                }
                JSONObject result = (JSONObject) Response;
                donatePhoneID = result.getString("donatephone_id");
                Toast.makeText(ctx, ctx.getString(R.string.donate_req_toast), Toast.LENGTH_LONG)
                        .show();
            }
        });
    }
}
