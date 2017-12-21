package edu.usc.csci571.searchonfb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;

import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    boolean isFavorite;
    private String type;
    private String id;
    private String name;
    private String url;
    private Menu favMenu;
    private ShareDialog sharedialog = new ShareDialog(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setBundleInfo();
        toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        viewPager = (ViewPager) findViewById(R.id.details_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.details_tablayout);
        setViewPager();
        tabLayout.setupWithViewPager(viewPager);
        setupTabLayout();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Constants.DETAILS_TITLE);
        fbSdkInit();
    }

    private void setBundleInfo() {
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        name = bundle.getString("postName");
        url = bundle.getString("imgUrl");
        isFavorite = bundle.getBoolean("isfavorite");
        type = bundle.getString("type");
    }

    private void fbSdkInit() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        CallbackManager callbackManager = CallbackManager.Factory.create();
        sharedialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(getApplicationContext(),"FB share", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),"Sharing Cancelled!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void setupTabLayout() {
        tabLayout.getTabAt(0).setIcon(R.mipmap.albums_b);
        tabLayout.getTabAt(1).setIcon(R.mipmap.posts_b);
    }

    private void setViewPager() {
        DetailsAdapter adapter = new DetailsAdapter(getSupportFragmentManager());
        Fragment albumFragment = new AlbumFragment();
        Fragment postFragment = new PostFragment();

        Bundle albumArgs = new Bundle();
        albumArgs.putString("id", id);
        albumFragment.setArguments(albumArgs);

        Bundle postArgs = new Bundle();
        postArgs.putString("id", id);
        postArgs.putString("postName", name);
        postArgs.putString("imgUrl", url);
        postFragment.setArguments(postArgs);
        adapter.addFragment(albumFragment, "Albums");
        adapter.addFragment(postFragment, "Posts");
        viewPager.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fav_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.favMenu = menu;
        MenuItem addToFav = menu.findItem(R.id.id_add);
        MenuItem removeFromFav = menu.findItem(R.id.id_remove);
        if (isFavorite) {
            removeFromFav.setVisible(true);
            addToFav.setVisible(false);
        } else {
            removeFromFav.setVisible(false);
            addToFav.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.id_add:
                addToFavorites();
                return true;
            case R.id.id_remove:
                removeFromFavorites();
                return true;
            case R.id.id_share:
                shareOnFB();
                return super.onOptionsItemSelected(item);
            default:
                return false;
        }
    }

    private void addToFavorites() {
        MenuItem addMenu = favMenu.findItem(R.id.id_add);
        MenuItem removeMenu = favMenu.findItem(R.id.id_remove);
        removeMenu.setVisible(true);
        addMenu.setVisible(false);
        Toast.makeText(this, Constants.ADDED_TO_FAV, Toast.LENGTH_SHORT).show();
        Info info = new Info();
        info.id = id;
        info.url = url;
        info.name = name;
        Map<String, Info> dataMap = getMap();
        dataMap.put(id, info);
        commitPreferences(dataMap);
        isFavorite = true;
        onPrepareOptionsMenu(favMenu);
    }

    private void removeFromFavorites() {
        MenuItem addMenu = favMenu.findItem(R.id.id_add);
        MenuItem removeMenu = favMenu.findItem(R.id.id_remove);
        removeMenu.setVisible(false);
        addMenu.setVisible(true);
        Toast.makeText(this, Constants.REMOVED_FROM_FAV, Toast.LENGTH_SHORT).show();
        Map<String, Info> dataMap = getMap();
        dataMap.remove(id);
        commitPreferences(dataMap);
        isFavorite = false;
        onPrepareOptionsMenu(favMenu);
    }

    private void commitPreferences(Map<String, Info> dataMap) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("favSave", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(dataMap);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(type, json);
        editor.commit();
    }


    private  Map<String, Info> getMap() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("favSave", MODE_PRIVATE);
        String theType = sharedPreferences.getString(type, "");
        Type _class = new TypeToken<Map<String, Info>>() {}.getType();
        Gson gson = new Gson();
        Map<String, Info> map = gson.fromJson(theType, _class);
        return map;
    }

    public Intent getIntent(Context context, List<Info> infoList, int position, boolean isFav) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra("id",id);
        intent.putExtra("postName", infoList.get(position).name);
        intent.putExtra("imgUrl", infoList.get(position).url);
        intent.putExtra("type", "group");
        intent.putExtra("isfavorite", isFav);
        return intent;
    }
    private void shareOnFB() {
        Toast.makeText(this, "Sharing "+ name + "!!", Toast.LENGTH_SHORT).show();
        if(ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent fbDialog = new ShareLinkContent.Builder()
                    .setContentTitle(name)
                    .setContentUrl(Uri.parse(Constants.FB_COM))
                    .setImageUrl(Uri.parse(url))
                    .setContentDescription(Constants.SHARE_TEXT)
                    .build();
            sharedialog.show(fbDialog);}
    }

    class DetailsAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> listFragment = new ArrayList<>();
        private List<String> listFragmentTitle = new ArrayList<>();

        public DetailsAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return listFragment.get(position);
        }

        @Override
        public int getCount() {
            return listFragment.size();
        }

        void addFragment(Fragment fragment, String title) {
            listFragment.add(fragment);
            listFragmentTitle.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return listFragmentTitle.get(position);
        }


    }
}
