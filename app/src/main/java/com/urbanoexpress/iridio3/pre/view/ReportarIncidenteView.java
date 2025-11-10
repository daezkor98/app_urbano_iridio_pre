package com.urbanoexpress.iridio3.pre.view;

import androidx.fragment.app.Fragment;
import android.widget.EditText;

public interface ReportarIncidenteView extends BaseView {
    void showImage(String imagePath);
    EditText getViewTxtComentarios();
    Fragment getFragment();
}
