package com.jamil.companion.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.jamil.companion.R;
import com.jamil.companion.adapter.SeriesAdapter;
import com.jamil.companion.gateway.ApiGateway;
import com.jamil.companion.model.Character;
import com.jamil.companion.model.Entity;
import com.jamil.companion.model.Series;

import java.util.ArrayList;

public class SeriesResultsActivity extends BaseResultsActivity {

    public static final String SERIES_DETAIL = "seriesDetail";
    private Character mCharacter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_results);

        // Showing fewer for series than the default of 20
        mLimit = 10;

        bindUiVariables();

        Intent intent = getIntent();
        Parcelable parcelable = intent.getParcelableExtra(CharacterDetailActivity.CHARACTER);
        mCharacter = parcelable == null ? null : (Character) parcelable;

        if (mCharacter != null) {
            setTitle("Series with " + mCharacter.getName());
        }

        setUpPaginationListeners();
        searchForEntities();
    }

    @Override
    protected void searchForEntities()
    {
        // Search series related to the character
        if (mCharacter == null) {
            Log.d(TAG, "No character passed through the intent");
            return;
        }

        initSearchUi();
        ApiGateway.searchSeriesByCharacter(this, mCharacter.getId(), mOffset, mLimit, this);
    }

    @Override
    public void updateView(final ArrayList<Entity> entities, int offset, int limit, int total, int count) {
        SeriesAdapter adapter = new SeriesAdapter(this, entities);
        showResultsInUi(adapter, entities, offset, limit, total, count);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Clicked on position " + position);
                startSeriesDetailActivity((Series) entities.get(position));
            }
        });
    }

    private void startSeriesDetailActivity(Series series)
    {
        Intent intent = new Intent(this, SeriesDetailActivity.class);
        intent.putExtra(SERIES_DETAIL, series);
        pageTransition(intent);
    }
}
