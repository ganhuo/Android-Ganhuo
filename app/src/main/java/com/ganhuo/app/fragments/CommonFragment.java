package com.ganhuo.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ganhuo.app.R;
import com.ganhuo.app.actions.DataController;

import java.util.List;

public abstract class CommonFragment<T> extends ContentFragmentBase<T> {
    protected RecyclerView mRecyclerView;
    protected ViewGroup mRoot, mPagerPage, mLoadingPage;
    protected ContentLoadingProgressBar mLoadingBar;

    protected TextView mLoadingTips;
    protected Button mLoadingRetry;

    protected SwipeRefreshLayout mSwipeRefresh;
    protected RecyclerView.Adapter mAdapter;
    protected DataController<T> mDataController;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_content, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_content);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPagerPage = (ViewGroup) rootView.findViewById(R.id.pager_page);
        mLoadingPage = (ViewGroup) rootView.findViewById(R.id.loading_page);
        mLoadingBar = (ContentLoadingProgressBar) mLoadingPage.findViewById(R.id.loading_bar);
        mLoadingRetry = (Button) mLoadingPage.findViewById(R.id.loading_refresh);
        mLoadingTips = (TextView) mLoadingPage.findViewById(R.id.loading_tips);
        mSwipeRefresh = (SwipeRefreshLayout) mPagerPage;
        mRoot = rootView;
        if (enableRefresh()) {
            mSwipeRefresh.setOnRefreshListener(this);
        } else {
            mSwipeRefresh.setEnabled(false);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mDataController = onGenerateDataController();
        mAdapter = onGenerateAdapter(mDataController);
        mDataController.addUIRespondent(this);
        mDataController.addAdapterRespondent(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    protected abstract DataController<T> onGenerateDataController();

    protected abstract RecyclerView.Adapter onGenerateAdapter(DataController<T> controller);

    protected abstract boolean enableRefresh();

    @Override
    public void onDataChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDataInsert(int position) {
        mAdapter.notifyItemInserted(position);
    }

    @Override
    public void onDataRemove(int position) {
        mAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onRefreshingStart() {
        super.onRefreshingStart();
        mSwipeRefresh.setRefreshing(true);
    }

    @Override
    public void onRefreshDone(Exception e, List<T> data) {
        if (e == null) {
            if (data == null || data.size() == 0) {
                Toast.makeText(getActivity(), "没啦，干货正在准备中呢！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "成功挖掘 " + data.size() + " 条新干货", Toast.LENGTH_SHORT).show();
            }
        } else {
            e.printStackTrace();
            Toast.makeText(getActivity(), "出现了些小问题", Toast.LENGTH_SHORT).show();
        }
        mSwipeRefresh.setRefreshing(false);
    }

    @Override
    public void onInitializeDone(Exception e, List<T> data) {
        super.onInitializeDone(e, data);
        mLoadingBar.hide();
        if (e == null) {
            mRoot.bringChildToFront(mPagerPage);
        } else {
            mLoadingRetry.setVisibility(View.VISIBLE);
            mLoadingTips.setVisibility(View.VISIBLE);
            mLoadingRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLoadingRetry.setVisibility(View.GONE);
                    mLoadingTips.setVisibility(View.GONE);
                    mDataController.initialize();
                }
            });
            mLoadingTips.setVisibility(View.VISIBLE);
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        mDataController.refresh();
    }

}


