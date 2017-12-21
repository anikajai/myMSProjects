package edu.usc.csci571.searchonfb;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static int selectedTabId;

    public FavoriteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.fav_result_viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.fav_result_tablayout);
        selectedTabId = 0;
        setViewPager();
        tabLayout.setupWithViewPager(viewPager);
        setupTabLayout();
        return view;
    }
    private void setupTabLayout() {
        tabLayout.getTabAt(0).setIcon(R.mipmap.users_b);
        tabLayout.getTabAt(1).setIcon(R.mipmap.pages_b);
        tabLayout.getTabAt(2).setIcon(R.mipmap.events_b);
        tabLayout.getTabAt(3).setIcon(R.mipmap.places_b);
        tabLayout.getTabAt(4).setIcon(R.mipmap.groups_b);
    }

    private void setViewPager() {
        FavoriteFragmentAdapter adapter = new FavoriteFragmentAdapter(getChildFragmentManager());
        Fragment userFragment = new UserTabFragment();
        Fragment pageFragment = new PageTabFragment();
        Fragment eventFragment = new EventTabFragment();
        Fragment placeFragment = new PlaceTabFragment();
        Fragment groupFragment = new GroupTabFragment();
        adapter.addFragment(userFragment, "Users");
        adapter.addFragment(pageFragment, "Pages");
        adapter.addFragment(eventFragment,"Events");
        adapter.addFragment(placeFragment, "Places");
        adapter.addFragment(groupFragment, "Groups");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        setViewPager();
        tabLayout.setupWithViewPager(viewPager);
        setupTabLayout();
        TabLayout.Tab tab = tabLayout.getTabAt(selectedTabId);
        tab.select();
    }

    private class FavoriteFragmentAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> fragmentsList = new ArrayList<>();
        private final List<String> fragmentsTitleList = new ArrayList<>();

        FavoriteFragmentAdapter(FragmentManager manager) {
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
