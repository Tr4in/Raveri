package de.technopaki.aleks.raveri;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by aleks on 11.07.17.
 */

public class TabsAdapter extends FragmentPagerAdapter {

    public TabsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return new TaskActivity();
            case 1:
                return new QuestActivity();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
