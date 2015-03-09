package com.sherchen.heartrate.control.util;

import android.util.SparseArray;
import android.view.View;

/**
 * The description of use:The View holder for Adapter
 * <br />
 * Create Time:2014-1-20 05:12:31
 * @author Dave
 *	
 *
 */
public class ViewHolder {
    
    /**
     * Find child view by specified parameters
     * @param convertView the ListView item
     * @param id the resource id.
     * @return
     */
    public static View find(View convertView,int id){
        if(convertView == null) return null;
        SparseArray<View> viewHolder = (SparseArray<View>) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new SparseArray<View>();
            convertView.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if(childView == null){
            childView = convertView.findViewById(id);
            viewHolder.put(id, childView);
        }
        return childView;
    }
}
