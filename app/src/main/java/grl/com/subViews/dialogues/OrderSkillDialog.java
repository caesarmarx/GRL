package grl.com.subViews.dialogues;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import grl.wangu.com.grl.R;

/**
 * Created by macdev001 on 6/8/16.
 */

public class OrderSkillDialog extends DialogFragment implements
        android.view.View.OnClickListener{

    Button okBtn;
    Button cancelBtn;

    String skill;

    public static OrderSkillDialog newInstance() {
        OrderSkillDialog dlg = new OrderSkillDialog();
        return dlg;
    }

    public OrderSkillDialog()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, 0);
        setCancelable(false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        View view = getActivity().getLayoutInflater().inflate(R.layout.layout_dialog_submit_target, new LinearLayout(getActivity()), false);

        // Build dialog
        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setContentView(view);

        okBtn = (Button)view.findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(this);
        cancelBtn = (Button)view.findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(this);
        return builder;
    }


    public void setData(String newSkill) {
        skill = newSkill;
        if (skill == null) skill = "";
    }

    public boolean isShowing() {
        if (getDialog() != null && getDialog().isShowing())
            return true;
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                Intent intent = new Intent();
                intent.putExtra("Skill", skill);
                getTargetFragment().onActivityResult(getTargetRequestCode(), 1, intent);
                if (isShowing())
                    dismiss();
                break;
            case R.id.btn_cancel:
                if (isShowing())
                    dismiss();
                break;
        }
    }

}