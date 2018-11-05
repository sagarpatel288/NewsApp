package com.library.android.common.listeners;


import android.content.Intent;
import android.view.View;

import com.library.android.common.ui.dialogs.MyAlertDialog;

import java.util.List;

public abstract class Callbacks {
    /**
     * Interface callback from {@link MyAlertDialog} to host
     * <p>
     * Gives callback to host for whether {@link MyAlertDialog#btnLeft} or {@link MyAlertDialog#btnRight} has clicked
     * <p>
     *
     * @since 1.0
     */
    public interface OnDialogBtnClick {
        void onLeftBtnClick();

        void onRightBtnClick();
    }

    public interface OnFragmentLoad {
        void onFragmentVisible();

        void onFragmentHide();
    }

    public interface OnEventCallback {
        void onEventClick(View view, int positionTag);
    }

    public interface NetworkConnectionListener {
        void onConnectionChanged(boolean isConnected);
    }

    public interface ResultStatus {
        void onFail(Intent intent, String api, int pageNumber, Object... objects);

        void onSuccess(Intent intent, String api, int pageNumber, Object... objects);
    }

    public interface ApiResponse extends ResultStatus {
        void onGetData(Intent intent, String api, int pageNumber, List dataList, Object... objects);

        void onEmptyData(Intent intent, String api, int pageNumber, Object... objects);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
