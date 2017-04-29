package grl.com.subViews.fragment.prepare;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import grl.com.activities.energy.tGroup.TGroupPrepareActivity;
import grl.com.adapters.energy.PrepareUserListAdapter;
import grl.com.adapters.order.UserListAdapter;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.dataModels.UserModel;
import grl.com.httpRequestTask.tGroup.TGroupEstGetTask;
import grl.com.httpRequestTask.tGroup.TGroupEstSetTask;
import grl.com.httpRequestTask.tGroup.TeacherDiscipleTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_enter extends Fragment implements View.OnClickListener {
    private TGroupPrepareActivity ctx;
    private View layout;

    private LinearLayout userTopLayout;

    RecyclerView userListView;
    PrepareUserListAdapter userListAdapter;

    TextView myEstimateView;
    EditText myEstimateEdit;
    TextView otherEstimateView;
    EditText otherEstimateEdit;

    String myEstimate;
    String otherEstimate;

    Button saveBtn;

    String teacherId;
    int selectedIndex = -1;
    public Fragment_enter() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (layout == null) {
            ctx = (TGroupPrepareActivity)this.getActivity();
            layout = ctx.getLayoutInflater().inflate(R.layout.fragment_prepare_enter,
                    null);
            teacherId = ctx.teacherId;
            getViewByID();
            setOnListener();
            initializeData();

        } else {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        return layout;
    }

    public void getViewByID () {
        userTopLayout = (LinearLayout)layout.findViewById(R.id.ll_user_view);
        userListView = (RecyclerView)layout.findViewById(R.id.user_list_view);
        myEstimateView = (TextView)layout.findViewById(R.id.tv_my_estimate);
        myEstimateEdit = (EditText)layout.findViewById(R.id.edit_my_estimate);
        otherEstimateView = (TextView)layout.findViewById(R.id.tv_other_estimate);
        otherEstimateEdit = (EditText)layout.findViewById(R.id.edit_other_estimate);
        saveBtn = (Button)layout.findViewById(R.id.btn_save_estimate);
    }

    public void setOnListener() {
        saveBtn.setOnClickListener(this);

    }
    public void initializeData () {

        userListAdapter = new PrepareUserListAdapter(ctx, this);
        userListView.setItemAnimator(new DefaultItemAnimator());
        userListView.setAdapter(userListAdapter);


        if (teacherId.compareTo(SelfInfoModel.userID) != 0) {
            userListView.setVisibility(View.GONE);
        } else {
            userListView.setVisibility(View.VISIBLE);
        }
        refresh();
    }

    public void userClicked(int index) {
        selectedIndex = index;
        UserModel model = userListAdapter.myList.get(index);
        myEstimateEdit.setText("");
        otherEstimateEdit.setText("");
        new TGroupEstGetTask(ctx, SelfInfoModel.userID, teacherId, teacherId, model.userID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(ctx);
                    return;
                }
                try {
                    JSONObject result = (JSONObject) response;
                    myEstimate = GlobalVars.getStringFromJson(result, "est_value");
                } catch (Exception e) {
                    myEstimate = "";
                }
                myEstimateEdit.setText(myEstimate);
            }
        });
        new TGroupEstGetTask(ctx, SelfInfoModel.userID, teacherId, model.userID, teacherId, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(ctx);
                    return;
                }
                try {
                    JSONObject result = (JSONObject) response;
                    otherEstimate = GlobalVars.getStringFromJson(result, "est_value");
                } catch (Exception e) {
                    otherEstimate = "";
                }
                otherEstimateEdit.setText(otherEstimate);
            }
        });
    }

    public void refresh() {
        if (teacherId.compareTo(SelfInfoModel.userID) == 0) {
            userListAdapter.myList.clear();
            userListAdapter.notifyDataSetChanged();
            new TeacherDiscipleTask(ctx, teacherId, new HttpCallback() {
                @Override
                public void onResponse(Boolean flag, Object Response) throws JSONException {
                    if(!flag || Response == null) {
                        GlobalVars.showErrAlert(ctx);
                        return;
                    }
                    JsonObject result = (JsonObject) Response;
                    String jsonString = result.toString();
                    JSONObject jsonResult = new JSONObject(jsonString);
                    JSONArray users = jsonResult.getJSONArray("disciple_group");
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject object = users.getJSONObject(i);
                        UserModel model = new UserModel();
                        model.parseFromJson(object);
                        userListAdapter.myList.add(model);
                    }
                    userListAdapter.notifyDataSetChanged();;
                }
            });
        } else {
            new TGroupEstGetTask(ctx, SelfInfoModel.userID, teacherId, SelfInfoModel.userID, teacherId, new HttpCallback() {
                @Override
                public void onResponse(Boolean flag, Object response) {
                    if(!flag || response == null) {                 //failure
                        GlobalVars.showErrAlert(ctx);
                        return;
                    }
                    try {
                        JSONObject result = (JSONObject) response;
                        myEstimate = GlobalVars.getStringFromJson(result, "est_value");
                    } catch (Exception e) {
                        myEstimate = "";

                    }
                    myEstimateEdit.setText(myEstimate);
                }
            });
            new TGroupEstGetTask(ctx, SelfInfoModel.userID, teacherId, teacherId, SelfInfoModel.userID, new HttpCallback() {
                @Override
                public void onResponse(Boolean flag, Object response) {
                    if(!flag || response == null) {                 //failure
                        GlobalVars.showErrAlert(ctx);
                        return;
                    }
                    try {
                        JSONObject result = (JSONObject) response;
                        otherEstimate = GlobalVars.getStringFromJson(result, "est_value");
                    } catch (Exception e) {
                        otherEstimate = "";
                    }
                    otherEstimateEdit.setText(otherEstimate);
                }
            });
        }
    }

    public void saveAction() {
        myEstimate = myEstimateEdit.getText().toString();
        if (teacherId.compareTo(SelfInfoModel.userID) == 0) {
            if (selectedIndex < 0)
                return;
            UserModel model = userListAdapter.myList.get(selectedIndex);
            new TGroupEstSetTask(ctx, SelfInfoModel.userID, teacherId, teacherId, model.userID, myEstimate, new HttpCallback() {
                @Override
                public void onResponse(Boolean flag, Object response) {
                    if(!flag || response == null) {                 //failure
                        GlobalVars.showErrAlert(ctx);
                        return;
                    }
                    try {
                        JSONObject result = (JSONObject) response;
                        Boolean success = GlobalVars.getBooleanFromJson(result, "result");
                        if (success) {
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.save_success), Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            GlobalVars.showErrAlert(ctx);
                        }

                    } catch (Exception e) {

                    }
                }
            });
        } else {
            new TGroupEstSetTask(ctx, SelfInfoModel.userID, teacherId, SelfInfoModel.userID, teacherId, myEstimate, new HttpCallback() {
                @Override
                public void onResponse(Boolean flag, Object response) {
                    if(!flag || response == null) {                 //failure
                        GlobalVars.showErrAlert(ctx);
                        return;
                    }
                    try {
                        JSONObject result = (JSONObject) response;
                        Boolean success = GlobalVars.getBooleanFromJson(result, "result");
                        if (success) {
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.save_success), Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            GlobalVars.showErrAlert(ctx);
                        }

                    } catch (Exception e) {

                    }
                }
            });
        }
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_save_estimate:
                saveAction();
        }
    }
}
