package com.prescriptionpad.app.android.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sharana.b on 4/13/2017.
 */
public class VisitModel extends RealmObject {
    @PrimaryKey
    private long VisitId;
    private long UID;
    private String NextVisitDate;
    private String Notes;
    private String AssignedDoctor;
    private String CreatedDate;

    public String getAssignedDoctor() {
        return AssignedDoctor;
    }

    public void setAssignedDoctor(String assignedDoctor) {
        AssignedDoctor = assignedDoctor;
    }

    public long getVisitId() {
        return VisitId;
    }

    public void setVisitId(long visitId) {
        VisitId = visitId;
    }

    public long getUID() {
        return UID;
    }

    public void setUID(long UID) {
        this.UID = UID;
    }

    public String getNextVisitDate() {
        return NextVisitDate;
    }

    public void setNextVisitDate(String nextVisitDate) {
        NextVisitDate = nextVisitDate;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }
}
