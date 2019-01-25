package com.example.michele.bozze.StrutturaApp;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.example.michele.bozze.R;
import com.example.michele.bozze.Sezioni.ControlsFragment;
import com.example.michele.bozze.Sezioni.SettingsFragment;
import com.example.michele.bozze.Sezioni.StatisticsFragment;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewpager;
    private SectionsPageAdapter mSectionsPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        viewpager = findViewById(R.id.container);
        setupViewPager(viewpager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewpager);
    }

    private void setupViewPager(ViewPager viewpager){
        mSectionsPageAdapter.addFragment(new SettingsFragment(), getString(R.string.tab_text_1));
        mSectionsPageAdapter.addFragment(new ControlsFragment(), getString(R.string.tab_text_2));
        mSectionsPageAdapter.addFragment(new StatisticsFragment(), getString(R.string.tab_text_3));
        viewpager.setAdapter(mSectionsPageAdapter);
    }

}
