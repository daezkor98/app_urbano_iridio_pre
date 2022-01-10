package com.urbanoexpress.iridio.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio.util.CommonUtils;

/**
 * Created by mick on 03/06/16.
 */
public abstract class BaseModalsView {

    private static ProgressDialog progressDialog;

    public static void showToast(Context context, String message, int duration) {
        try {
            Toast.makeText(context, message, duration).show();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public static void showToast(Context context, int messageResId, int duration) {
        try {
            Toast.makeText(context, messageResId, duration).show();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public static void showSnackBar(View view, int messageResId, int duration,
                             int actionTextResId, View.OnClickListener onClickListener) {
        try {
            Snackbar snackbar = Snackbar.make(view, messageResId, duration).setAction(actionTextResId, onClickListener);
            TextView snackbar_text = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            TextView snackbar_action = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_action);
            snackbar_text.setMaxLines(10);
            snackbar_action.setTextColor(
                    ContextCompat.getColor(view.getContext(), R.color.colorAccent));
            snackbar.show();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public static void showSnackBar(View view, int messageResId, int duration) {
        try {
            Snackbar snackbar = Snackbar.make(view, messageResId, duration);
            TextView snackbar_text = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            snackbar_text.setMaxLines(10);
            snackbar.show();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public static void showSnackBar(View view, String message, int duration) {
        try {
            Snackbar snackbar = Snackbar.make(view, message, duration);
            TextView snackbar_text = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            snackbar_text.setMaxLines(10);
            snackbar.show();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public static void showAlertDialog(Context context, String title, String message,
                                String textPositiveButton, DialogInterface.OnClickListener onClickPositiveBtn) {
        ModalHelper.getBuilderAlertDialog(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(textPositiveButton, onClickPositiveBtn)
                .show();
    }

    public static void showAlertDialog(Context context, int titleResId, int messageResId,
                                int textPositiveButtonResId, DialogInterface.OnClickListener onClickPositiveBtn) {
        try {
            ModalHelper.getBuilderAlertDialog(context)
                    .setTitle(titleResId)
                    .setMessage(messageResId)
                    .setPositiveButton(textPositiveButtonResId, onClickPositiveBtn)
                    .show();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public static void showAlertDialog(Context context, int titleResId, int messageResId,
                                int textPositiveButtonResId, DialogInterface.OnClickListener onClickPositiveBtn,
                                int textNegativeButtonResId, DialogInterface.OnClickListener onClickNegativeBtn) {
        ModalHelper.getBuilderAlertDialog(context)
                .setTitle(titleResId)
                .setMessage(messageResId)
                .setPositiveButton(textPositiveButtonResId, onClickPositiveBtn)
                .setNegativeButton(textNegativeButtonResId, onClickNegativeBtn)
                .show();
    }

    public static void showAlertDialog(Context context, int titleResId, String message,
                                int textPositiveButtonResId, DialogInterface.OnClickListener onClickPositiveBtn,
                                int textNegativeButtonResId, DialogInterface.OnClickListener onClickNegativeBtn) {
        ModalHelper.getBuilderAlertDialog(context)
                .setTitle(titleResId)
                .setMessage(message)
                .setPositiveButton(textPositiveButtonResId, onClickPositiveBtn)
                .setNegativeButton(textNegativeButtonResId, onClickNegativeBtn)
                .show();
    }

    public void showAlertDialog(Context context, View view,
                                int textPositiveButtonResId, DialogInterface.OnClickListener onClickPositiveBtn,
                                int textNegativeButtonResId, DialogInterface.OnClickListener onClickNegativeBtn) {
        ModalHelper.getBuilderAlertDialog(context)
                .setView(view)
                .setPositiveButton(textPositiveButtonResId, onClickPositiveBtn)
                .setNegativeButton(textNegativeButtonResId, onClickNegativeBtn)
                .show();
    }

    public void showAlertDialog(Context context, int titleResId, int messageResId, int iconResId,
                                       int textPositiveButtonResId, DialogInterface.OnClickListener onClickPositiveBtn) {
        try {
            ModalHelper.getBuilderAlertDialog(context)
                    .setTitle(titleResId)
                    .setMessage(messageResId)
                    .setIcon(iconResId)
                    .setPositiveButton(textPositiveButtonResId, onClickPositiveBtn)
                    .show();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public static void showAlertDialog(Context context, int messageResId,
                                       int textPositiveButtonResId, DialogInterface.OnClickListener onClickPositiveBtn,
                                       int textNegativeButtonResId, DialogInterface.OnClickListener onClickNegativeBtn) {
        try {
            ModalHelper.getBuilderAlertDialog(context)
                    .setMessage(messageResId)
                    .setPositiveButton(textPositiveButtonResId, onClickPositiveBtn)
                    .setNegativeButton(textNegativeButtonResId, onClickNegativeBtn)
                    .show();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public void showProgressDialog(Context context, String title, String message) {
        initalizeProgressBar(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public static void showProgressDialog(Context context, int title, int message) {
        initalizeProgressBar(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(context.getString(message));
        progressDialog.show();
    }

    public static void showProgressDialog(Context context, int message) {
        initalizeProgressBar(context);
        progressDialog.setMessage(context.getString(message));
        progressDialog.show();
    }

    public static void setMsgProgressDialog(String message) {
        if (progressDialog != null) {
            progressDialog.setMessage(message);
        }
    }

    public static void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public static void showPgTop(FrameLayout containerPgTop) {
        containerPgTop.setVisibility(View.VISIBLE);
        Animation animScale = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animScale.setDuration(300);
        animScale.setInterpolator(new AccelerateInterpolator());
        animScale.setFillAfter(true);
        containerPgTop.startAnimation(animScale);
    }

    public static void hidePgTop(FrameLayout containerPgTop) {
        if (containerPgTop.getVisibility() == View.VISIBLE) {
            containerPgTop.setVisibility(View.GONE);
            Animation animScale = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animScale.setDuration(300);
            animScale.setInterpolator(new AccelerateDecelerateInterpolator());
            animScale.setFillAfter(true);
            containerPgTop.startAnimation(animScale);
        }
    }

    private static void initalizeProgressBar(Context context) {
        progressDialog = null;
        if (CommonUtils.isAndroidLollipop()) {
            progressDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
        } else {
            progressDialog = new ProgressDialog(context);
        }
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
    }

}
