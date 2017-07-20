package com.prescriptionpad.app.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.prescriptionpad.app.android.R;
import com.prescriptionpad.app.android.model.DiseaseModel;
import com.prescriptionpad.app.android.model.LabTestModel;
import com.prescriptionpad.app.android.model.VisitModel;
import com.prescriptionpad.app.android.util.Constants;
import com.prescriptionpad.app.android.util.RealmMasterClass;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;


/**
 * Created by sharana.b on 1/14/2017.
 */
public class CustomVisitsAdapter extends RecyclerView.Adapter<CustomVisitsAdapter.AdviceItemViewHolder> {

    private static final String TAG = CustomVisitsAdapter.class.getSimpleName();

    private Context context;
    private Activity activity;
    private List visitList = null;

    public CustomVisitsAdapter(Context context, Activity activity, List visitList) {
        this.context = context;
        this.activity = activity;
        if (visitList != null) this.visitList = visitList;
    }

    @Override
    public AdviceItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_visits_item_view, parent, false);
        AdviceItemViewHolder holder = new AdviceItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdviceItemViewHolder holder, int position) {
        if (visitList != null) {
            holder.noVisitValuesTxt.setVisibility(View.GONE);
            holder.visitValuesExistLyt.setVisibility(View.VISIBLE);
            final VisitModel model = (VisitModel) visitList.get(position);
            holder.nextVisitDateTxt.setText(model.getNextVisitDate());
            holder.assignedDoctorTxt.setText(model.getAssignedDoctor());
            holder.visitNotesTxt.setText(model.getNotes());
            holder.deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteVisitDetails(model);
                }
            });
        } else {
            holder.noVisitValuesTxt.setVisibility(View.VISIBLE);
            holder.visitValuesExistLyt.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (visitList != null) {
            return visitList.size();
        } else {
            return 1;
        }
    }

    public class AdviceItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView deleteImg;
        private LinearLayout visitValuesExistLyt;
        private TextView nextVisitDateTxt, assignedDoctorTxt, visitNotesTxt, noVisitValuesTxt;

        public AdviceItemViewHolder(View itemView) {
            super(itemView);
            deleteImg = (ImageView) itemView.findViewById(R.id.deleteImg);
            nextVisitDateTxt = (TextView) itemView.findViewById(R.id.nextVisitDateTxt);
            assignedDoctorTxt = (TextView) itemView.findViewById(R.id.assignedDoctorTxt);
            visitNotesTxt = (TextView) itemView.findViewById(R.id.visitNotesTxt);
            noVisitValuesTxt = (TextView) itemView.findViewById(R.id.noVisitValuesTxt);
            visitValuesExistLyt = (LinearLayout) itemView.findViewById(R.id.visitValuesExistLyt);
        }
    }

    private void deleteVisitDetails(final VisitModel modelOld) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.custom_dialog_confirm_window, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        // set color transpartent
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView saveBtnTxt = (TextView) dialogView.findViewById(R.id.saveBtnTxt);
        TextView cancelBtnTxt = (TextView) dialogView.findViewById(R.id.cancelBtnTxt);
        saveBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Realm realm = RealmMasterClass.initializeRealm(context);
                visitList.remove(modelOld);
                if (visitList.size() == 0) visitList = null;
                notifyDataSetChanged();
                VisitModel modelNew = realm.where(VisitModel.class).equalTo("VisitId", modelOld.getVisitId()).findFirst();
                if (modelNew != null) {
                    realm.beginTransaction();
                    modelNew.removeFromRealm();
                    realm.commitTransaction();
                    realm.close();
                }
                Toast.makeText(context, Constants.Errors.KEY_DELETE_SUCCESS, Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        cancelBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

}
