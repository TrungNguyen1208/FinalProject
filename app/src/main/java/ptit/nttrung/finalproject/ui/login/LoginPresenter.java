package ptit.nttrung.finalproject.ui.login;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.Presenter;
import ptit.nttrung.finalproject.data.local.SharedPreferenceHelper;
import ptit.nttrung.finalproject.data.local.StaticConfig;
import ptit.nttrung.finalproject.model.entity.User;

/**
 * Created by TrungNguyen on 12/16/2017.
 */

public class LoginPresenter extends Presenter<LoginView> {

    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener listener;

    public LoginPresenter(Context context) {
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void attachView(LoginView view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void onLogInClick() {
        if (!getView().checkValidate()) {
            getView().makeToastError("Vui lòng nhập lại!");
            return;
        }

        getView().showProgressDialog("Đăng nhập...");
        mAuth.signInWithEmailAndPassword(getView().getEmail(), getView().getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        getView().hideProgressDialog();
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            StaticConfig.UID = user.getUid();
                            saveUserInfo();
                        } else {
                            getView().makeToastError("Sai email hoặc mật khẩu!");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        getView().hideProgressDialog();
                        getView().makeToastError("Có lỗi xảy ra!");
                    }
                });
    }

    public void onSignUpClick() {
        getView().startRegisterActivity();
    }

    public void onRecoveryClick(final String email) {
        getView().showProgressDialog(context.getString(R.string.waiting));
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                getView().makeToastSucces("Đã gửi đến cho  " + email + ". Làm ơn kiểm tra!");
                getView().hideProgressDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                getView().makeToastError("Lỗi khi gửi đến email " + email + "!");
                getView().hideProgressDialog();
            }
        });
    }

    /**
     * Luu thong tin user info cho nguoi dung dang nhap
     */
    private void saveUserInfo() {
        FirebaseDatabase.getInstance().getReference().child("user/" + StaticConfig.UID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap hashUser = (HashMap) dataSnapshot.getValue();
                        User userInfo = new User();
                        userInfo.uid = StaticConfig.UID;
                        userInfo.name = (String) hashUser.get("name");
                        userInfo.email = (String) hashUser.get("email");
                        userInfo.avata = (String) hashUser.get("avata");
                        SharedPreferenceHelper.getInstance(context).saveUserInfo(userInfo);
                        getView().startMainActivity();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
