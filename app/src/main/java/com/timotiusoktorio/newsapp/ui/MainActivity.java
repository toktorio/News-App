package com.timotiusoktorio.newsapp.ui;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.timotiusoktorio.newsapp.R;
import com.timotiusoktorio.newsapp.data.NewsLoader;
import com.timotiusoktorio.newsapp.data.model.News;
import com.timotiusoktorio.newsapp.util.ConnectivityHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>, NewsAdapter.OnItemClickListener {

    private static final int LOADER_ID = 1;

    private FrameLayout mMainContent;
    private RecyclerView mRecyclerView;
    private LinearLayout mEmptyView;
    private ProgressBar mLoadingSpinner;
    private NewsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_activity_main);
        }

        mMainContent = findViewById(R.id.main_content);
        mRecyclerView = findViewById(R.id.recycler_view);
        mEmptyView = findViewById(R.id.empty_view);
        mLoadingSpinner = findViewById(R.id.loading_spinner);

        mAdapter = new NewsAdapter(this, new ArrayList<News>(), this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initiate the Loader if network connection is available.
        if (ConnectivityHelper.isNetworkAvailable(this)) {
            getLoaderManager().initLoader(LOADER_ID, null, this);
        } else {
            onNoNetworkDetected();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            refresh(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        // Create and return a new instance of NewsLoader.
        return new NewsLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        // Clear data if it is not already empty.
        if (mAdapter.getItemCount() > 0) {
            mAdapter.clearData();
        }

        // Add new data if the fetched data is not empty.
        if (data != null && !data.isEmpty()) {
            mAdapter.addData(data);
        }

        // Show the RecyclerView if data is not empty, else show the empty view.
        boolean isDataEmpty = mAdapter.getItemCount() == 0;
        mRecyclerView.setVisibility(isDataEmpty ? View.GONE : View.VISIBLE);
        mEmptyView.setVisibility(isDataEmpty ? View.VISIBLE : View.GONE);

        // Hide the loading spinner.
        mLoadingSpinner.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset. Clear data from the adapter.
        mAdapter.clearData();
    }

    @Override
    public void onItemClick(String newsUrl) {
        // This is an interface method from NewsAdapter that handles list item click which will send
        // an implicit Intent to open the news url using any of the installed browser applications.
        if (!TextUtils.isEmpty(newsUrl)) {
            Uri newsUri = Uri.parse(newsUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, newsUri);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

    public void refresh(View view) {
        // Show the loading spinner.
        mLoadingSpinner.setVisibility(View.VISIBLE);

        // Hide the RecyclerView and empty view if it's visible.
        if (mRecyclerView.getVisibility() == View.VISIBLE) mRecyclerView.setVisibility(View.GONE);
        if (mEmptyView.getVisibility() == View.VISIBLE) mEmptyView.setVisibility(View.GONE);

        // Restart the Loader if network connection is available.
        if (ConnectivityHelper.isNetworkAvailable(this)) {
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        } else {
            onNoNetworkDetected();
        }
    }

    private void onNoNetworkDetected() {
        mEmptyView.setVisibility(View.VISIBLE);
        mLoadingSpinner.setVisibility(View.GONE);
        Snackbar.make(mMainContent, R.string.message_network_unavailable, Snackbar.LENGTH_SHORT).show();
    }
}