package com.iems5722.group1.pharos.module.chatrooms;

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

public class LvAdapter_Chatroom extends BaseAdapter{
    private List<Entity_Chatroom> content;
    private LayoutInflater mInflater;
  //  private OnClickListener mListener;

    public LvAdapter_Chatroom(Context context, List<Entity_Chatroom> content) {
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
        Entity_Chatroom entity = this.content.get(position);
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = this.mInflater.inflate(R.layout.module_chat_room_list_content, null);
            viewHolder = new ViewHolder();
            TextView tvFriendName = (TextView)convertView.findViewById(R.id.tv_friend_name);
            viewHolder.tvFriendName = tvFriendName;
            TextView tvMsg = (TextView)convertView.findViewById(R.id.tv_latest_msg);
            viewHolder.tvMsg = tvMsg;
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.tvFriendName.setText(entity.getName());
        viewHolder.tvMsg.setText(entity.getMsg());
        return convertView;
    }

    private class ViewHolder {
        public TextView tvFriendName;
        public TextView tvMsg;
        ViewHolder() {}
    }

}
