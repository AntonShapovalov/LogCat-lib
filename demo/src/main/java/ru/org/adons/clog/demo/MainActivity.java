package ru.org.adons.clog.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import ru.org.adons.clog.RecyclerAdapter;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "message";
    private MyRecyclerAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // test: debug message
        Log.d(RecyclerAdapter.LOG_TAG, this.getLocalClassName() + ": onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerAdapter();
        recyclerView.setAdapter(adapter);

        progressBar = (ProgressBar) findViewById(android.R.id.progress);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.loadItems();
            }
        });
    }

    private class MyRecyclerAdapter extends RecyclerAdapter {
        @Override
        public void onLoadStarted() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoadFinished(boolean isLoadSuccess) {
            progressBar.setVisibility(View.INVISIBLE);
            if (!isLoadSuccess) {
                Toast.makeText(MainActivity.this, RecyclerAdapter.ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onClickItem(String logMessage) {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra(EXTRA_MESSAGE, logMessage);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_clear) {
            adapter.clearItems();
        }
        return super.onOptionsItemSelected(item);
    }

}
