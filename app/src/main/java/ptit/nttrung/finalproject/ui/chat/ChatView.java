package ptit.nttrung.finalproject.ui.chat;

import com.google.firebase.database.DataSnapshot;

import ptit.nttrung.finalproject.base.BaseView;

public interface ChatView extends BaseView {
    void showMessage(DataSnapshot dataSnapshot);
}
