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
import com.prescriptionpad.app.android.model.MedicineModel;
import com.prescriptionpad.app.android.util.Constants;
import com.prescriptionpad.app.android.util.RealmMasterClass;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by sharana.b on 4/15/2017.
 */
public class CustomMedViewAdapter extends RecyclerView.Adapter<CustomMedViewAdapter.MedItemViewHolder> {

    private static final String TAG = LabTestListViewAdapter.class.getSimpleName();

    private Context context;
    private Activity activity;
    private List medList = null;

    public CustomMedViewAdapter(Context context, Activity activity, List medList) {
        this.context = context;
        this.activity = activity;
        if (medList != null) this.medList = medList;
    }

    @Override
    public MedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        MedItemViewHolder holder = null;
        View view = inflater.inflate(R.layout.custom_medicine_item_view, parent, false);
        holder = new MedItemViewHolder(view);
        view.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(MedItemViewHolder holder, int position) {
        if (medList != null) {
            final MedicineModel model = (MedicineModel) medList.get(position);
            holder.noMedValuesTxt.setVisibility(View.GONE);
            holder.medExistsLyt.setVisibility(View.VISIBLE);
            holder.brandNameTxt.setText(model.getBrandName());
            holder.dosageFromTxt.setText(model.getDosageFrom());
            holder.intervalTxt.setText(model.getForPeriod() + " " + model.getUnit());
            holder.howTxt.setText(model.getHow());
            holder.whenTxt.setText(model.getWhen());
            holder.remarkTxt.setText(model.getRemark());
            holder.symbolsTxt.setText(model.getSymbols());
            holder.deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteMedDetails(model);
                }
            });
        } else {
            holder.noMedValuesTxt.setVisibility(View.VISIBLE);
            holder.medExistsLyt.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (medList != null) {
            return medList.size();
        } else {
            return 1;
        }
    }

    public class MedItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView deleteImg;
        private LinearLayout medExistsLyt;
        private TextView brandNameTxt, dosageFromTxt, intervalTxt, howTxt, whenTxt, remarkTxt, symbolsTxt, noMedValuesTxt;

        public MedItemViewHolder(View itemView) {
            super(itemView);
            deleteImg = (ImageView) itemView.findViewById(R.id.deleteImg);
            brandNameTxt = (TextView) itemView.findViewById(R.id.brandNameTxt);
            dosageFromTxt = (TextView) itemView.findViewById(R.id.dosageFromTxt);
            intervalTxt = (TextView) itemView.findViewById(R.id.intervalTxt);
            howTxt = (TextView) itemView.findViewById(R.id.howTxt);
            whenTxt = (TextView) itemView.findViewById(R.id.whenTxt);
            remarkTxt = (TextView) itemView.findViewById(R.id.remarkTxt);
            symbolsTxt = (TextView) itemView.findViewById(R.id.symbolsTxt);
            noMedValuesTxt = (TextView) itemView.findViewById(R.id.noMedValuesTxt);
            medExistsLyt = (LinearLayout) itemView.findViewById(R.id.medExistsLyt);
        }
    }

    public void deleteMedDetails(final MedicineModel modelOld) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.custom_dialog_confirm_window, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.show();
        TextView saveBtnTxt = (TextView) dialogView.findViewById(R.id.saveBtnTxt);
        TextView cancelBtnTxt = (TextView) dialogView.findViewById(R.id.cancelBtnTxt);
        saveBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Realm realm = RealmMasterClass.initializeRealm(context);
                medList.remove(modelOld);
                if (medList.size() == 0) medList = null;
                notifyDataSetChanged();
                MedicineModel modelNew = realm.where(MedicineModel.class).equalTo("MedId", modelOld.getMedId()).findFirst();
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
