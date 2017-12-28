package ptit.nttrung.finalproject.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;

import butterknife.ButterKnife;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.BaseDrawerActivity;
import ptit.nttrung.finalproject.ui.change_profile.ProfileActivity;
import ptit.nttrung.finalproject.ui.friend.FriendActivity;
import ptit.nttrung.finalproject.util.helper.ActivityUtils;

public class MainActivity extends BaseDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();
        getToolbar().setNavigationIcon(R.drawable.ic_menu);
        getSupportActionBar().setTitle("Trang chủ");
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
                        drawerLayout.closeDrawer(Gravity.LEFT, true);
                        break;
                    case R.id.menu_chat_friend:
                        startActivity(new Intent(MainActivity.this, FriendActivity.class));
                        break;
                    case R.id.menu_change_profile:
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        break;
                    case R.id.menu_signout:
                        ActivityUtils.signOutConfirmation(MainActivity.this);
                        break;
                    case R.id.feed_back:
                        ActivityUtils.sendFeedBack(MainActivity.this);
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
    public void onBackPressed() {
        ActivityUtils.alert(this, true, "Thoát", "Bạn có muốn đóng ứng dụng",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MainActivity.this.finish();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }
}
