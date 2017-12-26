package ptit.nttrung.finalproject.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import ptit.nttrung.finalproject.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();

    private static final String LOGIN_FRAGMENT = "LOGIN_FRAGMENT";
    private static final String REGISTER_FRAGMENT = "REGISTER_FRAGMENT";
    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;

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
