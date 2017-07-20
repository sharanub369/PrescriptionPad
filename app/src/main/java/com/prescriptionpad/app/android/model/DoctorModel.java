package com.prescriptionpad.app.android.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by sharana.b on 4/13/2017.
 */

@RealmClass
public class DoctorModel extends RealmObject {

    @PrimaryKey
    private long Id;
    private String Name;
    private String Qualification;
    private String Speciality;
    private String REGNo;
    private String Email;
    private String HospitalName;
    private String HospitalAddress;
    private String HospitalEmail;
    private String HospitalMobileNo;
    private String HospitalPhoneNumber;
    private String FooterMessage;
    private String FileName;
    private byte[] HospitalLogoBytes;

    public String getName() {
        return Name;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getQualification() {
        return Qualification;
    }

    public void setQualification(String qualification) {
        Qualification = qualification;
    }

    public String getSpeciality() {
        return Speciality;
    }

    public void setSpeciality(String speciality) {
        Speciality = speciality;
    }

    public String getREGNo() {
        return REGNo;
    }

    public void setREGNo(String REGNo) {
        this.REGNo = REGNo;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getHospitalName() {
        return HospitalName;
    }

    public void setHospitalName(String hospitalName) {
        HospitalName = hospitalName;
    }

    public String getHospitalAddress() {
        return HospitalAddress;
    }

    public void setHospitalAddress(String hospitalAddress) {
        HospitalAddress = hospitalAddress;
    }

    public String getHospitalEmail() {
        return HospitalEmail;
    }

    public void setHospitalEmail(String hospitalEmail) {
        HospitalEmail = hospitalEmail;
    }

    public String getHospitalMobileNo() {
        return HospitalMobileNo;
    }

    public void setHospitalMobileNo(String hospitalMobileNo) {
        HospitalMobileNo = hospitalMobileNo;
    }

    public String getHospitalPhoneNumber() {
        return HospitalPhoneNumber;
    }

    public void setHospitalPhoneNumber(String hospitalPhoneNumber) {
        HospitalPhoneNumber = hospitalPhoneNumber;
    }

    public String getFooterMessage() {
        return FooterMessage;
    }

    public void setFooterMessage(String footerMessage) {
        FooterMessage = footerMessage;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public byte[] getHospitalLogoBytes() {
        return HospitalLogoBytes;
    }

    public void setHospitalLogoBytes(byte[] hospitalLogoBytes) {
        HospitalLogoBytes = hospitalLogoBytes;
    }
}
