package grl.com.adapters.energy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import grl.com.activities.energy.tGroup.league.LeagueActivity;
import grl.com.activities.energy.tGroup.league.LeagueSettingActivity;
import grl.com.subViews.dialogues.league.TimeSettingDialog;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/11/2016.
 */
public class LeagueSettingRoundAdapter extends RecyclerView.Adapter<LeagueSettingRoundAdapter.MyViewHolder>{

    LeagueSettingActivity context;
    JsonArray fights;
    String roundTime;
    int round;

    public LeagueSettingRoundAdapter (Activity context) {
        this.context = (LeagueSettingActivity) context;
        this.fights = new JsonArray();
        this.round = 0;
        roundTime = "";
    }

    public void notifyData(JsonArray data, String roundTime, int roundNum) {
        this.round = roundNum;
        this.fights = data;
        this.roundTime = roundTime;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.league_setting_round_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final JsonObject item = fights.get(position).getAsJsonObject();
        String leftUserName = LeagueActivity.tGroupMemberModel.getUserNameFromDiscipleID(item.get("left_player").getAsString());
        String rightUserName = LeagueActivity.tGroupMemberModel.getUserNameFromDiscipleID(item.get("right_player").getAsString());
        String fightsText = leftUserName + " V " + rightUserName;
        String fightsTime = roundTime + " " + item.get("start_time").getAsString() + "-" + item.get("end_time").getAsString();
        holder.tvLeague.setText(fightsText);
        holder.tvLeagueTime.setText(fightsTime);

        // 항목을 선택하였을 때의 처리 진행
        holder.view.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 대전시간을 설정한다.
//                showTimePicker(position);
                TimeSettingDialog timeDialog = new TimeSettingDialog(context);
                timeDialog.setData(item.get("start_time").getAsString(), item.get("end_time").getAsString());
                timeDialog.setTitle(context.getString(R.string.time_setting_dialog_title));
                AlertDialog dlg = timeDialog.create();
                dlg.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 확인 단추를 누를 때의 처리 진행
                        Dialog dlg = Dialog.class.cast(dialog);
                        TextView tvStart = (TextView)dlg.findViewById(R.id.tvStartTime);
                        TextView tvEnd = (TextView)dlg.findViewById(R.id.tvEndTime);
                        JsonObject fightItem  = fights.get(position).getAsJsonObject();
                        fightItem.addProperty("start_time", tvStart.getText().toString());
                        fightItem.addProperty("end_time", tvEnd.getText().toString());
                        fights.set(position, fightItem);
                        notifyDataSetChanged();
                    }
                });
                dlg.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 취소단추를 누를 때의 처리 진행
                    }
                });
                dlg.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.fights.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvLeague;
        private final TextView tvLeagueTime;
        private final View view;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvLeague = (TextView) itemView.findViewById(R.id.tvRound);
            tvLeagueTime = (TextView) itemView.findViewById(R.id.tvRoundTime);
            view = itemView;
        }
    }
}
