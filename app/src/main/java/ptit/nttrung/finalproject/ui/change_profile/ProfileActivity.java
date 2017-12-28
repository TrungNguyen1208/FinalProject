package ptit.nttrung.finalproject.ui.change_profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;

import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.BaseDrawerActivity;
import ptit.nttrung.finalproject.ui.friend.FriendActivity;
import ptit.nttrung.finalproject.ui.main.MainActivity;
import ptit.nttrung.finalproject.util.helper.ActivityUtils;


public class ProfileActivity extends BaseDrawerActivity {
    private static String TAG = ProfileActivity.class.getName();

    private static final String PROFILE_FRAGMENT = "PROFILE_FRAGMENT";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ProfileFragment profileFragment = (ProfileFragment) this.getSupportFragmentManager()
                .findFragmentByTag(PROFILE_FRAGMENT);

        if (profileFragment == null) {
            profileFragment = ProfileFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    profileFragment, R.id.contentFrame, PROFILE_FRAGMENT);
        }
    }

    @Override
    public void setNavigationItemSelected() {
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView vNavigation = (NavigationView) findViewById(R.id.vNavigation);

        vNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_feed:
                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                        break;
                    case R.id.menu_chat_friend:
                        startActivity(new Intent(ProfileActivity.this, FriendActivity.class));
                        break;
                    case R.id.menu_change_profile:
                        drawerLayout.closeDrawer(Gravity.LEFT, true);
                        break;
                    case R.id.menu_signout:
                        ActivityUtils.signOutConfirmation(ProfileActivity.this);
                        break;
                    case R.id.feed_back:
                        ActivityUtils.sendFeedBack(ProfileActivity.this);
                        break;
                    default:
                        break;
                }

                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                return true;
            }
        });
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();
        getToolbar().setNavigationIcon(R.drawable.ic_menu);
        getSupportActionBar().setTitle("Thông tin cá nhân");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}