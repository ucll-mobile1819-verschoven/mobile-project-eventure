package com.ucll.eventure.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ucll.eventure.R;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Random;

public class ImageAdapter extends BaseAdapter {
    private ArrayList<Bitmap> events;
    private Context context;
    private int clicked;

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

        final SimpleDraweeView imageView = vi.findViewById(R.id.my_image_view);

        if (events.get(position) != null && imageView != null) {
            final Bitmap toDisplay = events.get(position);
            imageView.setImageDrawable(new BitmapDrawable(context.getResources(), toDisplay));
            clicked = 0;

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clicked++;
                    if (clicked == 1) {
                        Toast.makeText(context, "Click again to save image", Toast.LENGTH_LONG).show();
                    } else {
                        clicked = 0;
                        String root = Environment.getExternalStorageDirectory().toString();
                        File myDir = new File(root + "/Pictures/Eventure");
                        myDir.mkdirs();
                        Log.d("myloc", root);
                        Random generator = new Random();
                        int n = 10000;
                        n = generator.nextInt(n);
                        String fname = "Image-" + n + ".jpg";
                        File file = new File(myDir, fname);
                        if (file.exists())
                            file.delete();
                        try {
                            FileOutputStream out = new FileOutputStream(file);
                            toDisplay.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            out.flush();
                            out.close();

                            Toast.makeText(context, "Image Saved Under Pictures/Eventure", Toast.LENGTH_LONG).show();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                Intent install = new Intent(Intent.ACTION_VIEW);
                                Uri photoURI = FileProvider.getUriForFile(context,
                                        "com.ucll.eventure.com.vansuita.pickimage.provider",
                                        file);
                                install.setDataAndType(photoURI, "image/jpeg");

                                context.startActivity(install);

                                //
                            } else {
                                Log.d("diterror2", "ik ben hier2");
                                final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
                                context.sendBroadcast(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        return vi;
    }
}
