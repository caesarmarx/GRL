package grl.com.subViews.fragment.prepare;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import grl.com.activities.energy.tGroup.TGroupPrepareActivity;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.httpRequestTask.tGroup.LessonRuleGetTask;
import grl.com.httpRequestTask.tGroup.LessonRuleSetTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_rule extends Fragment implements View.OnClickListener {
    private TGroupPrepareActivity ctx;
    private View layout;

    EditText ruleEdit;
    Button saveBtn;

    String teacherId;
    String rule;
    public Fragment_rule() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (layout == null) {
            ctx = (TGroupPrepareActivity)this.getActivity();
            layout = ctx.getLayoutInflater().inflate(R.layout.fragment_prepare_rule,
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
        ruleEdit = (EditText)layout.findViewById(R.id.edit_rule);
        saveBtn = (Button)layout.findViewById(R.id.btn_save_rule);

    }

    public void setOnListener() {
        saveBtn.setOnClickListener(this);
    }
    public void initializeData () {
        if (teacherId.compareTo(SelfInfoModel.userID) != 0) {
            ruleEdit.setEnabled(false);
            saveBtn.setVisibility(View.INVISIBLE);
        }
        refresh();
    }

    public void refresh() {
        new LessonRuleGetTask(ctx, teacherId, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if (!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(ctx);
                    return;
                }
                try {
                    JSONObject result = (JSONObject) response;
                    rule = GlobalVars.getStringFromJson(result, "lesson_value");
                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }
                ruleEdit.setText(rule);
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_save_rule:
                saveRule();
                break;
        }
    }

    public void saveRule() {
        rule = ruleEdit.getText().toString();
        new LessonRuleSetTask(ctx, teacherId, rule, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if (!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(ctx);
                    return;
                }
                try {
                    JSONObject result = (JSONObject) response;
                    Boolean success = GlobalVars.getBooleanFromJson(result, "result");
                    if (success) {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.save_success), Toast.LENGTH_SHORT)
                                .show();
                        refresh();
                    } else {
                        GlobalVars.showErrAlert(ctx);
                    }
                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }
            }
        });
    }
}