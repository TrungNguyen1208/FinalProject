package ptit.nttrung.finalproject.ui.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.BaseFragment;
import ptit.nttrung.finalproject.util.helper.ActivityUtils;

public class RegisterFragment extends BaseFragment implements RegisterView{

    @BindView(R.id.edit_email_register)
    EditText inputEmail;
    @BindView(R.id.edit_pass_register)
    EditText inputPassword;
    @BindView(R.id.edit_repass_register)
    EditText inputReEnterPassword;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.link_login)
    TextView linkLogin;
    Unbinder unbinder;

    private RegisterPresenter presenter;

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_register, container, false);
        unbinder = ButterKnife.bind(this, contentView);
        return contentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (presenter == null) {
            presenter = new RegisterPresenter(getContext());
        }
        presenter.attachView(this);
    }

    @Override
    public boolean checkValidate() {
        boolean valid = true;

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String reEnterPassword = inputReEnterPassword.getText().toString();


        if (!ActivityUtils.isEmailValid(email)) {
            inputEmail.setError("enter a valid email address");
            valid = false;
        } else {
            inputEmail.setError(null);
        }


        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            inputPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            inputPassword.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            inputReEnterPassword.setError("Password Do not match");
            valid = false;
        } else {
            inputReEnterPassword.setError(null);
        }

        return valid;
    }

    @Override
    public String getEmail() {
        return inputEmail.getText().toString();
    }

    @Override
    public String getPassword() {
        return inputPassword.getText().toString();
    }

    @Override
    public String getPasswordConfirm() {
        return inputReEnterPassword.getText().toString();
    }

    @Override
    public void startLoginActivity() {
        presenter.detachView();
        getActivity().getSupportFragmentManager().popBackStack();
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @OnClick({R.id.btn_register, R.id.link_login, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                presenter.onCreateAccountClick();
                break;
            case R.id.link_login:
                startLoginActivity();
                break;
            case R.id.iv_back:
                presenter.detachView();
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                break;
        }
    }
}
