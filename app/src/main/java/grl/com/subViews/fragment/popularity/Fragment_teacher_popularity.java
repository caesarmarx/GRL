package grl.com.subViews.fragment.popularity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import grl.com.adapters.popularity.PopularityAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.dataModels.PopularityModel;
import grl.com.httpRequestTask.popularity.PopularityEstimateTask;
import grl.com.httpRequestTask.popularity.PopularityPraiseTask;
import grl.com.httpRequestTask.popularity.TeacherPopularityTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_teacher_popularity extends Fragment implements PopularityAdapter.OnItemOnClickListener {

    private Activity ctx;
    private View layout;
    private WindowManager mWindowManager;

    private ImageView userPhotoView;
    private RecyclerView popularListView;
    private PopularityAdapter popularAdapter;

    private RadioGroup menuGroup;

    private int menuIndex = 0;

    public Fragment_teacher_popularity() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (layout == null) {
            ctx = this.getActivity();
            layout = ctx.getLayoutInflater().inflate(R.layout.fragment_teacher_popularity,
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
        userPhotoView = (ImageView)layout.findViewById(R.id.img_user_photo);
        popularListView = (RecyclerView)layout.findViewById(R.id.popularity_list);
        menuGroup = (RadioGroup)layout.findViewById(R.id.rg_factor_menu);
    }

    public void setOnListener() {

    }

    public void initMenuView() {
        for (int i = 0; i < GlobalVars.factorList.size(); i++) {
            RadioButton button = (RadioButton)ctx.getLayoutInflater().inflate(R.layout.layout_tab_btn, null);
            button.setId(i);
            if (i == 0)
                button.setChecked(true);
            button.setTextSize(15);
            if (i == 0) {
                button.setText(GlobalVars.factorList.get(i).estValue);
            } else {
                char prefix = GlobalVars.factorList.get(i).estName.charAt(0);
                button.setText(prefix + GlobalVars.factorList.get(i).estValue);
            }

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuClicked(v.getId());
                }
            });
            menuGroup.addView(button);
        }
    }

    public void initializeData () {
        popularAdapter = new PopularityAdapter(ctx);
        popularAdapter.setItemOnClickListener(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx);
        popularListView.setLayoutManager(mLayoutManager);
        popularListView.setItemAnimator(new DefaultItemAnimator());
        popularListView.setAdapter(popularAdapter);
    }

    public void menuClicked(int index) {
        menuIndex = index;
        refresh();
    }

    public void refresh() {
        if (menuIndex >= GlobalVars.factorList.size())
            return;
        String estName = GlobalVars.factorList.get(menuIndex).estName;
        popularAdapter.myList.clear();
        popularAdapter.notifyDataSetChanged();
        new TeacherPopularityTask(ctx, estName, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(ctx);
                    return;
                }
                try {
                    JSONArray result = (JSONArray) response;
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    for (int i =0; i < result.length(); i++) {
                        JSONObject object = result.getJSONObject(i);
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

    @Override
    public void onItemClick(int position, int resId) {
        switch (resId) {
            case R.id.fl_praise_layout:
                praiseAction(position);
                break;
            case R.id.fl_estimate_layout:
                estimateAction(position);
                break;
        }
    }

    private void praiseAction(final int position) {

        PopularityModel model = popularAdapter.myList.get(position);

        boolean bPraised = false;
        for (int i = 0; i < model.praiseList.length(); i++) {
            try {
                JSONObject object = model.praiseList.getJSONObject(i);
                if (GlobalVars.getStringFromJson(object, "user_id").compareTo(SelfInfoModel.userID) == 0) {
                    bPraised = true;
                    break;
                }
            } catch (Exception ex) {

            }
        }

        if (bPraised == true) {
            Toast.makeText(ctx, "你已赞了", Toast.LENGTH_SHORT);
            return;
        }

        if (model.estName.compareTo(Constant.TEACHER_FACTOR) == 0) {
            String[] praiseArray = {"好", "中", "差"};

            final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setItems(praiseArray, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            sendPraiseRequest(position, Constant.GOOD_PRAISE);
                            dialog.dismiss();
                            break;
                        case 1:
                            sendPraiseRequest(position, Constant.NORMAL_PRAISE);
                            dialog.dismiss();
                            break;
                        case 2:
                            sendPraiseRequest(position, Constant.BAD_PRAISE);
                            dialog.dismiss();
                            break;
                    }
                }
            }).setCancelable(true);
            AlertDialog dialog = builder.create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.show();
        } else {
            sendPraiseRequest(position, 0);
        }
    }

    private void sendPraiseRequest(final int position, int value) {
        PopularityModel model = popularAdapter.myList.get(position);



        new PopularityPraiseTask(ctx, SelfInfoModel.userID, model.twitterId, String.valueOf(value), new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(ctx);
                    return;
                }
                try {
                    JSONObject result = (JSONObject) response;
                    PopularityModel model = new PopularityModel();
                    model.parseFromJson(result);
                    popularAdapter.myList.set(position, model);
//                    Toast.makeText(ctx, "", Toast.LENGTH_SHORT);
                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }
                refresh();
            }
        });
    }

    private void estimateAction(final int position) {
        PopularityModel model = popularAdapter.myList.get(position);
        boolean bEstimated = false;
        for (int i = 0; i < model.estimateList.length(); i++) {
            try {
                JSONObject object = model.estimateList.getJSONObject(i);
                if (GlobalVars.getStringFromJson(object, "user_id").compareTo(SelfInfoModel.userID) == 0) {
                    bEstimated = true;
                    break;
                }
            } catch (Exception ex) {

            }
        }

        if (bEstimated == true) {
            Toast.makeText(ctx, "你已评论了", Toast.LENGTH_SHORT);
            return;
        }

        new PopularityEstimateTask(ctx, SelfInfoModel.userID, model.twitterId, "", "", new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(ctx);
                    return;
                }
                try {
                    JSONObject result = (JSONObject) response;
                    PopularityModel model = new PopularityModel();
                    model.parseFromJson(result);
                    popularAdapter.myList.set(position, model);
                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }
                refresh();
            }
        });

    }

}
