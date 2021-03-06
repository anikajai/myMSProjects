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


public class PlaceViewAdapter extends ArrayAdapter<List> {

    private final Activity context;
    private final List<Info> listInfo;

    public PlaceViewAdapter(Activity context, List listInfo) {
        super(context, R.layout.fragment_place_list, listInfo);
        this.listInfo = listInfo;
        this.context = context;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.fragment_place_list, null, true);
        final String id = listInfo.get(position).id;
        final boolean favoriteYes = isFavorite(id);
        ImageView favImage = (ImageView) rowView.findViewById(R.id.fav_place_imageView);
        if (favoriteYes) {
            favImage.setImageResource(R.mipmap.favorites_on);
        } else {
            favImage.setImageResource(R.drawable.favorites_off);
        }
        TextView title = (TextView) rowView.findViewById(R.id.place_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.iconPlace);
        title.setText(listInfo.get(position).name);
        Picasso.with(this.getContext()).load(listInfo.get(position).url).into(imageView);
        ImageView detailsImageView = (ImageView) rowView.findViewById(R.id.details_place_imageView);
        detailsImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.favFragment.selectedTabId = 3;
                DetailsActivity act = new DetailsActivity();
                Intent intent = act.getIntent(context, listInfo, position, favoriteYes);
                context.startActivity(intent);
            }
        });
        return rowView;
    }

    private boolean isFavorite(String id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("favSave", Context.MODE_PRIVATE);
        String typeJson = sharedPreferences.getString("place", "");
        Gson gson = new Gson();
        Type _class = new TypeToken<Map<String, Info>>() {
        }.getType();
        Map<String, Info> idEntryMap = gson.fromJson(typeJson, _class);
        Info info = idEntryMap.get(id);
        return info == null ? false : true;
    }
}
