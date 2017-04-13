package com.iems5722.group1.pharos.module.contact;

import android.content.Context;
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

public class LvAdapter_Contact extends BaseAdapter{
    private List<Entity_Contact> content;
    private LayoutInflater mInflater;
  //  private OnClickListener mListener;

    public LvAdapter_Contact(Context context, List<Entity_Contact> content) {
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
        Entity_Contact entity = this.content.get(position);
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = this.mInflater.inflate(R.layout.module_contact_content, null);
            viewHolder = new ViewHolder();
            TextView tvFriendName = (TextView)convertView.findViewById(R.id.tv_friend_name);
            viewHolder.tvFriendName = tvFriendName;
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.tvFriendName.setText(entity.getName());
        return convertView;
    }

    private class ViewHolder {
        public TextView tvFriendName;
        ViewHolder() {}
    }

}
