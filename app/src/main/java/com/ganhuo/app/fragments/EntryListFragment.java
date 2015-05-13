package com.ganhuo.app.fragments;

import android.support.v7.widget.RecyclerView;

import com.ganhuo.app.actions.DataController;
import com.ganhuo.app.adapters.EntryListAdapter;
import com.ganhuo.app.models.Entry;
import com.ganhuo.app.providers.EntryListProvider;

public class EntryListFragment extends CommonFragment<Entry> {
    @Override
    protected DataController<Entry> onGenerateDataController() {
        return new EntryListProvider();
    }

    @Override
    protected RecyclerView.Adapter onGenerateAdapter(DataController<Entry> controller) {
        return new EntryListAdapter(getActivity(), controller);
    }

    @Override
    protected boolean enableRefresh() {
        return true;
    }
}
