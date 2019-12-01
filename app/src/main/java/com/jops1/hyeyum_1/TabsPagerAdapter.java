package com.jops1.hyeyum_1;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return new com.jops1.hyeyum_1.HY_SS_FirstFragment();
            case 1:
                return new com.jops1.hyeyum_1.HY_SS_SecondFragment();
            case 2:
                return new com.jops1.hyeyum_1.HY_SS_ThirdFragment();
            case 3:
                return new com.jops1.hyeyum_1.HY_SS_FourthFragment();
            case 4:
                return new com.jops1.hyeyum_1.HY_SS_FifthFragment();
            case 5:
                return new com.jops1.hyeyum_1.HY_SS_SixthFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 6;
    }
}
