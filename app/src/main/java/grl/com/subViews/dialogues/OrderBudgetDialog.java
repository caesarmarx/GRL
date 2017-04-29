package grl.com.subViews.dialogues;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import grl.com.configuratoin.Constant;
import grl.wangu.com.grl.R;

/**
 * Created by macdev001 on 6/8/16.
 */

public class OrderBudgetDialog extends DialogFragment implements
        View.OnClickListener{

    Button okBtn;
    Button cancelBtn;

    EditText budgetEdit;
    String budget;

    public static OrderBudgetDialog newInstance() {
        OrderBudgetDialog dlg = new OrderBudgetDialog();
        return dlg;
    }

    public OrderBudgetDialog()
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
        View view = getActivity().getLayoutInflater().inflate(R.layout.layout_dialog_submit_budget, new LinearLayout(getActivity()), false);

        // Build dialog
        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setContentView(view);

        budgetEdit = (EditText)view.findViewById(R.id.budget_edit);
        okBtn = (Button)view.findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(this);
        cancelBtn = (Button)view.findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(this);

        budgetEdit.setText(budget);

        return builder;
    }


    public void setData(String newBudget) {
        budget = newBudget;
//        if (budget == null) budget = "";
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
                budget = budgetEdit.getText().toString();
                Intent intent = new Intent();
                intent.putExtra(Constant.ORDER_BUDGET, budget);
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