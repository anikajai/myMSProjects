package edu.usc.csci571.searchonfb;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        final EditText searchField = (EditText) view.findViewById(R.id.searchArea);
        Button searchButton = (Button) view.findViewById(R.id.searchButton);
        Button clearButton = (Button) view.findViewById(R.id.clear);
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Editable editable = searchField.getText();
                if (editable == null) {
                    Toast.makeText(getActivity(), Constants.MISSING_KEYWORD, Toast.LENGTH_SHORT).show();
                } else {
                    String searchValue = searchField.getText().toString();
                    if (null == searchValue || searchValue.isEmpty()) {
                        Toast.makeText(getActivity(), Constants.MISSING_KEYWORD, Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(getActivity(), ResultActivity.class);
                        intent.putExtra("keyword", searchValue);
                        startActivity(intent);
                    }
                }

            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() { //Clear Button

            @Override
            public void onClick(View v) {
                searchField.setText("");
            }
        });
        return view;
    }
}
