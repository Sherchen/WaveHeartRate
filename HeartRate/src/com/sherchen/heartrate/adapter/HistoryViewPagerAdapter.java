package com.sherchen.heartrate.adapter;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import java.util.List;

public class HistoryViewPagerAdapter extends EditablePagerAdapter {
        
        private android.view.ViewGroup.LayoutParams DefaultParams = new android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, 
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
        );



        public HistoryViewPagerAdapter(List<View> views) {
            super(views);
        }

		@Override
		public CharSequence getPageTitle(int position) {
            Log.v("RefreshWidget", "getPageTitle");
			return "";
		}

		public Object instantiateItem(View container, int position) {
			View view = getViewInPosition(position);
            Log.v("RefreshWidget", "instantiateItem--position:"+position+" view:"+view);
			if(view != null){
                ((ViewPager) container).addView(view,DefaultParams);
            }
			return view;
		}

	}