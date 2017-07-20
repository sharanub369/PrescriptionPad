package com.prescriptionpad.app.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prescriptionpad.app.android.R;
import com.prescriptionpad.app.android.model.PatientModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharana.b on 4/15/2017.
 */
public class CustomPatientReportViewAdapter extends RecyclerView.Adapter<CustomPatientReportViewAdapter.ReportItemViewHolder> {

    private static final String TAG = LabTestListViewAdapter.class.getSimpleName();

    private Context context;
    private Activity activity;
    private String diseaseName;
    private List patientList = new ArrayList();

    public CustomPatientReportViewAdapter(Context context, Activity activity, List patientList, String diseaseName) {
        this.context = context;
        this.activity = activity;
        this.patientList = patientList;
        this.diseaseName = diseaseName;
    }

    @Override
    public ReportItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ReportItemViewHolder holder = null;
        View view = inflater.inflate(R.layout.custom_report_by_disease_item_view, parent, false);
        holder = new ReportItemViewHolder(view);
        view.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(ReportItemViewHolder holder, int position) {
        if (patientList.size() > 0) {
            PatientModel model = (PatientModel) patientList.get(position);
            holder.uidTxt.setText(model.getUID() + "");
            holder.patientNameTxt.setText(model.getPatientName());
            holder.ageTxt.setText(model.getAge() + "");
            holder.sexTxt.setText(model.getGender());
            holder.createdDateTxt.setText(model.getCreatedDate());
            holder.diseaseTxt.setText(diseaseName);
        }
    }

    @Override
    public int getItemCount() {
        if (patientList != null) {
            return patientList.size();
        } else {
            return 0;
        }
    }

    public class ReportItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView deleteImg;
        private TextView uidTxt, patientNameTxt, ageTxt, howTxt, sexTxt, createdDateTxt, diseaseTxt;

        public ReportItemViewHolder(View itemView) {
            super(itemView);
            deleteImg = (ImageView) itemView.findViewById(R.id.deleteImg);
            uidTxt = (TextView) itemView.findViewById(R.id.uidTxt);
            patientNameTxt = (TextView) itemView.findViewById(R.id.patientNameTxt);
            ageTxt = (TextView) itemView.findViewById(R.id.ageTxt);
            sexTxt = (TextView) itemView.findViewById(R.id.sexTxt);
            createdDateTxt = (TextView) itemView.findViewById(R.id.createdDateTxt);
            diseaseTxt = (TextView) itemView.findViewById(R.id.diseaseTxt);
        }
    }
}
