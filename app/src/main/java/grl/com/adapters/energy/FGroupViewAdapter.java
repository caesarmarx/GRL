package grl.com.adapters.energy;

import android.app.Activity;
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

import grl.com.activities.energy.fGroup.FriendGroupMainActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */
public class FGroupViewAdapter extends RecyclerView.Adapter<FGroupViewAdapter.MyViewHolder>{

    public JSONArray myList;
    private Activity context;
    private Integer groupType;              // 0: 가입한 친구계 1: 본인이 창조한 친구계

    public FGroupViewAdapter(Activity context, Integer type) {
        this.context = context;
        this.groupType = type;

        this.myList = new JSONArray();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_fgroup_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if(this.myList.length() == 0){
            holder.tvEmpty.setVisibility(View.VISIBLE);
            holder.borderView.setVisibility(View.INVISIBLE);
            holder.groupLogoShow(false);
            return;
        } else {
            holder.tvEmpty.setVisibility(View.GONE);
            holder.groupLogoShow(true);
        }
        try {
            JSONObject jsonObject = this.myList.getJSONObject(position);
            holder.groupName.setText(jsonObject.getString("group_name"));
            String strSubDetail = jsonObject.getString("member_cnt") + "个人";
            holder.groupDetail.setText(strSubDetail);
            JSONArray imagesArray = jsonObject.getJSONArray("images");
            holder.initGroupLogo(imagesArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // set onClickListener
        holder.view.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 친구계를 선택하면 해당한 친구계 기본창으로 이행한다.
                try {
                    JSONObject jsonObject = myList.getJSONObject(position);
                    String groupTitle = holder.groupName.getText().toString() + "贵人圈";
                    Utils.start_Activity(context, FriendGroupMainActivity.class,
                            new BasicNameValuePair(Constant.BACK_TITLE_ID, context.getString(R.string.friend_group_title)),
                            new BasicNameValuePair(Constant.TITLE_ID, groupTitle),
                            new BasicNameValuePair("boss_id", jsonObject.getString("boss_id")),
                            new BasicNameValuePair("group_id",jsonObject.getString("group_id")));
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

        private ImageView[] groupLogo;
        private final TextView groupName;
        private final TextView groupDetail;
        private final TextView tvEmpty;
        private final View view;
        private final View borderView;
        private final LinearLayout layoutImage;
        public MyViewHolder(View itemView) {
            super(itemView);
            groupName = (TextView) itemView.findViewById(R.id.txt_group_name);
            groupDetail = (TextView) itemView.findViewById(R.id.txt_group_detail);
            tvEmpty = (TextView) itemView.findViewById(R.id.tvEmpty);
            view = itemView;
            layoutImage = (LinearLayout) itemView.findViewById(R.id.layoutImages);
            borderView = (View) itemView.findViewById(R.id.view_border);
            groupLogo = new ImageView[4];
            groupLogo[0] = (ImageView) itemView.findViewById(R.id.img_first);
            groupLogo[1] = (ImageView) itemView.findViewById(R.id.img_second);
            groupLogo[2] = (ImageView) itemView.findViewById(R.id.img_third);
            groupLogo[3] = (ImageView) itemView.findViewById(R.id.img_fourth);
        }

        public void groupLogoShow(Boolean flag) {

                if (flag) {                      // 화상들을 보여주기
                    layoutImage.setVisibility(View.VISIBLE);
                } else {                        //화상들을 감추기 진행
                    layoutImage.setVisibility(View.INVISIBLE);
                }
        }

        public void initGroupLogo(JSONArray jsonArray) {
            // 화상초기화 진행
            for(int i = 0; i < groupLogo.length; i ++) {
                groupLogo[i].setImageResource(android.R.color.transparent);
            }
            for(int i = 0; i < jsonArray.length(); i ++) {
                try {
                    String imagePath = jsonArray.getString(i);
                    GlobalVars.loadImage(groupLogo[i], imagePath);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
