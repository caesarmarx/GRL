package grl.com.adapters.energy;

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

import grl.com.activities.energy.tGroup.TGroupDetailActivity;
import grl.com.activities.otherRel.OtherMainActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/17/2016.
 */
public class TGroupDetailAdapter extends RecyclerView.Adapter<TGroupDetailAdapter.MyItemViewHolder>{

    TGroupDetailActivity context;
    public JSONArray myList;
    public TGroupDetailAdapter(Activity context) {
        this.context = (TGroupDetailActivity) context;
        this.myList = new JSONArray();
    }
    @Override
    public MyItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_tgroup_row, parent, false);
        return new MyItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyItemViewHolder holder, final int position) {
        final MyItemViewHolder itemHolder = (MyItemViewHolder)holder;
        //bind your view here
        if(myList.length() == 0) {
            itemHolder.tvEmpty.setVisibility(View.VISIBLE);
            return;
        } else {
            itemHolder.tvEmpty.setVisibility(View.GONE);
        }
        try {
            JSONObject jsonObj = this.myList.getJSONObject(position);
            String relType = "";
            if(jsonObj.get("type") != null) {
                Integer type = jsonObj.getInt("type");
                if(type == 1) {
                    itemHolder.tvRelType.setVisibility(View.VISIBLE);
                } else {
                    itemHolder.tvRelType.setVisibility(View.INVISIBLE);
                }
            }
            itemHolder.tvRelative.setText(jsonObj.getString("relation_type"));
            itemHolder.tvUserName.setText(jsonObj.getString("user_name"));
            GlobalVars.loadImage(itemHolder.imgUserPhoto, jsonObj.getString("user_photo"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(context.strType != null)
            return;
        // 항목을 선택하였을 때의 처리 진행
        itemHolder.view.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 제3인자창으로 이행한다.
                try {
                    JSONObject obj = myList.getJSONObject(position);
                    String userID = obj.getString("user_id");
                    String userPhoto = obj.getString("user_photo");
                    Utils.start_Activity(context, OtherMainActivity.class,
                            new BasicNameValuePair(Constant.TITLE_ID, itemHolder.tvUserName.getText().toString()),
                            new BasicNameValuePair("user_id", userID),
                            new BasicNameValuePair("user_photo", userPhoto));
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
            return 1;
        return cnt;
    }

    // custom cell
    class MyItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvRelative;
        private final TextView tvRelType;
        private final TextView tvUserName;
        private final ImageView imgUserPhoto;
        private final TextView tvMore;
        private final TextView tvEmpty;
        private final View view;

        public MyItemViewHolder(View itemView) {
            super(itemView);

            tvRelative = (TextView) itemView.findViewById(R.id.txt_relative);
            tvRelType = (TextView) itemView.findViewById(R.id.rel_type);
            tvUserName = (TextView) itemView.findViewById(R.id.txt_member_name);
            imgUserPhoto = (ImageView) itemView.findViewById(R.id.user_photo);
            tvMore = (TextView) itemView.findViewById(R.id.tvMore);
            tvEmpty = (TextView) itemView.findViewById(R.id.tvEmpty);
            view = itemView;
        }
    }
}
