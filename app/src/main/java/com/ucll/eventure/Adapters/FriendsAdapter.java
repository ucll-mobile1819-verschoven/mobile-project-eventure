package com.ucll.eventure.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ucll.eventure.Data.Friend;

import java.util.List;

public class FriendsAdapter extends BaseAdapter {

    private List<Friend> friends;
    private Context context;
    private LayoutInflater inflater;

    public FriendsAdapter(){

    }

    public FriendsAdapter(Context context, List<Friend> friends, LayoutInflater inflater){
        this.friends = friends; this.context=context; this.inflater=inflater;
    }


    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
