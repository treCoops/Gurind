package com.dtinnovation.gurind.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.dtinnovation.gurind.Monthly_Sales_Value;
import com.dtinnovation.gurind.Monthly_Square_Meter_Value_Ticknesswise;
import com.dtinnovation.gurind.Monthly_Square_Meter_Value_Ticknesswise_value;
import com.dtinnovation.gurind.R;
import com.dtinnovation.gurind.Monthly_Square_Meter_Value;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3, R.string.tab_text_4};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0 :
                return new Monthly_Sales_Value();

            case 1 :
                return new Monthly_Square_Meter_Value();

            case 2 :
                return new Monthly_Square_Meter_Value_Ticknesswise();

            case 3 :
                return new Monthly_Square_Meter_Value_Ticknesswise_value();

            default:
                return new Monthly_Sales_Value();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 4;
    }
}