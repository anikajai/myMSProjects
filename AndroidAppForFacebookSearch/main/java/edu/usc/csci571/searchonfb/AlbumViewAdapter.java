package edu.usc.csci571.searchonfb;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AlbumViewAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private List<Album> albums;

    public AlbumViewAdapter(Activity context, List<Album> albums) {
        this.context = context;
        this.albums = albums;
    }

    @Override
    public int getGroupCount() {
        return this.albums.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return albums.get(groupPosition).getImageSources().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return albums.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return albums.get(groupPosition).getImageSources().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.fragment_album_list, parent, false);
        }
        setAlbum(groupPosition, view);
        return view;
    }

    private void setAlbum(int groupPosition, View convertView) {
        String headerTitle = ((Album) getGroup(groupPosition)).getName();
        CheckedTextView albumName = (CheckedTextView) convertView
                .findViewById(R.id.album_name);
        albumName.setText(headerTitle);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.fragment_album_images, null);
        }
        ImageView albumImageView = (ImageView) view.findViewById(R.id.id_image1);
        Picasso.with(this.context).load(albums.get(groupPosition).getImageSources().get(childPosition)).into(albumImageView);
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
