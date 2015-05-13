package com.ganhuo.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.ganhuo.app.actions.DataController;
import com.ganhuo.app.holders.EntryViewHolder;
import com.ganhuo.app.models.Entry;

public class EntryListAdapter extends ContentAdapterBase<Entry> {

    private Context mContext;

    public EntryListAdapter(Context context, DataController<Entry> dataController) {
        super(context, dataController);
        this.mContext = context;
    }


    @Override
    protected RecyclerView.ViewHolder onCreateCustomContentHolder(ViewGroup parent, int viewType) {
        return EntryViewHolder.create(mContext, parent);
    }

    @Override
    protected void onBindCustomViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((EntryViewHolder) holder).onBindViewHolder(mDataController.getData(position));
    }

}
