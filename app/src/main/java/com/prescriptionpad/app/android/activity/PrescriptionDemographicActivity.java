package com.prescriptionpad.app.android.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.prescriptionpad.app.android.R;
import com.prescriptionpad.app.android.adapter.CustomVisitsAdapter;
import com.prescriptionpad.app.android.adapter.LabTestListViewAdapter;
import com.prescriptionpad.app.android.adapter.CustomDiseaseAdapter;
import com.prescriptionpad.app.android.model.LabTestModel;
import com.prescriptionpad.app.android.model.VisitModel;
import com.prescriptionpad.app.android.model.DiseaseModel;
import com.prescriptionpad.app.android.model.PatientModel;
import com.prescriptionpad.app.android.util.Constants;
import com.prescriptionpad.app.android.util.CustomAutoCompleteTextView;
import com.prescriptionpad.app.android.util.ImageFileHandler;
import com.prescriptionpad.app.android.util.RealmMasterClass;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by sharana.b on 4/12/2017.
 */
public class PrescriptionDemographicActivity extends NavigationActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TextWatcher {

    private static final int TAKE_PICTURE = 22005;
    private static final int PICK_PICTURE = 22015;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };


    private Realm realm;
    private Toolbar actionBarToolbar;
    private Calendar mCalendar = null;
    private TextView pageTitleTxt;
    private String profileImagePath;
    private Uri imageUri;

    @InjectView(R.id.uidEdtTxt)
    public EditText uidEdtTxt;
    @InjectView(R.id.pCodeEdtTxt)
    public EditText pCodeEdtTxt;
    @InjectView(R.id.profilePicImg)
    public ImageView profilePicImg;
    @InjectView(R.id.profilePicLyt)
    public LinearLayout profilePicLyt;
    @InjectView(R.id.createdDateEdtTxt)
    public TextView createdDateEdtTxt;
    @InjectView(R.id.patientNameEdtTxt)
    public EditText patientNameEdtTxt;
    @InjectView(R.id.ageEdtTxt)
    public EditText ageEdtTxt;
    @InjectView(R.id.dobEdtTxt)
    public EditText dobEdtTxt;
    @InjectView(R.id.bloodGroupSpinner)
    public Spinner bloodGroupSpinner;
    @InjectView(R.id.genderRadioGroup)
    public RadioGroup genderRadioGroup;
    @InjectView(R.id.fatHusNameEdtTxt)
    public EditText fatHusNameEdtTxt;
    @InjectView(R.id.contactNoEdtTxt)
    public EditText contactNoEdtTxt;
    @InjectView(R.id.emailIdEdtTxt)
    public EditText emailIdEdtTxt;
    @InjectView(R.id.addressEdtTxt)
    public EditText addressEdtTxt;

    //For LabTest
    @InjectView(R.id.labExpandCollapseImg)
    public ImageView labExpandCollapseImg;
    @InjectView(R.id.labTestGroupHeadLyt)
    public LinearLayout labTestGroupHeadLyt;
    @InjectView(R.id.labTestDetailsLyt)
    public LinearLayout labTestDetailsLyt;
    @InjectView(R.id.labNameEdtTxt)
    public CustomAutoCompleteTextView labNameEdtTxt;
    @InjectView(R.id.testNameEdtTxt)
    public CustomAutoCompleteTextView testNameEdtTxt;
    @InjectView(R.id.completionDateEdtTxt)
    public EditText completionDateEdtTxt;
    @InjectView(R.id.labTestUnitEdtTxt)
    public EditText labTestUnitEdtTxt;
    @InjectView(R.id.addLabTestBtnTxt)
    public TextView addLabTestBtnTxt;

    //For Disease
    @InjectView(R.id.diseaseExpandCollapseImage)
    public ImageView diseaseExpandCollapseImage;
    @InjectView(R.id.diseaseGroupHeadLyt)
    public LinearLayout diseaseGroupHeadLyt;
    @InjectView(R.id.diseaseDetailsLyt)
    public LinearLayout diseaseDetailsLyt;
    @InjectView(R.id.diseaseNameEdtTxt)
    public CustomAutoCompleteTextView diseaseNameEdtTxt;
    @InjectView(R.id.diseaseRemarkEdtTxt)
    public EditText diseaseRemarkEdtTxt;
    @InjectView(R.id.addDiseaseBtnTxt)
    public TextView addDiseaseBtnTxt;

    //For Advice Investigation
    @InjectView(R.id.visitExpandCollapseImg)
    public ImageView visitExpandCollapseImg;
    @InjectView(R.id.visitGroupHeadLyt)
    public LinearLayout visitGroupHeadLyt;
    @InjectView(R.id.visitDetailsLyt)
    public LinearLayout visitDetailsLyt;
    @InjectView(R.id.nextVisitDateEdtTxt)
    public EditText nextVisitDateEdtTxt;
    @InjectView(R.id.assignedDoctorEdtTxt)
    public EditText assignedDoctorEdtTxt;
    @InjectView(R.id.visitNotesEdtTxt)
    public EditText visiNotesEdtTxt;

    @InjectView(R.id.medicineBtn)
    public Button medicineBtn;
    @InjectView(R.id.previewBtn)
    public Button previewBtn;
    @InjectView(R.id.demographicBtn)
    public Button demographicBtn;
    @InjectView(R.id.labTestRecyclerView)
    public RecyclerView complaintRecyclerView;
    @InjectView(R.id.diseaseRecyclerView)
    public RecyclerView diseaseRecyclerView;
    @InjectView(R.id.addAdviceBtnTxt)
    public TextView addAdviceBtnTxt;
    @InjectView(R.id.visitRecyclerView)
    public RecyclerView adviceRecyclerView;

    @InjectView(R.id.saveBtn)
    public Button saveBtn;
    @InjectView(R.id.searchBtnTxt)
    public TextView searchBtnTxt;

    private RecyclerView.LayoutManager layoutManager;

    private List<LabTestModel> complaintsList = new ArrayList<LabTestModel>();
    private List<DiseaseModel> diseaseList = new ArrayList<DiseaseModel>();
    private List<VisitModel> adviceList = new ArrayList<VisitModel>();


    long uid = 0;
    private Boolean isDobDatePicker = true;
    private Boolean isVisitbDatePicker = false;
    private Boolean isLabTestExpanded = false;
    private Boolean isDiseaseExpanded = false;
    private Boolean isVisitExpanded = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_prescription_demographic);
        ButterKnife.inject(this);
        setUpToolbar();
        setOnclickListener();
        setAutoCompleteValues();
    }

    @Override
    protected void onStart() {
        super.onStart();
        realm = RealmMasterClass.initializeRealm(this);
        setInitialValues();
    }

    private void setAutoCompleteValues() {
        String[] labTests = getResources().getStringArray(R.array.labTest);
        ArrayAdapter<String> adapterLabTests = new ArrayAdapter<String>
                (this, R.layout.custom_autocomplete_item_view, labTests);
        testNameEdtTxt.setAdapter(adapterLabTests);
        testNameEdtTxt.setThreshold(1);

        String[] diseases = getResources().getStringArray(R.array.disease);
        ArrayAdapter<String> adapterdisease = new ArrayAdapter<String>
                (this, R.layout.custom_autocomplete_item_view, diseases);
        diseaseNameEdtTxt.setAdapter(adapterdisease);
        diseaseNameEdtTxt.setThreshold(1);
    }

    private void setInitialValues() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            //UId from db
            RealmResults<PatientModel> results = realm.where(PatientModel.class).findAllSorted(Constants.FieldsKey.KEY_UID);
            if (results.size() > 0) {
                PatientModel model = results.last();
                long uid = model.getUID() + 1;
                uidEdtTxt.setText(uid + "");
                pCodeEdtTxt.setText(model.getPCode());
            } else {
                uidEdtTxt.setText("1");
                pCodeEdtTxt.setText("1");
            }

            DateFormat sdf = new SimpleDateFormat(Constants.KEY_DATE_FORMAT);
            mCalendar = Calendar.getInstance();
            Date date = mCalendar.getTime();
            createdDateEdtTxt.setText(sdf.format(date));

            layoutManager = new LinearLayoutManager(this);
            LabTestListViewAdapter adapter = new LabTestListViewAdapter(this, this, null);
            complaintRecyclerView.setLayoutManager(layoutManager);
            complaintRecyclerView.setAdapter(adapter);
            layoutManager = new LinearLayoutManager(this);
            CustomDiseaseAdapter adapter1 = new CustomDiseaseAdapter(this, this, null);
            diseaseRecyclerView.setLayoutManager(layoutManager);
            diseaseRecyclerView.setAdapter(adapter1);
            layoutManager = new LinearLayoutManager(this);
            layoutManager = new LinearLayoutManager(this);
            CustomVisitsAdapter adapter2 = new CustomVisitsAdapter(this, this, null);
            adviceRecyclerView.setLayoutManager(layoutManager);
            adviceRecyclerView.setAdapter(adapter2);
        } else {
            uidEdtTxt.setText(bundle.get(Constants.KEY_UID).toString());
            searchPatient();
        }
    }

    private void fetchLabTestDetails(long uid) {
        RealmResults<LabTestModel> results = realm.where(LabTestModel.class).equalTo(Constants.FieldsKey.KEY_UID, uid).findAll();
        complaintsList.clear();
        if (results.size() > 0) {
            for (LabTestModel model : results) {
                complaintsList.add(model);
            }
            layoutManager = new LinearLayoutManager(this);
            LabTestListViewAdapter adapter = new LabTestListViewAdapter(this, this, complaintsList);
            complaintRecyclerView.setLayoutManager(layoutManager);
            complaintRecyclerView.setAdapter(adapter);

        }
    }

    private void fetchDisease(long uid) {
        RealmResults<DiseaseModel> results = realm.where(DiseaseModel.class).equalTo(Constants.FieldsKey.KEY_UID, uid).findAll();
        diseaseList.clear();
        if (results.size() > 0) {
            for (DiseaseModel model : results) {
                diseaseList.add(model);
            }
            layoutManager = new LinearLayoutManager(this);
            CustomDiseaseAdapter adapter = new CustomDiseaseAdapter(this, this, diseaseList);
            diseaseRecyclerView.setLayoutManager(layoutManager);
            diseaseRecyclerView.setAdapter(adapter);
        }
    }

    private void fetchVisitDetails(long uid) {
        RealmResults<VisitModel> results = realm.where(VisitModel.class).equalTo(Constants.FieldsKey.KEY_UID, uid).findAll();
        adviceList.clear();
        if (results.size() > 0) {
            for (VisitModel model : results) {
                adviceList.add(model);
            }
            layoutManager = new LinearLayoutManager(this);
            CustomVisitsAdapter adapter = new CustomVisitsAdapter(this, this, adviceList);
            adviceRecyclerView.setLayoutManager(layoutManager);
            adviceRecyclerView.setAdapter(adapter);
        }
    }

    private void setOnclickListener() {
        labTestGroupHeadLyt.setOnClickListener(this);
        diseaseGroupHeadLyt.setOnClickListener(this);
        visitGroupHeadLyt.setOnClickListener(this);
        dobEdtTxt.setOnClickListener(this);
        previewBtn.setOnClickListener(this);
        medicineBtn.setOnClickListener(this);
        demographicBtn.setOnClickListener(this);
        completionDateEdtTxt.setOnClickListener(this);
        addLabTestBtnTxt.setOnClickListener(this);
        addDiseaseBtnTxt.setOnClickListener(this);
        addAdviceBtnTxt.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        searchBtnTxt.setOnClickListener(this);
        profilePicLyt.setOnClickListener(this);
        profilePicImg.setOnClickListener(this);
        uidEdtTxt.addTextChangedListener(this);
        pCodeEdtTxt.addTextChangedListener(this);
        patientNameEdtTxt.addTextChangedListener(this);
        nextVisitDateEdtTxt.setOnClickListener(this);
    }


    private void setUpToolbar() {
        ActionBar actionBar = getActionbarToolbar();
        pageTitleTxt = (TextView) findViewById(R.id.actionbarTitleTxt);
        pageTitleTxt.setText(Constants.PageTitles.KEY_DEMOGRAPHIC_PAGE);
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
            case R.id.labTestGroupHeadLyt:
                if (!isLabTestExpanded) {
                    labExpandCollapseImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_collapse, null));
                    labTestDetailsLyt.setVisibility(View.VISIBLE);
                    isLabTestExpanded = true;
                } else {
                    labExpandCollapseImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_expand, null));
                    labTestDetailsLyt.setVisibility(View.GONE);
                    isLabTestExpanded = false;
                }
                break;
            case R.id.diseaseGroupHeadLyt:
                if (!isDiseaseExpanded) {
                    diseaseExpandCollapseImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_collapse, null));
                    diseaseDetailsLyt.setVisibility(View.VISIBLE);
                    isDiseaseExpanded = true;
                } else {
                    diseaseExpandCollapseImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_expand, null));
                    diseaseDetailsLyt.setVisibility(View.GONE);
                    isDiseaseExpanded = false;
                }
                break;
            case R.id.visitGroupHeadLyt:
                if (!isVisitExpanded) {
                    visitExpandCollapseImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_collapse, null));
                    visitDetailsLyt.setVisibility(View.VISIBLE);
                    isVisitExpanded = true;
                } else {
                    visitExpandCollapseImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_expand, null));
                    visitDetailsLyt.setVisibility(View.GONE);
                    isVisitExpanded = false;
                }
                break;
            case R.id.dobEdtTxt:
                isDobDatePicker = true;
                isVisitbDatePicker = false;
                selectDobFromCalender();
                break;
            case R.id.profilePicImg:
            case R.id.profilePicLyt:
                uploadPatientProfilePic();
                break;
            case R.id.saveBtn:
                uid = Long.parseLong(uidEdtTxt.getText().toString());
                saveDataToDb();
                break;
            case R.id.medicineBtn:
                if (isUIDExists()) {
                    intent = new Intent(PrescriptionDemographicActivity.this, PrescriptionMedicineActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong(Constants.KEY_UID, uid);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, Constants.Errors.KEY_SAVE_DATA, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.previewBtn:
                if (isUIDExists()) {
                    intent = new Intent(PrescriptionDemographicActivity.this, PrescriptionPreviewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong(Constants.KEY_UID, uid);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, Constants.Errors.KEY_SAVE_DATA, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.demographicBtn:
                break;
            case R.id.addLabTestBtnTxt:
                addLabTestListView();
                break;
            case R.id.addDiseaseBtnTxt:
                addDiseaseListView();
                break;
            case R.id.addAdviceBtnTxt:
                addNextVisitView();
                break;
            case R.id.searchBtnTxt:
                uid = Long.parseLong(uidEdtTxt.getText().toString());
                searchPatient();
                break;
            case R.id.completionDateEdtTxt:
                isDobDatePicker = false;
                isVisitbDatePicker = false;
                selectDobFromCalender();
                break;
            case R.id.nextVisitDateEdtTxt:
                isDobDatePicker = false;
                isVisitbDatePicker = true;
                selectDobFromCalender();
                break;
            default:
                break;
        }
    }

    private void uploadPatientProfilePic() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_select_file_resource_view, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.dialog_theme);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        TextView galleryBtnTxt = (TextView) dialogView.findViewById(R.id.galleryBtnTxt);
        TextView cameraBtnTxt = (TextView) dialogView.findViewById(R.id.cameraBtnTxt);

        galleryBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPhotoFromGallery();
                dialog.dismiss();
            }
        });

        cameraBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhotoFromCamera();
                dialog.dismiss();
            }
        });


//        final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.dialog_theme);
//        // builder.setTitle("Select Resource");
//        View view = LayoutInflater.from(this).inflate(R.layout.custom_dialog_title, null);
//        builder.setCustomTitle(view);
//        final String[] sources = getResources().getStringArray(R.array.list_of_file_sources);
//        builder.setItems(sources, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Boolean isSdPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
//                if (isSdPresent) {
//                    if (sources[which].contains("Gallery")) {
//                        pickPhotoFromGallery();
//                    } else if (sources[which].contains("Camera")) {
//                        takePhotoFromCamera();
//                    }
//                } else {
//
//                }
//            }
//        });
//
//        builder.show();
    }

    private void pickPhotoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PICTURE);
    }

    private void takePhotoFromCamera() {
        imageUri = getImageUri();
        if (imageUri != null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(intent, TAKE_PICTURE);
        }
    }

    private Uri getImageUri() {
        Uri returnUri = null;
        // define the file-name to save photo taken by Camera activity
        verifyExternalStoragePermissions();
        String fileName = System.currentTimeMillis() + ".jpg";
        Boolean isSdMounted = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (isSdMounted) {
            profileImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName;
            File imageFile = new File(profileImagePath);
            returnUri = Uri.fromFile(imageFile);
        } else {
            Toast.makeText(this, Constants.Errors.KEY_NO_EXTERNAL_STORAGE, Toast.LENGTH_LONG).show();
        }

        return returnUri;
    }

    private void verifyExternalStoragePermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String docExtension = "";
        File file = null;
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_PICTURE) {
                imageUri = data.getData();
                ContentResolver cR = getContentResolver();
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                docExtension = mime.getExtensionFromMimeType(cR.getType(imageUri));
            } else {
                docExtension = "jpg";
            }
            //  imageFileName = ImageFileHandler.getFileName(imageUri);
            profileImagePath = ImageFileHandler.getRealPathFromURI(imageUri, this);
            file = new File(profileImagePath);
            long lengthInMb = file.length() / (1024 * 1024);

            if (docExtension.equalsIgnoreCase("jpg") || docExtension.equalsIgnoreCase("jpeg") || docExtension.equalsIgnoreCase("png")) {
                if (lengthInMb <= 3) {
                    switch (requestCode) {
                        case PICK_PICTURE:
                            if (resultCode == RESULT_OK) {
                                if (imageUri != null) {
                                    try {
                                        setImageToView(imageUri);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(this, "No proper Photo Selected", Toast.LENGTH_LONG).show();
                                }
                            }

                        case TAKE_PICTURE:
                            if (resultCode == RESULT_OK) {
                                try {
                                    setImageToView(imageUri);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                    }
                } else {
                    Toast.makeText(this, Constants.Errors.KEY_IMAGE_SIZE, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, Constants.Errors.KEY_IMAGE_FORMAT, Toast.LENGTH_LONG).show();
            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, Constants.Errors.KEY_FILE_UPLOAD_FAILURE, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, Constants.Errors.KEY_FILE_UPLOAD_FAILURE, Toast.LENGTH_SHORT).show();
        }
    }

    private void setImageToView(Uri imageUri) {
        Picasso.with(this).load(imageUri)
                .resize(120, 120)
                .centerCrop()
                .error(R.drawable.ic_profile_pic_default)
                .into(profilePicImg);
    }

    private boolean isUIDExists() {
        RealmResults<PatientModel> results = realm.where(PatientModel.class).equalTo(Constants.FieldsKey.KEY_UID, uid).findAll();
        if (results.size() > 0) return true;
        return false;
    }

    private void searchPatient() {
        long uid = 0;
        RealmResults<PatientModel> patientsResults = null;
        if (uidEdtTxt.getText().toString().isEmpty() && patientNameEdtTxt.getText().toString().isEmpty()) {
            Toast.makeText(this, Constants.Errors.KEY_UID_NAME_REQUIRED, Toast.LENGTH_LONG).show();
        } else {
            if (!uidEdtTxt.getText().toString().isEmpty()) {
                uid = Long.parseLong(uidEdtTxt.getText().toString());
                patientsResults = realm.where(PatientModel.class).equalTo(Constants.FieldsKey.KEY_UID, uid).findAll();
                if (patientsResults.size() > 0) {
                    for (PatientModel model : patientsResults) {
                        pCodeEdtTxt.setText(model.getPCode());
                        createdDateEdtTxt.setText(model.getCreatedDate());
                        patientNameEdtTxt.setText(model.getPatientName());
                        ageEdtTxt.setText(model.getAge() + "");
                        dobEdtTxt.setText(model.getDateOdBirth());
                        ArrayAdapter<String> array_spinner = (ArrayAdapter<String>) bloodGroupSpinner.getAdapter();
                        bloodGroupSpinner.setSelection(array_spinner.getPosition(model.getBloodGroup()));

                        if (model.getGender().equalsIgnoreCase("Male")) {
                            ((RadioButton) findViewById(R.id.maleRadioBtn)).setChecked(true);
                        } else if (model.getGender().equalsIgnoreCase("Female")) {
                            ((RadioButton) findViewById(R.id.femaladioBtn)).setChecked(true);
                        } else {
                            ((RadioButton) findViewById(R.id.otherRadioBtn)).setChecked(true);
                        }
                        fatHusNameEdtTxt.setText(model.getFatherHusbandName());
                        contactNoEdtTxt.setText(model.getContactNo());
                        emailIdEdtTxt.setText(model.getEmailAddress());
                        addressEdtTxt.setText(model.getPatientAddress());
                        if (model.getPatientImageBytes() != null) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(model.getPatientImageBytes(), 0, model.getPatientImageBytes().length);
                            profilePicImg.setImageBitmap(bitmap);
                        }
                    }
                }
            } else if (patientNameEdtTxt.getText().toString().isEmpty()) {
                patientsResults = realm.where(PatientModel.class).equalTo(Constants.FieldsKey.KEY_PATIENT_NAME, patientNameEdtTxt.getText().toString()).findAll();
            }
        }

        if (patientsResults.size() <= 0) {
            Toast.makeText(this, Constants.Errors.KEY_NO_PATIENT_FOUND, Toast.LENGTH_LONG).show();
        } else {
            if (uid > 0) {
                fetchLabTestDetails(uid);
                fetchDisease(uid);
                fetchVisitDetails(uid);
            }
        }
    }

    private void saveDataToDb() {
        if (isFieldsValid()) {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(getPatientModel());
            realm.commitTransaction();
            Toast.makeText(this, Constants.SuccessMessages.KEY_SAVE_TEST, Toast.LENGTH_LONG).show();
        }
    }

    private void saveAdvices(VisitModel model) {
        if (model != null) {
            if (!uidEdtTxt.getText().toString().isEmpty()) {
                uid = Long.parseLong(uidEdtTxt.getText().toString());
                PatientModel modelPatient = realm.where(PatientModel.class).equalTo(Constants.FieldsKey.KEY_UID, uid).findFirst();
                if (modelPatient != null) {
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(getAdviceModel(model));
                    realm.commitTransaction();

                    layoutManager = new LinearLayoutManager(this);
                    CustomVisitsAdapter adapter = new CustomVisitsAdapter(this, this, adviceList);
                    adviceRecyclerView.setLayoutManager(layoutManager);
                    adviceRecyclerView.setAdapter(adapter);
                } else {
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(getPatientModel());
                    realm.commitTransaction();


                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(getAdviceModel(model));
                    realm.commitTransaction();

                    layoutManager = new LinearLayoutManager(this);
                    CustomVisitsAdapter adapter = new CustomVisitsAdapter(this, this, adviceList);
                    adviceRecyclerView.setLayoutManager(layoutManager);
                    adviceRecyclerView.setAdapter(adapter);
                }
            } else {
                Toast.makeText(this, Constants.Errors.KEY_UID_REQUIRED, Toast.LENGTH_LONG).show();
            }
        }

    }

    private RealmObject getAdviceModel(VisitModel model) {
        int visitId = 0;
        RealmResults<VisitModel> visitResults = realm.where(VisitModel.class).findAll();
        if (visitResults != null) {
            visitId = visitResults.size() + 1;
        }
        model.setVisitId(visitId);
        model.setUID(Long.parseLong(uidEdtTxt.getText().toString()));
        model.setNextVisitDate(model.getNextVisitDate());
        model.setNotes(model.getNotes());
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.KEY_DATE_FORMAT);
        Date date = new Date();
        String dateString = sdf.format(date);
        model.setCreatedDate(dateString);
        adviceList.add(model);
        return model;
    }


    private void saveDiseases(DiseaseModel model) {
        if (model != null) {
            if (!uidEdtTxt.getText().toString().isEmpty()) {
                uid = Long.parseLong(uidEdtTxt.getText().toString());
                PatientModel modelPatient = realm.where(PatientModel.class).equalTo(Constants.FieldsKey.KEY_UID, uid).findFirst();
                if (modelPatient != null) {
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(getDiseaseModel(model));
                    realm.commitTransaction();

                    layoutManager = new LinearLayoutManager(this);
                    CustomDiseaseAdapter adapter = new CustomDiseaseAdapter(this, this, diseaseList);
                    diseaseRecyclerView.setLayoutManager(layoutManager);
                    diseaseRecyclerView.setAdapter(adapter);
                } else {
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(getPatientModel());
                    realm.commitTransaction();


                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(getDiseaseModel(model));
                    realm.commitTransaction();

                    layoutManager = new LinearLayoutManager(this);
                    CustomDiseaseAdapter adapter = new CustomDiseaseAdapter(this, this, diseaseList);
                    diseaseRecyclerView.setLayoutManager(layoutManager);
                    diseaseRecyclerView.setAdapter(adapter);
                }
            } else {
                Toast.makeText(this, Constants.Errors.KEY_UID_REQUIRED, Toast.LENGTH_LONG).show();
            }
        }
    }

    private RealmObject getDiseaseModel(DiseaseModel diseaseModel) {
        int diseaseId = 0;
        DiseaseModel model = new DiseaseModel();
        RealmResults<DiseaseModel> diseaseResults = realm.where(DiseaseModel.class).findAll();
        if (diseaseResults != null) {
            diseaseId = diseaseResults.size() + 1;
        }
        model.setDiseaseId(diseaseId);
        model.setUID(Long.parseLong(uidEdtTxt.getText().toString()));
        model.setDiseaseName(diseaseModel.getDiseaseName());
        model.setDiseaseRemark(diseaseModel.getDiseaseRemark());
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.KEY_DATE_FORMAT);
        Date date = new Date();
        String dateString = sdf.format(date);
        model.setCreatedDate(dateString);
        diseaseList.add(model);
        return model;
    }

    private void saveLabTest(LabTestModel model) {
        if (!uidEdtTxt.getText().toString().isEmpty()) {
            uid = Long.parseLong(uidEdtTxt.getText().toString());
            PatientModel modelPatient = realm.where(PatientModel.class).equalTo(Constants.FieldsKey.KEY_UID, uid).findFirst();
            if (modelPatient != null) {
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(getComplaintsModel(model));
                realm.commitTransaction();

                layoutManager = new LinearLayoutManager(this);
                LabTestListViewAdapter adapter = new LabTestListViewAdapter(this, this, complaintsList);
                complaintRecyclerView.setLayoutManager(layoutManager);
                complaintRecyclerView.setAdapter(adapter);
                labNameEdtTxt.setText("");
            } else {
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(getPatientModel());
                realm.commitTransaction();


                realm.beginTransaction();
                realm.copyToRealmOrUpdate(getComplaintsModel(model));
                realm.commitTransaction();

                layoutManager = new LinearLayoutManager(this);
                LabTestListViewAdapter adapter = new LabTestListViewAdapter(this, this, complaintsList);
                complaintRecyclerView.setLayoutManager(layoutManager);
                complaintRecyclerView.setAdapter(adapter);
                labNameEdtTxt.setText("");
            }
        } else {
            Toast.makeText(this, Constants.Errors.KEY_UID_REQUIRED, Toast.LENGTH_LONG).show();
        }

    }

    private RealmObject getComplaintsModel(LabTestModel labTestAdviceModel) {
        int labTestId = 0;
        LabTestModel model = new LabTestModel();
        RealmResults<LabTestModel> labTestResults = realm.where(LabTestModel.class).findAll();
        if (labTestResults != null) {
            labTestId = labTestResults.size() + 1;
        }
        model.setLabTestId(labTestId);
        model.setUID(Long.parseLong(uidEdtTxt.getText().toString()));
        model.setLabName(labTestAdviceModel.getLabName());
        model.setTestName(labTestAdviceModel.getTestName());
        model.setCompletionDate(labTestAdviceModel.getCompletionDate());
        model.setUnit(labTestAdviceModel.getUnit());
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.KEY_DATE_FORMAT);
        Date date = new Date();
        String dateString = sdf.format(date);
        model.setCreatedDate(dateString);
        complaintsList.add(model);
        return model;
    }

    private RealmObject getPatientModel() {
        PatientModel model = new PatientModel();
        model.setUID(Long.parseLong(uidEdtTxt.getText().toString()));
        model.setPCode(pCodeEdtTxt.getText().toString());
        model.setCreatedDate(createdDateEdtTxt.getText().toString());
        model.setPatientName(patientNameEdtTxt.getText().toString());
        model.setDateOdBirth(dobEdtTxt.getText().toString());
        if (!ageEdtTxt.getText().toString().isEmpty())
            model.setAge(Long.parseLong(ageEdtTxt.getText().toString()));
        model.setBloodGroup(bloodGroupSpinner.getSelectedItem().toString());
        int checkedRadioButtonId = genderRadioGroup.getCheckedRadioButtonId();
        String gender = "Male";
        if (checkedRadioButtonId == -1) {
            gender = "Male";
        } else {
            RadioButton radioButton = (RadioButton) findViewById(genderRadioGroup.getCheckedRadioButtonId());
            gender = radioButton.getText().toString();
        }
        model.setGender(gender);
        model.setFatherHusbandName(fatHusNameEdtTxt.getText().toString());
        model.setContactNo(contactNoEdtTxt.getText().toString());
        model.setEmailAddress(emailIdEdtTxt.getText().toString());
        model.setPatientAddress(addressEdtTxt.getText().toString());
        if (profileImagePath != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                byte[] byteArray = outputStream.toByteArray();
                model.setPatientImageBytes(byteArray);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return model;
    }

    private boolean isFieldsValid() {
        Boolean isFieldsValid = true;
        if (uidEdtTxt.getText().toString().isEmpty()) {
            isFieldsValid = false;
            uidEdtTxt.setBackground(getResources().getDrawable(R.drawable.custom_edit_error_text_bg, null));
            Toast.makeText(this, Constants.Errors.KEY_UID_REQUIRED, Toast.LENGTH_LONG).show();
        } else if (pCodeEdtTxt.getText().toString().isEmpty()) {
            isFieldsValid = false;
            pCodeEdtTxt.setBackground(getResources().getDrawable(R.drawable.custom_edit_error_text_bg, null));
            Toast.makeText(this, Constants.Errors.KEY_PCODE_REQUIRED, Toast.LENGTH_LONG).show();
        } else if (ageEdtTxt.getText().toString().isEmpty()) {
            isFieldsValid = false;
            ageEdtTxt.setBackground(getResources().getDrawable(R.drawable.custom_edit_error_text_bg, null));
            Toast.makeText(this, Constants.Errors.KEY_AGE_REQUIRED, Toast.LENGTH_LONG).show();
        } else if (contactNoEdtTxt.getText().toString().isEmpty()) {
            isFieldsValid = false;
            contactNoEdtTxt.setBackground(getResources().getDrawable(R.drawable.custom_edit_error_text_bg, null));
            Toast.makeText(this, Constants.Errors.KEY_CONTACT_NO_REQUIRED, Toast.LENGTH_LONG).show();
        } else if (!emailIdEdtTxt.getText().toString().isEmpty()) {
            String emailAddress = emailIdEdtTxt.getText().toString();
            Pattern pattern = Pattern.compile(Constants.KEY_EMAIL_REGEX);
            Matcher matcher = pattern.matcher(emailAddress);
            if (!matcher.matches()) {
                isFieldsValid = false;
                Toast.makeText(this, Constants.Errors.KEY_EMAIL_INVALID, Toast.LENGTH_LONG).show();
            }
        }
        return isFieldsValid;
    }

    private void addNextVisitView() {
        if (!nextVisitDateEdtTxt.getText().toString().isEmpty()) {
            if (isFieldsValid()) {
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
                        VisitModel model = new VisitModel();
                        model.setNextVisitDate(nextVisitDateEdtTxt.getText().toString());
                        model.setAssignedDoctor(assignedDoctorEdtTxt.getText().toString());
                        model.setNotes(visiNotesEdtTxt.getText().toString());
                        SimpleDateFormat sdf = new SimpleDateFormat(Constants.KEY_DATE_FORMAT);
                        Date date = new Date();
                        String dateString = sdf.format(date);
                        model.setCreatedDate(dateString);

                        saveAdvices(model);
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
        } else {
            Toast.makeText(this, Constants.Errors.KEY_ADVICE_REQUIRED, Toast.LENGTH_LONG).show();
        }
    }

    private void addDiseaseListView() {
        if (!diseaseNameEdtTxt.getText().toString().isEmpty()) {
            if (isFieldsValid()) {
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
                        DiseaseModel model = new DiseaseModel();
                        model.setDiseaseName(diseaseNameEdtTxt.getText().toString());
                        model.setDiseaseRemark(diseaseRemarkEdtTxt.getText().toString());
                        SimpleDateFormat sdf = new SimpleDateFormat(Constants.KEY_DATE_FORMAT);
                        Date date = new Date();
                        String dateString = sdf.format(date);
                        model.setCreatedDate(dateString);


                        saveDiseases(model);
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
        } else {
            Toast.makeText(this, Constants.Errors.KEY_DISEASE_NAME_REQUIRED, Toast.LENGTH_LONG).show();
        }
    }

    private void addLabTestListView() {
        if (!labNameEdtTxt.getText().toString().isEmpty()) {
            if (isFieldsValid()) {
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
                        LabTestModel model = new LabTestModel();
                        model.setLabName(labNameEdtTxt.getText().toString());
                        model.setTestName(testNameEdtTxt.getText().toString());
                        model.setCompletionDate(completionDateEdtTxt.getText().toString());
                        model.setUnit(labTestUnitEdtTxt.getText().toString());
                        SimpleDateFormat sdf = new SimpleDateFormat(Constants.KEY_DATE_FORMAT);
                        Date date = new Date();
                        String dateString = sdf.format(date);
                        model.setCreatedDate(dateString);
                        saveLabTest(model);
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
        } else {
            Toast.makeText(this, Constants.Errors.KEY_LAB_NAME_REQUIRED, Toast.LENGTH_LONG).show();
        }
    }

    private void selectDobFromCalender() {
//        mCalendar = Calendar.getInstance();
//        new DatePickerDialog(this, this, mCalendar
//                .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
//                mCalendar.get(Calendar.DAY_OF_MONTH)).show();
        Calendar calendar = Calendar.getInstance();
       // calendar.setTimeZone(TimeZone.getTimeZone(Consts.UTC));

        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        DatePickerDialog dialog = new DatePickerDialog(this,  android.R.style.Theme_Holo_Light_Dialog, this, year, month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (isDobDatePicker) {
            dialog.setTitle("Date O Birth");
        } else if (!isDobDatePicker && !isVisitbDatePicker) {
            dialog.setTitle("LAb Test Ending Date");
        } else {
            dialog.setTitle("Next Visit Date");
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
        if (isDobDatePicker) {
            dobEdtTxt.setText(selectedDate);
        } else if (!isDobDatePicker && !isVisitbDatePicker) {
            completionDateEdtTxt.setText(selectedDate);
        } else {
            nextVisitDateEdtTxt.setText(selectedDate);
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
        layoutManager = new LinearLayoutManager(PrescriptionDemographicActivity.this);
        LabTestListViewAdapter adapterComplaint = new LabTestListViewAdapter(PrescriptionDemographicActivity.this, PrescriptionDemographicActivity.this, null);
        complaintRecyclerView.setLayoutManager(layoutManager);
        complaintRecyclerView.setAdapter(adapterComplaint);
        CustomDiseaseAdapter adapterDisease = new CustomDiseaseAdapter(PrescriptionDemographicActivity.this, PrescriptionDemographicActivity.this, null);
        diseaseRecyclerView.setAdapter(adapterDisease);
        CustomVisitsAdapter adapter = new CustomVisitsAdapter(PrescriptionDemographicActivity.this, PrescriptionDemographicActivity.this, null);
        adviceRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        realm.close();
    }
}
