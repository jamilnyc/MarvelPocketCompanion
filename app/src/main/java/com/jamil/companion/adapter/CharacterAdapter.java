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
import com.jamil.companion.model.Character;
import com.jamil.companion.model.Entity;
import com.jamil.companion.model.Image;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

public class CharacterAdapter extends EntityAdapter {

    public CharacterAdapter (Context context, ArrayList<Entity> entities)
    {
        super(context, entities);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // convertView is the object that is reused to render list items and save memory
        // It is null for the first rendering
        if (convertView == null) {
            // Converts an xml layout to a View object, representing the view of a single list item
            convertView = LayoutInflater.from(mContext).inflate(R.layout.character_list_item, null);

            holder = new ViewHolder();

            // Set all the view objects in the view holder
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);

            // Sets a tag for the vie that can be reused. On subsequent calls this is the view that
            // will be reused
            convertView.setTag(holder);
        } else {
            // Get the existing holder that was previously initialized
            holder = (ViewHolder) convertView.getTag();
        }

        Character character = (Character) mEntities.get(position);

        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(1)
                .build();

        Picasso.with(mContext)
                .load(character.getThumbnail().getImageUrl(Image.AspectRatio.STANDARD, Image.Size.FANTASTIC))
                .placeholder(R.drawable.loading_placeholder)
                .error(R.drawable.error_placeholder)
                .transform(transformation)
                .into(holder.iconImageView);

        holder.nameTextView.setText(character.getName());
        holder.nameTextView.setTypeface( ((BaseActivity) mContext).getComicLetteringFont() );

        return convertView;
    }

    public static class ViewHolder
    {
        ImageView iconImageView;
        TextView nameTextView;
    }
}
