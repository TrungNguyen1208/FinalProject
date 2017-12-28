package ptit.nttrung.finalproject.ui.login;

import ptit.nttrung.finalproject.base.BaseView;

public interface RegisterView extends BaseView{

    boolean checkValidate();

    String getEmail();

    String getPassword();

    String getPasswordConfirm();

    void startLoginActivity();
}
