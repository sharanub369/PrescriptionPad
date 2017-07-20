package com.prescriptionpad.app.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.prescriptionpad.app.android.R;
import com.prescriptionpad.app.android.util.Constants;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sharana.b on 4/20/2017.
 */
public class SettingsActivity extends NavigationActivity implements View.OnClickListener {

    private Toolbar actionBarToolbar;
    private TextView pageTitleTxt;


    @InjectView(R.id.doctorDetailBtn)
    public Button doctorDetailBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.inject(this);
        setUpToolbar();
        setOnclickListener();
    }

    private void setOnclickListener() {
        doctorDetailBtn.setOnClickListener(this);
    }

    private void setUpToolbar() {
        ActionBar actionBar = getActionbarToolbar();
        pageTitleTxt = (TextView) findViewById(R.id.actionbarTitleTxt);
        pageTitleTxt.setText("Settings");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return 0;
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }



    private ActionBar getActionbarToolbar() {
        if (actionBarToolbar == null) {
            actionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (actionBarToolbar != null) {
                setSupportActionBar(actionBarToolbar);
            }
        }
        return getSupportActionBar();
    }


    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.doctorDetailBtn:
                intent = new Intent(SettingsActivity.this, DoctorDetailsAddActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.IS_DOCTOR_EXIST, true);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
