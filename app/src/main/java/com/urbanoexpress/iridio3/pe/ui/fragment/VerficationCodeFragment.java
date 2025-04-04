package com.urbanoexpress.iridio3.pe.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.FragmentVerficationCodeBinding;
import com.urbanoexpress.iridio3.pe.presenter.VerficationCodePresenter;
import com.urbanoexpress.iridio3.pe.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.view.VerficationCodeView;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VerficationCodeFragment extends AppThemeBaseFragment implements VerficationCodeView {

    public static final String TAG = "VerficationCodeFragment";

    private FragmentVerficationCodeBinding binding;
    private VerficationCodePresenter presenter;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;

    public static VerficationCodeFragment newInstance(String isoCountry, String phone) {
        VerficationCodeFragment fragment = new VerficationCodeFragment();
        Bundle args = new Bundle();
        args.putString("codePhone", isoCountry);
        args.putString("phone", phone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            presenter = new VerficationCodePresenter(this,
                    getArguments().getString("codePhone"),
                    getArguments().getString("phone")
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

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getViewContext(), gso);

        binding.googleSignInButton.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void replaceFragment(Fragment fragment, String tag) {
        if (getLifecycle().getCurrentState().equals(Lifecycle.State.STARTED) ||
                getLifecycle().getCurrentState().equals(Lifecycle.State.RESUMED)) {
            try {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment, tag).commit();
            } catch (NullPointerException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error ocurrido", ex);
            }
        }
    }

    private void setupViews() {
        Glide.with(this)
                .load(R.drawable.bg_bienvenida_v2)
                .into(binding.image);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                showLoginError();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String email = user.getEmail();
                    presenter.onBtnContinueClick(email);
                }
            } else {
                showMessageError();
            }
        });
    }

    private void showMessageError() {
        ModalHelper.getBuilderAlertDialog(requireContext())
                .setTitle(R.string.ver_code_title_authentication_error)
                .setMessage(R.string.ver_code_phone_msg_authentication_error)
                .setPositiveButton(R.string.ver_code_phone_accept, null)
                .show();
    }

    private void showLoginError() {
        ModalHelper.getBuilderAlertDialog(requireContext())
                .setTitle(R.string.ver_code_title_login_error)
                .setMessage(R.string.ver_code_phone_msg_login_error)
                .setPositiveButton(R.string.ver_code_phone_accept, null)
                .show();
    }


}
