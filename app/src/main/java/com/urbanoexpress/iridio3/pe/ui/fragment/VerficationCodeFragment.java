package com.urbanoexpress.iridio3.pe.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.FragmentVerficationCodeBinding;
import com.urbanoexpress.iridio3.pe.presenter.VerficationCodePresenter;
import com.urbanoexpress.iridio3.pe.ui.MainActivity;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.view.VerficationCodeView;

import java.util.concurrent.Executor;

public class VerficationCodeFragment extends AppThemeBaseFragment implements VerficationCodeView {

    public static final String TAG = "VerficationCodeFragment";

    private FragmentVerficationCodeBinding binding;
    private VerficationCodePresenter presenter;
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;

    public static VerficationCodeFragment newInstance(String isoCountry, String phone,
                                                      String firebaseToken, Boolean isGoogleMock) {
        VerficationCodeFragment fragment = new VerficationCodeFragment();
        Bundle args = new Bundle();
        args.putString("isoCountry", isoCountry);
        args.putString("phone", phone);
        args.putString("firebaseToken", firebaseToken);
        args.putBoolean("isGoogleMock", isGoogleMock);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            presenter = new VerficationCodePresenter(this,
                    "pe",
                    getArguments().getString("phone"),
                    getArguments().getString("firebaseToken"),
                    getArguments().getBoolean("isGoogleMock")
            );
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setBackgroundDrawable(
                ContextCompat.getDrawable(getActivity(), R.drawable.bg_fragment_bienvenida));
        CommonUtils.changeColorStatusBar(getActivity(), R.color.statusBarColor);
        binding = FragmentVerficationCodeBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Crear el cliente de GoogleSignIn
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getViewContext(), gso);

        binding.googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getViewProgress().getVisibility() != View.VISIBLE) {
                    finishActivity();
                }
            }
        });

        if (presenter != null) {
            presenter.init();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public String getTextCode1() {
        return binding.txtCode1.getText().toString().trim();
    }

    @Override
    public String getTextCode2() {
        return binding.txtCode2.getText().toString().trim();
    }

    @Override
    public String getTextCode3() {
        return binding.txtCode3.getText().toString().trim();
    }

    @Override
    public String getTextCode4() {
        return binding.txtCode4.getText().toString().trim();
    }

    @Override
    public String getTextCode5() {
        return binding.txtCode5.getText().toString().trim();
    }

    @Override
    public String getTextCode6() {
        return binding.txtCode6.getText().toString().trim();
    }

    @Override
    public void setTextCode1(String text) {
        binding.txtCode1.setText(text);
    }

    @Override
    public void setTextCode2(String text) {
        binding.txtCode2.setText(text);
    }

    @Override
    public void setTextCode3(String text) {
        binding.txtCode3.setText(text);
    }

    @Override
    public void setTextCode4(String text) {
        binding.txtCode4.setText(text);
    }

    @Override
    public void setTextCode5(String text) {
        binding.txtCode5.setText(text);
    }

    @Override
    public void setTextCode6(String text) {
        binding.txtCode6.setText(text);
    }

    @Override
    public void setHtmlLblMsg(String html) {
        binding.lblMsg.setText(HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    @Override
    public void setEnabledButtonNext(boolean enabled) {
        binding.nextButton.setEnabled(enabled);
    }

    @Override
    public void replaceFragment(Fragment fragment, String tag) {
        if (getLifecycle().getCurrentState().equals(Lifecycle.State.STARTED) ||
                getLifecycle().getCurrentState().equals(Lifecycle.State.RESUMED)) {
            try {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment, tag).commit();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void setupViews() {
        Glide.with(this)
                .load(R.drawable.bg_bienvenida_v2)
                .into(binding.image);

        binding.txtCode6.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                presenter.onBtnContinuarClick();
            }
            return false;
        });

        binding.nextButton.setOnClickListener(v -> presenter.onBtnContinuarClick2());

        binding.txtCode1.addTextChangedListener(textWatcherVerificationCode);
        binding.txtCode2.addTextChangedListener(textWatcherVerificationCode);
        binding.txtCode3.addTextChangedListener(textWatcherVerificationCode);
        binding.txtCode4.addTextChangedListener(textWatcherVerificationCode);
        binding.txtCode5.addTextChangedListener(textWatcherVerificationCode);
        binding.txtCode6.addTextChangedListener(textWatcherVerificationCode);

        binding.txtCode1.setOnKeyListener(onKeyListener);
        binding.txtCode2.setOnKeyListener(onKeyListener);
        binding.txtCode3.setOnKeyListener(onKeyListener);
        binding.txtCode4.setOnKeyListener(onKeyListener);
        binding.txtCode5.setOnKeyListener(onKeyListener);
        binding.txtCode6.setOnKeyListener(onKeyListener);
    }

    TextWatcher textWatcherVerificationCode = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (binding.txtCode1.isFocused()) {
                if (!binding.txtCode1.getText().toString().isEmpty())
                    binding.txtCode2.requestFocus();
            } else if (binding.txtCode2.isFocused()) {
                if (!binding.txtCode2.getText().toString().isEmpty())
                    binding.txtCode3.requestFocus();
            } else if (binding.txtCode3.isFocused()) {
                if (!binding.txtCode3.getText().toString().isEmpty())
                    binding.txtCode4.requestFocus();
            } else if (binding.txtCode4.isFocused()) {
                if (!binding.txtCode4.getText().toString().isEmpty())
                    binding.txtCode5.requestFocus();
            } else if (binding.txtCode5.isFocused()) {
                if (!binding.txtCode5.getText().toString().isEmpty())
                    binding.txtCode6.requestFocus();
            }
        }
    };

    View.OnKeyListener onKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL
                    && event.getAction() == KeyEvent.ACTION_UP) {
                if (binding.txtCode2.isFocused()) {
                    if (binding.txtCode2.getText().toString().isEmpty())
                        binding.txtCode1.requestFocus();
                } else if (binding.txtCode3.isFocused()) {
                    if (binding.txtCode3.getText().toString().isEmpty())
                        binding.txtCode2.requestFocus();
                } else if (binding.txtCode4.isFocused()) {
                    if (binding.txtCode4.getText().toString().isEmpty())
                        binding.txtCode3.requestFocus();
                } else if (binding.txtCode5.isFocused()) {
                    if (binding.txtCode5.getText().toString().isEmpty())
                        binding.txtCode4.requestFocus();
                } else if (binding.txtCode6.isFocused()) {
                    if (binding.txtCode6.getText().toString().isEmpty())
                        binding.txtCode5.requestFocus();
                }
            }
            return false;
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        Log.d("Hola","este es el token: "+idToken);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(  FirebaseUser user){
        if (user!=null){
            presenter.onBtnContinuarClick2();
           // Toast.makeText(getViewContext(), "Bienvenido: "+user.getDisplayName(), Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getViewContext(), "el usuario es nulo", Toast.LENGTH_SHORT).show();
        }

    }

}
