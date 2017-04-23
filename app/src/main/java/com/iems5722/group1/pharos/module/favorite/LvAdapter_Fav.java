package com.iems5722.group1.pharos.module.favorite;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iems5722.group1.pharos.R;

import java.util.List;

/**
 * Created by Sora on 16/2/17.
 */

public class LvAdapter_Fav extends BaseAdapter{
    private List<Entity_Fav> content;
    private LayoutInflater mInflater;
  //  private OnClickListener mListener;

    public LvAdapter_Fav(Context context, List<Entity_Fav> content) {
        this.content = content;
        this.mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return this.content.size();
    }

    public Object getItem(int position) {
        return this.content.get(position);
    }

    public long getItemId(int position) {
        return (long)position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Entity_Fav entity = this.content.get(position);
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = this.mInflater.inflate(R.layout.module_fav_content, null);
            viewHolder = new ViewHolder();
            viewHolder.tvPlaceName = (TextView)convertView.findViewById(R.id.tv_place_name);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Log.e("adapter",entity.getName());
        viewHolder.tvPlaceName.setText(entity.getName());
        return convertView;
    }

    private class ViewHolder {
        public TextView tvPlaceName;
        ViewHolder() {}
    }

}
