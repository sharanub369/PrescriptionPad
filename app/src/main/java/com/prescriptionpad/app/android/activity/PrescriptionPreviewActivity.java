package com.prescriptionpad.app.android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import com.prescriptionpad.app.android.adapter.CustomHistoryAdapter;
import com.prescriptionpad.app.android.model.LabTestModel;
import com.prescriptionpad.app.android.model.VisitModel;
import com.prescriptionpad.app.android.model.DiseaseModel;
import com.prescriptionpad.app.android.model.DoctorModel;
import com.prescriptionpad.app.android.model.MedicineModel;
import com.prescriptionpad.app.android.model.PatientModel;
import com.prescriptionpad.app.android.util.Constants;
import com.prescriptionpad.app.android.util.HeaderFooterPageEvent;
import com.prescriptionpad.app.android.util.RealmMasterClass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sharana.b on 4/12/2017.
 */
public class PrescriptionPreviewActivity extends NavigationActivity implements View.OnClickListener {


    private static final String LOG_TAG = PrescriptionPreviewActivity.class.getSimpleName();

    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 20,
            Font.BOLD, BaseColor.BLUE);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD, BaseColor.MAGENTA);
    private static Font smallNormal = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL);


    private Realm realm;
    private Toolbar actionBarToolbar;
    public RecyclerView.LayoutManager layoutManager;
    private TextView pageTitleTxt;
    private long UID = 0;
    private String patientName;
    private String doctorName = "";
    private String footerMessage;


    @InjectView(R.id.shareSaveImg)
    public ImageView shareSaveImg;
    @InjectView(R.id.medicineBtn)
    public Button medicineBtn;
    @InjectView(R.id.previewBtn)
    public Button previewBtn;
    @InjectView(R.id.demographicBtn)
    public Button demographicBtn;
    @InjectView(R.id.labTestRecyclerView)
    public RecyclerView labTestRecyclerView;
    @InjectView(R.id.noLabTestErrorTxt)
    public TextView noLabTestErrorTxt;
    @InjectView(R.id.diseaseRecyclerView)
    public RecyclerView diseaseRecyclerView;
    @InjectView(R.id.noDiseaseErrorTxt)
    public TextView noDiseaseErrorTxt;
    @InjectView(R.id.visitRecyclerView)
    public RecyclerView visitRecyclerView;
    @InjectView(R.id.noVisitErrorTxt)
    public TextView noVisitErrorTxt;
    @InjectView(R.id.medicinesRecyclerView)
    public RecyclerView medicinesRecyclerView;
    @InjectView(R.id.noMedErrorTxt)
    public TextView noMedErrorTxt;

    //For Hospital
    @InjectView(R.id.hospitalLogoImg)
    public ImageView hospitalLogoImg;
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

    public LinearLayout viewHistoryLyt;
    private String imageFileUri = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription_preview);
        ButterKnife.inject(this);
        setUpToolbar();
        setOnclickListener();
        setBundleValues();
    }

    private void setBundleValues() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            UID = bundle.getLong(Constants.KEY_UID);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        realm = RealmMasterClass.initializeRealm(this);
        setInitialValues();
    }

    private void setOnclickListener() {
        previewBtn.setOnClickListener(this);
        medicineBtn.setOnClickListener(this);
        demographicBtn.setOnClickListener(this);
        shareSaveImg.setOnClickListener(this);
        viewHistoryLyt.setOnClickListener(this);
    }


    private void setUpToolbar() {
        ActionBar actionBar = getActionbarToolbar();
        pageTitleTxt = (TextView) findViewById(R.id.actionbarTitleTxt);
        pageTitleTxt.setText("Preview");
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
            viewHistoryLyt = (LinearLayout) findViewById(R.id.viewHistoryLyt);
            if (actionBarToolbar != null) {
                setSupportActionBar(actionBarToolbar);
            }
        }
        return getSupportActionBar();
    }

    private void setInitialValues() {
        if (UID > 0) {
            setDoctorDetailsView();
            setPatientDetails();
            setLabTestHistory();
            setDiseaseHistory();
            setVisitHistory();
            setMedicineHistory();
        }
    }

    private void setDoctorDetailsView() {
        RealmResults<DoctorModel> results = realm.where(DoctorModel.class).findAll();
        for (DoctorModel model : results) {
            if (model.getHospitalLogoBytes() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(model.getHospitalLogoBytes(), 0, model.getHospitalLogoBytes().length);
                setHospitalLogoToView(bitmap);
            }

            doctorName = model.getName();
            footerMessage = model.getFooterMessage();
            hospitalNameTxt.setText(model.getHospitalName());
            doctorNameTxt.setText(model.getName());
            qualificationTxt.setText(model.getQualification() + "," + model.getSpeciality());
            hospitalAddressTxt.setText(model.getHospitalAddress());
            hospitalPhoneNoTxt.setText(model.getHospitalMobileNo() + ", " + model.getHospitalPhoneNumber());
            regNoTxt.setText(model.getREGNo());
            //imageFileUri = model.getFilePath();
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

    private void setHospitalLogoToView(Bitmap bitmap) {
        if (bitmap != null)
            hospitalLogoImg.setImageBitmap(bitmap);
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
            labTestRecyclerView.setLayoutManager(layoutManager);
            labTestRecyclerView.setAdapter(adapter);
            labTestRecyclerView.setNestedScrollingEnabled(true);
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

    public void setVisitHistory() {
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
            visitRecyclerView.setLayoutManager(layoutManager);
            visitRecyclerView.setAdapter(adapter);
            visitRecyclerView.setNestedScrollingEnabled(true);
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
    public void onClick(View view) {
        Intent intent = null;
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.medicineBtn:
                intent = new Intent(PrescriptionPreviewActivity.this, PrescriptionMedicineActivity.class);
                bundle = new Bundle();
                bundle.putLong(Constants.KEY_UID, UID);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.previewBtn:
                break;
            case R.id.demographicBtn:
                intent = new Intent(PrescriptionPreviewActivity.this, PrescriptionDemographicActivity.class);
                bundle = new Bundle();
                bundle.putLong(Constants.KEY_UID, UID);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.viewHistoryLyt:
                intent = new Intent(PrescriptionPreviewActivity.this, ViewHistoryActivity.class);
                bundle = new Bundle();
                patientName = patientNameTxt.getText().toString();
                bundle.putLong(Constants.KEY_UID, UID);
                bundle.putString(Constants.KEY_PATIENT_NAME, patientName);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
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
        dialog.show();

        TextView saveSharePdfTxt = (TextView) dialogView.findViewById(R.id.saveSharePdfTxt);
        TextView cancelBtnTxt = (TextView) dialogView.findViewById(R.id.cancelBtnTxt);
        saveSharePdfTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File pdfFilePath = generatePdfDoc();
                doPrint(pdfFilePath);
                dialog.dismiss();
            }
        });

        cancelBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //generateImageDoc();
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
            pdfFile = new File(pdfFolder + File.separator + patientNameTxt.getText().toString() + ".pdf");
            document = new Document(PageSize.A4);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
            addTitlePage(document);
            addDoctorDetails(document);
            addSeparatorLine(document);
            addPatientDetails(document);
            addTextBottomOfPage(document, pdfWriter);
            document.close();

        } catch (Exception exp) {
            exp.printStackTrace();
        }

        return pdfFile;
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
            Paragraph p = new Paragraph();
            addEmptyLine(p, 4);
            document.add(p);
            Chunk glue = new Chunk();
            Paragraph p1 = new Paragraph();
            addEmptyLine(p1, 4);
            p1.setIndentationLeft(100);
            if (image != null) p1.add(new Chunk(image, -0.002f, -0.005f));
            p1.add(new Chunk(glue));
            Chunk chunk = new Chunk(hospitalNameTxt.getText().toString(), catFont);
            // chunk.setBackground(new BaseColor(46, 206, 153));
            p1.add(new Chunk(chunk));
            document.add(p1);


        } catch (Exception ex) {
            return;
        }

        // Will create: Report generated by: _name, _date
        Paragraph prefaceX = new Paragraph();
        prefaceX.add(new Paragraph(
                "Report generated by: " + doctorNameTxt.getText().toString() + ", " + new Date(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                smallNormal));
        document.add(prefaceX);
        addEmptyLine(prefaceX, 3);
    }

    private void addDoctorDetails(Document document) throws DocumentException {
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        document.add(preface);
        Chunk glue = new Chunk(new VerticalPositionMark());
        Paragraph p1 = new Paragraph();
        Chunk greenText = new Chunk(doctorNameTxt.getText().toString(), subFont);
        p1.add(greenText);
        p1.add(new Chunk(glue));
        p1.add(hospitalAddressTxt.getText().toString());
        document.add(p1);

        Paragraph p2 = new Paragraph(qualificationTxt.getText().toString(), smallNormal);
        p2.add(new Chunk(glue));
        p2.add(hospitalPhoneNoTxt.getText().toString());
        document.add(p2);

        Paragraph p3 = new Paragraph(regNoTxt.getText().toString(), smallNormal);
        p3.add(new Chunk(glue));
        p3.add(doctorEmailTxt.getText().toString());
        document.add(p3);
    }


    public void addPatientDetails(Document document) throws DocumentException {
        Paragraph preface = new Paragraph();
        preface.setAlignment(Element.ALIGN_CENTER);
        preface.setIndentationLeft(210);
        addEmptyLine(preface, 1);
        preface.add(new Paragraph(Constants.SubTitles.KEY_PATIENT_SUB_TITLE, subFont));
        document.add(preface);

        // document.add(Chunk.NEWLINE);

        Chunk glue = new Chunk(new VerticalPositionMark());
        Paragraph p11 = new Paragraph("UID/Patient Name:\t\t" + patientUidTxt.getText().toString() + "/" + patientNameTxt.getText().toString(), smallNormal);
        p11.add(new Chunk(glue));
        p11.add("Pcode/Created Date:\t\t" + pcodeTxt.getText().toString() + "/" + patientCreateDateTxt.getText().toString());
        document.add(p11);

        Paragraph p12 = new Paragraph("Father/Husb Name:\t\t" + guardianTxt.getText().toString(), smallNormal);
        p12.add(new Chunk(glue));
        p12.add("Age/Sex:\t\t" + patientSexAgeTxt.getText().toString());
        document.add(p12);

        Paragraph p21 = new Paragraph("Contact No:\t\t" + patientMobileNoTxt.getText().toString(), smallNormal);
        p21.add(new Chunk(glue));
        p21.add("Email ID:\t\t" + doctorEmailTxt.getText().toString());
        document.add(p21);

        Paragraph p22 = new Paragraph("DOB/Blood Group:\t\t" + patientDobTxt.getText().toString() + "/" + bloodGroupTxt.getText().toString(), smallNormal);
        p22.add(new Chunk(glue));
        p22.add("Address:\t\t" + patientAddressTxt.getText().toString());
        document.add(p22);

        // document.add(Chunk.NEWLINE);
        DottedLineSeparator dottedLine = new DottedLineSeparator();
        dottedLine.setOffset(-2);
        dottedLine.setGap(2f);
        document.add(dottedLine);
        // document.add(Chunk.NEWLINE);
        Paragraph pComplaintsTitle = new Paragraph();
        pComplaintsTitle.setAlignment(Element.ALIGN_CENTER);
        pComplaintsTitle.setIndentationLeft(210);
        pComplaintsTitle.add(new Paragraph(Constants.SubTitles.KEY_LAB_SUB_TITLE, subFont));
        document.add(pComplaintsTitle);
        //document.add(Chunk.NEWLINE);
        RealmResults<LabTestModel> results = realm.where(LabTestModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findAll();
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
            Paragraph pComplaints = new Paragraph(history, smallNormal);
            document.add(pComplaints);
        }
        //  document.add(Chunk.NEWLINE);
        dottedLine = new DottedLineSeparator();
        dottedLine.setOffset(-2);
        dottedLine.setGap(2f);
        document.add(dottedLine);
        // document.add(Chunk.NEWLINE);
        Paragraph pDiseaseTitle = new Paragraph();
        pDiseaseTitle.setAlignment(Element.ALIGN_CENTER);
        pDiseaseTitle.setIndentationLeft(210);
        pDiseaseTitle.add(new Paragraph(Constants.SubTitles.KEY_DISEASE_SUB_TITLE, subFont));
        document.add(pDiseaseTitle);
        //  document.add(Chunk.NEWLINE);
        RealmResults<DiseaseModel> resultsDisease = realm.where(DiseaseModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findAll();
        for (DiseaseModel model : resultsDisease) {
            String history = "";
            history = model.getDiseaseName();
            if (model.getDiseaseRemark() != null && !model.getDiseaseRemark().isEmpty()) {
                history = history + ", " + model.getDiseaseRemark();
            }
            Paragraph pComplaints = new Paragraph(history, smallNormal);
            document.add(pComplaints);
        }

        // document.add(Chunk.NEWLINE);
        dottedLine = new DottedLineSeparator();
        dottedLine.setOffset(-2);
        dottedLine.setGap(2f);
        document.add(dottedLine);
        //  document.add(Chunk.NEWLINE);
        Paragraph pAdvicesTitle = new Paragraph();
        pAdvicesTitle.setAlignment(Element.ALIGN_CENTER);
        pAdvicesTitle.setIndentationLeft(210);
        pAdvicesTitle.add(new Paragraph(Constants.SubTitles.KEY_VISIT_SUB_TITLE, subFont));
        document.add(pAdvicesTitle);
        // document.add(Chunk.NEWLINE);
        RealmResults<MedicineModel> resultsAdvices = realm.where(MedicineModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findAll();
        for (MedicineModel model : resultsAdvices) {
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

            Paragraph pComplaints = new Paragraph(history, smallNormal);
            document.add(pComplaints);
        }

        //  document.add(Chunk.NEWLINE);
        dottedLine = new DottedLineSeparator();
        dottedLine.setOffset(-2);
        dottedLine.setGap(2f);
        document.add(dottedLine);
        // document.add(Chunk.NEWLINE);
        Paragraph pMedicinesTitle = new Paragraph();
        pMedicinesTitle.setAlignment(Element.ALIGN_CENTER);
        pMedicinesTitle.setIndentationLeft(210);
        pMedicinesTitle.add(new Paragraph(Constants.SubTitles.KEY_MED_SUB_TITLE, subFont));
        document.add(pMedicinesTitle);
        //  document.add(Chunk.NEWLINE);
        RealmResults<MedicineModel> resultsMedicines = realm.where(MedicineModel.class).equalTo(Constants.FieldsKey.KEY_UID, UID).findAll();
        for (MedicineModel model : resultsMedicines) {
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
            Paragraph pComplaints = new Paragraph(history, smallNormal);
            document.add(pComplaints);
        }
    }

    public void addTextBottomOfPage(Document document, PdfWriter writer) throws DocumentException {
        HeaderFooterPageEvent event = new HeaderFooterPageEvent(doctorName, footerMessage);
        writer.setPageEvent(event);
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }


    private void generateImageDoc() {
        File root = Environment.getExternalStorageDirectory();
        File pdfFolder = new File(root.getAbsolutePath() + "/" + Constants.KEY_PP_FOLDER_NAME);
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
        }
        Document document = null;
        try {
            File pdfFile = new File(pdfFolder + File.separator + patientNameTxt.getText().toString() + ".pdf");
            OutputStream output = new FileOutputStream(pdfFile);

            //Step 1
            document = new Document();

            //Step 2
            PdfWriter.getInstance(document, output);

            //Step 3
            document.open();
            document.add(new Paragraph(hospitalNameTxt.getText().toString()));
            document.add(new Paragraph(doctorNameTxt.getText().toString()));

            View myRootView = getWindow().getDecorView().getRootView();

            Bitmap screen;
            View v1 = myRootView.getRootView();
            v1.setDrawingCacheEnabled(true);
            screen = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.activity_prescription_preview, null);
            rootView.setDrawingCacheEnabled(true);
            screen = getBitmapFromView(this.getWindow().findViewById(R.id.rootLyt)); // here give id of our root layout (here its my RelativeLayout's id)
            try {
                document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                document.open();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                screen.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                addImage(document, byteArray);
                document.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            document.close();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public Bitmap getBitmapFromView(View view) {
        view.setDrawingCacheEnabled(true);
        view.refreshDrawableState();
        view.buildDrawingCache(true);
        Bitmap bitmap = view.getDrawingCache(true).copy(Bitmap.Config.ARGB_8888, false);
        view.destroyDrawingCache();
        return bitmap;
    }

    private static void addImage(Document document, byte[] byteArray) {
        Image image = null;
        try {
            image = Image.getInstance(byteArray);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            document.add(image);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }


    private void doPrint(File fIle) {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        if (fIle.exists()) {
            intentShareFile.setType(Constants.KEY_FILE_TYPE);
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