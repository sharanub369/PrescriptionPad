package com.prescriptionpad.app.android.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.prescriptionpad.app.android.R;
import com.prescriptionpad.app.android.model.DoctorModel;
import com.prescriptionpad.app.android.util.Constants;
import com.prescriptionpad.app.android.util.CustomAutoCompleteTextView;
import com.prescriptionpad.app.android.util.ImageFileHandler;
import com.prescriptionpad.app.android.util.RealmMasterClass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sharana.b on 4/11/2017.
 */
public class DoctorDetailsAddActivity extends NavigationActivity implements View.OnClickListener {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private static final int TAKE_PICTURE = 22005;
    private static final int PICK_PICTURE = 22015;

    @InjectView(R.id.clearBtn)
    public Button clearBtn;
    @InjectView(R.id.saveBtn)
    public Button saveBtn;
    @InjectView(R.id.browseBtnTxt)
    public TextView browseBtnTxt;
    @InjectView(R.id.specialityEdtTxt)
    public CustomAutoCompleteTextView specialityTxt;
    @InjectView(R.id.doctorNameEdtTxt)
    public TextView doctorNameTxt;
    @InjectView(R.id.qualificationEdtTxt)
    public TextView qualificationTxt;
    @InjectView(R.id.regNoEdtTxt)
    public TextView regNoTxt;
    @InjectView(R.id.doctorEmailEdtTxt)
    public TextView doctorEmailTxt;
    @InjectView(R.id.hospitalNameEdtTxt)
    public TextView hospitalNametTxt;
    @InjectView(R.id.hospitalAddressEdtTxt)
    public TextView hospitalAddressTxt;
    @InjectView(R.id.hospitalEmailEdtTxt)
    public TextView hospitalEmailTxt;
    @InjectView(R.id.hospitalMobileNoEdtTxt)
    public TextView hospitalMobileNoTxt;
    @InjectView(R.id.hospitalPhoneNoEdtTxt)
    public TextView hospitalPhoneNoTxt;
    @InjectView(R.id.footerMessageEdtTxt)
    public TextView footerMessageTxt;
    @InjectView(R.id.fileNameEdtTxt)
    public TextView fileNameTxt;

    private String imageFilePath = null;
    private Uri imageUri;
    private Boolean isFromSetting = false;
    private Realm realm;

    private Toolbar actionBarToolbar;
    private TextView pageTitleTxt;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_doctor_details_add);
        ButterKnife.inject(this);
        setUpToolbar();
        setAutocompleteValues();
        setOnclickListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        realm = RealmMasterClass.initializeRealm(this);
        getBundleValues();
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

    private void getBundleValues() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isFromSetting = bundle.getBoolean(Constants.IS_DOCTOR_EXIST);
            if (isFromSetting) {
                setInitialValues();
            }
        }
    }

    private void setInitialValues() {
        RealmResults<DoctorModel> results = realm.where(DoctorModel.class).findAll();
        for (DoctorModel model : results) {
            doctorNameTxt.setText(model.getName());
            qualificationTxt.setText(model.getQualification());
            specialityTxt.setText(model.getSpeciality());
            regNoTxt.setText(model.getREGNo());
            doctorEmailTxt.setText(model.getEmail());
            hospitalNametTxt.setText(model.getHospitalName());
            hospitalAddressTxt.setText(model.getHospitalAddress());
            hospitalEmailTxt.setText(model.getHospitalEmail());
            hospitalMobileNoTxt.setText(model.getHospitalMobileNo());
            hospitalPhoneNoTxt.setText(model.getHospitalPhoneNumber());
            footerMessageTxt.setText(model.getFooterMessage());
            fileNameTxt.setText(model.getFileName());
        }
    }

    private void setAutocompleteValues() {
        String[] specialities = getResources().getStringArray(R.array.speciality);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, R.layout.custom_autocomplete_item_view, specialities);
        specialityTxt.setAdapter(adapter);
        specialityTxt.setThreshold(1);
    }

    private void setOnclickListener() {
        clearBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        browseBtnTxt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clearBtn:
                clearDetailsConfirmWindow();
                break;
            case R.id.saveBtn:
                saveDoctorInformationDb();
                break;
            case R.id.browseBtnTxt:
                uploadFile();
                break;
            default:
                break;
        }
    }

    private void clearAllFields() {
        doctorNameTxt.setText("");
        qualificationTxt.setText("");
        specialityTxt.setText("");
        regNoTxt.setText("");
        doctorEmailTxt.setText("");
        hospitalNametTxt.setText("");
        hospitalAddressTxt.setText("");
        hospitalMobileNoTxt.setText("");
        hospitalPhoneNoTxt.setText("");
        fileNameTxt.setText("");
        footerMessageTxt.setText("");
        hospitalEmailTxt.setText("");
    }

    private void saveDoctorInformationDb() {
        if (isFieldsValid()) {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(getDoctorModel());
            realm.commitTransaction();
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Details Saved Successfully.", Toast.LENGTH_LONG).show();
        }
    }

    private DoctorModel getDoctorModel() {
        DoctorModel model = new DoctorModel();
        model.setId(1);
        model.setName(doctorNameTxt.getText().toString());
        model.setQualification(qualificationTxt.getText().toString());
        model.setSpeciality(specialityTxt.getText().toString());
        model.setREGNo(regNoTxt.getText().toString());
        model.setHospitalEmail(doctorEmailTxt.getText().toString());
        model.setHospitalName(hospitalNametTxt.getText().toString());
        model.setHospitalAddress(hospitalAddressTxt.getText().toString());
        model.setEmail(doctorEmailTxt.getText().toString());
        model.setHospitalEmail(hospitalEmailTxt.getText().toString());
        model.setHospitalMobileNo(hospitalMobileNoTxt.getText().toString());
        model.setHospitalPhoneNumber(hospitalPhoneNoTxt.getText().toString());
        model.setFooterMessage(footerMessageTxt.getText().toString());
        if (imageFilePath != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                byte[] byteArray = outputStream.toByteArray();
                model.setFileName(fileNameTxt.getText().toString());
                model.setHospitalLogoBytes(byteArray);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return model;
    }

    private void uploadFile() {
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
            imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName;
            File imageFile = new File(imageFilePath);
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
            imageFilePath = ImageFileHandler.getRealPathFromURI(imageUri, this);
            file = new File(imageFilePath);
            long lengthInMb = file.length() / (1024 * 1024);

            if (docExtension.equalsIgnoreCase("jpg") || docExtension.equalsIgnoreCase("jpeg") || docExtension.equalsIgnoreCase("png")) {
                if (lengthInMb <= 3) {
                    switch (requestCode) {
                        case PICK_PICTURE:
                            if (resultCode == RESULT_OK) {
                                if (imageUri != null) {
                                    try {
                                        setFileNameToView(file.getName());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(this, Constants.Errors.KEY_NO_PROPER_IMAGE, Toast.LENGTH_LONG).show();
                                }
                            }

                        case TAKE_PICTURE:
                            if (resultCode == RESULT_OK) {
                                try {
                                    setFileNameToView(file.getName());
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

    private void setFileNameToView(String name) {
        fileNameTxt.setText(name);
    }

    public Boolean isFieldsValid() {
        Boolean isFieldsValid = true;
        if (doctorNameTxt.getText().toString().isEmpty()) {
            doctorNameTxt.setBackground(getResources().getDrawable(R.drawable.custom_edit_error_text_bg, null));
            Toast.makeText(this, Constants.Errors.KEY_DOCTOR_NAME_EMPTY, Toast.LENGTH_LONG).show();
            isFieldsValid = false;
        } else if (qualificationTxt.getText().toString().isEmpty()) {
            qualificationTxt.setBackground(getResources().getDrawable(R.drawable.custom_edit_error_text_bg, null));
            Toast.makeText(this, Constants.Errors.KEY_QUALIFICATION_EMPTY, Toast.LENGTH_LONG).show();
            isFieldsValid = false;
        } else if (specialityTxt.getText().toString().isEmpty()) {
            specialityTxt.setBackground(getResources().getDrawable(R.drawable.custom_edit_error_text_bg, null));
            Toast.makeText(this, Constants.Errors.KEY_SPECIALITY_EMPTY, Toast.LENGTH_LONG).show();
            isFieldsValid = false;
        } else if (regNoTxt.getText().toString().isEmpty()) {
            regNoTxt.setBackground(getResources().getDrawable(R.drawable.custom_edit_error_text_bg, null));
            Toast.makeText(this, Constants.Errors.KEY_REG_NO_EMPTY, Toast.LENGTH_LONG).show();
            isFieldsValid = false;
        } else if (doctorEmailTxt.getText().toString().isEmpty()) {
            doctorEmailTxt.setBackground(getResources().getDrawable(R.drawable.custom_edit_error_text_bg, null));
            Toast.makeText(this, Constants.Errors.KEY_DOCTOR_EMAIL_EMPTY, Toast.LENGTH_LONG).show();
            isFieldsValid = false;
        } else if (hospitalNametTxt.getText().toString().isEmpty()) {
            hospitalNametTxt.setBackground(getResources().getDrawable(R.drawable.custom_edit_error_text_bg, null));
            Toast.makeText(this, Constants.Errors.KEY_HOSPITAL_NAME_EMPTY, Toast.LENGTH_LONG).show();
            isFieldsValid = false;
        } else if (hospitalAddressTxt.getText().toString().isEmpty()) {
            hospitalAddressTxt.setBackground(getResources().getDrawable(R.drawable.custom_edit_error_text_bg, null));
            Toast.makeText(this, Constants.Errors.KEY_HOSPITAL_ADDRESS_EMPTY, Toast.LENGTH_LONG).show();
            isFieldsValid = false;
        } else if (hospitalEmailTxt.getText().toString().isEmpty()) {
            hospitalEmailTxt.setBackground(getResources().getDrawable(R.drawable.custom_edit_error_text_bg, null));
            Toast.makeText(this, Constants.Errors.KEY_HOSPITAL_EMAIL_EMPTY, Toast.LENGTH_LONG).show();
            isFieldsValid = false;
        } else if (hospitalMobileNoTxt.getText().toString().isEmpty()) {
            hospitalMobileNoTxt.setBackground(getResources().getDrawable(R.drawable.custom_edit_error_text_bg, null));
            Toast.makeText(this, Constants.Errors.KEY_HOSPITAL_MOBILE_NO_EMPTY, Toast.LENGTH_LONG).show();
            isFieldsValid = false;
        } else if (hospitalPhoneNoTxt.getText().toString().isEmpty()) {
            hospitalPhoneNoTxt.setBackground(getResources().getDrawable(R.drawable.custom_edit_error_text_bg, null));
            Toast.makeText(this, Constants.Errors.KEY_HOSPITAL_PHONE_EMPTY, Toast.LENGTH_LONG).show();
            isFieldsValid = false;
        } else if (fileNameTxt.getText().toString().isEmpty()) {
            fileNameTxt.setBackground(getResources().getDrawable(R.drawable.custom_edit_error_text_bg, null));
            Toast.makeText(this, Constants.Errors.KEY_HOSPITAL_LOGO_EMPTY, Toast.LENGTH_LONG).show();
            isFieldsValid = false;
        } else if (!doctorEmailTxt.getText().toString().isEmpty()) {
            String emailAddress = doctorEmailTxt.getText().toString();
            Pattern pattern = Pattern.compile(Constants.KEY_EMAIL_REGEX);
            Matcher matcher = pattern.matcher(emailAddress);
            if (!matcher.matches()) {
                isFieldsValid = false;
                Toast.makeText(this, Constants.Errors.KEY_EMAIL_INVALID, Toast.LENGTH_LONG).show();
            }
        } else if (!hospitalEmailTxt.getText().toString().isEmpty()) {
            String emailAddress = hospitalEmailTxt.getText().toString();
            Pattern pattern = Pattern.compile(Constants.KEY_EMAIL_REGEX);
            Matcher matcher = pattern.matcher(emailAddress);
            if (!matcher.matches()) {
                isFieldsValid = false;
                Toast.makeText(this, Constants.Errors.KEY_EMAIL_INVALID, Toast.LENGTH_LONG).show();
            }
        }
        return isFieldsValid;
    }

    public void clearDetailsConfirmWindow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_clear_confirm_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog_theme);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.show();
        TextView clearTxtBtn = (TextView) dialogView.findViewById(R.id.clearTxtBtn);
        TextView cancelTxtBtn = (TextView) dialogView.findViewById(R.id.cancelTxtBtn);
        clearTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllFields();
                dialog.dismiss();
            }
        });
        cancelTxtBtn.setOnClickListener(new View.OnClickListener() {
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
}
