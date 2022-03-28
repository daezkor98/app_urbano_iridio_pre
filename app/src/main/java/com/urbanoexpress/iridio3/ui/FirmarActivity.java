package com.urbanoexpress.iridio3.ui;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.ActivityFirmarBinding;
import com.urbanoexpress.iridio3.presenter.EntregaGEPresenter;
import com.urbanoexpress.iridio3.util.CameraUtils;
import com.urbanoexpress.iridio3.util.FileUtils;

public class FirmarActivity extends AppThemeBaseActivity {

    private ActivityFirmarBinding binding;
    private File fileFirma;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFirmarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar(binding.toolbar);
        setScreenTitle(R.string.title_activity_firmar);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_firmar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.borrarFirma) {
            binding.touchDrawCanvas.newCanvas();
            return true;
        } else if (item.getItemId() == R.id.guardarFirma) {
            if (binding.touchDrawCanvas.wasTouched()) {
                fileFirma = FileUtils.generateFile(FirmarActivity.this,
                        CameraUtils.generateImageName("Firma"),
                        getIntent().getExtras().getString("pathDirectory"));
                binding.touchDrawCanvas.saveCanvas(fileFirma);
                sendOnSaveFirmaReceiver();
                finish();
            } else {
                new AlertDialog.Builder(FirmarActivity.this)
                        .setTitle(R.string.text_advertencia)
                        .setMessage(R.string.activity_firmar_message_no_puede_tomar_foto)
                        .setPositiveButton(R.string.text_aceptar, null).create().show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void animOnLeaveActivity() {
        overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_bottom);
    }

    /**
     * Receiver
     *
     * {@link EntregaGEPresenter#saveFirmaReceiver}
     */
    private void sendOnSaveFirmaReceiver() {
        Intent intent = new Intent("OnSaveFirma");
        intent.putExtra("file", fileFirma);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
