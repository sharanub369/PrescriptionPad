package com.prescriptionpad.app.android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itextpdf.text.Image;
import com.prescriptionpad.app.android.R;
import com.prescriptionpad.app.android.model.DoctorModel;
import com.prescriptionpad.app.android.util.BackupRestoreMasterClass;
import com.prescriptionpad.app.android.util.Constants;
import com.prescriptionpad.app.android.util.RealmMasterClass;

import java.io.ByteArrayOutputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.Realm;

/**
 * Created by sharana.b on 4/11/2017.
 */
public class HomeActivity extends NavigationActivity implements View.OnClickListener {

    private Realm realm;

    @InjectView(R.id.prescriptionLyt)
    public LinearLayout prescriptionLyt;
    @InjectView(R.id.patientHistoryLyt)
    public LinearLayout patientHistoryLyt;
    @InjectView(R.id.patientSearchLyt)
    public LinearLayout patientSearchLyt;
    @InjectView(R.id.reportBtnLyt)
    public LinearLayout reportBtnLyt;
    @InjectView(R.id.dataBackUpLyt)
    public LinearLayout dataBackUpLyt;
    @InjectView(R.id.hospitalLogoImg)
    public ImageView hospitalLogoImg;
    @InjectView(R.id.doctorNameTxt)
    public TextView doctorNameTxt;
    @InjectView(R.id.hospitalNameTxt)
    public TextView hospitalNameTxt;
    @InjectView(R.id.hospitalNameFooterTxt)
    public TextView hospitalNameFooterTxt;

    private DoctorModel model;
    private Toolbar actionBarToolbar;
    private TextView pageTitleTxt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);
        setUpToolbar();
        setOnclickListener();

    }

    private void setOnclickListener() {
        prescriptionLyt.setOnClickListener(this);
        patientHistoryLyt.setOnClickListener(this);
        patientSearchLyt.setOnClickListener(this);
        reportBtnLyt.setOnClickListener(this);
        dataBackUpLyt.setOnClickListener(this);
    }

    private void setUpToolbar() {
        ActionBar actionBar = getActionbarToolbar();
        pageTitleTxt = (TextView) findViewById(R.id.actionbarTitleTxt);
        pageTitleTxt.setText(Constants.PageTitles.KEY_BASE_TITLE);
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

    @Override
    protected void onStart() {
        super.onStart();
        realm = RealmMasterClass.initializeRealm(this);
        setInitialValues();
    }

    private void setInitialValues() {
        model = realm.where(DoctorModel.class).findFirst();
        if (model != null) {
            doctorNameTxt.setText(model.getName());
            hospitalNameTxt.setText(model.getHospitalName());
            hospitalNameFooterTxt.setText("©" + model.getHospitalName() + "");
            setHospitalLogo(model);
        }
    }

    private void setHospitalLogo(DoctorModel model) {
        if (model.getHospitalLogoBytes() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(model.getHospitalLogoBytes(), 0, model.getHospitalLogoBytes().length);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            //  Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, true);
            int ih = (int) (bitmap.getHeight() * (100.0 / bitmap.getWidth()));
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, ih, true);
            hospitalLogoImg.setImageBitmap(scaledBitmap);
            // scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        }
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
            case R.id.prescriptionLyt:
                if (model != null) {
                    intent = new Intent(HomeActivity.this, PrescriptionDemographicActivity.class);
                    startActivity(intent);
                } else {
                    showAddDoctorConfirmDialog();
                }
                break;
            case R.id.patientHistoryLyt:
                if (model != null) {
                    intent = new Intent(HomeActivity.this, ViewHistoryActivity.class);
                    startActivity(intent);
                } else {
                    showAddDoctorConfirmDialog();
                }
                break;
            case R.id.patientSearchLyt:
                if (model != null) {
                    intent = new Intent(HomeActivity.this, SearchPatientsActivity.class);
                    startActivity(intent);
                } else {
                    showAddDoctorConfirmDialog();
                }
                break;
            case R.id.reportBtnLyt:
                if (model != null) {
                    showOptionDialog();
                } else {
                    showAddDoctorConfirmDialog();
                }
                break;
            case R.id.dataBackUpLyt:
                showDialogToExportImport();
                break;
        }
    }

    private void showAddDoctorConfirmDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_add_doctor_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.dialog_theme);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.show();
        TextView addDoctorTxtBtn = (TextView) dialogView.findViewById(R.id.addDoctorTxtBtn);
        addDoctorTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, DoctorDetailsAddActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
    }

    private void showDialogToExportImport() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_data_backup_dialog,null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.dialog_theme);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
       // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        TextView exportBtnTxt = (TextView) dialogView.findViewById(R.id.exportBtnTxt);
        TextView importBtnTxt = (TextView) dialogView.findViewById(R.id.importBtnTxt);
        exportBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filePath = BackupRestoreMasterClass.exportRealmDB(realm, HomeActivity.this, HomeActivity.this);
                dialog.dismiss();
                showSuccessDialog(filePath);
            }
        });

        importBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogForConfirm();
                dialog.dismiss();
            }
        });
    }

    private void showDialogForConfirm() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_dialog_import_confirm, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.dialog_theme);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.show();
        TextView yesTxtBtn = (TextView) dialogView.findViewById(R.id.yesTxtBtn);
        TextView noTxtBtn = (TextView) dialogView.findViewById(R.id.noTxtBtn);
        yesTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BackupRestoreMasterClass.importRealDB(realm, HomeActivity.this, HomeActivity.this);
                dialog.dismiss();
            }
        });

        noTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void showSuccessDialog(String filePath) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_file_exported_success_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.dialog_theme);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.show();
        TextView fileExportSuccessTxt = (TextView) dialogView.findViewById(R.id.fileExportSuccessTxt);
        TextView okTxtBtn = (TextView) dialogView.findViewById(R.id.okTxtBtn);
        String SuccessText = "File Successfully saved in bellow path <font color='blue'>\n" + filePath + "</font>";
        fileExportSuccessTxt.setText(Html.fromHtml(SuccessText));
        okTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }


    private void showOptionDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_report_item_select_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.dialog_theme);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.show();
        TextView diseaseWiseBtnTxt = (TextView) dialogView.findViewById(R.id.diseaseWiseBtnTxt);
        TextView betweenDatesBtnTxt = (TextView) dialogView.findViewById(R.id.betweenDatesBtnTxt);
        diseaseWiseBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, PatientReportByDiseaseActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        betweenDatesBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, PatientReportBetweenDateActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        realm.close();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}

