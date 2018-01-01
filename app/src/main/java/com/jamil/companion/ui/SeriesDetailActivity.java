package com.jamil.companion.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jamil.companion.BaseActivity;
import com.jamil.companion.R;
import com.jamil.companion.model.Creator;
import com.jamil.companion.model.Series;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Calendar;

public class SeriesDetailActivity extends BaseActivity {

    public static final String TAG = SeriesDetailActivity.class.getSimpleName();
    private Series mSeries;

    private ImageView mThumbnail;
    private TextView mTitle;
    private TextView mType;
    private TextView mRating;
    private TextView mStartYear;
    private TextView mEndYear;
    private TextView mDescription;
    private Button mBuyButton;
    private GridLayout mContributorsGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_detail);

        // Set up views
        mThumbnail = (ImageView) findViewById(R.id.seriesDetailThumbnail);
        mTitle = (TextView) findViewById(R.id.seriesDetailTitle);
        mRating = (TextView) findViewById(R.id.seriesDetailRating);
        mStartYear = (TextView) findViewById(R.id.seriesDetailStartYear);
        mEndYear = (TextView) findViewById(R.id.seriesDetailEndYear);
        mDescription = (TextView) findViewById(R.id.seriesDetailDescription);
        mBuyButton = (Button) findViewById(R.id.seriesDetailBuyButton);
        mType = (TextView) findViewById(R.id.seriesDetailType);
        mContributorsGrid = (GridLayout) findViewById(R.id.seriesDetailContributorGrid);


        // Read data from Intent
        Intent intent = getIntent();
        Parcelable parcelable = intent.getParcelableExtra(SeriesResultsActivity.SERIES_DETAIL);
        mSeries = (Series) parcelable;

        setTitle(mSeries.getTitle());

        setListeners();

        // Update the view
        updateView();
    }

    private void setListeners()
    {
        mBuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMidtownComicsSearch();
            }
        });
    }

    private void setSeriesThumbnail()
    {
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(1)
                .build();

        Picasso.with(this)
            .load(mSeries.getThumbnail().getRawImageUrl())
            .placeholder(R.drawable.loading_placeholder)
            .error(R.drawable.error_placeholder)
            .transform(transformation)
            .into(mThumbnail);
    }

    private void updateView()
    {
        setSeriesThumbnail();
        mTitle.setText(mSeries.getTitle().toUpperCase());
        mTitle.setTypeface(getComicTitleFont());

        setTextOrHide(mSeries.getType(), mType, "");
        setTextOrHide(mSeries.getRating(), mRating, "Age Rating: ");
        setTextOrHide(mSeries.getDescription(), mDescription, "Description: ");
        mDescription.setTypeface(getComicLetteringFont());

        mStartYear.setText("Started: " + mSeries.getStartYear());

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        if (currentYear >= mSeries.getEndYear()) {
            mEndYear.setText("Ended: " + mSeries.getEndYear());
        } else {
            mEndYear.setVisibility(View.GONE);
        }

        makeRoundedColoredButton(mBuyButton, "#5fcf80");

        setCreatorsTable();
    }

    private void setTextOrHide(String text, TextView textView, String prefix)
    {
        if (text == null || text.isEmpty()) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(prefix + text);
        }
    }

    private void setCreatorsTable()
    {
        ArrayList<Creator> creators = mSeries.getCreators();

        mContributorsGrid.removeAllViews();
        mContributorsGrid.setColumnCount(2);
        mContributorsGrid.setRowCount(creators.size());
        for (int i = 0; i < creators.size(); ++i) {
            TextView creatorName = new TextView(this);
            creatorName.setText(cleanUpName(creators.get(i).getName()));
            creatorName.setTextSize(16);

            TextView creatorRole = new TextView(this);
            String role = creators.get(i).getRole();
            role = role.length() > 1 ? role.substring(0,1).toUpperCase() + role.substring(1) : role;
            role = "\t\t" + role;
            creatorRole.setText(role);
            creatorRole.setTextSize(16);

            mContributorsGrid.addView(creatorName);
            mContributorsGrid.addView(creatorRole);
        }
    }

    private void openMidtownComicsSearch()
    {
        String query = mSeries.getSearchFriendlyTitle();

        // TODO: Move these strings to a config file
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("www.midtowncomics.com")
                .appendPath("store")
                .appendPath("search.asp")
                .appendQueryParameter("q", query)
        ;

        Uri uri = builder.build();
        playCashRegister();
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    // TODO: Move to util class
    public static String titleCase(String s) {
        final String DELIMITERS = " '-/";
        StringBuilder sb = new StringBuilder();
        boolean capNext = true;
        for (char c : s.toCharArray()) {
            c = (capNext)
                    ? Character.toUpperCase(c)
                    : Character.toLowerCase(c);
            sb.append(c);
            capNext = (DELIMITERS.indexOf((int) c) >= 0);
        }
        return sb.toString();
    }

    public static String removeNonAlphaDashSpaceChars(String original)
    {
        return original.replaceAll("[^A-Za-z\\-\\s]", "");
    }

    // TODO: Move to model class
    private String cleanUpName(String name)
    {
        return titleCase(removeNonAlphaDashSpaceChars(name));
    }
}
