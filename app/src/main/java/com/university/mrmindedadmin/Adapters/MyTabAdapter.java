package com.university.mrmindedadmin.Adapters;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.university.mrmindedadmin.Fragments.PdfFragment;
import com.university.mrmindedadmin.Fragments.VideoFragment;

public class MyTabAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;
    private Bundle bundle;

    public MyTabAdapter(Context c, FragmentManager fm, int totalTabs, Bundle bundle) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
        this.bundle = bundle;
    }


    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position) {
            case 0:
                VideoFragment videoFragment = new VideoFragment();
                videoFragment.setArguments(bundle);
                return videoFragment;
            case 1:
                PdfFragment pdfFragment = new PdfFragment();
                pdfFragment.setArguments(bundle);
                return pdfFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}