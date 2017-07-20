package com.prescriptionpad.app.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prescriptionpad.app.android.R;
import com.prescriptionpad.app.android.model.PatientModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharana.b on 4/20/2017.
 */
public class CustomSearchResultByDateAdapter extends RecyclerView.Adapter<CustomSearchResultByDateAdapter.SearchResultByDateItemViewHolder> {

    private static final String TAG = LabTestListViewAdapter.class.getSimpleName();

    private Context context;
    private Activity activity;
    private List patientList = new ArrayList();

    public CustomSearchResultByDateAdapter(Context context, Activity activity, List patientList) {
        this.context = context;
        this.activity = activity;
        this.patientList = patientList;
    }

    @Override
    public SearchResultByDateItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        SearchResultByDateItemViewHolder holder = null;
        View view = inflater.inflate(R.layout.custom_search_by_date_item_view, parent, false);
        holder = new SearchResultByDateItemViewHolder(view);
        view.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(SearchResultByDateItemViewHolder holder, int position) {
        if (patientList.size() > 0) {
            PatientModel model = (PatientModel) patientList.get(position);
            holder.uidTxt.setText(model.getUID() + "");
            holder.patientNameTxt.setText(model.getPatientName());
            holder.guardianNameTxt.setText(model.getFatherHusbandName());
            holder.ageTxt.setText(model.getAge() + "");
            holder.addressTxt.setText(model.getPatientAddress());
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

    public class SearchResultByDateItemViewHolder extends RecyclerView.ViewHolder {
        private TextView uidTxt, patientNameTxt, guardianNameTxt, ageTxt, addressTxt;

        public SearchResultByDateItemViewHolder(View itemView) {
            super(itemView);
            uidTxt = (TextView) itemView.findViewById(R.id.uidTxt);
            patientNameTxt = (TextView) itemView.findViewById(R.id.patientNameTxt);
            guardianNameTxt = (TextView) itemView.findViewById(R.id.guardianNameTxt);
            ageTxt = (TextView) itemView.findViewById(R.id.ageTxt);
            addressTxt = (TextView) itemView.findViewById(R.id.addressTxt);
        }
    }
}
