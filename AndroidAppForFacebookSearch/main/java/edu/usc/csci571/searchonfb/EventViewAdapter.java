package edu.usc.csci571.searchonfb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;


public class EventViewAdapter extends ArrayAdapter<List> {

    private final Activity context;
    private final List<Info> listInfo;
    SharedPreferences sharedPreferences;

    public EventViewAdapter(Activity context, List listInfo) {
        super(context, R.layout.fragment_event_list, listInfo);
        this.listInfo = listInfo;
        this.context = context;
        sharedPreferences = context.getSharedPreferences("favSave", Context.MODE_PRIVATE);
    }

    public View getView(final int position, View view, final ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.fragment_event_list, null, true);
        final String id = listInfo.get(position).id;
        final boolean favoriteYes = isFavorite(id);
        ImageView favImage = (ImageView) rowView.findViewById(R.id.fav_event_imageView);
        if (favoriteYes) {
            favImage.setImageResource(R.mipmap.favorites_on);
        } else {
            favImage.setImageResource(R.drawable.favorites_off);
        }
        TextView title = (TextView) rowView.findViewById(R.id.event_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.iconEvent);
        title.setText(listInfo.get(position).name);
        Picasso.with(this.getContext()).load(listInfo.get(position).url).into(imageView);
        ImageView detailsImageView = (ImageView) rowView.findViewById(R.id.details_event_imageView);
        detailsImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.favFragment.selectedTabId = 2;
                DetailsActivity act = new DetailsActivity();
                Intent intent = act.getIntent(context, listInfo, position, favoriteYes);
                context.startActivity(intent);
            }
        });
        return rowView;

    }

    ;

    private boolean isFavorite(String id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("favSave", Context.MODE_PRIVATE);
        String typeJson = sharedPreferences.getString("event", "");
        Type _class = new TypeToken<Map<String, Info>>() {
        }.getType();
        Gson gson = new Gson();
        Map<String, Info> map = gson.fromJson(typeJson, _class);
        Info info = map.get(id);
        return info == null ? false : true;
    }
}
