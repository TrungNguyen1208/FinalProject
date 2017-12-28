package ptit.nttrung.finalproject.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.BaseFragment;
import ptit.nttrung.finalproject.ui.main.MainActivity;

public class LoginFragment extends BaseFragment implements LoginView{

    @BindView(R.id.edit_email)
    EditText editEmail;
    @BindView(R.id.edit_pass)
    EditText editPass;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_register)
    Button tvRegister;
    @BindView(R.id.tv_lost_pass)
    Button tvLostPass;

    private LoginFragmentCallback callback;

    public interface LoginFragmentCallback {
        void onClickRegister();
    }

    public void setCallback(LoginFragmentCallback callback) {
        this.callback = callback;
    }

    private LoginPresenter presenter;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, contentView);

        if (presenter == null) {
            presenter = new LoginPresenter(getContext());
        }
        presenter.attachView(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onLogInClick();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onSignUpClick();
            }
        });

        tvLostPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialogRecoEmail();
            }
        });
        return contentView;
    }

    @Override
    public boolean checkValidate() {
        boolean valid = true;
        String email = editEmail.getText().toString();
        String password = editPass.getText().toString();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Enter a valid email address");
            valid = false;
        } else {
            editEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            editPass.setError("Between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            editPass.setError(null);
        }

        return valid;
    }

    @Override
    public String getEmail() {
        return editEmail.getText().toString();
    }

    @Override
    public String getPassword() {
        return editPass.getText().toString();
    }

    @Override
    public void startMainActivity() {
        presenter.detachView();
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    @Override
    public void startRegisterActivity() {
        callback.onClickRegister();
    }

    @Override
    public void showInputDialogRecoEmail() {
        new LovelyTextInputDialog(getContext(), R.style.EditTextTintTheme)
                .setTopColorRes(R.color.colorAccent)
                .setTitle("Recovery my account")
                .setMessage("Enter your email")
                .setIcon(R.drawable.ic_email)
                .setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                .setInputFilter("Email not found", new LovelyTextInputDialog.TextFilter() {
                    @Override
                    public boolean check(String text) {
                        Pattern VALID_EMAIL_ADDRESS_REGEX =
                                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
                        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(text);
                        return matcher.find();
                    }
                })
                .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String email) {
                        //Quen mat khau
                        presenter.onRecoveryClick(email);
                    }
                })
                .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }
}
