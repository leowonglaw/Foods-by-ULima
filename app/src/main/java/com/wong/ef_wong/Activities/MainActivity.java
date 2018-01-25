package com.wong.ef_wong.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.wong.ef_wong.Fragments.PubMapFragment;
import com.wong.ef_wong.Fragments.PubListFragment;
import com.wong.ef_wong.R;


public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    private boolean isDrawerOpen = false;
    Toolbar toolbar;
    Fragment pubListFragment, pubMapFragment;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pubListFragment = new PubListFragment();
        loadFragment(pubListFragment);

        new Thread(){
            @Override
            public void run() {
                pubMapFragment = new PubMapFragment();
            }
        }.start();



        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("food's");

            actionBar.setHomeAsUpIndicator(R.drawable.ic_hamburger);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);


        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu1) {
                    loadFragment(pubListFragment);
                } else if (item.getItemId() == R.id.menu2) {
                    loadFragment(pubMapFragment);
                } else{

                }
                drawerLayout.closeDrawers();
                isDrawerOpen=false;
                return true;
            }
        });
        if (savedInstanceState == null) {
            navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    public NavigationView getNavigationView() {
        return navigationView;
    }

    public void loadFragment(Fragment fragment){
        FragmentManager manager = getFragmentManager();
        android.app.FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.idFragment,fragment);
        transaction.commit();
    }

    public Fragment getPubListFragment() {
        return pubListFragment;
    }

    public Fragment getPubMapFragment() {
        return pubMapFragment;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
                    if(!isDrawerOpen){
                        drawerLayout.openDrawer(GravityCompat.START);
                        isDrawerOpen=true;
                    }else{
                        drawerLayout.closeDrawers();
                        isDrawerOpen=false;
                    }
        }else if(item.getItemId() == R.id.menuPublicar){
            Intent intent = new Intent(MainActivity.this, PublicarActivity.class);
            if(getIntent().getExtras()!=null){
                intent.putExtras(getIntent().getExtras());
            }
            startActivity(intent);
        }else{
            Toast.makeText(this, "¡Eso no me lo esperaba!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(navigationView.getMenu().getItem(1).isChecked()){
            navigationView.getMenu().getItem(0).setChecked(true);
            loadFragment(pubListFragment);
        }else{
            super.onBackPressed();
        }
    }
}