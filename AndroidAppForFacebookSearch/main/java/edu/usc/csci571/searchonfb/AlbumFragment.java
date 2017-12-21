package edu.usc.csci571.searchonfb;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AlbumFragment extends Fragment {

    private List<Album> albums = new ArrayList<Album>();

    public AlbumFragment() {
        // Do nothing
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_album, container, false);
        final ExpandableListView expandableListView = (ExpandableListView) view.findViewById(R.id.album_list_view);
        final TextView noAlbumView = (TextView) view.findViewById(R.id.album_no_found);
        Bundle bundle = this.getArguments();
        String id = bundle.getString("id");
        String cloudUrl = Constants.CLOUD_URL + "id=" + id;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                cloudUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String finalJson = response.toString();
                    JSONObject parentObject = new JSONObject(finalJson);
                    JSONObject albumParent = parentObject.getJSONObject("albums");
                    if (albumParent != null) {
                        JSONArray albumParentArray = albumParent.getJSONArray("data");
                        for (int albumIndex = 0; albumIndex < albumParentArray.length(); albumIndex++) {
                            Album anAlbum = new Album();
                            JSONObject albumJSON = albumParentArray.getJSONObject(albumIndex);
                            String albumName = albumJSON.getString("name");
                            anAlbum.setName(albumName);
                            JSONObject photosObject = albumJSON.getJSONObject("photos");
                            if (null != photosObject) {
                                JSONArray photos = photosObject.getJSONArray("data");
                                for (int photoIndex = 0; photoIndex < photos.length(); photoIndex++) {
                                    JSONObject anImage = photos.getJSONObject(photoIndex);
                                    String picture = anImage.getString("picture");
                                    List<String> imageSrcs = anAlbum.getImageSources();
                                    imageSrcs.add(picture);
                                }
                            }
                            albums.add(anAlbum);
                        }
                        expandableListView.setVisibility(View.VISIBLE);
                        noAlbumView.setVisibility(View.GONE);
                    } else {
                        noAlbumView.setVisibility(View.VISIBLE);
                        expandableListView.setVisibility(View.GONE);
                    }
                    expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                        int prevPosition = 10;

                        @Override
                        public void onGroupExpand(int groupPosition) {
                            // for group collapsing.
                            if (groupPosition != prevPosition) {
                                expandableListView.collapseGroup(prevPosition);
                            }
                            prevPosition = groupPosition;
                        }
                    });
                    AlbumViewAdapter albumViewAdapter = new AlbumViewAdapter(getActivity(), albums);
                    expandableListView.setAdapter(albumViewAdapter);
                } catch (JSONException jsonException) {
                    Log.d("Exception", jsonException.getMessage());
                    noAlbumView.setVisibility(View.VISIBLE);
                    expandableListView.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", "Error: " + error.getMessage());
                noAlbumView.setText("No Data Found");
                noAlbumView.setVisibility(View.VISIBLE);
                expandableListView.setVisibility(View.GONE);
            }
        });
        ApplicationCtrl.getInstance().addToRequestQueue(req);
        return view;
    }
}
