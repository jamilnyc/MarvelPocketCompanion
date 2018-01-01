package com.jamil.companion;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jamil.companion.cache.EntityCache;
import com.jamil.companion.ui.AboutActivity;
import com.jamil.companion.ui.CharacterResultsActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int MAX_RECENT_HISTORY = 8;

    @BindView(R.id.searchQuery) EditText mSearchQuery;
    @BindView(R.id.searchButton) Button mSearchButton;
    @BindView(R.id.recentSearchesLabel) TextView mRecentSearchesList;
    @BindView(R.id.recentSearchesList) TextView mRecentSearchsLabel;
    @BindView(R.id.aboutButton) ImageButton mAboutButton;
    @BindView(R.id.clearHistoryButton) ImageButton mClearHistory;

    ArrayList<String> mRecents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(16);
        shape.setColor(Color.parseColor("#3399ff"));
        mSearchButton.setBackground(shape);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Search Button Clicked");
                String query = mSearchQuery.getText().toString().trim();
                if (query.isEmpty()) {
                    Toast.makeText(MainActivity.this, "You didn't type anything to search!", Toast.LENGTH_SHORT).show();
                } else {
                    startResultsActivity(query);
                    writeRecentSearchQuery(query);
                }
            }
        });

        mAboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAboutActivity();
            }
        });

        mClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearHistory();
            }
        });

        mRecentSearchsLabel.setTypeface(getComicLetteringFont());
        mRecentSearchesList.setTypeface(getComicLetteringFont());

        clearOldResultCacheEntries();
    }

    private void startResultsActivity(String query)
    {
        Intent intent = new Intent(this, CharacterResultsActivity.class);
        intent.putExtra("query", query);
        pageTransition(intent);
    }

    /**
     * Write the given search query to local storage.
     *
     * @param query The search query to store
     */
    private void writeRecentSearchQuery(String query)
    {
        mRecents.add(0, query);
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput("recents.txt", Context.MODE_PRIVATE);
            for (int i  = 0; i < mRecents.size() && i < MAX_RECENT_HISTORY; ++i) {
                outputStream.write(mRecents.get(i).getBytes());
                outputStream.write(System.getProperty("line.separator").getBytes());
            }
            outputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Error writing to recents file");
        }
    }

    /**
     * Reads recent search queries from local storage and updates text view storing them.
     */
    private void loadRecentSearchQueries()
    {
        try {
            mRecents.clear();
            Log.d(TAG, "Opening file");
            FileInputStream fis = openFileInput("recents.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            Log.d(TAG, "reading in lines");
            StringBuilder sb = new StringBuilder();
            int itemCount = 1;
            while ((line = bufferedReader.readLine()) != null) {
                Log.d(TAG, "Recent Search: " + line);
                mRecents.add(line);
                sb.append(itemCount++).append(". ").append(line);
                sb.append("\n");
            }
            String recentSearchText = sb.toString();
            mRecentSearchsLabel.setVisibility(recentSearchText.isEmpty() ? View.INVISIBLE : View.VISIBLE);
            mRecentSearchesList.setText(recentSearchText);
        } catch (IOException e) {
            Log.e(TAG, "Unable to read file");
            e.printStackTrace();
        }
    }

    /**
     * Clears history stored in local storage.
     */
    private void clearHistory()
    {
        EntityCache cache = new EntityCache(this);
        int removed = cache.clearAll();

        try {
            Log.d(TAG, "Clearing history");
            FileOutputStream outputStream;
            outputStream = openFileOutput("recents.txt", Context.MODE_PRIVATE);
            outputStream.write("".getBytes());
            outputStream.close();
            mRecentSearchesList.setText("");
            mRecents.clear();
            Toast.makeText(this, "Search History and " + removed + " cache entries cleared!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e(TAG, "Error writing to recents file");
            e.printStackTrace();
            Toast.makeText(this, "Error clearing your history. Don't worry. The NSA already got it.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecentSearchQueries();
    }

    private void startAboutActivity()
    {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}
