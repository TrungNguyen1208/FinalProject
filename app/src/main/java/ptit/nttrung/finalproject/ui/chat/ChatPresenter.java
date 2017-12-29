package ptit.nttrung.finalproject.ui.chat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import ptit.nttrung.finalproject.base.Presenter;
import ptit.nttrung.finalproject.model.entity.Message;

/**
 * Created by TrungNguyen on 12/29/2017.
 */

public class ChatPresenter extends Presenter<ChatView> {

    public ChatPresenter() {
    }

    @Override
    public void attachView(ChatView view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void getListMesseage(String roomId) {
        FirebaseDatabase.getInstance().getReference().child("message/" + roomId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    getView().showMessage(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendMessage(String roomId, Message newMessage) {
        FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().setValue(newMessage);
    }
}
