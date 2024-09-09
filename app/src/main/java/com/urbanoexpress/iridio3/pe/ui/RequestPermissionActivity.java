package com.urbanoexpress.iridio3.pe.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.ActivityInitBinding;
import com.urbanoexpress.iridio3.pe.ui.fragment.RequestLocationPermissionFragment;
import com.urbanoexpress.iridio3.pe.ui.fragment.RequestPermissionFragment;
import com.urbanoexpress.iridio3.pe.util.PermissionUtils;

public class RequestPermissionActivity extends AppCompatActivity {

    private ActivityInitBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (!PermissionUtils.checkBasicPermissions(this)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    RequestPermissionFragment.newInstance(),
                    RequestPermissionFragment.TAG).commit();
            return;
        }

        if (!PermissionUtils.checkBackgroundLocationPermission(this)) {
            getSupportFragmentManager().beginTransaction().add(R.id.container,
                    RequestLocationPermissionFragment.newInstance(),
                    RequestLocationPermissionFragment.TAG).commit();
        }
    }

    @Override
    public void onBackPressed() { }
}
