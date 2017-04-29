package grl.com.adapters.donate;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grl.com.activities.common.ContentShowActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.discovery.donate.DonateConfirmTask;
import grl.com.httpRequestTask.discovery.donate.DonateHelpingTask;
import grl.com.network.HttpCallback;
import grl.com.subViews.fragment.donate.Fragment_donate_help;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/10/2016.
 */
public class DonateHelpAdapter extends RecyclerView.Adapter<DonateHelpAdapter.MyViewHolder>{
    Activity context;
    public JSONArray myList;
    Integer adatperType;

    // tasks
    DonateConfirmTask donateConfirmTask;
    DonateHelpingTask donateHelpingTask;

    public DonateHelpAdapter(Activity context, Integer type) {
        this.context = context;
        this.adatperType = type;
        this.myList = new JSONArray();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_donate_help_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
            JSONObject jsonObject = this.myList.getJSONObject(position);
            String content = "";
            switch (this.adatperType) {
                case 0:                                 // 요청이 들어온 경우(확인)
                    holder.btnConfirm.setText(context.getString(R.string.donate_request_confirm));
                    content = jsonObject.getString("user_name") + ": " + jsonObject.getString("req_content");
                    holder.tvContent.setText(content);
                    break;
                case 1:                                 // 기부요청진행(청구)
                    holder.btnConfirm.setText(context.getString(R.string.donate_request_claim));
                    content = jsonObject.getString("user_name") + ": " + jsonObject.getString("req_content");
                    holder.tvContent.setText(content);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.tvContent.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 요청내용을 누르기하는 경우 상세보기를 진행한다.
                String title = "";
                String msg = "";
                try {
                    JSONObject jsonObject = myList.getJSONObject(position);
                    switch (adatperType) {
                        case 0:                             // 요청이 들어온 경우(확인)
                            title = jsonObject.getString("user_name");
                            msg = jsonObject.getString("req_content");
                            break;
                        case 1:                             // 기부요청진행(청구)
                            title = jsonObject.getString("user_name");
                            msg = jsonObject.getString("req_content");
                            break;
                    }
                    // 상세한 내용을 보기한다.
                    Utils.start_Activity(context, ContentShowActivity.class,
                            new BasicNameValuePair(Constant.BACK_TITLE_ID, context.getString(R.string.donate_title)),
                            new BasicNameValuePair(Constant.TITLE_ID, title),
                            new BasicNameValuePair(Constant.CONTENT_VIEW_KEY, msg));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.btnConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // 단추를 누르기하는 경우 해당한 처리를 진행한다.
                try {
                    JSONObject jsonObject = myList.getJSONObject(position);
                    switch (adatperType) {
                        case 0:                             // 확인 단추
                            onConfirmDonate(SelfInfoModel.userID, jsonObject.getString("donatephone_id"), position);
                            break;
                        case 1:                             // 청구 단추
                            onHelpingDonate(SelfInfoModel.userID, jsonObject.getString("donatephone_id"), position);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.myList.length();
    }

    public void onConfirmDonate (String userID, String donateID, final Integer position) {
        JSONObject params = new JSONObject();
        try {
            params.put("user_id", userID);
            params.put("donatephone_id", donateID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(params.length() == 0)
            return;
        donateConfirmTask = new DonateConfirmTask(context, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(context);
                    return;
                }
                JSONObject result = (JSONObject) Response;
                Boolean resultFlag = result.getBoolean("result");
                if(!resultFlag) {
                    GlobalVars.showErrAlert(context);
                }

                // 성공이면 목록에서 해당항목을 삭제한다.
                Fragment_donate_help.self.acceptingList.remove(position);
                Fragment_donate_help.self.notifyRecycler();
            }
        });
    }

    public void onHelpingDonate (String userID, String donateID, final Integer position) {
        JSONObject params = new JSONObject();
        try {
            params.put("user_id", userID);
            params.put("donatephone_id", donateID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(params.length() == 0)
            return;
        donateHelpingTask = new DonateHelpingTask(context, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(context);
                    return;
                }
                JSONObject result = (JSONObject) Response;
                Boolean resultFlag = result.getBoolean("result");
                if(!resultFlag) {
                    GlobalVars.showErrAlert(context);
                }
                // 성공이면 목록에서 해당항목을 삭제한다.
                Fragment_donate_help.self.requesting.remove(position);
                Fragment_donate_help.self.notifyRecycler();
            }
        });
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvContent;
        private final Button btnConfirm;
        public MyViewHolder(View itemView) {
            super(itemView);

            btnConfirm = (Button) itemView.findViewById(R.id.btn_request);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
        }
    }

}
