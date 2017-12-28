package ptit.nttrung.finalproject.ui.change_profile;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.Presenter;
import ptit.nttrung.finalproject.data.firebase.FirebaseUtil;
import ptit.nttrung.finalproject.data.local.SharedPreferenceHelper;
import ptit.nttrung.finalproject.data.local.StaticConfig;
import ptit.nttrung.finalproject.model.entity.Configuration;
import ptit.nttrung.finalproject.model.entity.User;

public class ProfilePresenter extends Presenter<ProfileView> {

    private Context context;
    private User myAccount;
    private List<Configuration> listConfig = new ArrayList<>();

    private static final String USERNAME_LABEL = "Username";
    private static final String EMAIL_LABEL = "Email";
    private static final String SIGNOUT_LABEL = "Sign out";
    private static final String RESETPASS_LABEL = "Change Password";

    public ProfilePresenter(Context context, User myAccount) {
        this.context = context;
        this.myAccount = myAccount;
    }

    @Override
    public void attachView(ProfileView view) {
        super.attachView(view);
        getUserInfo(StaticConfig.UID);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void onUserNameLabelClick() {
        getView().showRenameDialog(SharedPreferenceHelper.getInstance(context).getUserInfo());
    }

    public void onResetPassLabelClick() {
        getView().showOkCofimDialog();
    }

    public void onConfimRenameClick(final User myAccount, final String newName) {
        FirebaseUtil.getUserRef().child(StaticConfig.UID).child("name")
                .setValue(newName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            myAccount.name = newName;
                            SharedPreferenceHelper.getInstance(context).saveUserInfo(myAccount);

                            getView().setTextName(newName);
                            listConfig = setupArrayListInfo(myAccount);
                            getView().notifyDataSetChanged(listConfig);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        getView().makeToastError("Có lỗi xảy ra!");
                    }
                });
    }

    public List<Configuration> setupArrayListInfo(User myAccount) {
        listConfig.clear();

        Configuration userNameConfig = new Configuration(USERNAME_LABEL,
                myAccount.name, R.drawable.ic_account_box);
        listConfig.add(userNameConfig);

        Configuration emailConfig = new Configuration(EMAIL_LABEL,
                myAccount.email, R.drawable.ic_email);
        listConfig.add(emailConfig);

        Configuration resetPass = new Configuration(RESETPASS_LABEL,
                "", R.drawable.ic_restore);
        listConfig.add(resetPass);

        Configuration sigout = new Configuration(SIGNOUT_LABEL,
                "", R.drawable.nav_logout);
        listConfig.add(sigout);

        return listConfig;
    }

    public void resetPassword(final User myAccount) {
        getView().showProgressDialog(context.getString(R.string.waiting));
        FirebaseAuth.getInstance().sendPasswordResetEmail(myAccount.email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                getView().makeToastSucces("Đã gửi đến cho  " + myAccount.email + ". Làm ơn kiểm tra!");
                getView().hideProgressDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                getView().makeToastError("Lỗi khi gửi đến email " + myAccount.email + "!");
                getView().hideProgressDialog();
            }
        });
    }

    private void getUserInfo(final String uid) {
        getView().showProgressDialog("Đang xử lý");

        FirebaseUtil.getUserRef().child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getView().hideProgressDialog();
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    listConfig.clear();
                    myAccount = user;

                    listConfig = setupArrayListInfo(myAccount);
                    getView().notifyDataSetChanged(listConfig);
                    getView().setTextName(myAccount.name);

                    SharedPreferenceHelper.getInstance(context).saveUserInfo(myAccount);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getView().hideProgressDialog();
                getView().makeToastError(databaseError.getMessage());
            }
        });
    }
}
