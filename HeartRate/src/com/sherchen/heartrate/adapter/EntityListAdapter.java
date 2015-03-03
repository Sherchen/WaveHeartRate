package com.sherchen.heartrate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * The description of use:
 * <br />
 * Created time:2014/6/18 14:45
 * Created by Dave
 */
public abstract class EntityListAdapter<T> extends BaseAdapter {
    private List<T> m_List;
    private Context m_Context;
    private int m_LayoutResId;

    private LayoutInflater m_LayoutInflater;

    private int m_Count;

    protected EntityListAdapter(List<T> list, Context m_Context, int m_LayoutResId) {
        this.m_Context = m_Context;
        this.m_LayoutResId = m_LayoutResId;
        m_LayoutInflater = LayoutInflater.from(m_Context);
        setEntities(list);
    }

    public void setEntities(List<T> entities){
        m_List = entities;
        m_Count = m_List == null? 0 : m_List.size();
    }

    @Override
    public int getCount() {
        return m_Count;
    }

    @Override
    public T getItem(int position) {
        return m_List.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = m_LayoutInflater.inflate(m_LayoutResId, null);
        }
        bindView(convertView, getItem(position));
        return convertView;
    }

    protected abstract void bindView(View convertView, T entity);
}
