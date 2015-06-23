package com.gpf.app.arduinorc.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gpf.app.arduinorc.MainActivity;
import com.gpf.app.arduinorc.R;
import com.gpf.app.arduinorc.adapters.NavigationAdapter;
import com.gpf.app.arduinorc.utils.Constants;


public class NavigationFragment extends Fragment implements NavigationAdapter.ClickListener {

    private RecyclerView recyclerView;
    private NavigationAdapter adapter;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Fragment currentFragment;

    public static NavigationFragment newInstance() {
        return new NavigationFragment();
    }

    public NavigationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.navigation_list);
        adapter = new NavigationAdapter(getActivity(), Constants.getNavigationRows());
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void itemClick(View view, int position) {
        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        Fragment nextFragment = null;
        String fragmentName = "";
        switch (position){
            case 0:
                nextFragment = BluetoothFragment.newInstance(((MainActivity) getActivity()).getBluetoothState());
                fragmentName = getString(R.string.bt_fragment);
                break;
            case 1:
                nextFragment = ControllerFragment.newInstance(null, null);
                fragmentName = getString(R.string.controller_fragment);
                break;
        }
        if(nextFragment != null){
            currentFragment = nextFragment;
            ft.replace(R.id.fragmentContainer, nextFragment, fragmentName).commit();
            mDrawerLayout.closeDrawers();
            //((MainActivity)getActivity()).setToolBarTitle(fragmentName);
        }
        //markNavigationRow(view, position);
    }

    public void setUp(DrawerLayout drawerLayout, final Toolbar toolbar){
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if(slideOffset<0.6) {
                    toolbar.setAlpha(1 - slideOffset);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }
}
