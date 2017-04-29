package grl.com.adapters.energy;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.dataModels.ContactsModel;
import grl.com.httpRequestTask.newEnergy.EnergyAcceptTask;
import grl.com.network.HttpCallback;
import grl.com.subViews.dialogues.PhotoCardDialog;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */
public class NewEnergyRecyclerAdapter extends RecyclerView.Adapter<NewEnergyRecyclerAdapter.MyViewHolder> {
    public JSONArray myList;
    public Activity ctx;
    public String strTitle;

    private List<String> energyTypeContent;
    private List<String> requestTypes;
    private List<String> requestOtherTypes;

    private EnergyAcceptTask energyAcceptTask;

    public NewEnergyRecyclerAdapter(Activity context) {
        this.ctx = context;
        this.myList = new JSONArray();
        energyTypeContent = Arrays.asList("  接 受  ", "已接受", "  邀 加  " ,"等待接受");
        requestTypes = Arrays.asList("收他为徒", "拜他为师", "拜他为贵人", "邀他加");
        requestOtherTypes = Arrays.asList("拜你为师","收你为徒", "拜你为贵人", "邀你加");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_newenergy_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
            JSONObject jsonObject = this.myList.getJSONObject(position);
            holder.userName.setText(jsonObject.getString("user_name"));
            Integer type = jsonObject.getInt("energy_type");
            holder.btnRequest.setText(energyTypeContent.get(type));
            if(type == Constant.ENERGY_ACCEPTED || type == Constant.ENERGY_REQUIRED) {
                holder.btnRequest.setEnabled(false);
                holder.btnRequest.setBackgroundColor(ctx.getResources().getColor(android.R.color.transparent));
                holder.btnRequest.setTextColor(ctx.getResources().getColor(R.color.light_gray_color));
            } else {
                holder.btnRequest.setEnabled(true);
                holder.btnRequest.setBackground(ctx.getResources().getDrawable(R.drawable.round_fill_yellow));
                holder.btnRequest.setTextColor(ctx.getResources().getColor(R.color.dark_gray_color));
            }
            if(jsonObject.getInt("request_teacher") == -1) {

            } else {
                if(!GlobalVars.loadImage(holder.userPhoto, jsonObject.getString("user_photo")))
                    holder.userPhoto.setImageDrawable(ctx.getResources().getDrawable(R.drawable.user_default));
            }
            String requestContent = "";
            List<String> temp;
            if(jsonObject.getInt("request_flags") == 0) {           // 접수자인 경우
                temp = requestOtherTypes;
            } else {                                                // 요청자인 경우
                temp = requestTypes;
            }
            if(jsonObject.getInt("request_teacher") == 0) {
                requestContent = temp.get(1);
            }
            if(jsonObject.getInt("request_disciple") == 0) {
                if(!requestContent.equals(""))
                    requestContent = requestContent + "+";
                requestContent = requestContent + temp.get(0);
            }
            if(jsonObject.getInt("request_grl") != 2) {
                if(!requestContent.equals(""))
                    requestContent = requestContent + "+";
                requestContent = requestContent + temp.get(2);
            }
            if(!jsonObject.getString("fgroup_id").equals("")){
                if(!requestContent.equals(""))
                    requestContent = requestContent + "+";
                requestContent = requestContent + temp.get(3) + jsonObject.getString("fgroup_name") + ctx.getString(R.string.friend_group_title);
            }
            holder.txtRequestType.setText(requestContent);
            holder.txtRequestType.setTextColor(ctx.getResources().getColor(R.color.light_gray_color));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 사용자의 사진을 누렀을 때의 처리 진행
        holder.userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonObject = myList.getJSONObject(position);
                    Intent intent = new Intent(ctx, PhotoCardDialog.class);
                    intent.putExtra("user_id", jsonObject.getString("user_id"));
                    intent.putExtra(Constant.BACK_TITLE_ID, strTitle);
                    ctx.startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
        // 접수 혹은 요청단추를 누를때의 처리 진행
        holder.btnRequest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendRequest(position);
            }
        });
    }

    public void sendRequest(final int index) {
        try {
            final JSONObject jsonObject = this.myList.getJSONObject(index);
            if(jsonObject.getInt("energy_type") == Constant.ENERGY_REQUIR) {        // 요청처리인 경우

            } else if(jsonObject.getInt("energy_type") == Constant.ENERGY_ACCEPT) { // 접수처리인 경우
                energyAcceptTask = new EnergyAcceptTask(this.ctx, jsonObject, new HttpCallback() {
                    @Override
                    public void onResponse(Boolean flag, Object Response) {
                        if(!flag || Response == null) {                 //failure
                            GlobalVars.showErrAlert(ctx);
                            return;
                        }
                        try {
                            JSONObject responseJSON = (JSONObject)Response;
                            Boolean requestResult = null;
                            requestResult = responseJSON.getBoolean("request_result");
                            if(!requestResult) {                                    //  이미 스승제자계에 존재
                                return;
                            }

                            // update values
                            jsonObject.put("energy_type", 1);
                            myList.remove(index);
                            myList = GlobalVars.insertObject(myList, jsonObject, index);
                            notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return this.myList.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView userPhoto;
        private final TextView userName;
        private final TextView txtRequestType;
        private final Button btnRequest;
        public MyViewHolder(View itemView) {
            super(itemView);
            userPhoto = (ImageView) itemView.findViewById(R.id.img_user_photo);
            userName = (TextView) itemView.findViewById(R.id.tv_user_name);
            txtRequestType = (TextView) itemView.findViewById(R.id.tv_request_type);
            btnRequest = (Button) itemView.findViewById(R.id.btn_energy_request);
        }
    }
}
