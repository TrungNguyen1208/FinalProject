package ptit.nttrung.finalproject.ui.chat;

import com.google.firebase.database.DataSnapshot;

import ptit.nttrung.finalproject.base.BaseView;

/**
 * Created by TrungNguyen on 12/29/2017.
 */

public interface ChatView extends BaseView {
    void showMessage(DataSnapshot dataSnapshot);
}
