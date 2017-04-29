package grl.com.subViews.view.Crash;

import android.app.Activity;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.RelativeLayout;

import grl.com.activities.discovery.crash.CrashActivity;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/30/2016.
 */
public class ExtraButView extends RelativeLayout {

    CrashActivity context;
    // view
    Button btnConsider;
    Button btnGiveup;
    // require

    // value


    public ExtraButView(Activity context) {
        super(context);
        init(context);
    }

    public ExtraButView(Activity context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExtraButView(Activity context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Activity context) {

        this.context = (CrashActivity) context;
        inflate(getContext(), R.layout.crash_result_view, this);

        this.btnConsider = (Button) findViewById(R.id.btnConsider);
        this.btnGiveup = (Button) findViewById(R.id.btnGiveup);

        // set on click listener
        btnGiveup.setOnClickListener(this.context);
        btnConsider.setOnClickListener(this.context);
    }

    public void setConsiderTitle (String title) {
        btnConsider.setText(title);
    }
}