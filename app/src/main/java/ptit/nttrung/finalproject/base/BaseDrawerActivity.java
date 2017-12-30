package ptit.nttrung.finalproject.base;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.data.local.SharedPreferenceHelper;
import ptit.nttrung.finalproject.data.local.StaticConfig;
import ptit.nttrung.finalproject.model.entity.User;
import ptit.nttrung.finalproject.ui.login.LoginActivity;
import ptit.nttrung.finalproject.ui.user_detail.UserDetailActivity;
import ptit.nttrung.finalproject.util.helper.ImageUtils;


/**
 * Created by TrungNguyen on 10/10/2017.
 */

public abstract class BaseDrawerActivity extends BaseActivity {


    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.vNavigation)
    NavigationView vNavigation;

    @BindDimen(R.dimen.global_menu_avatar_size)
    int avatarSize;
    @BindString(R.string.user_profile_photo)
    String profilePhoto;

    private ImageView ivMenuUserProfilePhoto;
    private TextView ivMenuUserName;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentViewWithoutInject(R.layout.activity_drawer);

        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.flContentRoot);
        LayoutInflater.from(this).inflate(layoutResID, viewGroup, true);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    StaticConfig.UID = firebaseAuth.getCurrentUser().getUid();
                } else {
                    finish();
                    startActivity(LoginActivity.createIntent(BaseDrawerActivity.this));
                }
            }
        };

        bindViews();
        setupHeader();
        setNavigationItemSelected();
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();
        if (getToolbar() != null) {
            getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void setupHeader() {
        View headerView = vNavigation.getHeaderView(0);
        ivMenuUserProfilePhoto = (ImageView) headerView.findViewById(R.id.ivMenuUserProfilePhoto);
        ivMenuUserName = (TextView) headerView.findViewById(R.id.ivMenuUserName);
        headerView.findViewById(R.id.vGlobalMenuHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGlobalMenuHeaderClick(v);
            }
        });

        User userInfo = SharedPreferenceHelper.getInstance(this).getUserInfo();
        if (userInfo.name != null)
            ivMenuUserName.setText(userInfo.name);
        ImageUtils.loadAvata(BaseDrawerActivity.this, userInfo.avata, ivMenuUserProfilePhoto);

    }

    public void onGlobalMenuHeaderClick(final View v) {
        drawerLayout.closeDrawer(Gravity.LEFT);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent userDetailIntent = new Intent(getApplicationContext(), UserDetailActivity.class);
                userDetailIntent.putExtra(UserDetailActivity.USER_ID_EXTRA_NAME, mAuth.getCurrentUser().getUid());

                startActivity(userDetailIntent);
                overridePendingTransition(0, 0);
            }
        }, 200);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public abstract void setNavigationItemSelected();
}
