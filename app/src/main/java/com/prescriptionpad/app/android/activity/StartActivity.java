package com.prescriptionpad.app.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.prescriptionpad.app.android.R;
import com.prescriptionpad.app.android.util.BackupRestoreMasterClass;
import com.prescriptionpad.app.android.util.Constants;
import com.prescriptionpad.app.android.util.RealmMasterClass;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;


public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView forwardImg;
    private SharedPreferences sp;
    private Boolean IsFirstTimeInstall = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        //Fabric.with(this, new Crashlytics());
        forwardImg = (ImageView) findViewById(R.id.forwardImg);
        setAnimationForwardImage();
        forwardImg.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sp = getSharedPreferences(Constants.KEY_SP_NAME, MODE_PRIVATE);
        IsFirstTimeInstall = sp.getBoolean(Constants.KEY_FIRST_INSTALL, false);
    }

    private void setAnimationForwardImage() {
        final Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        forwardImg.startAnimation(animation);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forwardImg:
                if (!IsFirstTimeInstall) {
                    acceptTC();
                } else {
                    forwardNextPage();
                }
                break;
        }
    }

    public void acceptTC() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_tc_select_accept_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog_theme);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.show();
        TextView acceptTcTxtBtn = (TextView) dialogView.findViewById(R.id.acceptTcTxtBtn);
        TextView tcTxt = (TextView) dialogView.findViewById(R.id.tcTxt);
        tcTxt.setText(Html.fromHtml(getString(R.string.tcText)));
        final CheckBox acceptTCChkBox = (CheckBox) dialogView.findViewById(R.id.acceptTCChkBox);
        acceptTcTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (acceptTCChkBox.isChecked()) {
                    SharedPreferences.Editor spEdit = sp.edit();
                    spEdit.putBoolean(Constants.KEY_FIRST_INSTALL, true);
                    spEdit.apply();
                    showRestoreBackupDialog();
                    dialog.dismiss();
                } else {
                    Toast.makeText(StartActivity.this, Constants.KEY_ACCEPT_TC, Toast.LENGTH_LONG).show();
                }

            }
        });

        tcTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   startActivity(new Intent(StartActivity.this, TCActivity.class));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.KEY_TC_URL));
                startActivity(browserIntent);
            }
        });
    }

    private void showRestoreBackupDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_backup_restore_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog_theme);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.show();
        TextView ignoreTxtBtn = (TextView) dialogView.findViewById(R.id.ignoreTxtBtn);
        TextView restoreTxtBtn = (TextView) dialogView.findViewById(R.id.restoreTxtBtn);
        ignoreTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forwardNextPage();
            }
        });
        restoreTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (restoreBackup()) {
                    dialog.dismiss();
                    forwardNextPage();
                }
            }
        });
    }

    private Boolean restoreBackup() {
        Realm realm = RealmMasterClass.initializeRealm(this);
        return BackupRestoreMasterClass.importRealDB(realm, this, this);
    }

    private void forwardNextPage() {
        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

    }
}
