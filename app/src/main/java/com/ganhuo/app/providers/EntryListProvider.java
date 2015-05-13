package com.ganhuo.app.providers;

import com.avos.avoscloud.AVQuery;
import com.ganhuo.app.actions.DataController;
import com.ganhuo.app.models.Entry;

import java.util.List;

public class EntryListProvider extends DataController<Entry> {


    public List<Entry> getData() throws Exception {
        AVQuery<Entry> query = new AVQuery<>("Entry");
        query.setSkip(getRequestOffset());
        query.limit(getPageSize());
        query.orderByDescending("createdAt");
        return query.find();
    }

    public List<Entry> getLatest() throws Exception {
        if (getData() == null || getData().size() == 0) {
            return null;
        }
        AVQuery<Entry> query = new AVQuery<>("Entry");
        query.whereGreaterThan("createdAt", getData().get(0).getCreatedAt());
        return query.find();
    }

    @Override
    public List<Entry> doInitialize() throws Exception {
        return getData();
    }

    @Override
    public List<Entry> doRefresh() throws Exception {
        return getLatest();
    }

    @Override
    public List<Entry> doMore() throws Exception {
        return getData();
    }
}
