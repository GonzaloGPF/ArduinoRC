package com.gpf.app.arduinorc.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gpf.app.arduinorc.R;
import com.gpf.app.arduinorc.adapters.NavigationAdapter;
import com.gpf.app.arduinorc.utils.Constants;


public class NavigationFragment extends Fragment implements NavigationAdapter.ClickListener {

    private static final String CURRENT_FRAGMENT_NAME = "current_fragment_name";
    private RecyclerView recyclerView;
    private NavigationAdapter adapter;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Fragment currentFragment;
    private String currentFragmentName;
    private NavigationListener navigationListener;

    public static NavigationFragment newInstance() {
        return new NavigationFragment();
    }

    public NavigationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            currentFragmentName = savedInstanceState.getString(CURRENT_FRAGMENT_NAME);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_FRAGMENT_NAME, currentFragmentName);
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
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment nextFragment = null;
        switch (position){
            case 0:
                nextFragment = BluetoothFragment.newInstance();
                currentFragmentName = getString(R.string.bluetooth_fragment);
                break;
            case 1:
                nextFragment = ControllerFragment.newInstance();
                currentFragmentName = getString(R.string.controller_fragment);
                break;
        }
        if(nextFragment != null){
            currentFragment = nextFragment;
            ft.replace(R.id.fragmentContainer, nextFragment, currentFragmentName).commit();
            mDrawerLayout.closeDrawers();
            navigationListener.onNavigationChanged();
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

    public String getCurrentFragmentName(){
        if(currentFragmentName!=null) {
            return currentFragmentName;
        }else{
            return getString(R.string.bluetooth_fragment);
        }
    }

    public interface NavigationListener{
        void onNavigationChanged();
    }

    public void setListener(NavigationListener nl){
        navigationListener = nl;
    }
}
