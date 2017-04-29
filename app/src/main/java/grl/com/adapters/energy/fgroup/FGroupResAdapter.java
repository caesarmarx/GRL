package grl.com.adapters.energy.fgroup;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import grl.com.activities.common.TextFieldEditerActivity;
import grl.com.activities.energy.fGroup.FGroupResActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 7/8/2016.
 */
public class FGroupResAdapter extends RecyclerView.Adapter<FGroupResAdapter.MyViewHolder>{
    List<String> titls;
    List<String> values;
    JSONObject data;
    FGroupResActivity context;

    public FGroupResAdapter(Activity context) {
        this.context = (FGroupResActivity) context;
        titls = Arrays.asList(context.getString(R.string.fgroup_res_first), context.getString(R.string.fgroup_res_second),
                context.getString(R.string.fgroup_res_third), context.getString(R.string.fgroup_res_fourth),
                context.getString(R.string.fgroup_res_fifth), context.getString(R.string.fgroup_res_sixth),
                context.getString(R.string.fgroup_res_sixth));
        values = Arrays.asList("", "", "", "", "", "", "");
    }

    public void notifyData (JSONObject data) {
        this.data = data;
        try {
            this.values = Arrays.asList(data.getString("chamber_num"), data.getString("club_num"),
                    data.getString("fellow_num"), data.getString("alumni_num"), data.getString("colleague_num"),
                    data.getString("friends_num"), data.getString("interest_group_num"));
        } catch (JSONException e) {}
        notifyDataSetChanged();
    }

    public JSONObject getData () {
        JSONObject result = data;
        try {
            result.put("chamber_num", values.get(0));
            result.put("club_num", values.get(1));
            result.put("fellow_num", values.get(2));
            result.put("alumni_num", values.get(3));
            result.put("colleague_num", values.get(4));
            result.put("friends_num", values.get(5));
            result.put("interest_group_num", values.get(6));
        } catch (JSONException e) {}
        return result;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_fgroup_res_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tvKey.setText(this.titls.get(position));
        holder.tvValue.setText(this.values.get(position));

        // 항목을 선택
        if(!context.userID.equals(SelfInfoModel.userID))
            return;
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.start_ActivityForResult(context, TextFieldEditerActivity.class, position,
                        new BasicNameValuePair(Constant.TEXTFIELD_CONTENT, holder.tvValue.getText().toString()),
                        new BasicNameValuePair(Constant.TITLE_ID, titls.get(position)));
            }
        });
    }

    public void updateData (String str, int position) {
        this.values.set(position, str);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return titls.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvKey;
        private final TextView tvValue;
        private final View view;
        public MyViewHolder(View itemView) {
            super(itemView);

            tvKey = (TextView) itemView.findViewById(R.id.tvKey);
            tvValue = (TextView) itemView.findViewById(R.id.tvValue);
            view = itemView;
        }
    }
}
