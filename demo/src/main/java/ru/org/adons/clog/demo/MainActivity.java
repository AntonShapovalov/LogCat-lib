package ru.org.adons.clog.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
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

import ru.org.adons.clog.Message;
import ru.org.adons.clog.RecyclerAdapter;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "message";
    private MyRecyclerAdapter adapter;
    private ProgressBar progressBar;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // test: debug message
        Log.d(RecyclerAdapter.LOG_TAG, this.getLocalClassName() + ": onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }

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

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                filterList(menuItem.getItemId());
                return true;
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
        switch (item.getItemId()) {
            case R.id.action_clear:
                adapter.clearItems();
                return true;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void filterList(int itemID) {
        Message.Level level = null;
        switch (itemID) {
            case R.id.nav_verbose:
                level = Message.Level.VERBOSE;
                break;
            case R.id.nav_debug:
                level = Message.Level.DEBUG;
                break;
            case R.id.nav_info:
                level = Message.Level.INFO;
                break;
            case R.id.nav_warn:
                level = Message.Level.WARN;
                break;
            case R.id.nav_error:
                level = Message.Level.ERROR;
                break;
            case R.id.nav_assert:
                level = Message.Level.ASSERT;
        }
        adapter.filterByLevel(level);
    }
}
