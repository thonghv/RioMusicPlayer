package com.pine.pmedia.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.pine.pmedia.App;
import com.pine.pmedia.R;
import com.pine.pmedia.adapters.SongQueueRecyclerAdapter;
import com.pine.pmedia.helpers.OnStartDragListener;
import com.pine.pmedia.helpers.SimpleItemTouchHelperCallback;
import com.pine.pmedia.helpers.CommonHelper;
import com.pine.pmedia.helpers.Constants;
import com.pine.pmedia.helpers.MediaHelper;
import com.pine.pmedia.models.Filter;
import com.pine.pmedia.models.Song;
import com.pine.pmedia.services.MusicService;
import com.pine.pmedia.sqlite.DBManager;

import java.util.ArrayList;

public class QueueActivity extends BaseActivity implements OnStartDragListener {

    private MusicService mService;
    private ArrayList<Song> songs;
    private int totalDuration;
    private DBManager dbManager;
    private RecyclerView recyclerSongsView;
    private TextView titleCatControl;
    private TextView noteCatControl;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

        // Init db manager
        initDBManager();

        // Init control layout
        initControl();

        // Load songs by album
        onLoadDataUI();
    }

    @Override
    protected void onHandler() {

        mService = super.getMService();
    }

    public void initDBManager() {

        dbManager = new DBManager(this);
        dbManager.open();
    }

    private void initControl() {
        recyclerSongsView = findViewById(R.id.recycleViewSongsCat);
        titleCatControl = findViewById(R.id.titleCatControl);
        noteCatControl = findViewById(R.id.noteCatControl);
    }

    private void onLoadDataUI() {

        recyclerSongsView.setItemAnimator(new DefaultItemAnimator());
        recyclerSongsView.setLayoutManager(new LinearLayoutManager(this));

        Filter filter = MediaHelper.getQueue(dbManager, App.getInstance().getMediaPlayList());
        ArrayList<?> songs = filter != null ? filter.getSongs() : new ArrayList<>();

        this.songs = (ArrayList<Song>) songs;
        this.totalDuration = filter.getTotalDuration();

        String note = this.songs.size() + Constants.SPACE + Constants.SONGS
                + Constants.MINUS + CommonHelper.toFormatTime(totalDuration);
        titleCatControl.setText(R.string.nowPlaying);
        noteCatControl.setText(note);

        SongQueueRecyclerAdapter songQueueRecyclerAdapter =
                new SongQueueRecyclerAdapter(this, songs, this);
        recyclerSongsView.setAdapter(songQueueRecyclerAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(songQueueRecyclerAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerSongsView);
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

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
