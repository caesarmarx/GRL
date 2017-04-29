package grl.com.adapters.otherRel;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/10/2016.
 */
public class EnergyRequestAdapter extends RecyclerView.Adapter<EnergyRequestAdapter.MyItemViewHolder>{

    public JSONArray myList;
    private Activity context;

    private int selectPos;
    public EnergyRequestAdapter(Activity context) {
        this.context = context;
        this.myList = new JSONArray();
        this.selectPos = -1;
    }
    @Override
    public MyItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_energy_request_row, parent, false);
        return new MyItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyItemViewHolder holder, final int position) {
        if(myList.length() == 0) {
            holder.tvEmpty.setVisibility(View.VISIBLE);
            holder.imgCheck.setVisibility(View.GONE);
            holder.fgroupName.setVisibility(View.GONE);
            return;
        } else {
            holder.tvEmpty.setVisibility(View.GONE);
            holder.imgCheck.setVisibility(View.VISIBLE);
            holder.fgroupName.setVisibility(View.VISIBLE);
        }
        if(this.selectPos == position)
            holder.imgCheck.setVisibility(View.VISIBLE);
        else
            holder.imgCheck.setVisibility(View.INVISIBLE);
        // 귀인계목록 현시
        try {
            JSONObject jsonObject = this.myList.getJSONObject(position);
            holder.fgroupName.setText(jsonObject.getString("group_name"));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        // 해당한 귀인계를 선택하였을 때의 처리 진행
        holder.view.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(selectPos == position)
                    selectPos = -1;
                else
                    selectPos = position;
                notifyDataSetChanged();
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

    public int getSelectedIndex () {
        return this.selectPos;
    }

    public class MyItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView fgroupName;
        private final ImageView imgCheck;
        private final View viewBorder;
        private final View view;
        private final TextView tvEmpty;

        public MyItemViewHolder(View itemView) {
            super(itemView);

            fgroupName = (TextView) itemView.findViewById(R.id.tvFGroupName);
            imgCheck = (ImageView) itemView.findViewById(R.id.imgCheck);
            viewBorder = (View) itemView.findViewById(R.id.view_border);
            tvEmpty = (TextView) itemView.findViewById(R.id.tvEmpty);
            view = itemView;
        }
    }
}
