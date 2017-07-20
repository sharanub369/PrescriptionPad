package com.prescriptionpad.app.android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.prescriptionpad.app.android.R;
import com.prescriptionpad.app.android.model.LabTestModel;
import com.prescriptionpad.app.android.model.VisitModel;
import com.prescriptionpad.app.android.model.DiseaseModel;
import com.prescriptionpad.app.android.model.DoctorModel;
import com.prescriptionpad.app.android.model.MedicineModel;
import com.prescriptionpad.app.android.model.PatientModel;
import com.prescriptionpad.app.android.model.ViewHistoryTempModel;
import com.prescriptionpad.app.android.util.Constants;
import com.prescriptionpad.app.android.util.CustomAutoCompleteTextView;
import com.prescriptionpad.app.android.util.HeaderFooterPageEvent;
import com.prescriptionpad.app.android.util.ImageFileHandler;
import com.prescriptionpad.app.android.util.RealmMasterClass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sharana.b on 4/18/2017.
 */
public class ViewHistoryActivity extends NavigationActivity implements View.OnClickListener {


    private static final String LOG_TAG = PrescriptionPreviewActivity.class.getSimpleName();

    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 20,
            Font.BOLD, BaseColor.BLUE);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD, BaseColor.BLUE);
    private static Font subFontBlue = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD, BaseColor.BLUE);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD, BaseColor.BLACK);
    private static Font smallNormal = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL);


    private Realm realm;
    private Toolbar actionBarToolbar;
    private TextView pageTitleTxt;
    private DoctorModel model;
    private long UID = 0;
    private String patientName;
    private String imageFileUri = null;
    private String hospitalName = "";
    private String doctorName = "";
    private String footerMessage;

    @InjectView(R.id.shareSaveImg)
    public ImageView shareSaveImg;

    @InjectView(R.id.searchByUidEdtTxt)
    public CustomAutoCompleteTextView searchByUidEdtTxt;
    @InjectView(R.id.searchByNameEdtTxt)
    public CustomAutoCompleteTextView searchByNameEdtTxt;
    @InjectView(R.id.historyLyt)
    public LinearLayout historyLyt;

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
    @InjectView(R.id.detailsNoLyt)
    public LinearLayout detailsNoLyt;
    @InjectView(R.id.detailsExistLyt)
    public LinearLayout detailsExistLyt;

    private List patientUidList;
    private List patientNameList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);
        ButterKnife.inject(this);
        setUpToolbar();
        setBundleValues();
        setOnclickListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        realm = RealmMasterClass.initializeRealm(this);
        setAutoCompleteValues();
        setInitialValues();
    }


    private void setBundleValues() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            UID = bundle.getLong(Constants.KEY_UID);
            patientName = bundle.getString(Constants.KEY_PATIENT_NAME);
            searchByUidEdtTxt.setText(UID + "");
            searchByNameEdtTxt.setText(patientName);
        }
    }

    private void setOnclickListener() {
        shareSaveImg.setOnClickListener(this);
        searchByUidEdtTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    UID = Long.parseLong(editable.toString());
                    PatientModel model = realm.where(PatientModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findFirst();
                    if (model != null) {
                        UID = model.getUID();
                        patientName = model.getPatientName();
                        detailsExistLyt.setVisibility(View.VISIBLE);
                        detailsNoLyt.setVisibility(View.GONE);
                    } else {
                        detailsExistLyt.setVisibility(View.GONE);
                        detailsNoLyt.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        searchByNameEdtTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    patientName = editable.toString();
                    PatientModel model = realm.where(PatientModel.class).equalTo(Constants.FieldsKey.KEY_PATIENT_NAME, patientName).findFirst();
                    if (model != null) {
                        UID = model.getUID();
                        patientName = model.getPatientName();
                        detailsExistLyt.setVisibility(View.VISIBLE);
                        detailsNoLyt.setVisibility(View.GONE);
                    } else {
                        detailsExistLyt.setVisibility(View.GONE);
                        detailsNoLyt.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        searchByUidEdtTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UID = Long.parseLong(adapterView.getAdapter().getItem(i).toString());
                PatientModel model = realm.where(PatientModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findFirst();
                if (model != null) {
                    searchByUidEdtTxt.setText(UID + "");
                    searchByNameEdtTxt.setText(model.getPatientName());
                    setInitialValues();
                }

            }
        });
        searchByNameEdtTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getAdapter().getItem(i).toString();
                patientName = selected;
                PatientModel model = realm.where(PatientModel.class).equalTo(Constants.FieldsKey.KEY_PATIENT_NAME, patientName).findFirst();
                UID = model.getUID();
                if (model != null) {
                    searchByUidEdtTxt.setText(UID + "");
                    searchByNameEdtTxt.setText(model.getPatientName());
                    setInitialValues();
                }
            }
        });
    }


    private void setUpToolbar() {
        ActionBar actionBar = getActionbarToolbar();
        pageTitleTxt = (TextView) findViewById(R.id.actionbarTitleTxt);
        pageTitleTxt.setText(Constants.PageTitles.KEY_HISTORY_DETAILS);
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
        patientUidList = new ArrayList();
        patientNameList = new ArrayList();
        for (PatientModel model : results) {
            patientUidList.add(model.getUID());
            patientNameList.add(model.getPatientName());
        }

        ArrayAdapter<String> adapterUids = new ArrayAdapter<String>
                (this, R.layout.custom_autocomplete_item_view, patientUidList);
        searchByUidEdtTxt.setAdapter(adapterUids);
        searchByUidEdtTxt.setThreshold(1);

        ArrayAdapter<String> adapterPatientNames = new ArrayAdapter<String>
                (this, R.layout.custom_autocomplete_item_view, patientNameList);
        searchByNameEdtTxt.setAdapter(adapterPatientNames);
        searchByNameEdtTxt.setThreshold(1);
    }

    private void setInitialValues() {
        if (UID > 0) {
            detailsExistLyt.setVisibility(View.VISIBLE);
            detailsNoLyt.setVisibility(View.GONE);
            getHospitalDetails();
            setPatientDetailsByID();
            setPatientDetailsDateWiseByID();
        } else {
            detailsExistLyt.setVisibility(View.GONE);
            detailsNoLyt.setVisibility(View.VISIBLE);
        }
    }

    private void getHospitalDetails() {
        model = realm.where(DoctorModel.class).findFirst();
        if (model != null) {
            hospitalName = model.getHospitalName();
            doctorName = model.getName();
            footerMessage = model.getFooterMessage();
        }
    }


    private void setPatientDetailsByID() {
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


    public void setPatientDetailsDateWiseByID() {
        RealmResults<MedicineModel> resultsMedicine = realm.where(MedicineModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findAllSorted(Constants.FieldsKey.KEY_CREATED_DATE);
        RealmResults<LabTestModel> resultsComplaint = realm.where(LabTestModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findAllSorted(Constants.FieldsKey.KEY_CREATED_DATE);
        RealmResults<DiseaseModel> resultsDisease = realm.where(DiseaseModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findAllSorted(Constants.FieldsKey.KEY_CREATED_DATE);
        RealmResults<VisitModel> resultsAdvice = realm.where(VisitModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findAllSorted(Constants.FieldsKey.KEY_CREATED_DATE);

        List datesList = new ArrayList();
        for (MedicineModel model : resultsMedicine) {
            datesList.add(model.getCreatedDate());
        }
        for (LabTestModel model : resultsComplaint) {
            datesList.add(model.getCreatedDate());
        }
        for (DiseaseModel model : resultsDisease) {
            datesList.add(model.getCreatedDate());
        }
        for (VisitModel model : resultsAdvice) {
            datesList.add(model.getCreatedDate());
        }

        Set<String> newSet = new LinkedHashSet<>(datesList);
        datesList.clear();
        datesList.addAll(newSet);
        final SimpleDateFormat sdf = new SimpleDateFormat(Constants.KEY_DATE_FORMAT);
        Collections.sort(datesList, new Comparator<String>() {
            public int compare(String m1, String m2) {
                try {
                    Date date1 = sdf.parse(m1);
                    Date date2 = sdf.parse(m2);
                    if (date1.getTime() > date2.getTime())
                        return -1;
                    else if (date1.getTime() == date2.getTime())
                        return 0;
                    else
                        return 1;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        historyLyt.removeAllViews();
        for (Object object : datesList) {
            String createdDate = (String) object;
            LayoutInflater inflaterDate = LayoutInflater.from(this);
            View viewDate = inflaterDate.inflate(R.layout.custom_history_date_view, null);
            TextView historyDateTxt = (TextView) viewDate.findViewById(R.id.historyDateTxt);
            historyDateTxt.setText(createdDate);
            TextView textView = new TextView(this);
            historyLyt.addView(textView);
            historyLyt.addView(viewDate);
            CardView cardView = new CardView(this);
            LinearLayout linearLayout = new LinearLayout(this);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(params);
            List historyList = null;
            ViewHistoryTempModel model = null;
            RealmResults<MedicineModel> resultMedicine = realm.where(MedicineModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).equalTo(Constants.FieldsKey.KEY_CREATED_DATE, createdDate).findAll();
            if (resultMedicine.size() > 0) {
                historyList = new ArrayList();
                for (MedicineModel model1 : resultMedicine) {
                    model = new ViewHistoryTempModel();
                    model.setTitle(Constants.SubTitles.KEY_MED_SUB_TITLE);
                    model.setHistoryText(getMedicineHistoryText(model1));
                    historyList.add(model);
                }

                int i = 0;
                for (Object object1 : historyList) {
                    ViewHistoryTempModel model1 = (ViewHistoryTempModel) object1;
                    LayoutInflater inflater = LayoutInflater.from(this);
                    View view = inflater.inflate(R.layout.custom_history_item_view, null);
                    TextView historyTitle = (TextView) view.findViewById(R.id.historyTitle);
                    TextView historyItemTxt = (TextView) view.findViewById(R.id.historyItemTxt);
                    if (i == 0) {
                        historyTitle.setText(model1.getTitle());
                    } else {
                        historyTitle.setVisibility(View.GONE);
                    }
                    historyItemTxt.setText(model1.getHistoryText());
                    linearLayout.addView(view);
                    i += 1;
                }
            }

            RealmResults<LabTestModel> resultComplaint = realm.where(LabTestModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).equalTo(Constants.FieldsKey.KEY_CREATED_DATE, createdDate).findAll();
            if (resultComplaint.size() > 0) {
                historyList = new ArrayList();
                for (LabTestModel model1 : resultComplaint) {
                    model = new ViewHistoryTempModel();
                    model.setTitle(Constants.SubTitles.KEY_LAB_SUB_TITLE);
                    model.setHistoryText(getComplaintHistoryText(model1));
                    historyList.add(model);
                }
                int j = 0;
                for (Object object1 : historyList) {
                    ViewHistoryTempModel model1 = (ViewHistoryTempModel) object1;
                    LayoutInflater inflater = LayoutInflater.from(this);
                    View view = inflater.inflate(R.layout.custom_history_item_view, null);
                    TextView historyTitle = (TextView) view.findViewById(R.id.historyTitle);
                    TextView historyItemTxt = (TextView) view.findViewById(R.id.historyItemTxt);
                    if (j == 0) {
                        historyTitle.setText(model1.getTitle());
                    } else {
                        historyTitle.setVisibility(View.GONE);
                    }
                    historyItemTxt.setText(model1.getHistoryText());
                    linearLayout.addView(view);
                    j += 1;
                }

            }


            RealmResults<DiseaseModel> resultDisease = realm.where(DiseaseModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).equalTo(Constants.FieldsKey.KEY_CREATED_DATE, createdDate).findAll();
            if (resultDisease.size() > 0) {
                historyList = new ArrayList();
                for (DiseaseModel model1 : resultDisease) {
                    model = new ViewHistoryTempModel();
                    model.setTitle(Constants.SubTitles.KEY_DISEASE_SUB_TITLE);
                    model.setHistoryText(getDiseaseHistoryText(model1));
                    historyList.add(model);
                }

                int k = 0;
                for (Object object1 : historyList) {
                    ViewHistoryTempModel model1 = (ViewHistoryTempModel) object1;
                    LayoutInflater inflater = LayoutInflater.from(this);
                    View view = inflater.inflate(R.layout.custom_history_item_view, null);
                    TextView historyTitle = (TextView) view.findViewById(R.id.historyTitle);
                    TextView historyItemTxt = (TextView) view.findViewById(R.id.historyItemTxt);
                    if (k == 0) {
                        historyTitle.setText(model1.getTitle());
                    } else {
                        historyTitle.setVisibility(View.GONE);
                    }
                    historyItemTxt.setText(model1.getHistoryText());
                    linearLayout.addView(view);
                    k += 1;
                }
            }


            RealmResults<VisitModel> resultAdvice = realm.where(VisitModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).equalTo(Constants.FieldsKey.KEY_CREATED_DATE, createdDate).findAll();
            if (resultDisease.size() > 0) {
                historyList = new ArrayList();
                for (VisitModel model1 : resultAdvice) {
                    model = new ViewHistoryTempModel();
                    model.setTitle(Constants.SubTitles.KEY_VISIT_SUB_TITLE);
                    model.setHistoryText(getAdviceHistoryText(model1));
                    historyList.add(model);
                }
                int m = 0;
                for (Object object1 : historyList) {
                    ViewHistoryTempModel model1 = (ViewHistoryTempModel) object1;
                    LayoutInflater inflater = LayoutInflater.from(this);
                    View view = inflater.inflate(R.layout.custom_history_item_view, null);
                    TextView historyTitle = (TextView) view.findViewById(R.id.historyTitle);
                    TextView historyItemTxt = (TextView) view.findViewById(R.id.historyItemTxt);
                    if (m == 0) {
                        historyTitle.setText(model1.getTitle());
                    } else {
                        historyTitle.setVisibility(View.GONE);
                    }
                    historyItemTxt.setText(model1.getHistoryText());
                    linearLayout.addView(view);
                    m += 1;
                }
            }
            cardView.addView(linearLayout);
            historyLyt.addView(cardView);
        }
    }


    private String getAdviceHistoryText(VisitModel model) {
        String history = "";
        history = model.getNextVisitDate();
        if (model.getAssignedDoctor() != null && !model.getAssignedDoctor().isEmpty()) {
            history = history + ", " + model.getAssignedDoctor();
        }
        if (model.getNotes() != null && !model.getNotes().isEmpty()) {
            history = history + ", " + model.getNotes();
        }
        return history;
    }

    private String getDiseaseHistoryText(DiseaseModel model) {
        String history = "";
        history = model.getDiseaseName();
        if (model.getDiseaseRemark() != null && !model.getDiseaseRemark().isEmpty()) {
            history = history + ", " + model.getDiseaseRemark();
        }

        return history;
    }

    private String getComplaintHistoryText(LabTestModel model) {
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

        return history;
    }

    private String getMedicineHistoryText(MedicineModel model) {
        String history = null;
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
        return history;
    }


    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.shareSaveImg:
                showOptionDialog();
                break;
            default:
                break;
        }
    }

    private void showOptionDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        // set color transpartent
        //  dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        TextView saveSharePdfTxt = (TextView) dialogView.findViewById(R.id.saveSharePdfTxt);
        TextView cancelBtnTxt = (TextView) dialogView.findViewById(R.id.cancelBtnTxt);
        saveSharePdfTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File pdfFilePath = generatePdfDoc();
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

    private File generatePdfDoc() {
        File pdfFile = null;
        File root = Environment.getExternalStorageDirectory();
        File pdfFolder = new File(root.getAbsolutePath() + "/" + Constants.KEY_PP_FOLDER_NAME);
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
        }
        Document document = null;
        try {
            pdfFile = new File(pdfFolder + File.separator + patientNameTxt.getText().toString() + "_History" + ".pdf");
            document = new Document(PageSize.A4);
            PdfWriter write = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
            addTitlePage(document);
            addDoctorDetails(document);
            addSeparatorLine(document);
            addHistorySubTitle(document);
            addSeparatorLine(document);
            addPatientPersonalDetails(document);
            addPatientDetails(document);
            addTextBottomOfPage(document, write);
            doPrint(pdfFile);
            document.close();

        } catch (Exception exp) {

        }

        return pdfFile;
    }

    private void addPatientPersonalDetails(Document document) throws DocumentException {

        PatientModel model = realm.where(PatientModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findFirst();
        if (model != null) {
            Chunk glue = new Chunk(new VerticalPositionMark());
            Paragraph p11 = new Paragraph("UID/Patient Name:\t\t" + model.getUID() + "/" + model.getPatientName(), smallNormal);
            p11.add(new Chunk(glue));
            p11.add("Pcode/Created Date:\t\t" + model.getPCode() + "/" + model.getCreatedDate());
            document.add(p11);

            Paragraph p12 = new Paragraph("Father/Husb Name:\t\t" + model.getFatherHusbandName(), smallNormal);
            p12.add(new Chunk(glue));
            p12.add("Age/Sex:\t\t" + model.getAge() + "/" + model.getGender());
            document.add(p12);

            Paragraph p21 = new Paragraph("Contact No:\t\t" + model.getContactNo(), smallNormal);
            p21.add(new Chunk(glue));
            p21.add("Email ID:\t\t" + model.getEmailAddress());
            document.add(p21);

            Paragraph p22 = new Paragraph("DOB/Blood Group:\t\t" + model.getDateOdBirth() + "/" + model.getBloodGroup(), smallNormal);
            p22.add(new Chunk(glue));
            p22.add("Address:\t\t" + model.getPatientAddress());
            document.add(p22);

            addSeparatorLine(document);
        }
    }

    private void addTitlePage(Document document)
            throws DocumentException, IOException {
        // We add one empty line
        Image image = null;
        try {
            DoctorModel model = realm.where(DoctorModel.class).findFirst();
            if (model.getHospitalLogoBytes() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(model.getHospitalLogoBytes(), 0, model.getHospitalLogoBytes().length);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                //  Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, true);
                int ih = (int) (bitmap.getHeight() * (82.0 / bitmap.getWidth()));
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 82, ih, true);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                image = Image.getInstance(stream.toByteArray());
            }


            Chunk glue = new Chunk();
            Paragraph p1 = new Paragraph();
            addEmptyLine(p1, 4);
            p1.setIndentationLeft(100);
            if (image != null) p1.add(new Chunk(image, -0.002f, -0.005f));
            p1.add(new Chunk(glue));
            Chunk chunk = new Chunk(hospitalName, catFont);
            // chunk.setBackground(new BaseColor(46, 206, 153));
            p1.add(new Chunk(chunk));
            document.add(p1);

            // Will create: Report generated by: _name, _date
            Paragraph prefaceX = new Paragraph();
            prefaceX.add(new Paragraph(
                    "Report generated by: " + doctorName + ", " + new Date(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    smallNormal));
            document.add(prefaceX);
            addEmptyLine(prefaceX, 3);

        } catch (IOException ex) {
            return;
        }

    }

    private void addDoctorDetails(Document document) throws DocumentException {
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        document.add(preface);
        Chunk glue = new Chunk(new VerticalPositionMark());
        Paragraph p1 = new Paragraph();
        Chunk greenText = new Chunk(model.getName(), subFont);
        p1.add(greenText);
        p1.add(new Chunk(glue));
        p1.add(model.getHospitalAddress());
        document.add(p1);

        Paragraph p2 = new Paragraph(model.getQualification() + "," + model.getSpeciality(), smallNormal);
        p2.add(new Chunk(glue));
        p2.add(model.getHospitalMobileNo() + ", " + model.getHospitalPhoneNumber());
        document.add(p2);

        Paragraph p3 = new Paragraph(model.getREGNo(), smallNormal);
        p3.add(new Chunk(glue));
        p3.add(model.getHospitalEmail());
        document.add(p3);
    }

    public void addHistorySubTitle(Document document) throws DocumentException {
        Chunk glue = new Chunk();
        Paragraph p1 = new Paragraph();
        p1.setIndentationLeft(120);
        p1.add(new Chunk(glue));
        Chunk chunk = new Chunk("Complete Clinical history of " + patientName, subFont);
        p1.add(new Chunk(chunk));
        document.add(p1);
        Paragraph p2 = new Paragraph();
        p2.setIndentationLeft(130);
        p2.add(new Chunk(glue));
        Chunk chunk2 = new Chunk("(In Descending order of Prescriptions Details)", smallBold);
        p2.add(new Chunk(chunk2));
        document.add(p2);
    }


    public void addPatientDetails(Document document) throws DocumentException {
        {
            RealmResults<MedicineModel> resultsMedicine = realm.where(MedicineModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findAllSorted(Constants.FieldsKey.KEY_CREATED_DATE);
            RealmResults<LabTestModel> resultsComplaint = realm.where(LabTestModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findAllSorted(Constants.FieldsKey.KEY_CREATED_DATE);
            RealmResults<DiseaseModel> resultsDisease = realm.where(DiseaseModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findAllSorted(Constants.FieldsKey.KEY_CREATED_DATE);
            RealmResults<VisitModel> resultsAdvice = realm.where(VisitModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findAllSorted(Constants.FieldsKey.KEY_CREATED_DATE);

            List datesList = new ArrayList();
            for (MedicineModel model : resultsMedicine) {
                datesList.add(model.getCreatedDate());
            }
            for (LabTestModel model : resultsComplaint) {
                datesList.add(model.getCreatedDate());
            }
            for (DiseaseModel model : resultsDisease) {
                datesList.add(model.getCreatedDate());
            }
            for (VisitModel model : resultsAdvice) {
                datesList.add(model.getCreatedDate());
            }

            Set<String> newSet = new LinkedHashSet<>(datesList);
            datesList.clear();
            datesList.addAll(newSet);
            final SimpleDateFormat sdf = new SimpleDateFormat(Constants.KEY_DATE_FORMAT);
            Collections.sort(datesList, new Comparator<String>() {
                public int compare(String m1, String m2) {
                    try {
                        Date date1 = sdf.parse(m1);
                        Date date2 = sdf.parse(m2);
                        if (date1.getTime() > date2.getTime())
                            return -1;
                        else if (date1.getTime() == date2.getTime())
                            return 0;
                        else
                            return 1;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });

            for (Object object : datesList) {
                String createdDate = (String) object;
                Paragraph p1 = new Paragraph();
                Chunk chunk = new Chunk(createdDate, subFont);
                p1.setAlignment(Element.ALIGN_CENTER);
                p1.add(chunk);
                document.add(p1);
                DottedLineSeparator dottedLine = new DottedLineSeparator();

                List historyList = new ArrayList();
                ViewHistoryTempModel model = null;
                RealmResults<MedicineModel> resultMedicine = realm.where(MedicineModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).equalTo(Constants.FieldsKey.KEY_CREATED_DATE, createdDate).findAll();
                if (resultMedicine.size() > 0) {
                    for (MedicineModel model1 : resultMedicine) {
                        model = new ViewHistoryTempModel();
                        model.setTitle(Constants.SubTitles.KEY_MED_SUB_TITLE);
                        model.setHistoryText(getMedicineHistoryText(model1));
                        historyList.add(model);
                    }
                }

                int i = 0;
                for (Object object1 : historyList) {
                    ViewHistoryTempModel model1 = (ViewHistoryTempModel) object1;
                    if (i == 0) {
                        Paragraph p11 = new Paragraph();
                        Chunk chunk2 = new Chunk(model1.getTitle(), subFontBlue);
                        p11.setAlignment(Element.ALIGN_LEFT);
                        p11.add(chunk2);
                        document.add(p11);
                    }
                    Paragraph p13 = new Paragraph();
                    Chunk chunk3 = new Chunk(model1.getHistoryText(), smallNormal);
                    p13.setAlignment(Element.ALIGN_LEFT);
                    p13.add(chunk3);
                    document.add(p13);
                    i += 1;
                    if (historyList.size() == i) {
                        dottedLine = new DottedLineSeparator();
                        dottedLine.setOffset(-2);
                        dottedLine.setGap(3f);
                        document.add(dottedLine);
                    }
                }

                historyList = new ArrayList();
                RealmResults<LabTestModel> resultComplaint = realm.where(LabTestModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).equalTo(Constants.FieldsKey.KEY_CREATED_DATE, createdDate).findAll();
                if (resultComplaint.size() > 0) {
                    for (LabTestModel model1 : resultComplaint) {
                        model = new ViewHistoryTempModel();
                        model.setTitle(Constants.SubTitles.KEY_LAB_SUB_TITLE);
                        model.setHistoryText(getComplaintHistoryText(model1));
                        historyList.add(model);
                    }
                }


                int j = 0;
                for (Object object1 : historyList) {
                    ViewHistoryTempModel model1 = (ViewHistoryTempModel) object1;
                    if (j == 0) {
                        Paragraph p11 = new Paragraph();
                        Chunk chunk2 = new Chunk(model1.getTitle(), subFontBlue);
                        p11.setAlignment(Element.ALIGN_LEFT);
                        p11.add(chunk2);
                        document.add(p11);
                    }
                    Paragraph p13 = new Paragraph();
                    Chunk chunk3 = new Chunk(model1.getHistoryText(), smallNormal);
                    p13.setAlignment(Element.ALIGN_LEFT);
                    p13.add(chunk3);
                    document.add(p13);
                    j += 1;
                    if (historyList.size() == j) {
                        dottedLine = new DottedLineSeparator();
                        dottedLine.setOffset(-2);
                        dottedLine.setGap(3f);
                        document.add(dottedLine);
                    }
                }

                historyList = new ArrayList();
                RealmResults<DiseaseModel> resultDisease = realm.where(DiseaseModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).equalTo(Constants.FieldsKey.KEY_CREATED_DATE, createdDate).findAll();
                if (resultDisease.size() > 0) {
                    for (DiseaseModel model1 : resultDisease) {
                        model = new ViewHistoryTempModel();
                        model.setTitle(Constants.SubTitles.KEY_DISEASE_SUB_TITLE);
                        model.setHistoryText(getDiseaseHistoryText(model1));
                        historyList.add(model);
                    }
                }


                int k = 0;
                for (Object object1 : historyList) {
                    ViewHistoryTempModel model1 = (ViewHistoryTempModel) object1;
                    if (k == 0) {
                        Paragraph p11 = new Paragraph();
                        Chunk chunk2 = new Chunk(model1.getTitle(), subFontBlue);
                        p11.setAlignment(Element.ALIGN_LEFT);
                        p11.add(chunk2);
                        document.add(p11);
                    }
                    Paragraph p13 = new Paragraph();
                    Chunk chunk3 = new Chunk(model1.getHistoryText(), smallNormal);
                    p13.setAlignment(Element.ALIGN_LEFT);
                    p13.add(chunk3);
                    document.add(p13);
                    k += 1;
                    if (historyList.size() == k) {
                        dottedLine = new DottedLineSeparator();
                        dottedLine.setOffset(-2);
                        dottedLine.setGap(3f);
                        document.add(dottedLine);
                    }
                }

                historyList = new ArrayList();
                RealmResults<VisitModel> resultAdvice = realm.where(VisitModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).equalTo(Constants.FieldsKey.KEY_CREATED_DATE, createdDate).findAll();
                if (resultDisease.size() > 0) {
                    for (VisitModel model1 : resultAdvice) {
                        model = new ViewHistoryTempModel();
                        model.setTitle(Constants.SubTitles.KEY_VISIT_SUB_TITLE);
                        model.setHistoryText(getAdviceHistoryText(model1));
                        historyList.add(model);
                    }
                }


                int m = 0;
                for (Object object1 : historyList) {
                    ViewHistoryTempModel model1 = (ViewHistoryTempModel) object1;
                    if (m == 0) {
                        Paragraph p11 = new Paragraph();
                        Chunk chunk2 = new Chunk(model1.getTitle(), subFontBlue);
                        p11.setAlignment(Element.ALIGN_LEFT);
                        p11.add(chunk2);
                        document.add(p11);
                    }
                    Paragraph p13 = new Paragraph();
                    Chunk chunk3 = new Chunk(model1.getHistoryText(), smallNormal);
                    p13.setAlignment(Element.ALIGN_LEFT);
                    p13.add(chunk3);
                    document.add(p13);
                    m += 1;
                }
                addSeparatorLine(document);
            }
        }
    }


    public void addTextBottomOfPage(Document document, PdfWriter writer) throws DocumentException {
        HeaderFooterPageEvent event = new HeaderFooterPageEvent(doctorName, footerMessage);
        writer.setPageEvent(event);
    }

    private void addSeparatorLine(Document document) {
        LineSeparator ls = new LineSeparator();
        ls.setLineColor(BaseColor.BLUE);
        ls.setLineWidth(2);
        try {
            document.add(new Chunk(ls));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private void doPrint(File fIle) {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        if (fIle.exists()) {
            intentShareFile.setType("application/pdf");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + fIle));
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
            startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        realm.close();
    }
}
