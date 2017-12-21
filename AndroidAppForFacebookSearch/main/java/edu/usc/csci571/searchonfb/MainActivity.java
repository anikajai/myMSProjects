package edu.usc.csci571.searchonfb;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuInflater;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();
    public static FavoriteFragment favFragment;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolBar;
    private FragmentTransaction fragmentTransaction;
    private NavigationView navigationView;
    private SharedPreferences sharedPreferences;
    private String viewSelected = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolBar = (Toolbar) findViewById(R.id.nav_action);
        setSupportActionBar(mToolBar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolBar, R.string.open, R.string.close);
        initialiseSharedPreferences();
        //openFragment();

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        getSupportActionBar().setTitle("Search on FB");
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.id_home:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new HomeFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Search on FB");
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        viewSelected = "home";
                        break;
                    case R.id.id_favourites:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        favFragment =  new FavoriteFragment();
                        fragmentTransaction.replace(R.id.main_container,favFragment);
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Favorites");
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        viewSelected = "favorites";
                        break;
                    case R.id.id_about_me:
                        Intent intent = new Intent(MainActivity.this, AboutMeActivity.class);
                        startActivity(intent);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.action_settings_main:
                        break;
                    default: break;
                }
                return true;
            }
        });
        openFragment();
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private void openFragment() {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, new HomeFragment());
        fragmentTransaction.commit();
        getSupportActionBar().setTitle("Search on FB");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (viewSelected != "favorites") {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.dummy_menu, menu);
        }
        return true;
    }

    public void initialiseSharedPreferences() {
        sharedPreferences = this.getSharedPreferences("favSave", MODE_PRIVATE);
        //Map<String, Map<String,Info>> userMap = new HashMap<String, Map<String, Info>>();
        Map<String, Info> userIdMap = new HashMap<String, Info>();
        //Map<String, Map<String,Info>> pageMap = new HashMap<String, Map<String, Info>>();
        Map<String, Info> pageIdMap = new HashMap<String, Info>();
        //Map<String, Map<String,Info>> eventMap = new HashMap<String, Map<String, Info>>();
        Map<String, Info> eventIdMap = new HashMap<String, Info>();
        //Map<String, Map<String,Info>> placeMap = new HashMap<String, Map<String, Info>>();
        Map<String, Info> placeIdMap = new HashMap<String, Info>();
        //Map<String, Map<String,Info>> groupMap = new HashMap<String, Map<String, Info>>();
        Map<String, Info> groupIdMap = new HashMap<String, Info>();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String userJson = gson.toJson(userIdMap);
        editor.putString("user", userJson);
        String pageJson = gson.toJson(pageIdMap);
        editor.putString("page", pageJson);
        String eventJson = gson.toJson(eventIdMap);
        editor.putString("event", eventJson);
        String placeJson = gson.toJson(eventIdMap);
        editor.putString("place", placeJson);
        String groupJson = gson.toJson(placeIdMap);
        editor.putString("group", groupJson);
        editor.commit();
    }
}
