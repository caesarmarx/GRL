package grl.com.adapters.energy;

import android.app.Activity;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import grl.com.activities.energy.tGroup.challenge.ChallengeDetailActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.ChallengeModel;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */

public class ChallengeListAdapter extends RecyclerView.Adapter<ChallengeListAdapter.MyViewHolder> {

    public List<ChallengeModel> myList;
    private Activity ctx;

    private String teacherId;
    private int tabIndex = 0;

    public ChallengeListAdapter(Activity context) {
        this.ctx = context;
        this.myList = new ArrayList<ChallengeModel>();
    }

    public void setTeacherId(String userId) {
        teacherId = userId;
    }
    public void setTabIndex(int index) {
        tabIndex = index;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_challenge_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        ChallengeModel model = this.myList.get(position);

        String startDate = GlobalVars.getDateStringFromLong(model.startDate, "yyyy-MM-dd");
        String endDate = GlobalVars.getDateStringFromLong(model.endDate, "yyyy-MM-dd");

        if (tabIndex == 0) {
            holder.userNameView.setText(model.toUserName);
            GlobalVars.loadImage(holder.userPhotoView, model.toUserPhoto);
        } else {
            holder.userNameView.setText(model.fromUserName);
            GlobalVars.loadImage(holder.userPhotoView, model.fromUserPhoto);
        }
        holder.dateView.setText(String.format("%s - %s", startDate, endDate));
        if (model.acceptState == 0) {
            holder.stateView.setText("新");
        }
        if (model.acceptState == 1) {
            holder.stateView.setText("验证");
        }
        if (model.acceptState == 2) {
            if (model.challengeState == 0)
                holder.stateView.setText(ctx.getResources().getString(R.string.challenge_start_state));
            if (model.challengeState == 0)
                holder.stateView.setText(ctx.getResources().getString(R.string.challenge_running_state));
            if (model.challengeState == 0)
                holder.stateView.setText(ctx.getResources().getString(R.string.challenge_end_state));
        }

        // set onClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
            //
                ChallengeModel model = myList.get(position);
                Utils.start_Activity(ctx, ChallengeDetailActivity.class,
                        new BasicNameValuePair(Constant.CHALLENGE_ID, model.challengeId),
                        new BasicNameValuePair(Constant.TEACHER_ID, teacherId),
                        new BasicNameValuePair("showType", String.valueOf(tabIndex))
                );

            }
        });

    }

    @Override
    public int getItemCount() {
        return this.myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView userPhotoView;
        private final TextView userNameView;
        private final TextView dateView;
        private final TextView stateView;

        public MyViewHolder(View itemView) {
            super(itemView);
            userPhotoView = (ImageView) itemView.findViewById(R.id.img_user_photo);
            userNameView = (TextView) itemView.findViewById(R.id.tv_user_name);
            dateView = (TextView) itemView.findViewById(R.id.tv_challenge_date);
            stateView = (TextView) itemView.findViewById(R.id.tv_challenge_state);
        }
    }
}
