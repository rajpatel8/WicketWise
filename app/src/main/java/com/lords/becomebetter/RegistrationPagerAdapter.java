package com.lords.becomebetter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class RegistrationPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 2;

    public RegistrationPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new CoachRegistrationFragment();
            case 1:
                return new StudentRegistrationFragment();
            default:
                return new CoachRegistrationFragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}