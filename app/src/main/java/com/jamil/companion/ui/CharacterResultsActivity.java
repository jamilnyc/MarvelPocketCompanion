package com.jamil.companion.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.jamil.companion.R;
import com.jamil.companion.adapter.CharacterAdapter;
import com.jamil.companion.gateway.ApiGateway;
import com.jamil.companion.model.Character;
import com.jamil.companion.model.Entity;

import java.util.ArrayList;

public class CharacterResultsActivity extends BaseResultsActivity {

    public static final String CHARACTER_DETAIL = "character_detail";
    public final String TAG = CharacterResultsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_results);

        bindUiVariables();

        Intent intent = getIntent();
        mQuery = intent.getStringExtra("query");

        setTitle("Results for \"" + mQuery + "\"");

        Log.d(TAG, "Fetching results for query: " + mQuery);
        setUpPaginationListeners();
        searchForEntities();
    }

    @Override
    protected void searchForEntities()
    {
        initSearchUi();
        ApiGateway.searchCharacters(this, mQuery, mOffset, mLimit, this);
    }

    @Override
    public void updateView(final ArrayList<Entity> entities, int offset, int limit, final int total, int count)
    {
        CharacterAdapter adapter = new CharacterAdapter(this, entities);
        showResultsInUi(adapter, entities, offset, limit, total, count);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // parent is the ListView itself
                // view is the item that was clicked
                // position is the position of the view in the list

                Log.d(TAG, "Clicked on position " + position);
                startCharacterDetailActivity((Character) entities.get(position));
            }
        });
    }

    public void startCharacterDetailActivity(Character character)
    {
        Intent intent = new Intent(this, CharacterDetailActivity.class);
        intent.putExtra(CHARACTER_DETAIL, character);
        pageTransition(intent);
    }

}
