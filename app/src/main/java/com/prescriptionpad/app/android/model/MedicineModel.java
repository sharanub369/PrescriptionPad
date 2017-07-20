package com.prescriptionpad.app.android.model;

import java.sql.Timestamp;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sharana.b on 4/15/2017.
 */
public class MedicineModel extends RealmObject {
    @PrimaryKey
    private long MedId;
    private long UID;
    private String BrandName;
    private String DosageFrom;
    private String Strength;
    private String Symbols;
    private String ForPeriod;
    private String Unit;
    private String How;
    private String When;
    private String Remark;
    private String CreatedDate;

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public long getMedId() {
        return MedId;
    }

    public void setMedId(long medId) {
        MedId = medId;
    }

    public long getUID() {
        return UID;
    }

    public void setUID(long UID) {
        this.UID = UID;
    }

    public String getBrandName() {
        return BrandName;
    }

    public void setBrandName(String brandName) {
        BrandName = brandName;
    }

    public String getDosageFrom() {
        return DosageFrom;
    }

    public void setDosageFrom(String dosageFrom) {
        DosageFrom = dosageFrom;
    }

    public String getStrength() {
        return Strength;
    }

    public void setStrength(String strength) {
        Strength = strength;
    }

    public String getSymbols() {
        return Symbols;
    }

    public void setSymbols(String symbols) {
        Symbols = symbols;
    }

    public String getForPeriod() {
        return ForPeriod;
    }

    public void setForPeriod(String forPeriod) {
        ForPeriod = forPeriod;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getHow() {
        return How;
    }

    public void setHow(String how) {
        How = how;
    }

    public String getWhen() {
        return When;
    }

    public void setWhen(String when) {
        When = when;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }
}
