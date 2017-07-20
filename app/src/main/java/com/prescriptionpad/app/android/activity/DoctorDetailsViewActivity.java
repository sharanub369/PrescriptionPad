package com.prescriptionpad.app.android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itextpdf.text.Image;
import com.prescriptionpad.app.android.R;
import com.prescriptionpad.app.android.model.DoctorModel;
import com.prescriptionpad.app.android.util.Constants;
import com.prescriptionpad.app.android.util.RealmMasterClass;

import java.io.ByteArrayOutputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.Realm;

/**
 * Created by sharana.b on 5/3/2017.
 */
public class DoctorDetailsViewActivity extends NavigationActivity {

    private Toolbar actionBarToolbar;
    private TextView pageTitleTxt;
    private Realm realm;

    @InjectView(R.id.hospitalLogoImg)
    public ImageView hospitalLogoImg;
    @InjectView(R.id.doctorNameTxt)
    public TextView doctorNameTxt;
    @InjectView(R.id.qualificationTxt)
    public TextView qualificationTxt;
    @InjectView(R.id.specialityTxt)
    public TextView specialityTxt;
    @InjectView(R.id.regNoTxt)
    public TextView regNoTxt;
    @InjectView(R.id.doctorEmailTxt)
    public TextView doctorEmailTxt;
    @InjectView(R.id.hospitalNameTxt)
    public TextView hospitalNameTxt;
    @InjectView(R.id.hospitalEmailTxt)
    public TextView hospitalEmailTxt;
    @InjectView(R.id.hospitalAddressTxt)
    public TextView hospitalAddressTxt;
    @InjectView(R.id.hospitalMobileTxt)
    public TextView hospitalMobileTxt;
    @InjectView(R.id.hospitalPhoneTxt)
    public TextView hospitalPhoneTxt;
    @InjectView(R.id.footerMsgTxt)
    public TextView footerMsgTxt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_details_view);
        ButterKnife.inject(this);
        setUpToolbar();
        setOnclickListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        realm = RealmMasterClass.initializeRealm(this);
        getBundleValues();
    }

    private void getBundleValues() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Boolean isDoctorExists = bundle.getBoolean(Constants.IS_DOCTOR_EXIST);
            if (isDoctorExists) {
                setInitialValues();
            }
        }
    }

    private void setInitialValues() {
        DoctorModel model = realm.where(DoctorModel.class).findFirst();
        if (model != null) {
            doctorNameTxt.setText(model.getName());
            qualificationTxt.setText(model.getQualification());
            specialityTxt.setText(model.getSpeciality());
            regNoTxt.setText(model.getREGNo());
            doctorEmailTxt.setText(model.getEmail());
            hospitalNameTxt.setText(model.getHospitalName());
            hospitalEmailTxt.setText(model.getHospitalEmail());
            hospitalAddressTxt.setText(model.getHospitalAddress());
            hospitalMobileTxt.setText(model.getHospitalMobileNo());
            hospitalPhoneTxt.setText(model.getHospitalPhoneNumber());
            footerMsgTxt.setText(model.getFooterMessage());
            if (model.getHospitalLogoBytes() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(model.getHospitalLogoBytes(), 0, model.getHospitalLogoBytes().length);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                //  Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, true);
                int ih = (int) (bitmap.getHeight() * (82.0 / bitmap.getWidth()));
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 82, ih, true);
                hospitalLogoImg.setImageBitmap(scaledBitmap);
            }
        }
    }

    private void setOnclickListener() {
        //  editDoctorLyt.setOnClickListener(this);
    }

    private void setUpToolbar() {
        ActionBar actionBar = getActionbarToolbar();
        pageTitleTxt = (TextView) findViewById(R.id.actionbarTitleTxt);
        pageTitleTxt.setText(Constants.PageTitles.KEY_DOCTOR_DETAILS);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(DoctorDetailsViewActivity.this, DoctorDetailsAddActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.IS_DOCTOR_EXIST, true);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
