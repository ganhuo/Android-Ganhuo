package com.ganhuo.app.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;

import com.ganhuo.app.interfaces.AdapterRespondent;
import com.ganhuo.app.interfaces.UIRespondent;

import java.util.List;

public abstract class ContentFragmentBase<T> extends Fragment implements UIRespondent<T>, AdapterRespondent, SwipeRefreshLayout.OnRefreshListener {


    @Override
    public void onInitializeStart() {

    }

    @Override
    public void onInitializeDone(Exception e, List<T> data) {

    }

    @Override
    public void onLoadingMoreStart() {

    }

    @Override
    public void onLoadMoreDone(Exception e, List<T> data) {

    }

    @Override
    public void onRefreshingStart() {

    }

    @Override
    public void onRefreshDone(Exception e, List<T> data) {

    }

    @Override
    public void onEnd() {

    }
}
