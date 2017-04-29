package grl.com.adapters.energy;

import android.app.Activity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import grl.com.activities.energy.tGroup.league.LeagueActivity;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/11/2016.
 */
public class LeagueSettingAdapter extends RecyclerView.Adapter<LeagueSettingAdapter.MyViewHolder>{

    Activity context;
    public JsonArray myList;

    public LeagueSettingAdapter (Activity context) {
        this.context = context;
        this.myList = new JsonArray();
    }

    public void notifyData (JsonArray data) {
        this.myList = data;
        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_league_setting_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        JsonObject data = myList.get(position).getAsJsonObject();
        int round = data.get("round_num").getAsInt();
        String strRound = LeagueActivity.convertstrForRound(round);
        holder.tvRoundTitle.setText(strRound);
        holder.tvRoundTime.setText(data.get("start_time").getAsString());
        JsonArray fights = data.get("fights").getAsJsonArray();
        holder.recyclerAdapter.notifyData(fights, data.get("start_time").getAsString(), position);
    }

    @Override
    public int getItemCount() {
        return this.myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvRoundTitle;
        private final TextView tvRoundTime;
        private final RecyclerView recyclerView;

        LeagueSettingRoundAdapter recyclerAdapter;
        public MyViewHolder(View itemView) {
            super(itemView);

            tvRoundTime = (TextView) itemView.findViewById(R.id.tvRoundTime);
            tvRoundTitle = (TextView) itemView.findViewById(R.id.tvRoundTitle);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view);

            this.setUpAdatper();
        }

        public void setUpAdatper () {
            // set up Adatper
            recyclerAdapter = new LeagueSettingRoundAdapter(context);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(recyclerAdapter);

        }
    }
}
