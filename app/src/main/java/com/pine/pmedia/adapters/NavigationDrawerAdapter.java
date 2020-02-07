package com.pine.pmedia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pine.pmedia.R;
import com.pine.pmedia.activities.MainActivity;
import com.pine.pmedia.fragments.AboutUsFragment;
import com.pine.pmedia.fragments.FavoriteFragment;
import com.pine.pmedia.fragments.SettingFragment;

import java.util.ArrayList;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder> {

    private ArrayList<String> _contentList;
    private int[] _getImages;
    private Context _context;

    public NavigationDrawerAdapter(ArrayList<String> _contentList, int[] _getImages, Context _context) {
        this._contentList = _contentList;
        this._getImages = _getImages;
        this._context = _context;
    }

    @NonNull
    @Override
    public NavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutInflater = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_custum_navigation_drawer, parent, false);
        return new NavViewHolder(layoutInflater);
    }

    @Override
    public void onBindViewHolder(@NonNull NavViewHolder holder, final int position) {
        holder.icon_Get.setBackgroundResource(_getImages[position]);
        holder.text_Get.setText(_contentList.get(position));

        holder.contentHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(position == 0) {
////                    ((MainActivity)_context).getSupportFragmentManager()
////                            .beginTransaction()
////                            .replace(R.id.detail_fragment, new MainScreenFragment())
////                            .commit();
//                } else if(position == 1) {
//                    ((MainActivity)_context).getSupportFragmentManager()
//                            .beginTransaction()
//                            .replace(R.id.detail_fragment, new FavoriteFragment())
//                            .commit();
//                } else if(position == 2) {
//                    ((MainActivity)_context).getSupportFragmentManager()
//                            .beginTransaction()
//                            .replace(R.id.detail_fragment, new SettingFragment())
//                            .commit();
//                } else {
//                    ((MainActivity)_context).getSupportFragmentManager()
//                            .beginTransaction()
//                            .replace(R.id.detail_fragment, new AboutUsFragment())
//                            .commit();
//                }

                MainActivity.drawerLayout.closeDrawers();
            }
        });
    }

    @Override
    public int getItemCount() {
        return _contentList.size();
    }

    public class NavViewHolder extends RecyclerView.ViewHolder {

        public ImageView icon_Get;
        public TextView text_Get;
        public RelativeLayout contentHolder;

        public NavViewHolder(@NonNull View itemView) {
            super(itemView);

            icon_Get = itemView.findViewById(R.id.icon_drawer);
            text_Get = itemView.findViewById(R.id.text_drawer);
            contentHolder = itemView.findViewById(R.id.nav_drawer_item_content_holder);
        }
    }
}
