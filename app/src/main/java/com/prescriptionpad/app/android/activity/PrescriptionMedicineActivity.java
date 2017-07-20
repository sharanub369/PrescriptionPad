package com.prescriptionpad.app.android.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.prescriptionpad.app.android.adapter.CustomMedViewAdapter;
import com.prescriptionpad.app.android.model.MedicineModel;
import com.prescriptionpad.app.android.util.Constants;
import com.prescriptionpad.app.android.util.CustomAutoCompleteTextView;
import com.prescriptionpad.app.android.util.RealmMasterClass;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.prescriptionpad.app.android.R;
import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by sharana.b on 4/12/2017.
 */
public class PrescriptionMedicineActivity extends NavigationActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private long UID = 0;
    private Realm realm;
    private Toolbar actionBarToolbar;
    private TextView pageTitleTxt;
    private Context context;
    private Calendar mCalendar;
    private RecyclerView.LayoutManager layoutManager;

    @InjectView(R.id.medicineBtn)
    public Button medicineBtn;
    @InjectView(R.id.previewBtn)
    public Button previewBtn;
    @InjectView(R.id.demographicBtn)
    public Button demographicBtn;
    @InjectView(R.id.addMedicineBtnTxt)
    public TextView addMedicineBtnTxt;
    @InjectView(R.id.brandNameEdtTxt)
    public CustomAutoCompleteTextView brandNameEdtTxt;
    @InjectView(R.id.dosageFromEdtTxt)
    public EditText dosageFromEdtTxt;
    @InjectView(R.id.strengthEdtTxt)
    public EditText strengthEdtTxt;
    @InjectView(R.id.symbolsAutoCompleteTxt)
    public CustomAutoCompleteTextView symbolsAutoCompleteTxt;
    @InjectView(R.id.periodEdtTxt)
    public CustomAutoCompleteTextView periodEdtTxt;
    @InjectView(R.id.unitEdtTxt)
    public CustomAutoCompleteTextView unitEdtTxt;
    @InjectView(R.id.howEdtTxt)
    public CustomAutoCompleteTextView howEdtTxt;
    @InjectView(R.id.whenEdtTxt)
    public CustomAutoCompleteTextView whenEdtTxt;
    @InjectView(R.id.remarkEdtTxt)
    public EditText remarkEdtTxt;
    @InjectView(R.id.medRecyclerView)
    public RecyclerView medRecyclerView;

    private List<MedicineModel> medicineList = new ArrayList<MedicineModel>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_prescription_medicine);
        ButterKnife.inject(this);
        this.context = this;
        setUpToolbar();
        setOnclickListener();
        setInitialValues();
    }

    @Override
    protected void onStart() {
        super.onStart();
        realm = RealmMasterClass.initializeRealm(this);
        fetchMedicines();
    }

    private void setInitialValues() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            UID = bundle.getLong(Constants.KEY_UID);
        }
        setAutoCompliteTextviewValues();
    }

    private void setAutoCompliteTextviewValues() {

        String[] medNames = getResources().getStringArray(R.array.medName);
        ArrayAdapter<String> adapterMedNames = new ArrayAdapter<String>
                (this, R.layout.custom_autocomplete_item_view, medNames);
        brandNameEdtTxt.setAdapter(adapterMedNames);
        brandNameEdtTxt.setThreshold(1);

        String[] symbols = getResources().getStringArray(R.array.symbols);
        ArrayAdapter<String> adapterSymbols = new ArrayAdapter<String>
                (this, R.layout.custom_autocomplete_item_view, symbols);
        symbolsAutoCompleteTxt.setAdapter(adapterSymbols);
        symbolsAutoCompleteTxt.setThreshold(1);


        String[] periods = getResources().getStringArray(R.array.periods);
        ArrayAdapter<String> adapterPeriods = new ArrayAdapter<String>
                (this, R.layout.custom_autocomplete_item_view, periods);
        periodEdtTxt.setAdapter(adapterPeriods);
        periodEdtTxt.setThreshold(1);

        String[] units = getResources().getStringArray(R.array.units);
        ArrayAdapter<String> adapterUnits = new ArrayAdapter<String>
                (this, R.layout.custom_autocomplete_item_view, units);
        unitEdtTxt.setAdapter(adapterUnits);
        unitEdtTxt.setThreshold(1);

        String[] how = getResources().getStringArray(R.array.how);
        ArrayAdapter<String> adapterHow = new ArrayAdapter<String>
                (this, R.layout.custom_autocomplete_item_view, how);
        howEdtTxt.setAdapter(adapterHow);
        howEdtTxt.setThreshold(1);

        String[] when = getResources().getStringArray(R.array.when);
        ArrayAdapter<String> adapterWhen = new ArrayAdapter<String>
                (this, R.layout.custom_autocomplete_item_view, when);
        whenEdtTxt.setAdapter(adapterWhen);
        whenEdtTxt.setThreshold(1);
    }

    private void fetchMedicines() {
        RealmResults<MedicineModel> results = realm.where(MedicineModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findAll();
        medicineList.clear();
        for (MedicineModel model : results) {
            medicineList.add(model);
        }
        if (medicineList.size() <= 0) {
            layoutManager = new LinearLayoutManager(this);
            CustomMedViewAdapter adapter = new CustomMedViewAdapter(this, this, null);
            medRecyclerView.setLayoutManager(layoutManager);
            medRecyclerView.setAdapter(adapter);
        } else {
            layoutManager = new LinearLayoutManager(this);
            CustomMedViewAdapter adapter = new CustomMedViewAdapter(this, this, medicineList);
            medRecyclerView.setLayoutManager(layoutManager);
            medRecyclerView.setAdapter(adapter);
            medRecyclerView.setNestedScrollingEnabled(false);
        }
    }

    private void setOnclickListener() {
        previewBtn.setOnClickListener(this);
        medicineBtn.setOnClickListener(this);
        demographicBtn.setOnClickListener(this);
        addMedicineBtnTxt.setOnClickListener(this);
        dosageFromEdtTxt.setOnClickListener(this);
    }


    private void setUpToolbar() {
        ActionBar actionBar = getActionbarToolbar();
        pageTitleTxt = (TextView) findViewById(R.id.actionbarTitleTxt);
        pageTitleTxt.setText(Constants.PageTitles.KEY_MED_DETAILS);
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
            case R.id.addMedicineBtnTxt:
                addMedicine();
                break;
            case R.id.medicineBtn:
                break;
            case R.id.previewBtn:
                intent = new Intent(PrescriptionMedicineActivity.this, PrescriptionPreviewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong(Constants.KEY_UID, UID);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.demographicBtn:
                intent = new Intent(PrescriptionMedicineActivity.this, PrescriptionDemographicActivity.class);
                startActivity(intent);
                break;
            case R.id.dosageFromEdtTxt:
                showCalender();
                break;
            default:
                break;
        }
    }

    private void saveMedicine(MedicineModel model) {
        if (model != null) {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(getMedicineModel(model));
            realm.commitTransaction();
        }

        Toast.makeText(this, "Saved Medication Successfully.", Toast.LENGTH_LONG).show();
    }

    private RealmObject getMedicineModel(MedicineModel medicineModel) {
        MedicineModel model = new MedicineModel();
        RealmResults<MedicineModel> results = realm.where(MedicineModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findAll();
        model.setMedId(results.size() + 1);
        model.setUID(UID);
        model.setBrandName(medicineModel.getBrandName());
        model.setDosageFrom(medicineModel.getDosageFrom());
        model.setStrength(medicineModel.getStrength());
        model.setSymbols(medicineModel.getSymbols());
        model.setForPeriod(medicineModel.getForPeriod());
        model.setUnit(medicineModel.getUnit());
        model.setHow(medicineModel.getHow());
        model.setWhen(medicineModel.getWhen());
        model.setRemark(medicineModel.getRemark());

        SimpleDateFormat sdf = new SimpleDateFormat(Constants.KEY_DATE_FORMAT);
        Date date = new Date();
        String dateString = sdf.format(date);
        model.setCreatedDate(dateString);
        medicineList.add(model);
        return model;
    }

    private void addMedicine() {
        if (!brandNameEdtTxt.getText().toString().isEmpty()) {
            saveMedicineDetails();
        } else {
            Toast.makeText(this, Constants.Errors.KEY_MED_NAME_REQUIRED, Toast.LENGTH_LONG).show();
        }
    }

    private void saveMedicineDetails() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_dialog_confirm_window, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        // set color transpartent
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView saveBtnTxt = (TextView) dialogView.findViewById(R.id.saveBtnTxt);
        TextView cancelBtnTxt = (TextView) dialogView.findViewById(R.id.cancelBtnTxt);
        saveBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MedicineModel model = new MedicineModel();
                model.setBrandName(brandNameEdtTxt.getText().toString());
                model.setDosageFrom(dosageFromEdtTxt.getText().toString());
                model.setStrength(strengthEdtTxt.getText().toString());
                model.setSymbols(symbolsAutoCompleteTxt.getText().toString());
                model.setForPeriod(periodEdtTxt.getText().toString());
                model.setUnit(unitEdtTxt.getText().toString());
                model.setHow(howEdtTxt.getText().toString());
                model.setWhen(whenEdtTxt.getText().toString());
                model.setRemark(remarkEdtTxt.getText().toString());

                saveMedicine(model);
                layoutManager = new LinearLayoutManager(context);
                CustomMedViewAdapter adapter = new CustomMedViewAdapter(context, PrescriptionMedicineActivity.this, medicineList);
                medRecyclerView.setLayoutManager(layoutManager);
                medRecyclerView.setAdapter(adapter);
                medRecyclerView.setNestedScrollingEnabled(false);


                Toast.makeText(PrescriptionMedicineActivity.this, Constants.SuccessMessages.KEY_SAVE_TEST, Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        cancelBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        realm.close();
    }


    private void showCalender() {
        mCalendar = Calendar.getInstance();
        int year = mCalendar.get(Calendar.YEAR);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        int month = mCalendar.get(Calendar.MONTH);
        DatePickerDialog dialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog, this, year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setTitle("Dosage From Date");
        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, monthOfYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String myFormat = Constants.KEY_DATE_FORMAT; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String selectedDate = sdf.format(mCalendar.getTime());
        dosageFromEdtTxt.setText(selectedDate);
    }
}
