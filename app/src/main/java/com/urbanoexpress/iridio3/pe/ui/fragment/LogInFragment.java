package com.urbanoexpress.iridio3.pe.ui.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.FragmentSplashLoginBinding;
import com.urbanoexpress.iridio3.pe.presenter.SplashLogInPresenter;
import com.urbanoexpress.iridio3.pe.ui.ForgotPasswordActivity;
import com.urbanoexpress.iridio3.pe.ui.ConfiguracionActivity;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.view.SplashLogInView;

/**
 * Created by mick on 24/08/16.
 */

public class LogInFragment extends BaseFragment implements SplashLogInView {

    public static final String TAG = "LogInFragment";

    private FragmentSplashLoginBinding binding;
    private SplashLogInPresenter splashLogInPresenter;

    public LogInFragment() {}

    public static LogInFragment newInstance() {
        return new LogInFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setBackgroundDrawable(
                ContextCompat.getDrawable(getActivity(), R.drawable.bg_fragment_login));
        binding = FragmentSplashLoginBinding.inflate(inflater, container, false);

        setupViews();

        if (splashLogInPresenter == null) {
            splashLogInPresenter = new SplashLogInPresenter(this);
        }

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_login, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_acercade) {
            Intent intent = new Intent(getActivity(), ConfiguracionActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("onlySectionAcercade", true);
            intent.putExtra("args", bundle);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_enter_from_bottom,
                    R.anim.not_slide);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showFormLogin() {
        binding.layoutFormLogin.setVisibility(View.VISIBLE);
    }

    @Override
    public void animateSplashScreen() {
        binding.imgLogoUrbano.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(binding.imgLogoUrbano, "alpha", 0f, 1f);
        objectAnimator.setDuration(2000);
        objectAnimator.start();
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                splashLogInPresenter.initPreLoading();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void setUserName(String userName) {
        binding.txtUserName.setText(userName);
    }

    @Override
    public void showOptionsMenu() {
        setHasOptionsMenu(true);
    }

    @Override
    public void setEnabledBtnLogIn(boolean enabled) {
        binding.loginButton.setEnabled(enabled);
    }

    private void setupViews() {
        setupToolbar(binding.toolbar);
        binding.toolbar.setTitle("");

        binding.loginButton.setOnClickListener(v -> {
            binding.loginButton.setEnabled(false);
            hideKeyboard();
            logIn();
        });

        binding.txtPassword.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                binding.loginButton.setEnabled(false);
                hideKeyboard();
                logIn();
            }
            return false;
        });

        binding.showPasswordButton.setOnClickListener(v -> {
            if (binding.txtPassword.getInputType() ==
                    (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                binding.showPasswordButton.setImageResource(R.drawable.ic_eye_grey);
                binding.txtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                binding.showPasswordButton.setImageResource(R.drawable.ic_eye_off_grey);
                binding.txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            binding.txtPassword.setSelection(binding.txtPassword.getText().toString().length());
        });

        binding.lblForgotPassword.setOnClickListener(v -> {
            getActivity().startActivity(new Intent(getActivity(), ForgotPasswordActivity.class));
            getActivity().overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
        });
    }

    private void logIn() {
        CommonUtils.showOrHideKeyboard(getViewContext(), false, binding.txtPassword);
        splashLogInPresenter.logIn(binding.txtUserName.getText().toString().trim(),
                binding.txtPassword.getText().toString().trim());
    }

    private void setupToolbar(Toolbar toolbar) {
        AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
}
