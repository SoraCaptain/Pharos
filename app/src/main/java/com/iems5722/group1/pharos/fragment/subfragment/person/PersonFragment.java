package com.iems5722.group1.pharos.fragment.subfragment.person;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.iems5722.group1.pharos.Constants;
import com.iems5722.group1.pharos.R;
import com.iems5722.group1.pharos.utils.Util;

import static com.iems5722.group1.pharos.utils.Util.getUsername;


public class PersonFragment extends Fragment{
    View view;
    TextView textView;
    Button btnExit;
    public static PersonFragment newInstance(String s){
        PersonFragment homeFragment = new PersonFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ARGS,s);
        homeFragment.setArguments(bundle);
        return homeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_item_me, container, false);
        Bundle bundle = getArguments();
        final String s = getUsername(getActivity());
        textView = (TextView) view.findViewById(R.id.lr);
        btnExit = (Button)view.findViewById(R.id.btnExit);
        if(s==null){
            btnExit.setVisibility(View.INVISIBLE);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), personLogin.class);
                    startActivity(intent);
                }
            });
        }
        else{
            textView.setText(s);
            btnExit.setVisibility(View.VISIBLE);
        }
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("click","button");
                btnExit.setVisibility(View.INVISIBLE);
                Util.delUserName(s, getActivity());
                textView.setText("login/register");
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), personLogin.class);
                        startActivity(intent);
                    }
                });
            }
        });
        return view;
    }

}
