package com.pine.pmedia.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.pine.pmedia.R;
import com.pine.pmedia.fragments.ContactsFragment;
import com.pine.pmedia.fragments.HomeFragment;

import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }
    @Override
    public int getCount() {
        return mFragmentList.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

}
