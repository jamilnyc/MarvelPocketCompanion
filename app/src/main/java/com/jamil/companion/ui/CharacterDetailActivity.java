package com.jamil.companion.ui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jamil.companion.BaseActivity;
import com.jamil.companion.R;
import com.jamil.companion.model.Character;
import com.jamil.companion.model.Image;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class CharacterDetailActivity extends BaseActivity {

    public static final String TAG = CharacterDetailActivity.class.getSimpleName();
    public static final String CHARACTER = "character";

    private ImageView mCharacterThumbnail;
    private TextView mCharacterName;
    private TextView mCharacterDescription;
    private Button mCharacterWikiButton;
    private Button mCharacterSeriesButton;

    Character mCharacter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_detail);

        mCharacterThumbnail = (ImageView) findViewById(R.id.characterThumbnail);
        mCharacterName = (TextView) findViewById(R.id.characterName);
        mCharacterDescription = (TextView) findViewById(R.id.characterDescription);
        mCharacterWikiButton = (Button) findViewById(R.id.characterWikiButton);
        mCharacterSeriesButton = (Button) findViewById(R.id.characterSeriesButton);

        // Pull character data from the Intent
        Intent intent = getIntent();
        Parcelable parcelable = intent.getParcelableExtra(CharacterResultsActivity.CHARACTER_DETAIL);
        mCharacter = (Character) parcelable;

        setTitle(mCharacter.getName());

        // Update the UI
        updateDetailView();
    }

    private void updateDetailView() {

        setThumbnailImage();
        Log.d(TAG, "Character Name: " + mCharacter.getName());
        mCharacterName.setText(mCharacter.getName());
        mCharacterName.setTypeface(getComicTitleFont());

        String description = mCharacter.getDescription();
        if (description.isEmpty()) {
            mCharacterDescription.setVisibility(View.GONE);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                description = android.text.Html.fromHtml(description, 0).toString();
            } else {
                description = android.text.Html.fromHtml(description).toString();
            }
            description = com.jamil.companion.util.Text.removeUnicodeReplacementCharacter(description);
            Log.d(TAG, "Description: " + description);
            mCharacterDescription.setText(description);
            mCharacterDescription.setTypeface(getComicLetteringFont());
        }

        // Zoom into character description (animated)
        Animator descriptionAnimator = AnimatorInflater.loadAnimator(this, R.animator.character_description_animation);
        descriptionAnimator.setTarget(mCharacterDescription);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(descriptionAnimator);
        animatorSet.start();

        // Hide Wiki button if wiki link isn't available
        if (mCharacter.getWikiLink().isEmpty()) {
            Log.d(TAG, "Character does not have a Wiki page");
            mCharacterWikiButton.setVisibility(View.GONE);
        } else {
            makeRoundedColoredButton(mCharacterWikiButton, "#ffad33");
        }

        makeRoundedColoredButton(mCharacterSeriesButton, "#d24dff");

        setupListeners();
    }

    private void setThumbnailImage()
    {
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(3)
                .cornerRadiusDp(330)
                .oval(false)
                .build();

        Picasso.with(this)
                .load(mCharacter.getThumbnail().getImageUrl(Image.AspectRatio.STANDARD, Image.Size.INCREDIBLE))
                .placeholder(R.drawable.loading_placeholder)
                .error(R.drawable.error_placeholder)
                .transform(transformation)
                .into(mCharacterThumbnail);
    }

    private void setupListeners()
    {
        mCharacterWikiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String wikiLink = mCharacter.getWikiLink();
                if (wikiLink.isEmpty()) {
                    Toast.makeText(CharacterDetailActivity.this, "This character does not have a Wiki Page!", Toast.LENGTH_LONG).show();
                } else {
                    Uri uri = Uri.parse(wikiLink);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });

        mCharacterSeriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Series Button Clicked");
                startSeriesResultActivity();
            }
        });
    }

    private void startSeriesResultActivity()
    {
        Intent intent = new Intent(this, SeriesResultsActivity.class);
        intent.putExtra(CHARACTER, mCharacter);
        pageTransition(intent);
    }
}
