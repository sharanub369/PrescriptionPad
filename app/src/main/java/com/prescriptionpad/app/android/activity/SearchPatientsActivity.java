package com.prescriptionpad.app.android.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.prescriptionpad.app.android.R;
import com.prescriptionpad.app.android.adapter.CustomHistoryAdapter;
import com.prescriptionpad.app.android.adapter.CustomSearchResultByDateAdapter;
import com.prescriptionpad.app.android.model.VisitModel;
import com.prescriptionpad.app.android.model.LabTestModel;
import com.prescriptionpad.app.android.model.DiseaseModel;
import com.prescriptionpad.app.android.model.DoctorModel;
import com.prescriptionpad.app.android.model.MedicineModel;
import com.prescriptionpad.app.android.model.PatientModel;
import com.prescriptionpad.app.android.util.Constants;
import com.prescriptionpad.app.android.util.CustomAutoCompleteTextView;
import com.prescriptionpad.app.android.util.RealmMasterClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sharana.b on 4/20/2017.
 */
public class SearchPatientsActivity extends NavigationActivity implements TextWatcher, DatePickerDialog.OnDateSetListener {
    private static final String LOG_TAG = SearchPatientsActivity.class.getSimpleName();
    private Calendar mCalendar;
    private Realm realm;
    private Toolbar actionBarToolbar;
    public RecyclerView.LayoutManager layoutManager;
    private TextView pageTitleTxt;
    private long UID = 0;
    private String Pcode;
    private String patientName;
    private String createdDate;
    private Boolean isDataExists = false;

    @InjectView(R.id.searchResultsByUidNameLyt)
    public LinearLayout searchResultsByUidNameLyt;
    @InjectView(R.id.searchResultsByDateLyt)
    public LinearLayout searchResultsByDateLyt;
    @InjectView(R.id.detailsNoLyt)
    public LinearLayout detailsNoLyt;

    @InjectView(R.id.searchByUidEdtTxt)
    public CustomAutoCompleteTextView searchByUidEdtTxt;
    @InjectView(R.id.searchByPcodeEdtTxt)
    public CustomAutoCompleteTextView searchByPcodeEdtTxt;
    @InjectView(R.id.searchByNameEdtTxt)
    public CustomAutoCompleteTextView searchByNameEdtTxt;
    @InjectView(R.id.searchByDateEdtTxt)
    public EditText searchByDateEdtTxt;
    @InjectView(R.id.labTestRecyclerView)
    public RecyclerView complaintRecyclerView;
    @InjectView(R.id.diseaseRecyclerView)
    public RecyclerView diseaseRecyclerView;
    @InjectView(R.id.visitRecyclerView)
    public RecyclerView adviceRecyclerView;
    @InjectView(R.id.medicinesRecyclerView)
    public RecyclerView medicinesRecyclerView;
    @InjectView(R.id.recyclerView)
    public RecyclerView recyclerView;
    @InjectView(R.id.noLabTestErrorTxt)
    public TextView noLabTestErrorTxt;
    @InjectView(R.id.noDiseaseErrorTxt)
    public TextView noDiseaseErrorTxt;
    @InjectView(R.id.noVisitErrorTxt)
    public TextView noVisitErrorTxt;
    @InjectView(R.id.noMedErrorTxt)
    public TextView noMedErrorTxt;

    //For Hospital
    @InjectView(R.id.hospitalNameTxt)
    public TextView hospitalNameTxt;
    @InjectView(R.id.doctorNameEdtTxt)
    public TextView doctorNameTxt;
    @InjectView(R.id.qualificationEdtTxt)
    public TextView qualificationTxt;
    @InjectView(R.id.hospitalAddressEdtTxt)
    public TextView hospitalAddressTxt;
    @InjectView(R.id.hospitalPhoneNoEdtTxt)
    public TextView hospitalPhoneNoTxt;
    @InjectView(R.id.doctorEmailEdtTxt)
    public TextView doctorEmailTxt;
    @InjectView(R.id.regNoEdtTxt)
    public TextView regNoTxt;

    //For Patient
    @InjectView(R.id.patientUidTxt)
    public TextView patientUidTxt;
    @InjectView(R.id.patientNameTxt)
    public TextView patientNameTxt;
    @InjectView(R.id.patientSexAgeTxt)
    public TextView patientSexAgeTxt;
    @InjectView(R.id.patientMobileNoTxt)
    public TextView patientMobileNoTxt;
    @InjectView(R.id.patientCreateDateTxt)
    public TextView patientCreateDateTxt;
    @InjectView(R.id.patientDobTxt)
    public TextView patientDobTxt;
    @InjectView(R.id.pcodeTxt)
    public TextView pcodeTxt;
    @InjectView(R.id.guardianTxt)
    public TextView guardianTxt;
    @InjectView(R.id.bloodGroupTxt)
    public TextView bloodGroupTxt;
    @InjectView(R.id.patientEmailTxt)
    public TextView patientEmailTxt;
    @InjectView(R.id.patientAddressTxt)
    public TextView patientAddressTxt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_search_patient);
        ButterKnife.inject(this);
        setUpToolbar();
        setOnclickListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCalendar = Calendar.getInstance();
        realm = RealmMasterClass.initializeRealm(this);
        setAutoCompleteValues();
    }

    private void setUpToolbar() {
        ActionBar actionBar = getActionbarToolbar();
        pageTitleTxt = (TextView) findViewById(R.id.actionbarTitleTxt);
        pageTitleTxt.setText("Search Details");
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

    public void setAutoCompleteValues() {
        RealmResults<PatientModel> results = realm.where(PatientModel.class).findAll();
        List patientUidList = new ArrayList();
        List patientPcodeList = new ArrayList();
        List patientCreatedDateList = new ArrayList();
        List patientNameList = new ArrayList();
        for (PatientModel model : results) {
            patientUidList.add(model.getUID());
            patientPcodeList.add(model.getPCode());
            patientCreatedDateList.add(model.getCreatedDate());
            patientNameList.add(model.getPatientName());
        }

        Set uidSet = new LinkedHashSet(patientUidList);
        patientUidList.clear();
        patientUidList.addAll(uidSet);
        ArrayAdapter<String> adapterUids = new ArrayAdapter<String>
                (this, R.layout.custom_autocomplete_item_view, patientUidList);
        searchByUidEdtTxt.setAdapter(adapterUids);
        searchByUidEdtTxt.setThreshold(1);

        Set pcodeSet = new LinkedHashSet(patientPcodeList);
        patientPcodeList.clear();
        patientPcodeList.addAll(pcodeSet);
        ArrayAdapter<String> adapterPatientPcode = new ArrayAdapter<String>
                (this, R.layout.custom_autocomplete_item_view, patientPcodeList);
        searchByPcodeEdtTxt.setAdapter(adapterPatientPcode);
        searchByPcodeEdtTxt.setThreshold(1);

        Set nameSet = new LinkedHashSet(patientNameList);
        patientNameList.clear();
        patientNameList.addAll(nameSet);
        ArrayAdapter<String> adapterPatientNames = new ArrayAdapter<String>
                (this, R.layout.custom_autocomplete_item_view, patientNameList);
        searchByNameEdtTxt.setAdapter(adapterPatientNames);
        searchByNameEdtTxt.setThreshold(1);

//        Set dateSet = new LinkedHashSet(patientCreatedDateList);
//        patientCreatedDateList.clear();
//        patientCreatedDateList.addAll(dateSet);
//        ArrayAdapter<String> adapterPatientCreatedDate = new ArrayAdapter<String>
//                (this, R.layout.custom_autocomplete_item_view, patientCreatedDateList);
//        searchByDateEdtTxt.setAdapter(adapterPatientCreatedDate);
//        searchByDateEdtTxt.setThreshold(1);
    }


    private void setOnclickListener() {
        searchByDateEdtTxt.addTextChangedListener(this);
        searchByUidEdtTxt.addTextChangedListener(this);
        searchByPcodeEdtTxt.addTextChangedListener(this);
        searchByNameEdtTxt.addTextChangedListener(this);

        searchByUidEdtTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UID = Long.parseLong(adapterView.getAdapter().getItem(i).toString());
                PatientModel model = realm.where(PatientModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findFirst();
                if (model != null) {
                    UID = model.getUID();
                    searchByUidEdtTxt.setText(UID + "");
                    setInitialValues();
                }
            }
        });
        searchByPcodeEdtTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Pcode = adapterView.getAdapter().getItem(i).toString();
                PatientModel model = realm.where(PatientModel.class).equalTo(Constants.FieldsKey.KEY_PCODE, Pcode).findFirst();
                if (model != null) {
                    UID = model.getUID();
                    searchByPcodeEdtTxt.setText(model.getPCode() + "");
                    setInitialValues();
                }
            }
        });
        searchByNameEdtTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                patientName = adapterView.getAdapter().getItem(i).toString();
                PatientModel model = realm.where(PatientModel.class).equalTo(Constants.FieldsKey.KEY_PATIENT_NAME, patientName).findFirst();
                if (model != null) {
                    UID = model.getUID();
                    searchByNameEdtTxt.setText(model.getPatientName());
                    setInitialValues();
                }
            }
        });
//        searchByDateEdtTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                createdDate = adapterView.getAdapter().getItem(i).toString();
//                RealmResults<PatientModel> results = realm.where(PatientModel.class).equalTo(Constants.FieldsKey.KEY_CREATED_DATE, createdDate).findAll();
//                for (PatientModel model : results) {
//                    searchByDateEdtTxt.setText(model.getCreatedDate());
//                    setInitialValuesByDate(createdDate);
//                }
//            }
//        });
        searchByDateEdtTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                // calendar.setTimeZone(TimeZone.getTimeZone(Consts.UTC));
                int year = calendar.get(Calendar.YEAR);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                DatePickerDialog dialog = new DatePickerDialog(SearchPatientsActivity.this, android.R.style.Theme_Holo_Light_Dialog, SearchPatientsActivity.this, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setTitle("Created Date");
                dialog.show();
            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, monthOfYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateLabel();
    }

    private void updateLabel() {
        String myFormat = Constants.KEY_DATE_FORMAT; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String selectedDate = sdf.format(mCalendar.getTime());
        searchByDateEdtTxt.setText(selectedDate);
        createdDate = selectedDate;
        RealmResults<PatientModel> results = realm.where(PatientModel.class).equalTo(Constants.FieldsKey.KEY_CREATED_DATE, createdDate).findAll();
        for (PatientModel model : results) {
            searchByDateEdtTxt.setText(model.getCreatedDate());
            setInitialValuesByDate(createdDate);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.length() > 0) {
            if (searchByUidEdtTxt.getText().hashCode() == editable.hashCode()) {
                UID = Long.parseLong(editable.toString());
                PatientModel model = realm.where(PatientModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findFirst();
                if (model != null) {
                    isDataExists = true;
                    detailsNoLyt.setVisibility(View.GONE);
                    searchResultsByDateLyt.setVisibility(View.GONE);
                    searchResultsByUidNameLyt.setVisibility(View.VISIBLE);
                    UID = model.getUID();
                    patientName = model.getPatientName();
                } else {
                    isDataExists = false;
                    detailsNoLyt.setVisibility(View.VISIBLE);
                    searchResultsByDateLyt.setVisibility(View.GONE);
                    searchResultsByUidNameLyt.setVisibility(View.GONE);
                }
            } else if (searchByPcodeEdtTxt.getText().hashCode() == editable.hashCode()) {
                Pcode = editable.toString();
                PatientModel model = realm.where(PatientModel.class).equalTo(Constants.FieldsKey.KEY_PCODE, Pcode).findFirst();
                if (model != null) {
                    isDataExists = true;
                    detailsNoLyt.setVisibility(View.GONE);
                    searchResultsByDateLyt.setVisibility(View.GONE);
                    searchResultsByUidNameLyt.setVisibility(View.VISIBLE);
                    UID = model.getUID();
                    patientName = model.getPatientName();
                } else {
                    isDataExists = false;
                    detailsNoLyt.setVisibility(View.VISIBLE);
                    searchResultsByDateLyt.setVisibility(View.GONE);
                    searchResultsByUidNameLyt.setVisibility(View.GONE);
                }
            } else if (searchByNameEdtTxt.getText().hashCode() == editable.hashCode()) {
                patientName = editable.toString();
                PatientModel model = realm.where(PatientModel.class).equalTo(Constants.FieldsKey.KEY_PATIENT_NAME, patientName).findFirst();
                if (model != null) {
                    isDataExists = true;
                    detailsNoLyt.setVisibility(View.GONE);
                    searchResultsByDateLyt.setVisibility(View.GONE);
                    searchResultsByUidNameLyt.setVisibility(View.VISIBLE);
                    UID = model.getUID();
                    patientName = model.getPatientName();
                } else {
                    isDataExists = false;
                    detailsNoLyt.setVisibility(View.VISIBLE);
                    searchResultsByDateLyt.setVisibility(View.GONE);
                    searchResultsByUidNameLyt.setVisibility(View.GONE);
                }
            } else if (searchByDateEdtTxt.getText().hashCode() == editable.hashCode()) {
                createdDate = editable.toString();
                PatientModel model = realm.where(PatientModel.class).equalTo(Constants.FieldsKey.KEY_CREATED_DATE, createdDate).findFirst();
                if (model != null) {
                    isDataExists = false;
                    detailsNoLyt.setVisibility(View.GONE);
                    searchResultsByUidNameLyt.setVisibility(View.GONE);
                    searchResultsByDateLyt.setVisibility(View.VISIBLE);
                    UID = model.getUID();
                    patientName = model.getPatientName();
                } else {
                    isDataExists = false;
                    detailsNoLyt.setVisibility(View.VISIBLE);
                    searchResultsByDateLyt.setVisibility(View.GONE);
                    searchResultsByUidNameLyt.setVisibility(View.GONE);
                }
            }
        }
    }

    private void setInitialValues() {
        if (UID > 0) {
            setDoctorDetailsView();
            setPatientDetails();
            setLabTestHistory();
            setDiseaseHistory();
            setVisitsHistory();
            setMedicineHistory();
        }
    }

    public void setInitialValuesByDate(String createdDate) {
        RealmResults<PatientModel> results = realm.where(PatientModel.class).equalTo(Constants.FieldsKey.KEY_CREATED_DATE, createdDate).findAll();
        List patientList = new ArrayList();
        for (PatientModel model : results) {
            PatientModel patientModel = new PatientModel();
            patientModel.setUID(model.getUID());
            patientModel.setPatientName(model.getPatientName());
            patientModel.setFatherHusbandName(model.getFatherHusbandName());
            patientModel.setAge(model.getAge());
            patientModel.setPatientAddress(model.getPatientAddress());
            patientList.add(patientModel);
        }

        layoutManager = new LinearLayoutManager(this);
        CustomSearchResultByDateAdapter adapter = new CustomSearchResultByDateAdapter(this, this, patientList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    private void setDoctorDetailsView() {
        RealmResults<DoctorModel> results = realm.where(DoctorModel.class).findAll();
        for (DoctorModel model : results) {
            hospitalNameTxt.setText(model.getHospitalName());
            doctorNameTxt.setText(model.getName());
            qualificationTxt.setText(model.getQualification() + "," + model.getSpeciality());
            hospitalAddressTxt.setText(model.getHospitalAddress());
            hospitalPhoneNoTxt.setText(model.getHospitalMobileNo() + ", " + model.getHospitalPhoneNumber());
            regNoTxt.setText(model.getREGNo());
            doctorEmailTxt.setText(model.getHospitalEmail());
        }
    }

    private void setPatientDetails() {
        RealmResults<PatientModel> results = realm.where(PatientModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findAll();
        for (PatientModel model : results) {
            patientUidTxt.setText(model.getUID() + "");
            patientNameTxt.setText(model.getPatientName());
            patientSexAgeTxt.setText(model.getAge() + "/" + model.getGender());
            patientMobileNoTxt.setText(model.getContactNo());
            patientCreateDateTxt.setText(model.getCreatedDate());
            patientDobTxt.setText(model.getDateOdBirth());
            pcodeTxt.setText(model.getPCode());
            guardianTxt.setText(model.getFatherHusbandName());
            bloodGroupTxt.setText(model.getBloodGroup());
            patientEmailTxt.setText(model.getEmailAddress());
            patientAddressTxt.setText(model.getPatientAddress());
        }
    }


    public void setLabTestHistory() {
        RealmResults<LabTestModel> results = realm.where(LabTestModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findAll();
        List labTestsHistoryList = new ArrayList();
        for (LabTestModel model : results) {
            String history = "";
            history = model.getLabName();
            if (model.getLabName() != null && !model.getLabName().isEmpty()) {
                history = history + ", " + model.getLabName();
            }
            if (model.getTestName() != null && !model.getTestName().isEmpty()) {
                history = history + ", " + model.getTestName();
            }
            if (model.getUnit() != null && !model.getUnit().isEmpty()) {
                history = history + ", " + model.getUnit();
            }
            if (model.getCompletionDate() != null && !model.getCompletionDate().isEmpty()) {
                history = history + ", " + model.getCompletionDate();
            }
            labTestsHistoryList.add(history);
        }
        if (labTestsHistoryList.size() > 0) {
            noLabTestErrorTxt.setVisibility(View.GONE);
            layoutManager = new LinearLayoutManager(this);
            CustomHistoryAdapter adapter = new CustomHistoryAdapter(this, this, labTestsHistoryList);
            complaintRecyclerView.setLayoutManager(layoutManager);
            complaintRecyclerView.setAdapter(adapter);
            complaintRecyclerView.setNestedScrollingEnabled(true);
        } else {
            noLabTestErrorTxt.setVisibility(View.VISIBLE);
        }
    }

    public void setDiseaseHistory() {
        RealmResults<DiseaseModel> results = realm.where(DiseaseModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findAll();
        List diseaseHistoryList = new ArrayList();
        for (DiseaseModel model : results) {
            String history = "";
            history = model.getDiseaseName();
            if (model.getDiseaseRemark() != null && !model.getDiseaseRemark().isEmpty()) {
                history = history + ", " + model.getDiseaseRemark();
            }
            diseaseHistoryList.add(history);
        }

        if (diseaseHistoryList.size() > 0) {
            noDiseaseErrorTxt.setVisibility(View.GONE);
            layoutManager = new LinearLayoutManager(this);
            CustomHistoryAdapter adapter = new CustomHistoryAdapter(this, this, diseaseHistoryList);
            diseaseRecyclerView.setLayoutManager(layoutManager);
            diseaseRecyclerView.setAdapter(adapter);
            diseaseRecyclerView.setNestedScrollingEnabled(true);
        } else {
            noDiseaseErrorTxt.setVisibility(View.VISIBLE);
        }
    }

    public void setVisitsHistory() {
        RealmResults<VisitModel> results = realm.where(VisitModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findAll();
        List visitHistoryList = new ArrayList();
        for (VisitModel model : results) {
            String history = "";
            history = model.getNextVisitDate();
            if (model.getAssignedDoctor() != null && !model.getAssignedDoctor().isEmpty()) {
                history = history + ", " + model.getAssignedDoctor();
            }
            if (model.getNotes() != null && !model.getNotes().isEmpty()) {
                history = history + ", " + model.getNotes();
            }
            visitHistoryList.add(history);
        }

        if (visitHistoryList.size() > 0) {
            noVisitErrorTxt.setVisibility(View.GONE);
            layoutManager = new LinearLayoutManager(this);
            CustomHistoryAdapter adapter = new CustomHistoryAdapter(this, this, visitHistoryList);
            adviceRecyclerView.setLayoutManager(layoutManager);
            adviceRecyclerView.setAdapter(adapter);
            adviceRecyclerView.setNestedScrollingEnabled(true);
        } else {
            noVisitErrorTxt.setVisibility(View.VISIBLE);
        }
    }

    public void setMedicineHistory() {
        RealmResults<MedicineModel> results = realm.where(MedicineModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findAll();
        List medicinesHistoryList = new ArrayList();
        for (MedicineModel model : results) {
            String history = "";
            history = model.getBrandName();
            if (model.getDosageFrom() != null && !model.getDosageFrom().isEmpty()) {
                history = history + ", " + model.getDosageFrom();
            }
            if (model.getStrength() != null && !model.getStrength().isEmpty()) {
                history = history + ", " + model.getStrength();
            }
            if (model.getSymbols() != null && !model.getSymbols().isEmpty()) {
                history = history + ", " + model.getSymbols();
            }
            if (model.getForPeriod() != null && !model.getForPeriod().isEmpty()) {
                history = history + ", " + model.getForPeriod();
            }
            if (model.getUnit() != null && !model.getUnit().isEmpty()) {
                history = history + ", " + model.getUnit();
            }
            if (model.getHow() != null && !model.getHow().isEmpty()) {
                history = history + ", " + model.getHow();
            }
            if (model.getWhen() != null && !model.getWhen().isEmpty()) {
                history = history + ", " + model.getWhen();
            }
            if (model.getRemark() != null && !model.getRemark().isEmpty()) {
                history = history + ", " + model.getRemark();
            }

            medicinesHistoryList.add(history);
        }

        if (medicinesHistoryList.size() > 0) {
            noMedErrorTxt.setVisibility(View.GONE);
            layoutManager = new LinearLayoutManager(this);
            CustomHistoryAdapter adapter = new CustomHistoryAdapter(this, this, medicinesHistoryList);
            medicinesRecyclerView.setLayoutManager(layoutManager);
            medicinesRecyclerView.setAdapter(adapter);
            medicinesRecyclerView.setNestedScrollingEnabled(true);
        } else {
            noMedErrorTxt.setVisibility(View.VISIBLE);
        }
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
                if (isDataExists) {
                    Intent intent = new Intent(SearchPatientsActivity.this, PrescriptionDemographicActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong(Constants.KEY_UID, UID);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        realm.close();
    }
}
