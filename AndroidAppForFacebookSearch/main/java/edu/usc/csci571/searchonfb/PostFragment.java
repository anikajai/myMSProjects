package edu.usc.csci571.searchonfb;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

public class PostFragment extends Fragment {

    private List<Post> posts = new ArrayList<Post>();
    private String postName;
    private String imgUrl;


    public PostFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_post, container, false);
        Bundle bundle = this.getArguments();
        String id = bundle.getString("id");
        postName = bundle.getString("postName");
        imgUrl = bundle.getString("imgUrl");

        String resultData= Constants.CLOUD_URL + "id=" + id;
        final ListView listView = (ListView) view.findViewById(R.id.post_list_view);
        final TextView emptyTextView = (TextView) view.findViewById(R.id.post_no_found);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                resultData, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    Log.v("response",response.toString());
                    String finalJson= response.toString();

                    JSONObject parentObject= new JSONObject(finalJson);
                    JSONObject postParent = parentObject.getJSONObject("posts");
                    if (postParent != null) {
                        JSONArray postParentArray = postParent.getJSONArray("data");

                        for (int postIndex = 0; postIndex < postParentArray.length(); postIndex++) {
                            Post post = new Post();
                            JSONObject aPost = postParentArray.getJSONObject(postIndex);
                            String message = aPost.getString("message");
                            post.setMessage(message);
                            String createdTime = aPost.getString("created_time");
                            post.setCreatedDate(createdTime);
                            posts.add(post);
                        }
                        emptyTextView.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                    } else{
                        emptyTextView.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                    }
                    PostViewAdapter postViewAdapter = new PostViewAdapter(getActivity(), posts);
                    postViewAdapter.setAdapter(posts, postName, imgUrl);
                    listView.setAdapter(postViewAdapter);
                } catch (JSONException exception) {
                    Log.e("Exception:", "" + exception.getMessage());
                    emptyTextView.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "" + error.getMessage());
                emptyTextView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }
        });
        ApplicationCtrl.getInstance().addToRequestQueue(req);
        return view;
    }
}
