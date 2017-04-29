package grl.com.adapters.energy;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import grl.com.activities.energy.tGroup.league.LeagueActivity;
import grl.com.configuratoin.GlobalVars;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/11/2016.
 */
public class LeagueAdapter extends RecyclerView.Adapter<LeagueAdapter.MyViewHolder>{

    LeagueActivity context;
    JsonArray myList;
    int statusType;
    int fightDesPos;                    // 대전상황묘사 인덱스

    public LeagueAdapter (Activity context) {
        this.context = (LeagueActivity)context;
        this.myList = new JsonArray();
        statusType = 0;
        fightDesPos = 0;
    }

    public void notifyData (JsonArray data, int statusType) {
        this.myList = data;
        this.statusType = statusType;
        notifyDataSetChanged();
    }

    public int getStatusType() {
        return this.statusType;
    }

    public JsonArray getRoundData () {
        return this.myList;
    }

    public void updateDataWithFightDes (JsonObject tempInfo) {
        myList.set(fightDesPos, tempInfo);
        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_league_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        JsonObject info = myList.get(position).getAsJsonObject();
        String leftID = info.get("left_player").getAsString();
        String rightID = info.get("right_player").getAsString();
        holder.tvLeftUserName.setText(LeagueActivity.tGroupMemberModel.getUserNameFromDiscipleID(leftID));
        holder.tvRightUserName.setText(LeagueActivity.tGroupMemberModel.getUserNameFromDiscipleID(rightID));
        if(!GlobalVars.loadImage(holder.imgLeftUser, LeagueActivity.tGroupMemberModel.getUserPhotoFromDiscipleID(leftID)))
            holder.imgLeftUser.setImageDrawable(context.getResources().getDrawable(R.drawable.user_default));
        if(!GlobalVars.loadImage(holder.imgRightUser, LeagueActivity.tGroupMemberModel.getUserPhotoFromDiscipleID(rightID)))
            holder.imgRightUser.setImageDrawable(context.getResources().getDrawable(R.drawable.user_default));
        holder.tvNO.setText(String.valueOf(position + 1));
        Drawable checkDrawable;
        if(info.get("status").getAsInt() == 0)
            checkDrawable = context.getResources().getDrawable(R.drawable.round_checked);
        else
            checkDrawable = context.getResources().getDrawable(R.drawable.round_unchecked);
        holder.imgCheck.setImageDrawable(checkDrawable);
        String leagueTime = info.get("start_time").getAsString() + "-" + info.get("end_time").getAsString();
        holder.tvLeagueTime.setText(leagueTime);
        holder.showLeagueResult(info.get("result").getAsInt());
        holder.updateViews();

        // check를 클릭할때의 처리
        holder.imgCheck.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                holder.onCheckClick(position);
            }
        });

        // V를 클릭할때의 처리
        holder.imgVS.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                fightDesPos = position;
                context.onFightDesAction(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvNO;
        private final TextView tvLeftUserName;
        private final TextView tvRightUserName;
        private final ImageView imgCheck;
        private final ImageView imgLeftUser;
        private final ImageView imgRightUser;
        private final ImageView imgVS;
        private final TextView tvRightResult;
        private final TextView tvLeftResult;
        private final TextView tvLeagueTime;
        public MyViewHolder(View itemView) {
            super(itemView);

            tvNO = (TextView) itemView.findViewById(R.id.tvNO);
            tvLeftUserName = (TextView) itemView.findViewById(R.id.txtLeft);
            tvRightUserName = (TextView) itemView.findViewById(R.id.txtRight);
            imgCheck = (ImageView) itemView.findViewById(R.id.imgCheck);
            imgLeftUser = (ImageView) itemView.findViewById(R.id.imgLeftPhoto);
            imgRightUser = (ImageView) itemView.findViewById(R.id.imgRightPhoto);
            imgVS = (ImageView) itemView.findViewById(R.id.imgvPhoto);
            tvLeftResult = (TextView) itemView.findViewById(R.id.tvLeftLeagueStatus);
            tvRightResult = (TextView) itemView.findViewById(R.id.tvRightLeagueStatus);
            tvLeagueTime = (TextView) itemView.findViewById(R.id.tvLeagueTime);
        }

        public void updateViews () {
            switch (statusType) {
                case LeagueActivity.PAST_STATUS:
                    imgCheck.setVisibility(View.GONE);
                    tvRightResult.setVisibility(View.VISIBLE);
                    tvLeftResult.setVisibility(View.VISIBLE);
                    break;
                case LeagueActivity.CURRENT_STATUS:
                    imgCheck.setVisibility(View.VISIBLE);
                    tvRightResult.setVisibility(View.GONE);
                    tvLeftResult.setVisibility(View.GONE);
                    break;
                case LeagueActivity.FUTURE_STATUS:
                    imgCheck.setVisibility(View.GONE);
                    tvRightResult.setVisibility(View.GONE);
                    tvLeftResult.setVisibility(View.GONE);
                    break;
            }

        }

        public void showLeagueResult (int result) {
            switch (result) {
                case 3:
                    tvRightResult.setText("负");
                    tvLeftResult.setText("胜");
                    break;
                case 1:
                    tvLeftResult.setText("平");
                    tvRightResult.setText("平");
                    break;
                case 0:
                    tvLeftResult.setText("负");
                    tvRightResult.setText("胜");
                    break;
                case -1:
                    tvLeftResult.setText("负");
                    tvRightResult.setText("负");
                    break;
                default:
                    break;
            }
        }

        public void onCheckClick (int position) {
            // 자기사문인 경우에만 체크를 할수있다.
            if(!LeagueActivity.tGroupMemberModel.areYouBoss())
                return;
            Drawable checkedDrawable = context.getResources().getDrawable(R.drawable.round_checked);
            Drawable uncheckedDrawable = context.getResources().getDrawable(R.drawable.round_unchecked);
            JsonObject temp = myList.get(position).getAsJsonObject();
            int status;
            switch (statusType) {
                case LeagueActivity.PAST_STATUS:
                case LeagueActivity.FUTURE_STATUS:
                    break;
                case LeagueActivity.CURRENT_STATUS:
                    if(temp.get("status").getAsInt() == 0) {
                        imgCheck.setImageDrawable(uncheckedDrawable);
                        status = 1;
                    } else {
                        imgCheck.setImageDrawable(checkedDrawable);
                        status = 0;
                    }
                    myList.get(position).getAsJsonObject().addProperty("status", status);
                    break;
            }
        }
    }
}
