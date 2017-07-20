package com.prescriptionpad.app.android.model;

import java.sql.Timestamp;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sharana.b on 4/13/2017.
 */
public class DiseaseModel extends RealmObject {

    @PrimaryKey
    private long DiseaseId;
    private long UID;
    private String DiseaseName;
    private String DiseaseRemark;
    private String CreatedDate;

    public DiseaseModel() {
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public long getDiseaseId() {
        return DiseaseId;
    }

    public void setDiseaseId(long diseaseId) {
        this.DiseaseId = diseaseId;
    }

    public long getUID() {
        return UID;
    }

    public void setUID(long UID) {
        this.UID = UID;
    }

    public DiseaseModel(String diseaseName, String diseaseRemark) {
        this.DiseaseName = diseaseName;
        this.DiseaseRemark = diseaseRemark;
    }

    public String getDiseaseName() {
        return DiseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.DiseaseName = diseaseName;
    }

    public String getDiseaseRemark() {
        return DiseaseRemark;
    }

    public void setDiseaseRemark(String diseaseRemark) {
        this.DiseaseRemark = diseaseRemark;
    }
}
