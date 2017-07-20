package com.prescriptionpad.app.android.model;

import java.sql.Timestamp;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sharana.b on 4/13/2017.
 */
public class LabTestModel extends RealmObject {
    @PrimaryKey
    private long LabTestId;
    private long UID;
    private String LabName;
    private String TestName;
    private String CompletionDate;
    private String Unit;
    private String CreatedDate;

    public long getLabTestId() {
        return LabTestId;
    }

    public void setLabTestId(long labTestId) {
        LabTestId = labTestId;
    }

    public long getUID() {
        return UID;
    }

    public void setUID(long UID) {
        this.UID = UID;
    }

    public String getLabName() {
        return LabName;
    }

    public void setLabName(String labName) {
        LabName = labName;
    }

    public String getTestName() {
        return TestName;
    }

    public void setTestName(String testName) {
        TestName = testName;
    }

    public String getCompletionDate() {
        return CompletionDate;
    }

    public void setCompletionDate(String completionDate) {
        CompletionDate = completionDate;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }
}
