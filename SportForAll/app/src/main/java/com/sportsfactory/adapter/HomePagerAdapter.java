package com.sportsfactory.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sportsfactory.fragment.HomeFragment;

public class HomePagerAdapter extends FragmentPagerAdapter {

    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return HomeFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "나의 관심종목";
            case 1:
                return "베스트 동호회";
            case 2:
                return "신생 동호회";
            case 3:
                return "우리동네 체육시설";
            case 4:
                return "운동생활 소식";
        }
        return null;
    }
}
