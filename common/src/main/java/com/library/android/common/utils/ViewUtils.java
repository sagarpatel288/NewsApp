package com.library.android.common.utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.library.android.common.ui.baseui.GlideApp;

// Note: 10/25/2018 by sagar  This class has no plan to be extended by and hence making it final
// Note: 10/25/2018 by sagar  https://stackoverflow.com/questions/5181578/what-is-the-point-of-final-class-in-java
// Note: 10/25/2018 by sagar  This class is not suppose to be inherited or extended by any other class and hence it is final
public final class ViewUtils {

    // Note: 10/25/2018 by sagar  Supressing constructor as it is never going to be instantiated
    // Note: 10/25/2018 by sagar  https://stackoverflow.com/questions/25658330/why-java-util-objects-private-constructor-throws-assertionerror
    // Note: 10/25/2018 by sagar  Reflection proof
    private ViewUtils() {
    }

    ;

    /*Shows toast message*/
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /*Toggles visibility for given view/s*/
    public static void toggleVisibility(int visibility, View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                view.setVisibility(visibility);
            }
        }
    }

    /*Tints color*/
    public static void tintColor(ImageView imageView, int color) {
        imageView.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    public static void setSelected(View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                view.setSelected(true);
            }
        }
    }

    public static void setText(TextView tv, String text) {
        if (tv != null) {
            tv.setVisibility(View.VISIBLE);
            tv.setText(StringUtils.getDefaultString(text, ""));
        }
    }

    public static void setEnable(boolean isEnable, View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                view.setEnabled(isEnable);
            }
        }
    }

    public static void setScrollable(TextView... textViews) {
        if (textViews != null) {
            for (TextView tv : textViews) {
                tv.setMovementMethod(new ScrollingMovementMethod());
                tv.setSelected(true);
            }
        }
    }

    public static void loadImage(Context context, int resourceId, int placeHolderId, int errorResId, ImageView imageView) {
        GlideApp.with(context)
                .load(resourceId)
                .placeholder(placeHolderId)
                .error(errorResId)
                .into(imageView);
    }

    public static void loadImage(Context context, String resourceUrl, int placeHolderId, int errorResId, ImageView imageView) {
        GlideApp.with(context)
                .load(resourceUrl)
                .placeholder(placeHolderId)
                .error(errorResId)
                .into(imageView);
    }

    public static void optimizeRecyclerView(RecyclerView recyclerView){
        if (recyclerView != null){
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(20);
        }
    }
}
