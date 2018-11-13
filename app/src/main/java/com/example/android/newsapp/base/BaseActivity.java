package com.example.android.newsapp.base;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewStub;
import android.widget.ProgressBar;

import com.library.android.common.databinding.BaseViewStubLayoutBinding;
import com.library.android.common.listeners.Callbacks;
import com.library.android.common.utils.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.util.Log.d;
import static com.library.android.common.appconstants.AppConstants.TAG;

public abstract class BaseActivity extends AppCompatActivity implements Callbacks.NetworkConnectionListener {

    BaseViewStubLayoutBinding binding;
    @BindView(com.library.android.common.R.id.progressbar)
    ProgressBar progressbar;
    @BindView(com.library.android.common.R.id.view_stub)
    ViewStub viewStub;
    private boolean hasStubInflated;
    private ConnectivityManager mConnectivityManager;
    private ConnectivityManager.NetworkCallback mNetworkCallback;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Normal method to inflate the layout
        setContentView(com.library.android.common.R.layout.base_view_stub_layout);
        ButterKnife.bind(this);
        //Data binding to inflate the layout and binding views at the same time
//        binding = BaseViewStubLayoutBinding.inflate(getLayoutInflater());
        /*For fragments, listview or recyclerview adapter, we can use below method:
        binding = DataBindingUtil.setContentView(this, R.layout.base_activity_layout);*/
        viewStub.setLayoutResource(getLayoutId());
        viewStub.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, View inflated) {
                //Abstract method
                onViewStubInflated(inflated, savedInstanceState);
                ButterKnife.bind(this, inflated);
                initNetworkManager();
                initControllers();
                handleViews();
                setListeners();
                restoreValues(savedInstanceState);
                // Note: 11/4/2018 by sagar  Manually check first time
                onGetConnectionState(NetworkUtils.isNetworkAvailable(getApplicationContext()));
                //Normal method to hide progress bar
                onViewStubInflated();
            }
        });

        if (!hasStubInflated) {
            viewStub.inflate();
        }
    }

    public abstract int getLayoutId();

    //Bind the inflatedView for data binding
    public abstract void onViewStubInflated(View inflatedView, Bundle savedInstanceState);

    private void initNetworkManager() {
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mNetworkCallback = new ConnectivityManager.NetworkCallback(){
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    d(TAG, "onAvailable: ");
                    // Note: 11/4/2018 by sagar  Notify that yes, we are connected to internet
                    onGetConnectionState(true);
                }

                @Override
                public void onLosing(Network network, int maxMsToLive) {
                    super.onLosing(network, maxMsToLive);
                    d(TAG, "onLosing: ");
                }

                @Override
                public void onLost(Network network) {
                    super.onLost(network);
                    d(TAG, "onLost: ");
                    // Note: 11/4/2018 by sagar  Notify that we have lost our internet connection
                    onGetConnectionState(false);
                }

                @Override
                public void onUnavailable() {
                    super.onUnavailable();
                    d(TAG, "onUnavailable: ");
                }

                @Override
                public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                    d(TAG, "onCapabilitiesChanged: ");
                }

                @Override
                public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
                    super.onLinkPropertiesChanged(network, linkProperties);
                    d(TAG, "onLinkPropertiesChanged: ");
                }
            };
        }
        if (mConnectivityManager != null) {
            // Note: 11/4/2018 by sagar  To support connectivity service for API >= N
            // Note: 11/4/2018 by sagar  https://developer.android.com/training/monitoring-device-state/connectivity-monitoring
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mConnectivityManager.registerDefaultNetworkCallback(mNetworkCallback);
            }
            // Note: 11/4/2018 by sagar  No else part because for API < N, we are doing connectivity service by NetworkUtils class
        }
    }

    public abstract void initControllers();

    public abstract void handleViews();

    public abstract void setListeners();

    public abstract void restoreValues(Bundle savedInstanceState);

    public abstract void onGetConnectionState(boolean isConnected);

    private void onViewStubInflated() {
        hasStubInflated = true;
        hideProgressbar();
    }

    public void hideProgressbar() {
        if (progressbar != null) {
            progressbar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hasStubInflated = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
        }
    }

    @Override
    public void onConnectionChanged(boolean isConnected) {
        onGetConnectionState(isConnected);
    }

    public ViewModel getViewModel(Class viewModelClass) {
        return ViewModelProviders.of(this).get(viewModelClass);
    }
}
