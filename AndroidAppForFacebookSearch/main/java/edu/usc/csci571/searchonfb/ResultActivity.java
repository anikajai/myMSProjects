package edu.usc.csci571.searchonfb;

import android.os.Bundle;

import java.util.List;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    private String keyword;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        setToolBar();
        toolbar = (Toolbar) findViewById(R.id.result_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        viewPager = (ViewPager) findViewById(R.id.result_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.result_tablayout);
        Bundle bundle = getIntent().getExtras();
        keyword = bundle.getString("keyword");
        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);
        setupTabLayout();
    }

    private void setToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Constants.RESULT_TITLE);
    }

    private void setupTabLayout() {
        tabLayout.getTabAt(0).setIcon(R.mipmap.users_b);
        tabLayout.getTabAt(1).setIcon(R.mipmap.pages_b);
        tabLayout.getTabAt(2).setIcon(R.mipmap.events_b);
        tabLayout.getTabAt(3).setIcon(R.mipmap.places_b);
        tabLayout.getTabAt(4).setIcon(R.mipmap.groups_b);
    }

    private void setupViewPager() {
        ResultAdapter adapter = new ResultAdapter(getSupportFragmentManager());
        Fragment userFragment = new UserTabFragment();
        Fragment pageFragment = new PageTabFragment();
        Fragment eventFragment = new EventTabFragment();
        Fragment placeFragment = new PlaceTabFragment();
        Fragment groupFragment = new GroupTabFragment();

        Bundle bundleArguments = new Bundle();
        bundleArguments.putString("text", keyword);
        userFragment.setArguments(bundleArguments);
        pageFragment.setArguments(bundleArguments);
        eventFragment.setArguments(bundleArguments);
        placeFragment.setArguments(bundleArguments);
        groupFragment.setArguments(bundleArguments);

        adapter.addFragment(userFragment, "Users");
        adapter.addFragment(pageFragment, "Pages");
        adapter.addFragment(eventFragment, "Events");
        adapter.addFragment(placeFragment, "Places");
        adapter.addFragment(groupFragment, "Groups");
        viewPager.setAdapter(adapter);
    }

    private class ResultAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> fragmentsList = new ArrayList<>();
        private final List<String> fragmentsTitleList = new ArrayList<>();

        ResultAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentsList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentsList.size();
        }

        void addFragment(Fragment fragment, String title) {
            fragmentsList.add(fragment);
            fragmentsTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentsTitleList.get(position);
        }
    }


}