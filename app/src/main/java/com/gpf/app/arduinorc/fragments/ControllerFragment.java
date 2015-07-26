package com.gpf.app.arduinorc.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gpf.app.arduinorc.R;
import com.gpf.app.arduinorc.adapters.ViewPagerAdapter;
import com.gpf.app.arduinorc.tabs.SlidingTabLayout;

public class ControllerFragment extends Fragment {

    public static ControllerFragment newInstance() {
        return  new ControllerFragment();
    }

    public ControllerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_controller, container, false);
        ViewPager mPager = (ViewPager) view.findViewById(R.id.pager);
        mPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), getActivity()));
        SlidingTabLayout mTabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        mTabs.setCustomTabView(R.layout.tab_controller, R.id.tab_text);
        mTabs.setDistributeEvenly(true);
        mTabs.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        mTabs.setSelectedIndicatorColors(getResources().getColor(R.color.accentColor));
        // Setting Custom Color for the Scroll bar indicator of the Tab View

        mTabs.setViewPager(mPager);
        return view;
    }
}
