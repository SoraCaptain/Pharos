package com.iems5722.group1.pharos.module.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
        return entity.getIsComMsg()?0:1;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Entity_Get_Msg entity = this.content.get(position);
        ViewHolder viewHolder;
        boolean isComMsg = entity.getIsComMsg();
//        Log.e("adapter name",entity.getName());
//        Log.e("adapter message",entity.getMessage());
//        Log.e("adapter time",entity.getTimestamp());
//        Log.e("ComMsg",String.valueOf(isComMsg));

        viewHolder = new ViewHolder();
        if(entity.getMsgType().equals("text") || entity.getMsgType().equals("map")) {
            if (isComMsg) {
                convertView = this.mInflater.inflate(R.layout.module_chat_content_left, null);
            } else {
                convertView = this.mInflater.inflate(R.layout.module_chat_content_right, null);
            }
            convertView.setTag(viewHolder);
            TextView txt_content = (TextView)convertView.findViewById(R.id.txt_content);
            viewHolder.tvContent = txt_content;
            viewHolder.tvContent.setText(entity.getMessage());
        }
        else if (entity.getMsgType().equals("img")){
            if (isComMsg){
                convertView = this.mInflater.inflate(R.layout.module_chat_content_img_left,null);
            }
            else{
                convertView = this.mInflater.inflate(R.layout.module_chat_content_img_right,null);
            }
            convertView.setTag(viewHolder);
            ImageView ivImg = (ImageView)convertView.findViewById(R.id.ivImg);
            viewHolder.ivImg = ivImg;
            viewHolder.ivImg.setImageBitmap(entity.getImage());
        }

        TextView txt_time = (TextView)convertView.findViewById(R.id.txt_time);
        viewHolder.tvUserName = (TextView)convertView.findViewById(R.id.txt_userName);
        viewHolder.tvSendTime = txt_time;

        String name="User: "+entity.getUserName();
        viewHolder.tvUserName.setText(name);
        viewHolder.tvSendTime.setText(entity.getTimestamp());
        return convertView;
    }

    private class ViewHolder {
        public TextView tvUserName;
        public TextView tvContent;
        public TextView tvSendTime;
        public ImageView ivImg;
        ViewHolder() {}
    }
}
