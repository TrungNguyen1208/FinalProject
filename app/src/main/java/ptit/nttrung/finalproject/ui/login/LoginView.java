package ptit.nttrung.finalproject.ui.login;

import ptit.nttrung.finalproject.base.BaseView;

public interface LoginView extends BaseView {

    boolean checkValidate();

    String getEmail();

    String getPassword();

    void startMainActivity();

    void startRegisterActivity();

    void showInputDialogRecoEmail();
}
