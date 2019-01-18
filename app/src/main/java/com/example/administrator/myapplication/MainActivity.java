package com.example.administrator.myapplication;

import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    /*seguito tutorial https://www.youtube.com/watch?v=bNpWGI_hGGg*/
    private static final String TAG = "MainActivity";

    private ViewPager viewpager;

    /*private SectionsPageAdapter mSectionsPageAdapter; tutorial instanziava un sectionspageadapter vuoto, non so perchè*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate start");

        /*mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());*/

        /**/
        viewpager = (ViewPager) findViewById(R.id.container);
        setupViewPager(viewpager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewpager);
        }

    private void setupViewPager(ViewPager viewpager){/*funzione ausiliaria per creare viewpager con 3 tab da passare al tab layout*/
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1Fragment(), "tab1");
        adapter.addFragment(new Tab2Fragment(), "tab2");
        adapter.addFragment(new Tab3Fragment(), "tab3");
        viewpager.setAdapter(adapter);
    }




}
