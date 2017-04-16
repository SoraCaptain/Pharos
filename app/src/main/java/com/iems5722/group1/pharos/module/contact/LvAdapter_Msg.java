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

public class LvAdapter_Msg extends BaseAdapter {

    private List<Entity_Get_Msg> content;
    private LayoutInflater mInflater;

    public LvAdapter_Msg(Context context, List<Entity_Get_Msg> content) {
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

    public int getItemViewType(int position) {
        Entity_Get_Msg entity = this.content.get(position);
        return entity.getMsgType()?0:1;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Entity_Get_Msg entity = this.content.get(position);
        ViewHolder viewHolder;
        boolean isComMsg = entity.getMsgType();
//        Log.e("adapter name",entity.getName());
//        Log.e("adapter message",entity.getMessage());
//        Log.e("adapter time",entity.getTimestamp());
//        Log.e("ComMsg",String.valueOf(isComMsg));

       // if(convertView == null) {
            viewHolder = new ViewHolder();
            if(isComMsg) {
                convertView = this.mInflater.inflate(R.layout.module_chat_content_left, null);
            } else {
                convertView = this.mInflater.inflate(R.layout.module_chat_content_right, null);
            }
            TextView txt_content = (TextView)convertView.findViewById(R.id.txt_content);
            TextView txt_time = (TextView)convertView.findViewById(R.id.txt_time);
            TextView txt_userName = (TextView)convertView.findViewById(R.id.txt_userName);
            viewHolder.tvUserName = txt_userName;
            viewHolder.tvContent = txt_content;
            viewHolder.tvSendTime = txt_time;
            convertView.setTag(viewHolder);
//        }
//        else {
//            viewHolder = (LvAdapter_Msg.ViewHolder)convertView.getTag();
//        }
        String name="User: "+entity.getUserName();
        viewHolder.tvUserName.setText(name);
        viewHolder.tvContent.setText(entity.getMessage());
        viewHolder.tvSendTime.setText(entity.getTimestamp());
        return convertView;
    }

    private class ViewHolder {
        public TextView tvUserName;
        public TextView tvContent;
        public TextView tvSendTime;
        ViewHolder() {}
    }
}
