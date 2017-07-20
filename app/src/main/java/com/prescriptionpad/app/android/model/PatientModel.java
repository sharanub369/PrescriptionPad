package com.prescriptionpad.app.android.model;

import android.net.Uri;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sharana.b on 4/13/2017.
 */
public class PatientModel extends RealmObject {

    @PrimaryKey
    private long UID;
    private String PCode;
    private String CreatedDate;
    private String PatientName;
    private long Age;
    private String DateOdBirth;
    private byte[] PatientImageBytes;
    private String BloodGroup;
    private String Gender;
    private String FatherHusbandName;
    private String ContactNo;
    private String EmailAddress;
    private String PatientAddress;


    public long getUID() {
        return UID;
    }

    public void setUID(long UID) {
        this.UID = UID;
    }

    public String getPCode() {
        return PCode;
    }

    public void setPCode(String PCode) {
        this.PCode = PCode;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getPatientName() {
        return PatientName;
    }

    public void setPatientName(String patientName) {
        PatientName = patientName;
    }

    public long getAge() {
        return Age;
    }

    public void setAge(long age) {
        Age = age;
    }

    public String getDateOdBirth() {
        return DateOdBirth;
    }

    public void setDateOdBirth(String dateOdBirth) {
        DateOdBirth = dateOdBirth;
    }

    public String getBloodGroup() {
        return BloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        BloodGroup = bloodGroup;
    }

    public String getFatherHusbandName() {
        return FatherHusbandName;
    }

    public void setFatherHusbandName(String fatherHusbandName) {
        FatherHusbandName = fatherHusbandName;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getContactNo() {
        return ContactNo;
    }

    public void setContactNo(String contactNo) {
        ContactNo = contactNo;
    }

    public String getEmailAddress() {
        return EmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        EmailAddress = emailAddress;
    }

    public String getPatientAddress() {
        return PatientAddress;
    }

    public void setPatientAddress(String patientAddress) {
        PatientAddress = patientAddress;
    }

    public byte[] getPatientImageBytes() {
        return PatientImageBytes;
    }

    public void setPatientImageBytes(byte[] patientImageBytes) {
        PatientImageBytes = patientImageBytes;
    }
}
