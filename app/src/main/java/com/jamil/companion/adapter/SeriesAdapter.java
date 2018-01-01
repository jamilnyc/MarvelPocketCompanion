package com.jamil.companion.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jamil.companion.BaseActivity;
import com.jamil.companion.R;
import com.jamil.companion.model.Entity;
import com.jamil.companion.model.Image;
import com.jamil.companion.model.Series;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

public class SeriesAdapter extends EntityAdapter {

    public static final String TAG  = SeriesAdapter.class.getSimpleName();
    public SeriesAdapter(Context context, ArrayList<Entity> entities)
   {
       super(context, entities);
   }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.series_list_item, null);
            holder = new ViewHolder();
            holder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.seriesItemThumbnail);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.seriesItemTitle);
            holder.typeTextView = (TextView) convertView.findViewById(R.id.seriesItemType);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Series series = (Series) mEntities.get(position);

        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(0.5f)
                .build();

        Picasso.with(mContext)
                .load(series.getThumbnail().getImageUrl(Image.AspectRatio.PORTRAIT, Image.Size.INCREDIBLE))
                .placeholder(R.drawable.loading_placeholder)
                .error(R.drawable.error_placeholder)
                .transform(transformation)
                .into(holder.thumbnailImageView);

        holder.typeTextView.setText(series.getType());
        holder.titleTextView.setText(series.getTitle());
        holder.titleTextView.setTypeface( ((BaseActivity) mContext).getComicLetteringFont() );

        return convertView;
    }

    public static class ViewHolder
    {
        ImageView thumbnailImageView;
        TextView titleTextView;
        TextView typeTextView;
    }
}
