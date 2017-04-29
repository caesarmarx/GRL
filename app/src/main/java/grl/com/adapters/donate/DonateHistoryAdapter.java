package grl.com.adapters.donate;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grl.com.configuratoin.GlobalVars;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/10/2016.
 */
public class DonateHistoryAdapter extends RecyclerView.Adapter<DonateHistoryAdapter.MyViewHolder> {
    Activity context;
    public JSONArray myList;
    public Integer type;

    public DonateHistoryAdapter(Activity context, Integer type) {
        this.context = context;
        this.type = type;
        this.myList = new JSONArray();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_donate_history_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

//        holder.tvContent.setText(this.myList.get(position).toString());
        String strContent = "";
        try {
            JSONObject jsonObject = myList.getJSONObject(position);
            switch (type) {
                case 0:
                    strContent = jsonObject.getString("user_name");
                    break;
                case 1:
                    strContent = jsonObject.getString("user_name") + " " + jsonObject.getString("req_content");
                    break;
                case 2:
                    String payDate = GlobalVars.getDateStringFromMongoDate(jsonObject.getJSONObject("date"), "yyyy-MM-dd");
                    strContent = payDate + "   " + jsonObject.getString("pay") + "令牌";
                    break;
                case 3:
                    String result_date = GlobalVars.getDateStringFromMongoDate(jsonObject.getJSONObject("res_date"), "yyyy/MM/dd");
                    strContent = result_date + "   " + jsonObject.getString("result") + "个令牌  来自 " + jsonObject.getString("user_name");
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.tvContent.setText(strContent);
        holder.tvContent.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 요청내용을 누르기하는 경우 상세보기를 진행한다.
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.myList.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvContent;
        public MyViewHolder(View itemView) {
            super(itemView);

            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
        }
    }
}
