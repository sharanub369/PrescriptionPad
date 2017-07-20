package com.prescriptionpad.app.android.util;

/**
 * Created by sharana.b on 4/12/2017.
 */
public class Constants {

    public static final String KEY_PP_FOLDER_NAME = "PrescriptionPad";
    public static final String KEY_REALM_DB_NAME = "PrescriptionPad.realm";
    public static final String KEY_SP_NAME = "sharedPrefName";
    public static final String KEY_FILE_TYPE = "application/pdf";

    public static final String KEY_FIRST_INSTALL = "IsFirstTimeInstall";
    public static final String KEY_UID = "uid";
    public static final String KEY_PATIENT_NAME = "patientName";
    public static final String KEY_DATE_FORMAT = "dd-MM-yyyy";
    public static final String KEY_EMAIL_REGEX = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
    public static final String IS_DOCTOR_EXIST= "isDoctorExist";
    public static final String IS_LOGOUT = "isLogout";

    public static final String KEY_FAQ_URL="http://www.epr.provansys.com/epmrfaq.html";
    public static final String KEY_TC_URL="http://www.epr.provansys.com/epmrdisclaimer.html";
    public static final String KEY_ACCEPT_TC = "Please accept Terms and Conditions.";

    public interface FieldsKey {
        String KEY_UID = "UID";
        String KEY_DISEASE_NAME = "DiseaseName";
        String KEY_PATIENT_NAME = "PatientName";
        String KEY_MED_NAME = "BrandName";
        String KEY_PCODE = "PCode";
        String KEY_CREATED_DATE = "CreatedDate";
    }

    public interface SuccessMessages {
        String KEY_SAVE_TEST = "Saved Details Successfully.";
        String KEY_IMPORT_SUCCESS = "Data import completed successfully.";
    }

    public interface PageTitles {
        String KEY_BASE_TITLE = "Prescription Pad";
        String KEY_DEMOGRAPHIC_PAGE = "Demographic Details";
        String KEY_DOCTOR_DETAILS = "Doctor Details";
        String KEY_MED_DETAILS = "Medicine Details";
        String KEY_HISTORY_DETAILS = "History";
        String KEY_FAQ_PAGE= "Frequently Asked Questions";
        String KEY_TC_PAGE= " Disclaimer and privacy policy";
    }

    public interface SubTitles {
        String KEY_PATIENT_SUB_TITLE = "Patient Details";
        String KEY_LAB_SUB_TITLE = "Lab Test Details";
        String KEY_DISEASE_SUB_TITLE = "Disease Details";
        String KEY_VISIT_SUB_TITLE = "Visit Details";
        String KEY_MED_SUB_TITLE = "Medicine Details";
    }

    public interface Frag {
        int FAQ = 3;
        int ABOUT = 4;
        int LOGOUT = 5;
        int LOGIN = 6;
        int FORGOTTEN_EMAIL = 7;
        int AUTH_2F = 8;
    }

    public interface Errors {
        public static String ERROR_MESSAGE = "error_message";
        public static String KEY_LOGOUT_MESSAGE = "Successfully logged out.";
        String KEY_NETWORK_CONN_ERROR = "Network connection not available";
        String KEY_DOCTOR_NAME_EMPTY = "Doctor Name Required.";
        String KEY_QUALIFICATION_EMPTY = "Doctor Qualification Required.";
        String KEY_SPECIALITY_EMPTY = "Doctor Speciality Required.";
        String KEY_REG_NO_EMPTY = "Doctor Registration Number Required.";
        String KEY_DOCTOR_EMAIL_EMPTY = "Doctor Email Address Required.";
        String KEY_HOSPITAL_NAME_EMPTY = "Hospital Name Required.";
        String KEY_HOSPITAL_ADDRESS_EMPTY = "Hospital Address Required.";
        String KEY_HOSPITAL_EMAIL_EMPTY = "Hospital Email Address Required.";
        String KEY_HOSPITAL_MOBILE_NO_EMPTY = "Hospital Mobile Number Required.";
        String KEY_HOSPITAL_PHONE_EMPTY = "Hospital Phone Number Required.";
        String KEY_HOSPITAL_LOGO_EMPTY = "Hospital Logo Required.";
        String KEY_NO_EXTERNAL_STORAGE = "External Storage not Found.";
        String KEY_IMAGE_FORMAT = "Supports only jpeg, jpg and png format.";
        String KEY_IMAGE_SIZE = "Image size is more then 3 MB.";
        String KEY_FILE_UPLOAD_FAILURE = "Failed to upload the file.";
        String KEY_SAVE_DATA = "Please Save the date for future use.";
        String KEY_ADVICE_REQUIRED = "Advice Investigation Required.";
        String KEY_DISEASE_NAME_REQUIRED = "Please Enter Disease Name.";
        String KEY_LAB_NAME_REQUIRED = "Please Enter Lab Name.";
        String KEY_UID_REQUIRED = "Patient Unique Id Required";
        String KEY_PCODE_REQUIRED = "Patient PCode Required";
        String KEY_AGE_REQUIRED = "Patient Age Required";
        String KEY_CONTACT_NO_REQUIRED = "Patient Contact Number Required";
        String KEY_EMAIL_INVALID = "Invalid Email Address.";
        String KEY_UID_NAME_REQUIRED = "Patient Unique Id or Name Required to search.";
        String KEY_NO_PATIENT_FOUND = "Patient details not found for this search criteria.";
        String KEY_MED_NAME_REQUIRED = "Medicine Name Required.";
        String KEY_DELETE_SUCCESS = "Deleted Data Successfully.";
        String KEY_NO_PROPER_IMAGE = "No proper Photo Selected";
        String KEY_FILE_NOT_FOUND = "Failed!. File Not Found at specified location.";

        String KEY_START_DATE_REQUIRED = "Start Date Required!";
        String KEY_END_DATE_REQUIRED = "End Date Required!";
        String KEY_END_DATE_GREATER = "End date should be greater then start date!";
    }
}
