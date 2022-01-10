package com.urbanoexpress.iridio.ui.helpers;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.urbanoexpress.iridio.R;

public class ModalHelper {

    public static void showToast(Context context, String message, int duration) {
        try {
            Toast.makeText(context, message, duration).show();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public static AlertDialog.Builder getBuilderAlertDialog(Context context) {
        return new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_App_MaterialAlertDialog);
    }

    public static class BottomPopup {

        public static class Builder {

            private Snackbar snackbar;
            private int actionTextColor = 0xFFD90B24;
            private int backgroundColor = 0xFF0F1015;

            public Builder(View view, String message) {
                if (view != null) {
                    snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
                } else {
                    throw new IllegalArgumentException("Please provide a valid view.");
                }
            }

            public Builder setAction(String text, View.OnClickListener listener) {
                snackbar.setAction(text, listener);
                snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
                return this;
            }

            public Builder setActionTextColor(int color) {
                actionTextColor = color;
                return this;
            }

            public Builder setBackground(int color) {
                backgroundColor = color;
                return this;
            }

            public Snackbar build() {
                snackbar.setBackgroundTint(backgroundColor);

                TextView snackbar_text = snackbar.getView().findViewById(
                        com.google.android.material.R.id.snackbar_text);
                snackbar_text.setMaxLines(10);

                TextView snackbar_action = snackbar.getView().findViewById(
                        com.google.android.material.R.id.snackbar_action);
                snackbar_action.setTextColor(actionTextColor);

                return snackbar;
            }
        }
    }
}
