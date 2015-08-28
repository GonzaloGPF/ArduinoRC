package com.gpf.app.arduinorc.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

import com.gpf.app.arduinorc.R;
import com.gpf.app.arduinorc.fragments.GamePadFragment;
import com.gpf.app.arduinorc.fragments.ProgrammerFragment;
import com.gpf.app.arduinorc.fragments.ReceiverFragment;

/**
 * Created by Zalo on 10/07/2015.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    CharSequence titles[];
    int icons[] = {R.drawable.ic_gamepad,
            R.drawable.ic_receiver,
            R.drawable.ic_program};
    Context context;

    public ViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        titles = context.getResources().getStringArray(R.array.tabs);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = GamePadFragment.newInstance();
                break;
            case 1:
                fragment = ReceiverFragment.newInstance();
                break;
            case 2:
                fragment = ProgrammerFragment.newInstance();
                break;
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable drawable = ContextCompat.getDrawable(context, icons[position]);
        drawable.setBounds(0, 0, 48, 48);
        ImageSpan imageSpan = new ImageSpan(drawable);
        SpannableString spannableString = new SpannableString(" ");
        spannableString.setSpan(imageSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    @Override
    public int getCount() {
        return icons.length;
    }
}
