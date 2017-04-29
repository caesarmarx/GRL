package grl.com.adapters.discovery;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;

import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.httpRequestTask.discovery.planet.PlanetSetTask;
import grl.com.network.HttpCallback;
import grl.com.subViews.dialogues.planet.PlanetSettingDialog;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/11/2016.
 */
public class StarSettingAdapter extends RecyclerView.Adapter<StarSettingAdapter.MyViewHolder>{

    private Activity context;
    public JsonArray myModel;

    // task
    PlanetSetTask planetSetTask;

    // values
    static final String WAITING_STATUS = "等待接受";
    static final String ALEADY_STATUS = "已接受";

    public StarSettingAdapter (Activity context) {
        this.context = context;
        this.myModel = new JsonArray();
    }

    public void notifyData (JsonArray data) {
        this.myModel = data;
        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_star_setting_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.initViews();
        for(int i = 0; i < myModel.size(); i ++ ) {
            JsonObject temp = myModel.get(i).getAsJsonObject();
            int relType = temp.get("rel_type").getAsInt();
            String userName = temp.get("user_name").getAsString();
            String user_phone = temp.get("user_phone").getAsString();
            if(relType > 0 && (relType - 1) == position) {
                holder.tvUserName.setText(userName);
                holder.tvPhoneNum.setText(user_phone);
                holder.imgStar.setImageDrawable(context.getResources().getDrawable(R.drawable.planet_star));
                if(temp.get("status").getAsInt() == 0) {
                    holder.tvStatus.setText(WAITING_STATUS);
                } else {
                    holder.tvStatus.setText(ALEADY_STATUS);
                }
            }
        }
        holder.view.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 항목을 선택하는 경우 귀성설정을 진행한다.
                showSettingAlert(holder, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 9;
    }

    public void showSettingAlert (final MyViewHolder holder, final int position) {
        PlanetSettingDialog dialog = new PlanetSettingDialog(context);
        dialog.setData(holder.tvUserName.getText().toString(), holder.tvPhoneNum.getText().toString());
        dialog.setTitle(context.getString(R.string.setting_title));
        AlertDialog dlg = dialog.create();
        dlg.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 확인 단추를 누를 때의 처리 진행
                Dialog dlg = Dialog.class.cast(dialog);
                EditText etUserName = (EditText) dlg.findViewById(R.id.etUserName);
                EditText etUserPhone = (EditText) dlg.findViewById(R.id.etUserPhone);
                String strUserName = etUserName.getText().toString();
                String strUserPhone = etUserPhone.getText().toString();
                // 본인의 전화번호인 경우 요청을 보내지 않는다.
                if(strUserPhone.equals(SelfInfoModel.userPhone)) {
                    GlobalVars.showCommonAlertDialog(context, context.getString(R.string.your_phone_is_not_allowed), "");
                    return;
                } else {
                    holder.tvUserName.setText(strUserName);
                    holder.tvPhoneNum.setText(strUserPhone);
                    holder.tvStatus.setText(WAITING_STATUS);
                    holder.imgStar.setImageDrawable(context.getResources().getDrawable(R.drawable.planet_star));
                    setPlanetUser(strUserName, strUserPhone, position + 1);
                }

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

    public void setPlanetUser (String userName, String phoneNumber, int relType) {
        final JsonObject params = new JsonObject();
        params.addProperty("user_id", SelfInfoModel.userID);
        params.addProperty("rel_type", relType);
        params.addProperty("user_phone", phoneNumber);
        params.addProperty("user_name", userName);
        params.addProperty("status", 0);

        planetSetTask =  new PlanetSetTask(context, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(context);
                    return;
                }
                JsonObject result = (JsonObject) Response;
                Boolean res = result.get("result").getAsBoolean();
                if(res) {                       // success
                    myModel.add(params);
                    notifyDataSetChanged();
                } else {                        // failure
                    GlobalVars.showErrAlert(context);
                }
            }
        });
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgStar;
        private final TextView tvUserName;
        private final TextView tvPhoneNum;
        private final TextView tvStatus;
        private final View view;
        public MyViewHolder(View itemView) {
            super(itemView);

            imgStar = (ImageView) itemView.findViewById(R.id.imgStar);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvPhoneNum = (TextView) itemView.findViewById(R.id.tvPhone);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
            view = itemView;
        }

        public void initViews () {
            tvUserName.setText("");
            tvPhoneNum.setText("");
            tvStatus.setText("");
            imgStar.setImageDrawable(context.getResources().getDrawable(R.drawable.default_star));
            view.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }
    }
}
