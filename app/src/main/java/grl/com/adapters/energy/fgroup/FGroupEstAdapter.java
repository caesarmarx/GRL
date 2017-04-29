package grl.com.adapters.energy.fgroup;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grl.com.activities.energy.fGroup.FGroupResActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.friendGroup.FGroupGetInfoTask;
import grl.com.httpRequestTask.friendGroup.FriendEstTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/23/2016.
 */
public class FGroupEstAdapter extends RecyclerView.Adapter<FGroupEstAdapter.MyViewHolder>{

    Activity context;
    JSONArray myList;
    JSONArray estList;
    Boolean estEnable;
    String fGroupID;

    // tasks
    FriendEstTask friendEstTask;
    public boolean[] items_check;

    public FGroupEstAdapter(Activity context) {
        this.context = context;
        this.myList = new JSONArray();
        this.estList = new JSONArray();
    }

    public void notifyData (JSONArray jsonArray, Boolean estEnable, String fGroupID) {
        this.myList = jsonArray;
        this.estEnable = estEnable;
        this.fGroupID = fGroupID;
        notifyDataSetChanged();
    }

    public void setEstList (JSONArray jsonArray) {
        this.estList = jsonArray;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_fgroup_est, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if(this.myList.length() == 0){
            holder.ShowEmptyText(true);
            return;
        } else {
            holder.ShowEmptyText(false);
        }
        try {
            JSONObject jsonObject = this.myList.getJSONObject(position);
            holder.tvUserName.setText(jsonObject.getString("user_name"));
            GlobalVars.loadImage(holder.imgUserPhoto, jsonObject.getString("user_photo"));
            // 평가가능한가를 검사한다.
            Integer estType = jsonObject.getInt("est_type");
            holder.enableEst(estType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // set listeners
        holder.tvEstUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 올리평가를 진행하였을 때
                sendFriendEstRequest(position, 0);
            }
        });
        holder.tvEstDown.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // 내림평가를 진행하였을 때
                sendFriendEstRequest(position, 1);
            }
        });
        holder.tvEstList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 평가지표목록 보여주기 진행
                showEstIndexList(position);
            }
        });
        holder.tvUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자 련계정보를 보여주기 진행
                showUserResources(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        int cnt = this.myList.length();
        if(cnt == 0)
            cnt = 1;
        return cnt;
    }

    public void showUserResources (int position) {
        try {
            final JSONObject obj = this.myList.getJSONObject(position);
            String userID = obj.getString("user_id");
            new FGroupGetInfoTask(context, userID, fGroupID, new HttpCallback() {
                @Override
                public void onResponse(Boolean flag, Object Response) throws JSONException {
                    if(!flag || Response == null) {
                        GlobalVars.showErrAlert(context);
                        return;
                    }
                    JSONObject result = (JSONObject) Response;
                    JSONObject userInfo = result.getJSONObject("user_info");
                    String title = obj.getString("user_name") + "的资源";
                    Utils.start_Activity(context, FGroupResActivity.class,
                            new BasicNameValuePair("data", userInfo.toString()),
                            new BasicNameValuePair(Constant.TITLE_ID, title),
                            new BasicNameValuePair(Constant.BACK_TITLE_ID, context.getString(R.string.fgroup_est_title)));
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void showEstIndexList (final Integer position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final String[] items_value = new String[estList.length()];                      // 평가지표목록
        for(int i = 0; i < estList.length(); i ++) {
            try {
                JSONObject estItem = estList.getJSONObject(i);
                items_value[i] = estItem.getString("est_value");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        items_check = new boolean[estList.length()];
        try {
            final JSONObject infoTemp = myList.getJSONObject(position);
            final JSONArray values = infoTemp.getJSONArray("est_content");
            for(int j = 0; j < items_check.length; j ++) {
                JSONObject estJsonObj = estList.getJSONObject(j);
                String est_id = estJsonObj.getString("est_id");
                for (int i = 0; i < values.length(); i++) {
                    if (values.getString(i).equals(est_id)) {
                        items_check[j] = true;
                    }
                }
            }
            builder.setMultiChoiceItems(items_value, items_check, new DialogInterface.OnMultiChoiceClickListener()
            {

                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    // 항목을 선택하였을 때의 처리 진행
                    try {
                        if(infoTemp.getInt("est_type") != -1) {               // 이미 평가를 진행하였을 때의 처리 진행
                            ((AlertDialog) dialog).getListView().setItemChecked(which,!isChecked);
                            return;
                        } else {
                            items_check[which] = isChecked;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

            builder.setCancelable(false);
            builder.setTitle(context.getString(R.string.fgroup_est_indes_list_title));
            builder.setPositiveButton(context.getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 확인단추를 눌렀을 때의 처리 진행
                    try{
                        JSONArray selectedIndexs = new JSONArray();
                        for(int i = 0; i < items_check.length; i ++) {
                            if(items_check[i]) {
                                JSONObject indexItem = estList.getJSONObject(i);
                                String est_id = indexItem.getString("est_id");
                                selectedIndexs.put(est_id);
                            }
                        }
                        infoTemp.put("est_content", selectedIndexs);                // 확인한 경우 자료갱신진행
                        myList.remove(position);
                        myList = GlobalVars.insertObject(myList, infoTemp, position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


        builder.setNegativeButton(context.getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 취소단추를 눌렀을 때의 처리 진행

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 친구에 대한 평가를 진행하기
    public void sendFriendEstRequest(final Integer position, final Integer estType) {
        if(!estEnable) {
            showEstDisableAlert();
            return;
        }
        JSONObject params = new JSONObject();
        try {
            JSONObject jsonObject = myList.getJSONObject(position);
            params.put("session_id", SelfInfoModel.sessionID);
            params.put("userfrom_id", SelfInfoModel.userID);
            params.put("userto_id", jsonObject.getString("user_id"));
            params.put("group_id", fGroupID);
            params.put("est_content", jsonObject.getString("est_content"));
            params.put("est_type", estType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(params.length() == 0)
            return;
        friendEstTask = new FriendEstTask(context, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(context);
                    return;
                }
                JSONObject result = (JSONObject) Response;
                Integer success = result.getInt("est_result");
                if(success < 1) {
                    GlobalVars.showWaitDialog(context);
                    return;
                }
                // refresh data
                JSONObject old = myList.getJSONObject(position);
                old.put("est_type", estType);
                notifyDataSetChanged();
            }
        });
    }

    // 친구에 대한 평가를 할수없다는 통보를 현시
    public void showEstDisableAlert () {
            GlobalVars.showCommonAlertDialog(context, context.getString(R.string.fgroup_est_disable), "");
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgUserPhoto;
        private final TextView tvUserName;
        private final TextView tvEstUp;
        private final TextView tvEstDown;
        private final TextView tvEstList;
        private final LinearLayout layoutUserInfo;
        private final TextView tvUserInfo;
        private final View viewBorder;
        private final TextView tvEmpty;
        public MyViewHolder(View itemView) {
            super(itemView);

            imgUserPhoto = (ImageView) itemView.findViewById(R.id.user_photo);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
            tvEstUp = (TextView) itemView.findViewById(R.id.tv_up);
            tvEstDown = (TextView) itemView.findViewById(R.id.tv_down);
            tvEstList = (TextView) itemView.findViewById(R.id.tv_est_list);
            layoutUserInfo = (LinearLayout) itemView.findViewById(R.id.layout_user_info);
            tvUserInfo = (TextView) itemView.findViewById(R.id.tv_user_info);
            viewBorder = (View) itemView.findViewById(R.id.layout_border);
            tvEmpty = (TextView) itemView.findViewById(R.id.tvEmpty);
        }

        public void ShowEmptyText(Boolean flag) {
            if(flag){
                tvEmpty.setVisibility(View.VISIBLE);
                viewBorder.setVisibility(View.INVISIBLE);
                layoutUserInfo.setVisibility(View.GONE);
                tvUserInfo.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                viewBorder.setVisibility(View.VISIBLE);
                layoutUserInfo.setVisibility(View.VISIBLE);
                tvUserInfo.setVisibility(View.VISIBLE);
            }
        }

        public void enableEst(Integer estType) {
            if(estType != -1) {
                tvEstUp.setClickable(false);
                tvEstDown.setClickable(false);
                tvEstUp.setTextColor(context.getResources().getColor(R.color.light_gray_color));
                tvEstDown.setTextColor(context.getResources().getColor(R.color.light_gray_color));
            } else {
                tvEstUp.setClickable(true);
                tvEstDown.setClickable(true);
                tvEstUp.setTextColor(context.getResources().getColor(android.R.color.black));
                tvEstDown.setTextColor(context.getResources().getColor(android.R.color.black));
            }
        }
    }
}
