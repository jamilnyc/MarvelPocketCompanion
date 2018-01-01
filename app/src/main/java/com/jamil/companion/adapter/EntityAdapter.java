package com.jamil.companion.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import com.jamil.companion.model.Entity;

import java.util.ArrayList;

/**
 * Base class for all entity adapters
 */

public abstract class EntityAdapter extends BaseAdapter{
    protected Context mContext;
    protected ArrayList<Entity> mEntities;

    public EntityAdapter(Context context, ArrayList<Entity> entities)
    {
        mContext = context;
        mEntities = entities;
    }

    @Override
    public int getCount() {
        return mEntities.size();
    }

    @Override
    public Object getItem(int position) {
        return mEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mEntities.get(position).getId();
    }
}
