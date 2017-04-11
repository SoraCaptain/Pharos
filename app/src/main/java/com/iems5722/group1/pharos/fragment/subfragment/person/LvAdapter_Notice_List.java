package com.iems5722.group1.pharos.fragment.subfragment.person;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iems5722.group1.pharos.R;

import java.util.List;

/**
 * Created by Sora on 10/4/17.
 */

public class LvAdapter_Notice_List extends BaseAdapter {
    private List<Entity_Notice_List> content;
    private LayoutInflater mInflater;

    public LvAdapter_Notice_List(Context context, List<Entity_Notice_List> content) {
        this.content = content;
        this.mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return 1;
    }

    public Object getItem(int position) {
        return 1;
    }

    public long getItemId(int position) {
        return (long)position;
    }

    public int getItemViewType(int position) {

        return 1;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Entity_Notice_List entity = this.content.get(position);
        LvAdapter_Notice_List.ViewHolder viewHolder;
        viewHolder = new LvAdapter_Notice_List.ViewHolder();
        if(entity.getAction() == 1) {
            convertView = this.mInflater.inflate(R.layout.fragment_me_notice_content, null);
        }
        TextView tvNotice = (TextView)convertView.findViewById(R.id.tv_content);
        Button btnRefuse = (Button)convertView.findViewById(R.id.btn_refuse);
        Button btnAccept = (Button)convertView.findViewById(R.id.btn_accept);
        viewHolder.tvNotice = tvNotice;
        viewHolder.btnRefuse = btnRefuse;
        viewHolder.btnAccept = btnAccept;
        convertView.setTag(viewHolder);

        viewHolder.tvNotice.setText(entity.getContent()+" wants to be your friend");

        viewHolder.btnAccept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.e("click","accept");
            }
        });

        viewHolder.btnRefuse.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.e("click","accept");
            }
        });
        return convertView;
    }

    private class ViewHolder {
        public TextView tvNotice;
        public Button btnRefuse;
        public Button btnAccept;
        ViewHolder() {}
    }


}
