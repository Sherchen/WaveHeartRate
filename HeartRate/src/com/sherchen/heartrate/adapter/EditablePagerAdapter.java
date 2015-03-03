package com.sherchen.heartrate.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * The description of use:
 * <br />
 * Create Time:2014-1-24 01:51:42
 * @author Dave
 *
 *
 */
public abstract class EditablePagerAdapter extends PagerAdapter {

    protected List<View> mViews;

    /**
     *
     */
    public EditablePagerAdapter(List<View> views) {
        if(views == null){
            views = new ArrayList<View>();
        }
        mViews = views;
    }

    public View getViewInPosition(int i) {
        return mViews.get(i);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        Log.v("RefreshWidget", "isViewFromObject---isEqual:" + (arg0 == arg1));
        return arg0 == arg1;
    }

    public int getViewSize(){
        return mViews.size();
    }

    @Override
    public int getCount() {
        Log.v("RefreshWidget", "getCount");
        return getViewSize();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.v("RefreshWidget", "destroyItem--position:" + position);
        if(position >= getViewSize()) return;
        container.removeView(getViewInPosition(position));
    }


    /**
     * wusq WsqLauncher this must return POSITION_NONE, otherwise notifyDataSetChanged cannot update the view
     *
     * #http://stackoverflow.com/questions/7263291/viewpager-pageradapter-not-updating-the-view
     */
    @Override
    public int getItemPosition(Object object) {
        Log.v("RefreshWidget", "getItemPosition");
//			return POSITION_NONE;
        int index = mViews.indexOf (object);
        if (index == -1) {
            return POSITION_NONE;
        } else {
            return index;
        }
    }


    //-----------------------------------------------------------------------------
    // Add "view" to right end of "views".
    // Returns the position of the new view.
    // The app should call this to add pages; not used by ViewPager.
    public int addView (View v)
    {
        return addView (v, mViews.size());
    }

    public int addViewNoRefresh (View v)
    {
        return addViewNoRefresh(v, mViews.size());
    }

    //-----------------------------------------------------------------------------
    // Add "view" at "position" to "views".
    // Returns position of new view.
    // The app should call this to add pages; not used by ViewPager.
    public int addView (View v, int position)
    {
        mViews.add (position, v);
        notifyDataSetChanged();
        return position;
    }

    public int addViewNoRefresh (View v, int position)
    {
        mViews.add(position, v);
        return position;
    }


    //-----------------------------------------------------------------------------
    // Removes "view" from "views".
    // Retuns position of removed view.
    // The app should call this to remove pages; not used by ViewPager.
    public int removeView (ViewPager pager, View v)
    {
        int position = mViews.indexOf(v);
        removeView (pager, position);
        return position;
    }

    public int removeViewNoRefresh(View v){
        int position = mViews.indexOf(v);
        removeViewNoRefresh(position);
        return position;
    }

    //-----------------------------------------------------------------------------
    // Removes the "view" at "position" from "views".
    // Retuns position of removed view.
    // The app should call this to remove pages; not used by ViewPager.
    public View removeView (ViewPager pager, int position)
    {
        // ViewPager doesn't have a delete method; the closest is to set the adapter
        // again.  When doing so, it deletes all its views.  Then we can delete the view
        // from from the adapter and finally set the adapter to the pager again.  Note
        // that we set the adapter to null before removing the view from "views" - that's
        // because while ViewPager deletes all its views, it will call destroyItem which
        // will in turn cause a null pointer ref.
        pager.setAdapter (null);
        View view = mViews.remove (position);
        pager.setAdapter (this);
        return view;
    }

    public View removeViewNoRefresh (int position){
        return mViews.remove(position);
    }

    public List<View> getViews() {
        return mViews;
    }

    public void refresh(ViewPager pager){
        pager.setAdapter (null);
        pager.setAdapter (this);
    }
}
