package ptit.nttrung.finalproject.ui.friend.friend_frag;

import com.google.firebase.database.DataSnapshot;

import ptit.nttrung.finalproject.base.BaseView;

/**
 * Created by TrungNguyen on 12/28/2017.
 */

public interface FriendsView extends BaseView {
    void showListFriendUId(DataSnapshot dataSnapshot);
}
