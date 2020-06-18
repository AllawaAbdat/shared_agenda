package com.example.monagendapartage.Fragments.MyEventsFragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.monagendapartage.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class EventsMessagerieFragment extends Fragment {

    /** Constructeur vide */
    public EventsMessagerieFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_messagerie, container, false);


        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager viewPager = view.findViewById(R.id.view_pager);

        EventsMessagerieFragment.ViewPagerAdapter viewPagerAdapter = new EventsMessagerieFragment.ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new EventsRequestSendedFragment(), "Envoyées");
        viewPagerAdapter.addFragment(new EventsRequestReceivedFragment(), "Reçus");
        viewPagerAdapter.addFragment(new ChatsFragment(), "Conversations");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

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
