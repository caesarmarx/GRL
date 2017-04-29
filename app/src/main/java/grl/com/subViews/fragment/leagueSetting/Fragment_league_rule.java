package grl.com.subViews.fragment.leagueSetting;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONException;

import grl.com.configuratoin.GlobalVars;
import grl.com.httpRequestTask.tGroup.LeagueRuleTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/11/2016.
 */
public class Fragment_league_rule extends Fragment implements View.OnClickListener{

    // view
    Activity ctx;
    View layout;
    TextView tvRule;

    // task
    LeagueRuleTask leagueRuleTask;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (layout == null) {
            ctx = this.getActivity();
            layout = ctx.getLayoutInflater().inflate(R.layout.fragment_league_rule,
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

    public void initViews () {
        tvRule = (TextView) layout.findViewById(R.id.tvLeagueRule);
    }

    public void initData () {
        leagueRuleTask = new LeagueRuleTask(ctx, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(ctx);
                    return;
                }
                JsonObject result = (JsonObject) Response;
                String rule = result.get("tgroup_rule").getAsString();
                tvRule.setText(rule);
            }
        });
    }

    public void setOnListener () {

    }

    @Override
    public void onClick(View v) {

    }
}
