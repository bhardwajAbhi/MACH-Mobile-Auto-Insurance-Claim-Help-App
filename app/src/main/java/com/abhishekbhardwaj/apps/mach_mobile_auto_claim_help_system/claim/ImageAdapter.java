package com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.claim;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.R;

import java.util.ArrayList;

public class ImageAdapter extends ArrayAdapter<MyImage> {


    private static class ViewHolder {
        ImageView imgIcon;
    }


    public ImageAdapter(@NonNull Context context, ArrayList<MyImage> images) {

        super(context, 0, images);
        Log.d("ClaimActivity", "ImageAdapter constructor called");
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d("ClaimActivity", "getView: starting method");


        ViewHolder viewHolder;

        if(convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_image, parent, false);
            viewHolder.imgIcon = (ImageView) convertView.findViewById(R.id.item_img_icon);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MyImage image = getItem(position);
        final int THUMBSIZE = 120;
        viewHolder.imgIcon.setImageBitmap(ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(image.getPath()), THUMBSIZE, THUMBSIZE));

        Log.d("ClaimActivity", "getView: Return Successfull");
        return convertView;
    }
}
