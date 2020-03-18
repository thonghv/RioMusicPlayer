package com.pine.pmedia.activities;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pine.pmedia.R;
import com.pine.pmedia.adapters.SongCatRecyclerAdapter;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.helpers.MediaHelper;
import com.pine.pmedia.models.Song;

import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity {

    private RecyclerView recyclerSongsView;
    private TextView titleCatControl;
    private TextView noteCatControl;
    private int playListId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Init control layout
        initControl();

        // On load data
        onLoadDataBundle();

        // Load songs by album
        onLoadSongs();
    }

    private void initControl() {
        recyclerSongsView = findViewById(R.id.recycleViewSongsCat);
        titleCatControl = findViewById(R.id.titleCatControl);
        noteCatControl = findViewById(R.id.noteCatControl);
    }

    private void onLoadDataBundle() {

        setTitle(null);

        Bundle data = getIntent().getExtras();

        String titleCat = data.getString(Constants.KEY_TITLE_CAT);
        String noteCat = data.getString(Constants.KEY_NOTE_CAT);
        playListId = data.getInt(Constants.KEY_ID);

        titleCatControl.setText(titleCat);
        noteCatControl.setText(noteCat);

    }

    private void onLoadSongs() {
        recyclerSongsView.setItemAnimator(new DefaultItemAnimator());
        recyclerSongsView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Song> songs = MediaHelper.getSongsByPlayListId(this, playListId);
        SongCatRecyclerAdapter songCatRecyclerAdapter = new SongCatRecyclerAdapter(this, songs, Constants.VIEW_FILTER);
        recyclerSongsView.setAdapter(songCatRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the search; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        getMenuInflater().inflate(R.menu.search, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_search) {
            Toast.makeText(this, "Android Menu is Clicked", Toast.LENGTH_LONG).show();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
