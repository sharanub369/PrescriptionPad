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
import com.prescriptionpad.app.android.model.DiseaseModel;
import com.prescriptionpad.app.android.model.LabTestModel;
import com.prescriptionpad.app.android.model.VisitModel;
import com.prescriptionpad.app.android.util.Constants;
import com.prescriptionpad.app.android.util.RealmMasterClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;


/**
 * Created by sharana.b on 1/14/2017.
 */
public class CustomDiseaseAdapter extends RecyclerView.Adapter<CustomDiseaseAdapter.DiseaseItemViewHolder> {

    private static final String TAG = CustomDiseaseAdapter.class.getSimpleName();

    private Context context;
    private Activity activity;
    private List diseaseList = null;

    public CustomDiseaseAdapter(Context context, Activity activity, List diseaseList) {
        this.context = context;
        this.activity = activity;
        if (diseaseList != null) this.diseaseList = diseaseList;
    }

    @Override
    public DiseaseItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_disease_item_view, parent, false);
        DiseaseItemViewHolder holder = new DiseaseItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DiseaseItemViewHolder holder, int position) {
        if (diseaseList != null) {
            holder.noDiseaseValuesTxt.setVisibility(View.GONE);
            holder.diseaseExistsLyt.setVisibility(View.VISIBLE);
            final DiseaseModel model = (DiseaseModel) diseaseList.get(position);
            holder.diseaseNameTxt.setText(model.getDiseaseName());
            holder.diseaseRemarkTxt.setText(model.getDiseaseRemark());
            holder.deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteLabTestDetails(model);
                }
            });
        } else {
            holder.noDiseaseValuesTxt.setVisibility(View.VISIBLE);
            holder.diseaseExistsLyt.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (diseaseList != null) {
            return diseaseList.size();
        } else {
            return 1;
        }
    }

    public class DiseaseItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView deleteImg;
        private LinearLayout diseaseExistsLyt;
        private TextView diseaseNameTxt, diseaseRemarkTxt, noDiseaseValuesTxt;

        public DiseaseItemViewHolder(View itemView) {
            super(itemView);
            deleteImg = (ImageView) itemView.findViewById(R.id.deleteImg);
            diseaseNameTxt = (TextView) itemView.findViewById(R.id.diseaseNameTxt);
            diseaseRemarkTxt = (TextView) itemView.findViewById(R.id.diseaseRemarkTxt);
            noDiseaseValuesTxt = (TextView) itemView.findViewById(R.id.noDiseaseValuesTxt);
            diseaseExistsLyt = (LinearLayout) itemView.findViewById(R.id.diseaseExistsLyt);
        }
    }

    private void deleteLabTestDetails(final DiseaseModel modelOld) {
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
                diseaseList.remove(modelOld);
                if (diseaseList.size() == 0) diseaseList = null;
                notifyDataSetChanged();
                DiseaseModel modelNew = realm.where(DiseaseModel.class).equalTo("DiseaseId", modelOld.getDiseaseId()).findFirst();
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
