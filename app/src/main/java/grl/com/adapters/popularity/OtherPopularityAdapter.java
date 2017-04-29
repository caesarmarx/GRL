package grl.com.adapters.popularity;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import grl.com.activities.discovery.popularity.NewPopularityActivity;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.PopularityModel;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */

public class OtherPopularityAdapter extends RecyclerView.Adapter<OtherPopularityAdapter.MyViewHolder> {

    public List<PopularityModel> myList;
    private Activity ctx;
    private String estName;

    public OtherPopularityAdapter(Activity context) {
        this.ctx = context;
        this.myList = new ArrayList<PopularityModel>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_my_popularity_row, parent, false);
        return new MyViewHolder(itemView);
    }

    public String getEstName() {
        return estName;
    }

    public void setEstName(String estName) {
        this.estName = estName;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        PopularityModel model = this.myList.get(position);

        holder.layout_add.setVisibility(View.GONE);
        holder.layout_content.setVisibility(View.VISIBLE);
        holder.descriptionView.setText(model.describeContent);
        if (model.photoPath.isEmpty()) {
            holder.imageView.setVisibility(View.GONE);
        } else {
            holder.imageView.setVisibility(View.VISIBLE);
            GlobalVars.loadImage(holder.imageView, model.photoPath);
        }
        if (model.sureMans == null || model.sureMans.length() == 0)
            holder.sureNameView.setVisibility(View.GONE);
        holder.praiseTotalView.setText(String.format("%s : %d", ctx.getResources().getString(R.string.popularity_praise), model.praiseTotal));
        holder.estimateTotalView.setText(String.format("%s : %d", ctx.getResources().getString(R.string.popularity_estimate), model.estimateTotal));
        if (model.estName.compareTo("SY_FACTOR") == 0) {
            int good = 0, normal = 0, bad = 0;
            for (int i = 0; i < model.estimateList.length(); i++) {
                try {
                    JSONObject object = model.estimateList.getJSONObject(i);
                    if (GlobalVars.getIntFromJson(object, "praise_value") == 3) {
                        good ++;
                    }
                    if (GlobalVars.getIntFromJson(object, "praise_value") == 1) {
                        normal ++;
                    }
                    if (GlobalVars.getIntFromJson(object, "praise_value") == -3) {
                        bad ++;
                    }
                } catch (Exception ex) {

                }
            }
            holder.praiseTotalView.setText(String.format("好:%d 中:%d 差:%d", good, normal, bad));
        }
        // set onClickListener
        holder.imageView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {


            }
        });

    }


    @Override
    public int getItemCount() {
        if (myList == null)
            return 0;
        return this.myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout layout_add;
        private final RelativeLayout layout_content;

        private final ImageView imgAddBtn;
        private final LinearLayout photoLayout;
        private final TextView descriptionView;
        private final TextView timeView;
        private final ImageView imageView;
        private final ImageView operationView;
        private final TextView sureNameView;
        private final TextView praiseTotalView;
        private final TextView estimateTotalView;

        public MyViewHolder(View itemView) {
            super(itemView);

            layout_add = (LinearLayout) itemView.findViewById(R.id.layout_add);
            layout_content = (RelativeLayout) itemView.findViewById(R.id.layout_content);

            imgAddBtn = (ImageView) itemView.findViewById(R.id.img_add);
            descriptionView = (TextView) itemView.findViewById(R.id.tv_description);
            photoLayout = (LinearLayout) itemView.findViewById(R.id.layout_photo);
            imageView = (ImageView) itemView.findViewById(R.id.img_photo);
            timeView = (TextView) itemView.findViewById(R.id.tv_time);
            operationView = (ImageView) itemView.findViewById(R.id.img_operation);
            sureNameView = (TextView) itemView.findViewById(R.id.tv_sure_man);
            praiseTotalView = (TextView) itemView.findViewById(R.id.tv_praise_total);
            estimateTotalView = (TextView) itemView.findViewById(R.id.tv_estimate_total);
        }
    }
}
