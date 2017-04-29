package grl.com.subViews.dialogues;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import grl.wangu.com.grl.R;

/**
 * Created by macdev001 on 6/8/16.
 */

public class WaitingDialog extends DialogFragment implements
        View.OnClickListener{

    ProgressBar progressBar;
    public static WaitingDialog newInstance() {
        WaitingDialog dlg = new WaitingDialog();
        return dlg;
    }

    public WaitingDialog()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.WaitingDialog);
        setCancelable(false);
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState)
//    {
//        setStyle(STYLE_NO_TITLE, R.style.WaitingDialog);
//        setCancelable(false);
//
//        View view = getActivity().getLayoutInflater().inflate(R.layout.layout_waiting_dialog, new LinearLayout(getActivity()), false);
//
//        // Build dialog
//        Dialog builder = new Dialog(getActivity());
//        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        builder.setContentView(view);
//
//
//
//        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
//        return builder;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the XML view for the help dialog fragment
        View view = inflater.inflate(R.layout.layout_waiting_dialog, container);
        return view;
    }


    public boolean isShowing() {
        if (getDialog() != null && getDialog().isShowing())
            return true;
        return false;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onStart() {
        super.onStart();

    }

}