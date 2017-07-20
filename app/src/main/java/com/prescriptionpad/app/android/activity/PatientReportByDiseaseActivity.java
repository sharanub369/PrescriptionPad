package com.prescriptionpad.app.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.prescriptionpad.app.android.R;
import com.prescriptionpad.app.android.adapter.CustomPatientReportViewAdapter;
import com.prescriptionpad.app.android.model.DiseaseModel;
import com.prescriptionpad.app.android.model.PatientModel;
import com.prescriptionpad.app.android.util.Constants;
import com.prescriptionpad.app.android.util.CustomAutoCompleteTextView;
import com.prescriptionpad.app.android.util.RealmMasterClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sharana.b on 4/21/2017.
 */
public class PatientReportByDiseaseActivity extends NavigationActivity implements View.OnClickListener {

    private Toolbar actionBarToolbar;
    private TextView pageTitleTxt;
    private Realm realm;

    @InjectView(R.id.detailsExistLyt)
    public LinearLayout detailsExistLyt;
    @InjectView(R.id.detailsNonExistLyt)
    public LinearLayout detailsNonExistLyt;
    @InjectView(R.id.searchBtnTxt)
    public TextView searchBtnTxt;
    @InjectView(R.id.diseaseNameEdtTxt)
    public CustomAutoCompleteTextView diseaseNameEdtTxt;
    @InjectView(R.id.detailsRecyclerView)
    public RecyclerView detailsRecyclerView;

    private Context context;
    private RecyclerView.LayoutManager layoutManager;
    private String diseaseName = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_patient_report_by_disease);
        ButterKnife.inject(this);
        setUpToolbar();
        setOnclickListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        realm = RealmMasterClass.initializeRealm(this);
        layoutManager = new LinearLayoutManager(this);
        setAutoCompleteDiseaseValues();
    }

    private void setAutoCompleteDiseaseValues() {
        RealmResults<DiseaseModel> results = realm.where(DiseaseModel.class).findAll();
        List<String> diseaseNameList = new ArrayList<>();
        for (DiseaseModel model : results) {
            diseaseNameList.add(model.getDiseaseName());
        }
        ArrayAdapter<String> adapterUids = new ArrayAdapter<String>
                (this, R.layout.custom_autocomplete_item_view, diseaseNameList);
        diseaseNameEdtTxt.setAdapter(adapterUids);
        diseaseNameEdtTxt.setThreshold(1);
    }

    private void setOnclickListener() {
        searchBtnTxt.setOnClickListener(this);
    }

    private void setUpToolbar() {
        ActionBar actionBar = getActionbarToolbar();
        pageTitleTxt = (TextView) findViewById(R.id.actionbarTitleTxt);
        pageTitleTxt.setText("Patient Report");
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
        switch (view.getId()) {
            case R.id.searchBtnTxt:
                setValuesToView();
                break;
        }
    }

    private void setValuesToView() {
        if (!diseaseNameEdtTxt.getText().toString().isEmpty()) {
            diseaseName = diseaseNameEdtTxt.getText().toString();
            RealmResults<DiseaseModel> results = realm.where(DiseaseModel.class).equalTo(Constants.FieldsKey.KEY_DISEASE_NAME, diseaseName).findAll();
            List uidList = new ArrayList();
            if (results.size() > 0) {
                for (DiseaseModel model : results) {
                    uidList.add(model.getUID());
                }
                if (uidList.size() > 0) {
                    List<PatientModel> patientList = new ArrayList<>();
                    for (Object obj : uidList) {
                        long uid = (long) obj;
                        PatientModel patientModel = realm.where(PatientModel.class).equalTo(Constants.FieldsKey.KEY_UID, uid).findFirst();
                        if (patientModel != null) {
                            patientList.add(patientModel);
                        }
                    }
                    if (patientList.size() > 0) {
                        detailsExistLyt.setVisibility(View.VISIBLE);
                        detailsNonExistLyt.setVisibility(View.GONE);
                        CustomPatientReportViewAdapter adapter = new CustomPatientReportViewAdapter(this, this, patientList, diseaseName);
                        detailsRecyclerView.setLayoutManager(layoutManager);
                        detailsRecyclerView.setAdapter(adapter);
                    } else {
                        detailsExistLyt.setVisibility(View.GONE);
                        detailsNonExistLyt.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                detailsExistLyt.setVisibility(View.GONE);
                detailsNonExistLyt.setVisibility(View.VISIBLE);
            }
        } else {
            Toast.makeText(this, Constants.Errors.KEY_DISEASE_NAME_REQUIRED, Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        realm.close();
    }
}
