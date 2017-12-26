package ptit.nttrung.finalproject.ui.login;

import ptit.nttrung.finalproject.base.BaseView;

/**
 * Created by TrungNguyen on 12/16/2017.
 */

public interface RegisterView extends BaseView{

    boolean checkValidate();

    String getEmail();

    String getPassword();

    String getPasswordConfirm();

    void startLoginActivity();
}
