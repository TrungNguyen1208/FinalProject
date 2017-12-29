package ptit.nttrung.finalproject.ui.add_group;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.Presenter;
import ptit.nttrung.finalproject.data.local.FriendDB;
import ptit.nttrung.finalproject.model.entity.ListFriend;
import ptit.nttrung.finalproject.model.entity.Room;

/**
 * Created by TrungNguyen on 12/29/2017.
 */

public class AddGroupPresenter extends Presenter<AddGroupView> {

    private Context context;

    public AddGroupPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void attachView(AddGroupView view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public ListFriend getListFriend() {
        return FriendDB.getInstance(context).getListFriend();
    }

    public void createGroup(final String idGroup, Room room) {
        FirebaseDatabase.getInstance().getReference().child("group/" + idGroup)
                .setValue(room)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        getView().addRoomForUser(idGroup, 0);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        getView().makeToastError("Có lỗi xảy ra!");
                    }
                });
    }

    public void editGroup(final String idGroup, Room room) {
        FirebaseDatabase.getInstance().getReference().child("group/" + idGroup).setValue(room)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        getView().addRoomForUser(idGroup, 0);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        getView().hideDialogWait();
                        new LovelyInfoDialog(context) {
                            @Override
                            public LovelyInfoDialog setConfirmButtonText(String text) {
                                findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dismiss();
                                    }
                                });
                                return super.setConfirmButtonText(text);
                            }
                        }
                                .setTopColorRes(R.color.colorAccent)
                                .setIcon(R.drawable.ic_add_group_dialog)
                                .setTitle("False")
                                .setMessage("Cannot connect database")
                                .setCancelable(false)
                                .setConfirmButtonText("Ok")
                                .show();
                    }
                });
    }
}
