package com.urbanoexpress.iridio.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.databinding.ActivityInitBinding;
import com.urbanoexpress.iridio.ui.fragment.RequestLocationPermissionFragment;
import com.urbanoexpress.iridio.ui.fragment.RequestPermissionFragment;
import com.urbanoexpress.iridio.util.PermissionUtils;

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
