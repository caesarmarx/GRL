package grl.com.subViews.fragment.popularity;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import grl.com.activities.discovery.popularity.PopularityMainActivity;
import grl.com.adapters.popularity.MyPopularityAdapter;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.dataModels.PopularityModel;
import grl.com.httpRequestTask.popularity.MyPopularityTask;
import grl.com.httpRequestTask.popularity.TeacherPopularityTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_my_popularity extends Fragment {

    private Activity ctx;
    private View layout;
    private WindowManager mWindowManager;

    int menuIndex = 1;

    int ability;

    String userId = "";

    private RadioGroup menuGroup;

    private RecyclerView popularListView;
    private MyPopularityAdapter popularAdapter;
    private TextView abilityView;
    private TextView commentView;
    public Fragment_my_popularity() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (layout == null) {
            ctx = this.getActivity();
            layout = ctx.getLayoutInflater().inflate(R.layout.fragment_my_popularity,
                    null);
            mWindowManager = (WindowManager) ctx
                    .getSystemService(Context.WINDOW_SERVICE);
            getViewByID();
            setOnListener();
            initMenuView();
            initializeData();
        } else {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void getViewByID () {
        popularListView = (RecyclerView)layout.findViewById(R.id.my_popularity_list);
        menuGroup = (RadioGroup)layout.findViewById(R.id.rg_factor_menu);
        abilityView = (TextView)layout.findViewById(R.id.tv_ability);
        commentView = (TextView)layout.findViewById(R.id.tv_est_comment);
    }

    public void setOnListener() {

    }

    public void initializeData () {
        popularAdapter = new MyPopularityAdapter(ctx);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx);
        popularListView.setLayoutManager(mLayoutManager);
        popularListView.setItemAnimator(new DefaultItemAnimator());
        popularListView.setAdapter(popularAdapter);
        userId = SelfInfoModel.userID;
    }

    public void initMenuView() {
        for (int i = 1; i < GlobalVars.factorList.size(); i++) {
            RadioButton button = (RadioButton)ctx.getLayoutInflater().inflate(R.layout.layout_tab_btn, null);
            if (i == 1)
                button.setChecked(true);
            char prefix = GlobalVars.factorList.get(i).estName.charAt(0);
            button.setId(i);
            button.setText(prefix + GlobalVars.factorList.get(i).estValue);
            button.setTextSize(15);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuClicked(v.getId());
                }
            });
            menuGroup.addView(button);
        }
    }

    public void menuClicked(int index) {
        menuIndex = index;
        refresh();
    }

    public void refresh() {
        if (menuIndex >= GlobalVars.factorList.size())
            return;
        String estName = GlobalVars.factorList.get(menuIndex).estName;
        commentView.setText(GlobalVars.factorList.get(menuIndex).description);
        // Set Est Name of Parent
        PopularityMainActivity parent = (PopularityMainActivity)getActivity();
        parent.estName = estName;

        popularAdapter.setEstName(estName);
        popularAdapter.myList.clear();
        popularAdapter.notifyDataSetChanged();
        new MyPopularityTask(ctx, userId, estName, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(ctx);
                    return;
                }
                try {
                    JSONObject result = (JSONObject) response;

                    ability = GlobalVars.getIntFromJson(result, "ability");
                    abilityView.setText(String.format("%d", ability));
                    JSONArray list = result.getJSONArray("twitters");
                    for (int i =0; i < list.length(); i++) {
                        JSONObject object = list.getJSONObject(i);
                        PopularityModel model = new PopularityModel();
                        model.parseFromJson(object);
                        popularAdapter.myList.add(model);
                    }
                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }
                popularAdapter.notifyDataSetChanged();
            }
        });
    }
}
