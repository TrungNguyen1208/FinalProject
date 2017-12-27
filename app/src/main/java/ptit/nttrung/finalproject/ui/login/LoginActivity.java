package ptit.nttrung.finalproject.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.data.local.StaticConfig;
import ptit.nttrung.finalproject.ui.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();

    private static final String LOGIN_FRAGMENT = "LOGIN_FRAGMENT";
    private static final String REGISTER_FRAGMENT = "REGISTER_FRAGMENT";
    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private boolean firstTimeAccess;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginFragment = (LoginFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.frame_content);

        registerFragment = (RegisterFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.frame_content);

        if (loginFragment == null) {
            loginFragment = LoginFragment.newInstance();
            loginFragment.setCallback(new LoginFragment.LoginFragmentCallback() {
                @Override
                public void onClickRegister() {
                    showRegisterFragment();
                }
            });

            showLoginFragment();
        }

        firstTimeAccess = true;
        initFirebase();
    }

    private void initFirebase() {
        //Khoi tao thanh phan de dang nhap, dang ky
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    StaticConfig.UID = user.getUid();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    if (firstTimeAccess) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        LoginActivity.this.finish();
                    }
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                firstTimeAccess = false;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void showLoginFragment() {
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_content, loginFragment, LOGIN_FRAGMENT);
        transaction.commit();
    }

    private void showRegisterFragment() {
        if (registerFragment == null) {
            registerFragment = RegisterFragment.newInstance();
        }
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_content, registerFragment, LOGIN_FRAGMENT)
                .addToBackStack(null)
                .setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
        transaction.commit();
    }

    public static Intent createIntent(Context context) {
        Intent in = new Intent();
        in.setClass(context, LoginActivity.class);
        return in;
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}
