package com.prescriptionpad.app.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prescriptionpad.app.android.R;
import com.prescriptionpad.app.android.model.DiseaseModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by sharana.b on 1/14/2017.
 */
public class CustomHistoryAdapter extends RecyclerView.Adapter<CustomHistoryAdapter.HistoryItemViewHolder> {

    private static final String TAG = CustomHistoryAdapter.class.getSimpleName();

    private Context context;
    private Activity activity;
    private List historyList = new ArrayList();

    public CustomHistoryAdapter(Context context, Activity activity, List historyList) {
        this.context = context;
        this.activity = activity;
        this.historyList = historyList;
    }

    @Override
    public HistoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_details_item_view, parent, false);
        HistoryItemViewHolder holder = new HistoryItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(HistoryItemViewHolder holder, int position) {
        if (historyList.size() > 0) {
            holder.detailsItemTxt.setText(historyList.get(position).toString());
        }
    }

    @Override
    public int getItemCount() {
        if (historyList != null) {
            return historyList.size();
        } else {
            return 0;
        }
    }

    public class HistoryItemViewHolder extends RecyclerView.ViewHolder {
        private TextView detailsItemTxt;

        public HistoryItemViewHolder(View itemView) {
            super(itemView);
            detailsItemTxt = (TextView) itemView.findViewById(R.id.detailsItemTxt);
        }
    }
}
