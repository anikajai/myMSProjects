package edu.usc.csci571.searchonfb;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class GroupTabFragment extends Fragment {

    private String key;
    private String resultData;
    private List<Info> totalList = new ArrayList<Info>();
    private List<Info> part1 = new ArrayList<Info>();
    private List<Info> part2 = new ArrayList<Info>();
    private List<Info> part3 = new ArrayList<Info>();
    private Button nextButton;
    private Button previousButton;
    private int currentPage = 0;
    int maxPage = 2;
    int size = 0;
    private GroupViewAdapter groupAdapter;

    private void setDefaults() {
        currentPage = 0;
        maxPage = 2;
        size = 0;
        totalList.clear();
        part1.clear();
        part2.clear();
        part3.clear();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (groupAdapter != null) {
            groupAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_group, container, false);
        key = "";
        Bundle args = getArguments();
        nextButton = (Button) view.findViewById(R.id.nextButtonGroup);
        previousButton = (Button) view.findViewById(R.id.previousButtonGroup);
        boolean isFavView = false;
        if (args != null && args.containsKey("text")) {
            key = args.getString("text");
        } else {
            isFavView = true;
        }
        final ListView listView = (ListView) view.findViewById(R.id.group_list_view);
        if (isFavView) {
            previousButton.setVisibility(View.INVISIBLE);
            nextButton.setVisibility(View.INVISIBLE);
            setFavList();
            groupAdapter = new GroupViewAdapter(getActivity(), totalList);
            listView.setAdapter(groupAdapter);
        } else {
            TextView textView = new TextView(getActivity());
            textView.setText(getArguments().getString("text"));
            resultData = Constants.CLOUD_URL + "key=" + key + "&searchType=group";
            JsonObjectRequest req = new JsonObjectRequest(Method.GET,
                    resultData, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String result = response.toString();
                        JSONObject parentObject = new JSONObject(result);
                        JSONArray parentArray = parentObject.getJSONArray("data");
                        for (int i = 0; i < parentArray.length(); i++) {
                            Info row = new Info();
                            JSONObject finalObject = parentArray.getJSONObject(i);
                            JSONObject picture = finalObject.getJSONObject("picture");
                            row.id = finalObject.getString("id");
                            row.name = finalObject.getString("name");
                            row.url = picture.getJSONObject("data").getString("url");
                            totalList.add(row);
                        }
                        int size = totalList.size();
                        if (size <= 10) {
                            maxPage = 0;
                            for (int index = 0; index < size; index++) {
                                part1.add(totalList.get(index));
                            }
                        }
                        if (size > 10 && size <= 20) {
                            maxPage = 1;
                            for (int i = 0; i < 10; i++) {
                                part1.add(totalList.get(i));
                            }
                            for (int i = 10; i < size; i++) {
                                part2.add(totalList.get(i));
                            }
                        }
                        if (size > 20) {
                            maxPage = 2;
                            for (int i = 0; i < 10; i++) {
                                part1.add(totalList.get(i));

                            }
                            for (int i = 10; i < 20; i++) {
                                part2.add(totalList.get(i));
                            }

                            for (int i = 20; i < size; i++) {
                                part3.add(totalList.get(i));
                            }
                        }
                        if (currentPage == 0) {
                            ListView listView = (ListView) view.findViewById(R.id.group_list_view);
                            groupAdapter = new GroupViewAdapter(getActivity(), part1);
                            listView.setAdapter(groupAdapter);
                            previousButton.setEnabled(false);
                        }

                    } catch (JSONException e) {
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });

            nextButton.setOnClickListener(new View.OnClickListener() {

                                              @Override
                                              public void onClick(View v) {
                                                  toggleButtons();
                                                  currentPage = currentPage + 1;
                                                  ListView listView = (ListView) view.findViewById(R.id.group_list_view);
                                                  if (currentPage == 1) {
                                                      groupAdapter = new GroupViewAdapter(getActivity(), part2);
                                                      listView.setAdapter(groupAdapter);
                                                  }
                                                  if (currentPage == 2) {
                                                      groupAdapter = new GroupViewAdapter(getActivity(), part3);
                                                      listView.setAdapter(groupAdapter);
                                                  }
                                              }
                                          }
            );

            previousButton.setOnClickListener(new View.OnClickListener() {

                                               @Override
                                               public void onClick(View v) {
                                                   currentPage = currentPage - 1;
                                                   ListView listView = (ListView) view.findViewById(R.id.group_list_view);
                                                   if (currentPage == 0) {
                                                       previousButton.setEnabled(false);
                                                       groupAdapter = new GroupViewAdapter(getActivity(), part1);
                                                   }
                                                   if (currentPage == 1) {
                                                       groupAdapter = new GroupViewAdapter(getActivity(), part2);
                                                   }
                                                   listView.setAdapter(groupAdapter);
                                                   toggleButtons();
                                               }
                                           }
            );
            ApplicationCtrl.getInstance().addToRequestQueue(req);
        }
        return view;
    }


    private void toggleButtons() {
        if (currentPage == 0) {
            previousButton.setEnabled(false);
            nextButton.setEnabled(true);
        } else if (currentPage == maxPage) {
            if (maxPage == 0) {
                nextButton.setEnabled(false);
                previousButton.setEnabled(false);
            } else {
                nextButton.setEnabled(false);
                previousButton.setEnabled(true);
            }
        } else {
            nextButton.setEnabled(true);
            previousButton.setEnabled(true);
        }
    }

    private void setFavList() {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("favSave", MODE_PRIVATE);
        String typeJson = sharedPreferences.getString("group", "");
        Type typeClass = new TypeToken<Map<String, Info>>() {
        }.getType();
        Map<String, Info> idEntryMap = gson.fromJson(typeJson, typeClass);
        for (String key : idEntryMap.keySet()) {
            Info resultRow = idEntryMap.get(key);
            totalList.add(resultRow);
        }
    }

}





