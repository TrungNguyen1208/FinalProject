package ptit.nttrung.finalproject.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.BaseDrawerActivity;
import ptit.nttrung.finalproject.ui.add_restaurant.AddRestaurantActivity;
import ptit.nttrung.finalproject.ui.change_profile.ProfileActivity;
import ptit.nttrung.finalproject.ui.friend.FriendActivity;
import ptit.nttrung.finalproject.ui.friend.ViewPagerAdapter;
import ptit.nttrung.finalproject.ui.main.nearby.NearbyFragment;
import ptit.nttrung.finalproject.ui.main.newfeed.NewfeedFragment;
import ptit.nttrung.finalproject.ui.setting.SettingActivity;
import ptit.nttrung.finalproject.util.helper.ActivityUtils;

public class MainActivity extends BaseDrawerActivity implements RadioGroup.OnCheckedChangeListener, NewfeedFragment.OnPostSelectedListener, MainView {

    private static final String TAG = MainActivity.class.getName();

    @BindView(R.id.view_pager_newfeed)
    ViewPager viewPager;
    @BindView(R.id.rb_places)
    AppCompatRadioButton rbPlaces;
    @BindView(R.id.rb_food)
    AppCompatRadioButton rbFood;
    @BindView(R.id.rg_toolbar_newfeed)
    RadioGroup rgToolbarNewfeed;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.iv_insert_restaurant)
    ImageView ivInsertRestaurent;

    private MainPresenter presenter = new MainPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        presenter.attachView(this);
        setupViewPager(viewPager);
        rgToolbarNewfeed.setOnCheckedChangeListener(this);
        ivInsertRestaurent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddRestaurantActivity.class));
            }
        });
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();
        getToolbar().setNavigationIcon(R.drawable.ic_menu);
        getSupportActionBar().setTitle("");
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new NewfeedFragment());
        adapter.addFrag(new NearbyFragment());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        rbPlaces.setChecked(true);
                        break;
                    case 1:
                        rbFood.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rb_places:
                viewPager.setCurrentItem(0);
                break;
            case R.id.rb_food:
                viewPager.setCurrentItem(1);
                break;
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
                    case R.id.menu_setting:
                        startActivity(new Intent(MainActivity.this, SettingActivity.class));
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

    @Override
    public void onPostComment(final String postKey) {

    }

    @Override
    public void onPostLike(final String postKey) {
        presenter.updateLike(postKey);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }
}
