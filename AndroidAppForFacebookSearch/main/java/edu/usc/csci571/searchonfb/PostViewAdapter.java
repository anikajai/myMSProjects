package edu.usc.csci571.searchonfb;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PostViewAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private List<Post> posts;
    private String apost;
    private String url;

    public PostViewAdapter(Activity context, List posts) {
        super(context, R.layout.fragment_post_list, posts);
        this.context=context;
    }

    public void setAdapter(List posts, String itemName, String postImageUrl) {
        this.posts = posts;
        this.apost = itemName;
        this.url = postImageUrl;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.fragment_post_list, null,true);

        Post post = posts.get(position);
        TextView postDate = (TextView) rowView.findViewById(R.id.id_post_date);
        postDate.setText(post.getCreatedDate());

        TextView message = (TextView) rowView.findViewById(R.id.id_post_message);
        message.setText(post.getMessage());

        TextView title = (TextView) rowView.findViewById(R.id.id_post_name);
        title.setText(apost);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.id_post_image);
        Picasso.with(this.context).load(url).into(imageView);
        return rowView;
    };
}
