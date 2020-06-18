package com.example.monagendapartage.Activities.PresentationActivities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.ProgressBar;

import com.example.monagendapartage.Fragments.PresentationFragment.StepOneFragment;
import com.example.monagendapartage.Fragments.PresentationFragment.StepTwoFragment;
import com.example.monagendapartage.R;

import java.util.ArrayList;

public class PresentationActivity extends AppCompatActivity {

    /** Déclaration de variables */
    private ProgressBar simpleProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);

        /** Init */
        ViewPager viewPager = findViewById(R.id.view_pager);
        simpleProgressBar = findViewById(R.id.progressBar);
        simpleProgressBar.setMax(100);

        PresentationActivity.ViewPagerAdapter viewPagerAdapter = new PresentationActivity.ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new StepOneFragment(), "StepOne");
        viewPagerAdapter.addFragment(new StepTwoFragment(), "StepTwo");

        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    simpleProgressBar.setProgress(50);
                } else if (position == 1) {
                    simpleProgressBar.setProgress(100);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * ViewPagerAdapter. Permet de stocker les fragments de la vue
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }


        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}
