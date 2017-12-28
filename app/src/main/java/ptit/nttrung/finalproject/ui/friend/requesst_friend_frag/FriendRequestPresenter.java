package ptit.nttrung.finalproject.ui.friend.requesst_friend_frag;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import ptit.nttrung.finalproject.base.Presenter;
import ptit.nttrung.finalproject.data.firebase.FirebaseUtil;
import ptit.nttrung.finalproject.data.local.StaticConfig;

public class FriendRequestPresenter extends Presenter<FriendRequestView> {

    public FriendRequestPresenter() {
    }

    @Override
    public void attachView(FriendRequestView view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void onAccecptFriendClick(String friendId) {
        FirebaseUtil.getRequestFriendRef()
                .child(StaticConfig.UID)
                .child(friendId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getView().makeToastSucces("Thêm bạn bè thành công");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        getView().makeToastError("Có lỗi xảy ra!");
                    }
                });
    }

    public void onDeniedRequestClick(String friendId) {
        FirebaseUtil.getRequestFriendRef()
                .child(StaticConfig.UID)
                .child(friendId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getView().makeToastSucces("Từ chối!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                getView().makeToastError("Có lỗi xảy ra!");
            }
        });
    }

    public void getAllFriendRequest() {
        FirebaseUtil.getRequestFriendRef().child(StaticConfig.UID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        getView().onAddRequestSuccess(dataSnapshot);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        getView().onDeniedRequestSuccess(dataSnapshot);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
