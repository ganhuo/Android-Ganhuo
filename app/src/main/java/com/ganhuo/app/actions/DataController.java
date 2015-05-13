package com.ganhuo.app.actions;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.ganhuo.app.interfaces.AdapterRespondent;
import com.ganhuo.app.interfaces.DataProvider;
import com.ganhuo.app.interfaces.UIRespondent;

import java.util.ArrayList;
import java.util.List;

public abstract class DataController<T> implements DataProvider<T> {

    private List<T> mRepository;

    private boolean isBusy = false;
    private boolean isEnd = false;
    private int mPageSize = 10;
    private int mNextTimeOffset = 0;
    private int mRequestTimes = 0;

    private List<UIRespondent<T>> mUIRespondents;
    private List<AdapterRespondent> mAdapterRespondents;

    public boolean isBusy() {
        return isBusy;
    }

    public boolean isEmpty() {
        return mRepository == null || mRepository.size() == 0;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public int getSize() {
        if (mRepository == null) {
            return 0;
        }
        return mRepository.size();
    }

    public int getRequestOffset() {
        return mNextTimeOffset;
    }

    public int getPageSize() {
        return mPageSize;
    }

    public void setPageSize(int pageSize) {
        mPageSize = pageSize;
    }

    public List<T> getData() throws Exception {
        return mRepository;
    }

    public void clear() {
        if (mRepository != null) {
            mRepository.clear();
            mNextTimeOffset = 0;
            mRequestTimes = 0;
        }
    }

    public void add(T data) {
        if (data != null) {
            mRepository.add(0, data);
            dispatchAdapterMessage(AdapterMessageType.INSERT, 0);
        }
    }

    public void append(T data) {
        if (data != null) {
            mRepository.add(data);
            dispatchAdapterMessage(AdapterMessageType.INSERT, mRepository.size() - 1);
        }
    }

    public void add(List<T> data) {
        if (data == null) {
            isEnd = true;
            return;
        }
        if (mRepository == null) {
            mRepository = new ArrayList<>();
        }
        if (data.size() < mPageSize) {
            isEnd = true;
        }
        mRepository.addAll(0, data);
    }

    public void append(List<T> data) {
        if (data == null) {
            isEnd = true;
            return;
        }
        if (mRepository == null) {
            mRepository = new ArrayList<>();
        }
        if (data.size() < mPageSize) {
            isEnd = true;
        }
        mRepository.addAll(data);
    }

    public void addUIRespondent(UIRespondent<T> respondent) {
        if (mUIRespondents == null) {
            mUIRespondents = new ArrayList<>();
        }
        mUIRespondents.add(respondent);
    }

    public void addAdapterRespondent(AdapterRespondent respondent) {
        if (mAdapterRespondents == null) {
            mAdapterRespondents = new ArrayList<>();
        }
        mAdapterRespondents.add(respondent);
    }

    public final void initialize() {
        doAction(ActionType.Initialize);
    }

    public final void refresh() {
        doAction(ActionType.Refresh);
    }

    public final void more() {
        doAction(ActionType.More);
    }

    protected void doAction(final ActionType type) {
        if (!type.equals(ActionType.Refresh)) {
            if ((isEnd || isBusy)) {
                return;
            }
        }
        isBusy = true;
        switch (type) {
            case Initialize:
                dispatchUIMessage(UIMessageType.InitializeStart, null, null);
                break;
            case More:
                dispatchUIMessage(UIMessageType.LoadingMoreStart, null, null);
                break;
            case Refresh:
                dispatchUIMessage(UIMessageType.RefreshingStart, null, null);
                break;
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                List<T> data = null;
                Exception exception = null;
                try {
                    switch (type) {
                        case Initialize:
                            data = doInitialize();
                            add(data);
                            break;
                        case More:
                            data = doMore();
                            append(data);
                            break;
                        case Refresh:
                            data = doRefresh();
                            add(data);
                            break;
                    }
                    mRequestTimes++;
                    mNextTimeOffset = mPageSize * mRequestTimes;
                } catch (Exception e) {
                    exception = e;
                    e.printStackTrace();
                } finally {
                    if (exception == null)
                        dispatchAdapterMessage(AdapterMessageType.CHANGE, 0);

                    switch (type) {
                        case Initialize:
                            dispatchUIMessage(UIMessageType.InitializeDone, exception, data);
                            break;
                        case More:
                            dispatchUIMessage(UIMessageType.LoadMoreDone, exception, data);
                            break;
                        case Refresh:
                            dispatchUIMessage(UIMessageType.RefreshingDone, exception, data);
                            break;
                    }
                    if (isEnd) {
                        dispatchUIMessage(UIMessageType.End, null, null);
                    }
                    isBusy = false;
                }
            }
        }).start();
    }


    private void dispatchAdapterMessage(AdapterMessageType type, int position) {
        Handler handler = new AdapterPosterHandler(Looper.getMainLooper());
        Message msg = Message.obtain(handler);
        msg.arg1 = type.ordinal();
        msg.arg2 = position;
        msg.sendToTarget();
    }

    private void dispatchUIMessage(UIMessageType type, Exception e, List<T> data) {
        if (mUIRespondents == null || mUIRespondents.size() == 0) {
            return;
        }
        Handler handler = new UIPosterHandler(Looper.getMainLooper());
        Message msg = Message.obtain(handler);
        msg.arg1 = type.ordinal();
        msg.obj = data != null ? data : e;
        msg.sendToTarget();
    }

    public T getData(int position) {
        return mRepository.get(position);
    }


    protected enum ActionType {
        Initialize,
        More,
        Refresh
    }

    public enum AdapterMessageType {
        INSERT,
        CHANGE, REMOVE
    }

    public enum UIMessageType {
        OnDataSetChanged,
        InitializeStart,
        InitializeDone,
        LoadingMoreStart,
        LoadMoreDone,
        RefreshingStart,
        RefreshingDone,
        End
    }

    public class AdapterPosterHandler extends Handler {

        public AdapterPosterHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            for (AdapterRespondent adapterRespondent : mAdapterRespondents) {
                AdapterMessageType type = AdapterMessageType.values()[msg.arg1];
                switch (type) {
                    case INSERT:
                        adapterRespondent.onDataInsert(msg.arg1);
                        break;
                    case REMOVE:
                        adapterRespondent.onDataRemove(msg.arg2);
                        break;
                    case CHANGE:
                        adapterRespondent.onDataChanged();
                        break;
                }

            }
        }

    }


    public class UIPosterHandler extends Handler {

        private UIPosterHandler(Looper looper) {
            super(looper);
        }


        @Override
        public void handleMessage(Message msg) {
            if (getLooper() != Looper.getMainLooper()) {
                throw new RuntimeException("Can not post UI update in non-main thread");
            }
            for (UIRespondent<T> ui : mUIRespondents) {
                UIMessageType type = UIMessageType.values()[msg.arg1];
                Object o = msg.obj;
                List<T> data = o instanceof List ? (List<T>) o : null;
                Exception e = o instanceof Exception ? (Exception) o : null;
                switch (type) {
                    case InitializeStart:
                        ui.onInitializeStart();
                        break;
                    case InitializeDone:
                        ui.onInitializeDone(e, data);
                        break;
                    case LoadingMoreStart:
                        ui.onLoadingMoreStart();
                        break;
                    case LoadMoreDone:
                        ui.onLoadMoreDone(e, data);
                        break;
                    case RefreshingStart:
                        ui.onRefreshingStart();
                        break;
                    case RefreshingDone:
                        ui.onRefreshDone(e, data);
                        break;
                    case End:
                        ui.onEnd();
                        break;
                }
            }
        }
    }
}
