package ptit.nttrung.finalproject.ui.login;

import ptit.nttrung.finalproject.base.BaseView;

/**
 * Created by TrungNguyen on 12/16/2017.
 */

public interface LoginView extends BaseView {

    boolean checkValidate();

    String getEmail();

    String getPassword();

    void startMainActivity();

    void startRegisterActivity();

    void showInputDialogRecoEmail();
}
