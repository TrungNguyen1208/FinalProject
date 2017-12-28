package ptit.nttrung.finalproject.ui.friend.friend_frag;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ptit.nttrung.finalproject.base.Presenter;
import ptit.nttrung.finalproject.data.firebase.FirebaseUtil;
import ptit.nttrung.finalproject.data.local.StaticConfig;

public class FriendsPresenter extends Presenter<FriendsView> {
    private Context context;

    public FriendsPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void attachView(FriendsView view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    /**
     * Lay danh sach ban be tren server
     */
    public void getListFriendUId() {
        getView().showProgressDialog("Lấy danh sách bạn bè");
        FirebaseDatabase.getInstance().getReference()
                .child("friend/" + StaticConfig.UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getView().showListFriendUId(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getView().hideProgressDialog();
            }
        });
    }

    public void getFriendInfo(final int index, final String id) {
        FirebaseUtil.getUserRef().child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    getView().onGetInfoSuccess(dataSnapshot, id);
                }
                getView().showAllFriendInfo(index + 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getView().hideProgressDialog();
            }
        });
    }

    public void addFriend() {
    }
}
