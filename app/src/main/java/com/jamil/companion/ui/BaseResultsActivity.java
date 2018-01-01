package com.jamil.companion.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jamil.companion.BaseActivity;
import com.jamil.companion.R;
import com.jamil.companion.adapter.EntityAdapter;
import com.jamil.companion.model.Entity;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;

public abstract class BaseResultsActivity extends BaseActivity implements Alertable{

    public static final String TAG = BaseResultsActivity.class.getSimpleName();

    protected String mQuery;
    protected int mOffset = 0;
    protected int mLimit = 20;

    protected ListView mListView;
    protected TextView mEmptyTextView;
    protected Button mPreviousButton;
    protected Button mNextButton;
    protected TextView mPageTextView;
    protected ProgressWheel mProgressWheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mQuery = savedInstanceState.getString("mQuery");
            mOffset = savedInstanceState.getInt("mOffset");
            mLimit = savedInstanceState.getInt("mLimit");
        }
    }

    @Override
    public void receiveData(ArrayList<Entity> entities, int offset, int limit, int total, int count) {
        Log.d(TAG, "Number of results: " + entities.size());
        for (Entity entity : entities) {
            Log.d(TAG, "Entity ID: " + entity.getId());
        }
        updateView(entities, offset, limit, total, count);
    }

    /**
     * Update the layout after receiving results.
     *
     * @param entities List of entities parsed out of the API response
     * @param offset Offset of the result set
     * @param limit Maximum number of results in this set
     * @param total Total number of results available
     * @param count Number of results in this result set
     */
    public abstract void updateView(final ArrayList<Entity> entities, int offset, int limit, final int total, int count);

    public int getTotalPages(int total, int limit)
    {
        if (limit <= 0) {
            return 0;
        }
        int numPages = total/limit;
        numPages += total % limit == 0 ? 0 : 1;
        return numPages;
    }

    public int getCurrentPage(int total, int limit, int offset)
    {
        if (offset < total && limit > 0) {
            return (offset/limit + 1);
        }
        return 0;
    }

    protected void bindUiVariables()
    {
        mListView = (ListView) findViewById(android.R.id.list);
        mEmptyTextView = (TextView) findViewById(android.R.id.empty);
        mPreviousButton = (Button) findViewById(R.id.previousResultsButton);
        mNextButton = (Button) findViewById(R.id.nextResultsButton);
        mPageTextView = (TextView) findViewById(R.id.pageTextView);
        mProgressWheel = (ProgressWheel) findViewById(R.id.progressWheel);
    }

    protected void initSearchUi()
    {
        mEmptyTextView.setText("");
        mProgressWheel.setVisibility(View.VISIBLE);
    }

    protected void showResultsInUi(final EntityAdapter adapter, final ArrayList<Entity> entities, int offset, int limit, int total, int count) {
        final int totalPages = getTotalPages(total, limit);
        final int currentPage = getCurrentPage(total, limit, offset);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressWheel.setVisibility(View.INVISIBLE);

                if (entities.isEmpty()) {
                    mEmptyTextView.setText(R.string.no_results_message);
                }

                String pageText = (currentPage > 0) ? "Page " + currentPage + " of " + totalPages : "";
                pageText = entities.isEmpty() ? "" : pageText;
                mPageTextView.setText(pageText);

                mListView.setAdapter(adapter);
                mListView.setEmptyView(mEmptyTextView);

                Log.d(TAG, "Current Page: " + currentPage);
                Log.d(TAG, "Total Pages: " + totalPages);

                mPreviousButton.setEnabled(currentPage > 1);
                mNextButton.setEnabled(currentPage < totalPages);
            }
        });
    }

    protected void setUpPaginationListeners()
    {
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOffset += mLimit;
                searchForEntities();
            }
        });

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOffset -= mLimit;
                mOffset = mOffset < 0 ? 0 : mOffset;
                searchForEntities();
            }
        });
    }

    abstract protected void searchForEntities();

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("mQuery", mQuery);
        savedInstanceState.putInt("mOffset", mOffset);
        savedInstanceState.putInt("mLimit", mLimit);
    }
}
