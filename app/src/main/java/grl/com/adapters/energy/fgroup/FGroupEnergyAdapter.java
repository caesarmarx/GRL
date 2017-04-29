package grl.com.adapters.energy.fgroup;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grl.com.activities.otherRel.OtherMainActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/23/2016.
 */
public class FGroupEnergyAdapter extends RecyclerView.Adapter<FGroupEnergyAdapter.MyViewHolder>{

    Activity context;
    JSONArray myList;

    public FGroupEnergyAdapter (Activity context) {
        this.context = context;
        this.myList = new JSONArray();
    }

    public void notifyData(JSONArray jsonArray) {
        this.myList = jsonArray;
        this.notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_fgroup_energy, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if(this.myList.length() == 0){
            holder.tvEmpty.setVisibility(View.VISIBLE);
            holder.borderView.setVisibility(View.INVISIBLE);
            holder.imgUserPhoto.setVisibility(View.GONE);
            return;
        } else {
            holder.tvEmpty.setVisibility(View.GONE);
            holder.borderView.setVisibility(View.VISIBLE);
            holder.imgUserPhoto.setVisibility(View.VISIBLE);
        }
        try {
            JSONObject jsonObject = this.myList.getJSONObject(position);
            String userInfo = context.getString(R.string.user_info_title) + " " +
                    jsonObject.getString("user_area") + "+" +
                    jsonObject.getString("user_skill") + "+" +
                    jsonObject.getString("user_attraction");
            holder.tvUserName.setText(jsonObject.getString("user_name"));
            holder.tvUserInfo.setText(userInfo);
            GlobalVars.loadImage(holder.imgUserPhoto, jsonObject.getString("user_photo"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // set onClickListener
        holder.view.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 항목을 선택하였을 때 제3인자창으로 이행한다.
                try {
                    JSONObject jsonObject = myList.getJSONObject(position);
                    String strBackTitle = context.getString(R.string.tab_energy_title);

                    Utils.start_Activity(context, OtherMainActivity.class,
                            new BasicNameValuePair(Constant.TITLE_ID,  jsonObject.getString("user_name")),
                            new BasicNameValuePair(Constant.BACK_TITLE_ID, strBackTitle),
                            new BasicNameValuePair("user_id", jsonObject.getString("user_id")),
                            new BasicNameValuePair("user_name", jsonObject.getString("user_name")),
                            new BasicNameValuePair("user_photo", jsonObject.getString("user_photo")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgUserPhoto;
        private final TextView tvUserName;
        private final TextView tvUserInfo;
        private final TextView tvEmpty;
        private final View view;
        private final View borderView;
        public MyViewHolder(View itemView) {
            super(itemView);

            imgUserPhoto = (ImageView) itemView.findViewById(R.id.user_photo);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
            tvUserInfo = (TextView) itemView.findViewById(R.id.txt_user_energy);
            tvEmpty = (TextView) itemView.findViewById(R.id.tvEmpty);
            borderView = (View) itemView.findViewById(R.id.layout_border);
            view = itemView;
        }
    }
}
