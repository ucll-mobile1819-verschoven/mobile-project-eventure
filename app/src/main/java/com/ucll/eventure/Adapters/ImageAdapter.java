package com.ucll.eventure.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.ucll.eventure.R;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private ArrayList<Bitmap> events;
    private Context context;

    public ImageAdapter(Context context, ArrayList<Bitmap> events) {
        this.context = context;
        this.events = events;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Bitmap getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);

        // Get View ID's
        // Set them

        PhotoView imageView = vi.findViewById(R.id.event_added_image);

        if (events.get(position) != null && imageView != null) {
            final Bitmap toDisplay = events.get(position);
            imageView.setImageDrawable(new BitmapDrawable(context.getResources(), toDisplay));
        }
        return vi;
    }
}
