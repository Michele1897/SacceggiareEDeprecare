package com.example.administrator.myapplication;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


/*classe a parte per esser riutilizzata in fragment con tab*/
public class SectionsPageAdapter extends FragmentPagerAdapter {

    /*lista dei frammenti*/
    private final List<Fragment> mFragmentsList = new ArrayList<>();
    /*lista titoli frammenti*/
    private final List<String> mFragmentsTitlesList = new ArrayList<>();
    private int fragCount;
    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
        fragCount =0;
    }

    public void addFragment(Fragment fragment, String title){
        mFragmentsList.add(fragment);
        mFragmentsTitlesList.add(title);
        fragCount=fragCount+1;
    }/*aggiunge frammento in liste*/
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentsTitlesList.get(position);
    }/*ritorna titolo frammento position */

    @Override
    public Fragment getItem(int i) {
        return mFragmentsList.get(i);
    }/*ritorna i-esimo framento in lista*/

    @Override
    public int getCount() {
        return fragCount;
    }/*ritorna numero frammenti in lista*/
}
