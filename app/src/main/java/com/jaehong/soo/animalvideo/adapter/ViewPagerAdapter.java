package com.jaehong.soo.animalvideo.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jaehong.soo.animalvideo.fragment.CarnivoresFragment;
import com.jaehong.soo.animalvideo.fragment.HerbivoresFragment;
import com.jaehong.soo.animalvideo.fragment.OmnivoreFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        if (position == 0)
            return new CarnivoresFragment();

        if(position == 1)
            return new HerbivoresFragment();

        if(position == 2)
            return new OmnivoreFragment();

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0)
            return "육식";
        if (position == 1)
            return "초식";
        if (position == 2)
            return "잡식";
        return super.getPageTitle(position);
    }
}
