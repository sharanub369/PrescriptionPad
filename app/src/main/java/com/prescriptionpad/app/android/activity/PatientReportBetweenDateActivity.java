package com.prescriptionpad.app.android.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.prescriptionpad.app.android.R;
import com.prescriptionpad.app.android.adapter.CustomPatientReportByDateAdapter;
import com.prescriptionpad.app.android.adapter.CustomPatientReportViewAdapter;
import com.prescriptionpad.app.android.model.DiseaseModel;
import com.prescriptionpad.app.android.model.PatientModel;
import com.prescriptionpad.app.android.util.Constants;
import com.prescriptionpad.app.android.util.CustomAutoCompleteTextView;
import com.prescriptionpad.app.android.util.RealmMasterClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sharana.b on 4/27/2017.
 */
public class PatientReportBetweenDateActivity extends NavigationActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private static final String KEY_CREATED_DATE = "";

    private Toolbar actionBarToolbar;
    private Calendar mCalendar;
    private TextView pageTitleTxt;
    private Realm realm;

    @InjectView(R.id.detailsExistLyt)
    public LinearLayout detailsExistLyt;
    @InjectView(R.id.detailsNonExistLyt)
    public LinearLayout detailsNonExistLyt;
    @InjectView(R.id.searchBtnTxt)
    public TextView searchBtnTxt;
    @InjectView(R.id.startDateEdtTxt)
    public EditText startDateEdtTxt;
    @InjectView(R.id.endDateEdtTxt)
    public EditText endDateEdtTxt;
    @InjectView(R.id.detailsRecyclerView)
    public RecyclerView detailsRecyclerView;

    private Context context;
    private RecyclerView.LayoutManager layoutManager;
    private Boolean isStartDate = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_report_between_date);
        ButterKnife.inject(this);
        setUpToolbar();
        setOnclickListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCalendar = Calendar.getInstance();
        realm = RealmMasterClass.initializeRealm(this);
        layoutManager = new LinearLayoutManager(this);
    }


    private void setOnclickListener() {
        startDateEdtTxt.setOnClickListener(this);
        endDateEdtTxt.setOnClickListener(this);
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
    protected void onStop() {
        super.onStop();
        realm.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startDateEdtTxt:
                isStartDate = true;
                showDateCalender();
                break;
            case R.id.endDateEdtTxt:
                isStartDate = false;
                showDateCalender();
                break;
            case R.id.searchBtnTxt:
                searchPatientDetails();
                break;
        }
    }

    private void searchPatientDetails() {
        List<PatientModel> patientModelList = new ArrayList<PatientModel>();
        if (isFieldsValid()) {
            RealmResults<PatientModel> results = realm.where(PatientModel.class).findAll();
            for (PatientModel model : results) {
                SimpleDateFormat sdf = new SimpleDateFormat(Constants.KEY_DATE_FORMAT);
                try {
                    Date startDate = sdf.parse(startDateEdtTxt.getText().toString());
                    Date endDate = sdf.parse(endDateEdtTxt.getText().toString());
                    Date createdDate = sdf.parse(model.getCreatedDate());
                    if (createdDate.after(startDate) && createdDate.before(endDate)) {
                        patientModelList.add(model);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                setValuesToView(patientModelList);
            }
        }
    }

    private void setValuesToView(List<PatientModel> patientModelList) {
        if (patientModelList.size() > 0) {
            detailsExistLyt.setVisibility(View.VISIBLE);
            detailsNonExistLyt.setVisibility(View.GONE);
            CustomPatientReportByDateAdapter adapter = new CustomPatientReportByDateAdapter(this, this, patientModelList);
            detailsRecyclerView.setLayoutManager(layoutManager);
            detailsRecyclerView.setAdapter(adapter);
        } else {
            detailsExistLyt.setVisibility(View.GONE);
            detailsNonExistLyt.setVisibility(View.VISIBLE);
        }

    }

    public Boolean isFieldsValid() {
        Boolean isFieldsValid = true;
        if (startDateEdtTxt.getText().toString().isEmpty()) {
            isFieldsValid = false;
            Toast.makeText(this, Constants.Errors.KEY_START_DATE_REQUIRED, Toast.LENGTH_LONG).show();
        } else if (endDateEdtTxt.getText().toString().isEmpty()) {
            Toast.makeText(this, Constants.Errors.KEY_END_DATE_REQUIRED, Toast.LENGTH_LONG).show();
            isFieldsValid = false;
        }
        if (isFieldsValid == true) {
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.KEY_DATE_FORMAT);
            try {
                Date startDate = sdf.parse(startDateEdtTxt.getText().toString());
                Date endDate = sdf.parse(endDateEdtTxt.getText().toString());
                if (endDate.before(startDate)) {
                    isFieldsValid = false;
                    Toast.makeText(this, Constants.Errors.KEY_END_DATE_GREATER, Toast.LENGTH_LONG).show();
                }
            } catch (ParseException e) {
                isFieldsValid = false;
                e.printStackTrace();
            }
        }
        return isFieldsValid;
    }

    private void showDateCalender() {
//        mCalendar = Calendar.getInstance();
//        new DatePickerDialog(this, this, mCalendar
//                .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
//                mCalendar.get(Calendar.DAY_OF_MONTH)).show();
        Calendar calendar = Calendar.getInstance();
        // calendar.setTimeZone(TimeZone.getTimeZone(Consts.UTC));

        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        DatePickerDialog dialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog, this, year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (isStartDate) {
            dialog.setTitle("Start Date");
        } else {
            dialog.setTitle("End Dates");
        }
        dialog.show();
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
        if (isStartDate) {
            startDateEdtTxt.setText(selectedDate);
        } else {
            endDateEdtTxt.setText(selectedDate);
        }
    }
}
