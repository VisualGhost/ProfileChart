package com.profilechart.profile;

import android.content.Context;

import com.profilechart.R;

public class ColorFactoryImpl implements ColorFactory {

    private final int[] mColors;

    public ColorFactoryImpl(Context context) {
        mColors = context.getResources().getIntArray(R.array.profile_chart_colors);
    }

    @Override
    public int getColor(final int index) {
        return mColors[index];
    }
}
