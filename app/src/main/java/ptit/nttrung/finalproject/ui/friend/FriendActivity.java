package ptit.nttrung.finalproject.ui.friend;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.BaseDrawerActivity;
import ptit.nttrung.finalproject.ui.change_profile.ProfileActivity;
import ptit.nttrung.finalproject.ui.friend.friend_frag.FriendsFragment;
import ptit.nttrung.finalproject.ui.friend.group_frag.GroupFragment;
import ptit.nttrung.finalproject.ui.friend.requesst_friend_frag.RequestFriendFragment;
import ptit.nttrung.finalproject.ui.main.MainActivity;
import ptit.nttrung.finalproject.util.helper.ActivityUtils;

public class FriendActivity extends BaseDrawerActivity {

    private static final String TAG = FriendActivity.class.getName();

    public static String STR_FRIEND_FRAGMENT = "FRIEND";
    public static String STR_GROUP_FRAGMENT = "GROUP";
    public static String STR_FRIEND_REQUEST_FRAGMENT = "FRIEND_RQ";

    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private ViewPagerAdapter mAdapter;
    private FirebaseUser user;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        initTab();
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
                        startActivity(new Intent(FriendActivity.this, MainActivity.class));
                        break;
                    case R.id.menu_chat_friend:
                        drawerLayout.closeDrawer(Gravity.LEFT, true);
                        break;
                    case R.id.menu_change_profile:
                        startActivity(new Intent(FriendActivity.this, ProfileActivity.class));
                        break;
                    case R.id.menu_signout:
                        ActivityUtils.signOutConfirmation(FriendActivity.this);
                        break;
                    case R.id.feed_back:
                        ActivityUtils.sendFeedBack(FriendActivity.this);
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
        getSupportActionBar().setTitle("Bạn bè");
    }

    private void initTab() {
        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorIndivateTab));
        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);
        setupTabIcons();
    }

    private void setupViewPager(ViewPager viewPager) {
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mAdapter.addFrag(new FriendsFragment(), STR_FRIEND_FRAGMENT);
        mAdapter.addFrag(new GroupFragment(), STR_GROUP_FRAGMENT);
        mAdapter.addFrag(new RequestFriendFragment(), STR_FRIEND_REQUEST_FRAGMENT);

        fab.setOnClickListener(((FriendsFragment) mAdapter.getItem(0)).onClickFloatButton.getInstance(this));
        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(3);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // Change floatButton
                if (mAdapter.getItem(position) instanceof FriendsFragment) {
                    fab.setVisibility(View.VISIBLE);
                    fab.setOnClickListener(((FriendsFragment) mAdapter.getItem(position))
                            .onClickFloatButton.getInstance(FriendActivity.this));
                    fab.setImageResource(R.drawable.ic_float_button);

                } else if (mAdapter.getItem(position) instanceof GroupFragment) {
                    fab.setVisibility(View.VISIBLE);
                    fab.setOnClickListener(((GroupFragment) mAdapter.getItem(position))
                            .onClickFloatButton.getInstance(FriendActivity.this));
                    fab.setImageResource(R.drawable.ic_float_add_group);
                } else {
                    fab.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupTabIcons() {
        int[] tabIcons = {
                R.drawable.ic_tab_person,
                R.drawable.ic_tab_group,
                R.drawable.ic_tab_infor
        };

        mTabLayout.getTabAt(0).setIcon(tabIcons[0]);
        mTabLayout.getTabAt(1).setIcon(tabIcons[1]);
        mTabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }
}
