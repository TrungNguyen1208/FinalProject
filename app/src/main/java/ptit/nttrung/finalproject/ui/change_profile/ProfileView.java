package ptit.nttrung.finalproject.ui.change_profile;

import android.app.Activity;

import java.util.List;

import ptit.nttrung.finalproject.base.BaseView;
import ptit.nttrung.finalproject.model.entity.Configuration;
import ptit.nttrung.finalproject.model.entity.User;

/**
 * Created by TrungNguyen on 12/25/2017.
 */

public interface ProfileView extends BaseView {
    void setAdapterData(List<Configuration> configurations);

    Activity getActivityContext();

    void setTextName(String name);

    void showRenameDialog(User myAccount);

    void showOkCofimDialog();

    void showCofimDialogResetPass();

    void showConfimDialogErrorSent();

    void notifyDataSetChanged(List<Configuration> configList);
}
