package edu.usc.csci571.searchonfb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
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

public class PlaceTabFragment extends Fragment {

    private String key;
    private String resultData;
    private List<Info> totalList = new ArrayList<Info>();
    private List<Info> part1 = new ArrayList<Info>();
    private List<Info> part2 = new ArrayList<Info>();
    private List<Info> part3 = new ArrayList<Info>();
    private Button nextButton;
    private Button previousButton;
    private int currentPage = 0;
    private int maxPage = 2;
    private String lattitude = "";
    private String longitude = "";

    private PlaceViewAdapter placeAdapter;

    @Override
    public void onResume() {
        super.onResume();
        if (placeAdapter != null) {
            placeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lattitude = Double.toString(location.getLatitude());
                longitude = Double.toString(location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
        } else {
            locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
        }
    }

    private void setDefaults() {
        currentPage = 0;
        maxPage = 2;
        totalList.clear();
        part1.clear();
        part2.clear();
        part3.clear();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_place, container, false);
        key = "";
        Bundle args = getArguments();
        boolean isFavView = false;
        if (args != null && args.containsKey("text")) {
            key = args.getString("text");
        } else {
            isFavView = true;
        }
        setDefaults();
        nextButton = (Button) view.findViewById(R.id.nextButtonplace);
        previousButton = (Button) view.findViewById(R.id.previousButtonPlace);
        setLocation();
        final ListView listView = (ListView) view.findViewById(R.id.place_list_view);
        if (isFavView) {
            previousButton.setVisibility(View.INVISIBLE);
            nextButton.setVisibility(View.INVISIBLE);
            setFavList();
            placeAdapter = new PlaceViewAdapter(getActivity(), totalList);
            listView.setAdapter(placeAdapter);
        } else {
            if (lattitude != null && longitude != null) {
                resultData = Constants.CLOUD_URL + "key=" + key + "&searchType=place";
            }
            TextView textView = new TextView(getActivity());
            textView.setText(getArguments().getString("text"));
            JsonObjectRequest req = new JsonObjectRequest(Method.GET,
                    resultData, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String json = response.toString();

                        JSONObject parentObject = new JSONObject(json);
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
                    } catch (JSONException e) {

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
                        previousButton.setEnabled(false);
                        ListView listView = (ListView) view.findViewById(R.id.place_list_view);
                        placeAdapter = new PlaceViewAdapter(getActivity(), part1);
                        listView.setAdapter(placeAdapter);
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
                                                  ListView listView = (ListView) view.findViewById(R.id.place_list_view);
                                                  if (currentPage == 1) {
                                                      placeAdapter = new PlaceViewAdapter(getActivity(), part2);
                                                  }
                                                  if (currentPage == 2) {
                                                      placeAdapter = new PlaceViewAdapter(getActivity(), part3);
                                                  }
                                                  listView.setAdapter(placeAdapter);
                                                  toggleButtons();
                                              }
                                          }
            );

            previousButton.setOnClickListener(new View.OnClickListener() {

                                                  @Override
                                                  public void onClick(View v) {
                                                      currentPage = currentPage - 1;
                                                      ListView listView = (ListView) view.findViewById(R.id.place_list_view);
                                                      if (currentPage == 0) {
                                                          previousButton.setEnabled(false);
                                                          placeAdapter = new PlaceViewAdapter(getActivity(), part1);
                                                          listView.setAdapter(placeAdapter);
                                                      }
                                                      if (currentPage == 1) {
                                                          placeAdapter = new PlaceViewAdapter(getActivity(), part2);
                                                      }
                                                      listView.setAdapter(placeAdapter);
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
        String typeJson = sharedPreferences.getString("place", "");
        Type typeClass = new TypeToken<Map<String, Info>>() {
        }.getType();
        Map<String, Info> idEntryMap = gson.fromJson(typeJson, typeClass);
        for (String key : idEntryMap.keySet()) {
            Info resultRow = idEntryMap.get(key);
            totalList.add(resultRow);
        }
    }
}





