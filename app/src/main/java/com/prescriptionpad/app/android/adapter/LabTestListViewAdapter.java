package com.prescriptionpad.app.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.prescriptionpad.app.android.R;
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
public class LabTestListViewAdapter extends RecyclerView.Adapter<LabTestListViewAdapter.ComplaintItemViewHolder> {

    private static final String TAG = LabTestListViewAdapter.class.getSimpleName();

    private Context context;
    private Activity activity;
    private List labTestList = null;

    public LabTestListViewAdapter(Context context, Activity activity, List labTestList) {
        this.context = context;
        this.activity = activity;
        if (labTestList != null) {
            this.labTestList = labTestList;
        }
    }

    @Override
    public ComplaintItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_lab_test_item_view, parent, false);
        ComplaintItemViewHolder holder = new ComplaintItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ComplaintItemViewHolder holder, int position) {
        if (labTestList != null) {
            final LabTestModel model = (LabTestModel) labTestList.get(position);
            holder.noLabTestValuesTxt.setVisibility(View.GONE);
            holder.labTestExistsLyt.setVisibility(View.VISIBLE);
            holder.labNameTxt.setText(model.getLabName());
            holder.testNameTxt.setText(model.getTestName());
            holder.completionDateTxt.setText(model.getCompletionDate());
            holder.unitsTxt.setText(model.getUnit());
            holder.deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteLabTestDetails(model);
                }
            });
        } else {
            holder.noLabTestValuesTxt.setVisibility(View.VISIBLE);
            holder.labTestExistsLyt.setVisibility(View.GONE);
        }
    }

    private void deleteLabTestDetails(final LabTestModel modelOld) {
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
                labTestList.remove(modelOld);
                if (labTestList.size() == 0) labTestList = null;
                notifyDataSetChanged();
                LabTestModel modelNew = realm.where(LabTestModel.class).equalTo("LabTestId", modelOld.getLabTestId()).findFirst();
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

    @Override
    public int getItemCount() {
        if (labTestList != null) {
            return labTestList.size();
        } else {
            return 1;
        }
    }

    public class ComplaintItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView deleteImg;
        private LinearLayout labTestExistsLyt;
        private TextView labNameTxt, testNameTxt, completionDateTxt, unitsTxt, noLabTestValuesTxt;

        public ComplaintItemViewHolder(View itemView) {
            super(itemView);
            deleteImg = (ImageView) itemView.findViewById(R.id.deleteImg);
            labNameTxt = (TextView) itemView.findViewById(R.id.labNameTxt);
            testNameTxt = (TextView) itemView.findViewById(R.id.testNameTxt);
            completionDateTxt = (TextView) itemView.findViewById(R.id.completionDateTxt);
            unitsTxt = (TextView) itemView.findViewById(R.id.unitsTxt);
            noLabTestValuesTxt = (TextView) itemView.findViewById(R.id.noLabTestValuesTxt);
            labTestExistsLyt = (LinearLayout) itemView.findViewById(R.id.labTestExistsLyt);
        }
    }
}
