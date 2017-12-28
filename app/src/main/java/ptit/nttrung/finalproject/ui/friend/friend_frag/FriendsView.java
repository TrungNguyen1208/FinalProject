package ptit.nttrung.finalproject.ui.friend.friend_frag;

import com.google.firebase.database.DataSnapshot;

import ptit.nttrung.finalproject.base.BaseView;

public interface FriendsView extends BaseView {
    void showListFriendUId(DataSnapshot dataSnapshot);

    void onGetInfoSuccess(DataSnapshot dataSnapshot, String id);

    void showAllFriendInfo(int index);
}
