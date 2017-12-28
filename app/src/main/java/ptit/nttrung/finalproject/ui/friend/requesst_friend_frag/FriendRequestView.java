package ptit.nttrung.finalproject.ui.friend.requesst_friend_frag;

import com.google.firebase.database.DataSnapshot;

import ptit.nttrung.finalproject.base.BaseView;

public interface FriendRequestView extends BaseView{

    void onAddRequestSuccess(DataSnapshot dataSnapshot);

    void onDeniedRequestSuccess(DataSnapshot dataSnapshot);
}
