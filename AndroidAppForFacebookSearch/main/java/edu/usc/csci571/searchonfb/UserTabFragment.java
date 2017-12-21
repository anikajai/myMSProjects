package edu.usc.csci571.searchonfb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class UserTabFragment extends Fragment {

    private String searchKeyword;
    private String resultData;
    private Button nextButton;
    private Button previousButton;
    private int currentPage = 0;
    int maxPage = 2;
    private UserViewAdapter userAdapter;
    private List<Info> totalList = new ArrayList<Info>();
    private List<Info> firstPart = new ArrayList<Info>();
    private List<Info> secondPart = new ArrayList<Info>();
    private List<Info> thirdPart = new ArrayList<Info>();

    public UserTabFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userAdapter != null) {
            userAdapter.notifyDataSetChanged();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_user, container, false);
        searchKeyword = "";
        Bundle args = getArguments();
        final ListView listView = (ListView) view.findViewById(R.id.user_list_view);
        setDefaults();
        nextButton = (Button) view.findViewById(R.id.nextButtonUser);
        previousButton = (Button) view.findViewById(R.id.previousButtonUser);
        boolean isFavView = false;
        if (args != null && args.containsKey("text")) {
            searchKeyword = args.getString("text");
        } else {
            isFavView = true;
        }
        if (isFavView) {
            previousButton.setVisibility(View.INVISIBLE);
            nextButton.setVisibility(View.INVISIBLE);
            setFavList();
            userAdapter = new UserViewAdapter(getActivity(), totalList);
            listView.setAdapter(userAdapter);
        } else {
            TextView textView = new TextView(getActivity());
            textView.setText(getArguments().getString("text"));
            resultData = Constants.CLOUD_URL + "key=" + searchKeyword + "&searchType=user";

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET,
                    resultData, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String finalJson = response.toString();
                        JSONObject parentObject = new JSONObject(finalJson);
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
                            for (int index = 0; index < size; index++) {
                                firstPart.add(totalList.get(index));
                            }
                            maxPage = 0;
                        }
                        if (size > 10 && size <= 20) {
                            for (int i = 0; i < 10; i++) {
                                firstPart.add(totalList.get(i));
                            }
                            for (int i = 10; i < size; i++) {
                                secondPart.add(totalList.get(i));
                            }
                            maxPage = 1;
                        }
                        if (size > 20) {
                            for (int i = 0; i < 10; i++) {
                                firstPart.add(totalList.get(i));

                            }
                            for (int i = 10; i < 20; i++) {
                                secondPart.add(totalList.get(i));
                            }

                            for (int i = 20; i < size; i++) {
                                thirdPart.add(totalList.get(i));
                            }
                            maxPage = 2;
                        }
                        if (currentPage == 0) {
                            ListView listView = (ListView) view.findViewById(R.id.user_list_view);
                            previousButton.setEnabled(false);
                            userAdapter = new UserViewAdapter(getActivity(), firstPart);
                            listView.setAdapter(userAdapter);
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
                                                  currentPage = currentPage + 1;
                                                  ListView listView = (ListView) view.findViewById(R.id.user_list_view);
                                                  if (currentPage == 1) {
                                                      userAdapter = new UserViewAdapter(getActivity(), secondPart);
                                                      }
                                                  if (currentPage == 2) {
                                                      userAdapter = new UserViewAdapter(getActivity(), thirdPart);
                                                  }
                                                  listView.setAdapter(userAdapter);
                                                  toggleButtons();
                                              }
                                          }
            );

            previousButton.setOnClickListener(new View.OnClickListener() {

                                                  @Override
                                                  public void onClick(View v) {
                                                      currentPage = currentPage - 1;
                                                      ListView listView = (ListView) view.findViewById(R.id.user_list_view);
                                                      if (currentPage == 0) {
                                                          previousButton.setEnabled(false);
                                                          userAdapter = new UserViewAdapter(getActivity(), firstPart);
                                                      }
                                                      if (currentPage == 1) {
                                                          userAdapter = new UserViewAdapter(getActivity(), secondPart);
                                                          }
                                                      listView.setAdapter(userAdapter);
                                                      toggleButtons();
                                                  }
                                              }
            );
            ApplicationCtrl.getInstance().addToRequestQueue(jsonObjReq);
        }
        return view;
    }

    private void setDefaults() {
        currentPage = 0;
        maxPage = 2;
        totalList.clear();
        firstPart.clear();
        secondPart.clear();
thirdPart.clear();
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
        String typeJson = sharedPreferences.getString("user", "");
        Type typeClass = new TypeToken<Map<String, Info>>() {
        }.getType();
        Map<String, Info> idEntryMap = gson.fromJson(typeJson, typeClass);
        for (String key : idEntryMap.keySet()) {
            Info resultRow = idEntryMap.get(key);
            totalList.add(resultRow);
        }
    }
}