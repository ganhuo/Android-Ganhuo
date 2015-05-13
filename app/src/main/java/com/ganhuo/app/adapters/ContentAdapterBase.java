package com.ganhuo.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.ganhuo.app.R;
import com.ganhuo.app.actions.DataController;
import com.ganhuo.app.holders.FooterViewHolder;
import com.ganhuo.app.interfaces.AdapterRespondent;
import com.ganhuo.app.interfaces.SimpleUIRespondent;

import java.util.List;

public abstract class ContentAdapterBase<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected Context mContext;
    protected LayoutInflater mInflater;
    protected DataController<T> mDataController;
    protected View mLoadingMoreView;

    public ContentAdapterBase(Context mContext, final DataController<T> mDataController) {
        this.mContext = mContext;
        this.mDataController = mDataController;
        this.mInflater = LayoutInflater.from(mContext);
        this.mDataController.addAdapterRespondent(new AdapterRespondent() {
            @Override
            public void onDataChanged() {
                notifyDataSetChanged();
            }

            @Override
            public void onDataInsert(int position) {
                notifyDataSetChanged();
            }

            @Override
            public void onDataRemove(int position) {
                notifyItemRemoved(position);
            }
        });
        this.mDataController.addUIRespondent(new DataObserver());
    }

    @Override
    public int getItemViewType(int position) {
        if (needHeader() && position == 0) {
            return CommonFeature.HEADER.ordinal();
        }

        if (position == getItemCount() - 1) {
            return CommonFeature.FOOTER.ordinal();
        }

        return CommonFeature.COMMON.ordinal();
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CommonFeature.FOOTER.ordinal()) {
            mLoadingMoreView = mInflater.inflate(R.layout.recyclerview_footer, parent, false);
            return new FooterViewHolder(mLoadingMoreView);
        } else if (viewType == CommonFeature.HEADER.ordinal()) {
            return onCreateCustomHeaderHolder(parent);
        } else {
            return onCreateCustomContentHolder(parent, viewType);
        }
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (needHeader() && position == 0) {
            onBindCustomHeaderHolder(holder);
            return;
        }

        if (needHeader()) {
            position -= 1;
        }

        if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            if (mDataController.isEnd()) {
                footerViewHolder.onBindViewHolder();
            } else {
                mDataController.more();
            }
        } else {
            onBindCustomViewHolder(holder, position);
        }

    }

    protected RecyclerView.ViewHolder onCreateCustomHeaderHolder(ViewGroup parent) {
        return null;
    }

    protected void onBindCustomHeaderHolder(RecyclerView.ViewHolder holder) {

    }

    protected abstract RecyclerView.ViewHolder onCreateCustomContentHolder(ViewGroup parent, int viewType);


    protected abstract void onBindCustomViewHolder(RecyclerView.ViewHolder holder, int position);

    protected boolean needHeader() {
        return false;
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mDataController.initialize();
    }

    @Override
    public int getItemCount() {
        if (mDataController.getSize() == 0) {
            if (needHeader()) {
                return 1;
            } else {
                return 0;
            }
        }
        if (needHeader()) {
            return mDataController.getSize() + 2;
        }

        return mDataController.getSize() + 1;
    }

    public enum CommonFeature{
        HEADER,
        COMMON,
        FOOTER
    }

    private class DataObserver extends SimpleUIRespondent<T> {
        @Override
        public void onLoadMoreDone(Exception e, List data) {
            if (e != null && mLoadingMoreView != null) {
                final Button refresh = (Button) mLoadingMoreView.findViewById(R.id.loading_more_retry);
                final TextView tips = (TextView) mLoadingMoreView.findViewById(R.id.loading_more_tips);
                final ProgressBar progressBar = (ProgressBar) mLoadingMoreView.findViewById(R.id.loading_more_progress);
                progressBar.setVisibility(View.INVISIBLE);
                refresh.setVisibility(View.VISIBLE);
                tips.setVisibility(View.VISIBLE);
                refresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refresh.setVisibility(View.INVISIBLE);
                        tips.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        mDataController.more();
                    }
                });
            }
        }
    }
}

