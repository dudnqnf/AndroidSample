package com.sportsfactory.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sportsfactory.fragment.ClubFragment;
import com.sportsfactory.sportforall.MainActivity;


public class ClubPagerAdapter extends FragmentPagerAdapter {

    public ClubPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return ClubFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "메인";
            case 1:
                return "게시판";
            case 2:
                return "사진영상";
            case 3:
                return "운영진";
        }
        return null;
    }
}