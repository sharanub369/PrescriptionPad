package com.prescriptionpad.app.android.model;

import java.io.Serializable;

/**
 * Created by sharana.b on 4/19/2017.
 */
public class ViewHistoryTempModel implements Serializable {
    private String title;
    private String historyText;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHistoryText() {
        return historyText;
    }

    public void setHistoryText(String historyText) {
        this.historyText = historyText;
    }
}
