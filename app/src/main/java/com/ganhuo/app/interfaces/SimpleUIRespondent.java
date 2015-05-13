package com.ganhuo.app.interfaces;

import java.util.List;

public class SimpleUIRespondent<T> implements UIRespondent<T> {
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
