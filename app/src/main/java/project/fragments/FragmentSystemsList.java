package project.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.activities.ActivityMain;
import project.activities.CAppCompatActivity;
import project.adapters.AdapterSystemsRecycler;
import project.connection.Commands;
import project.custom.CustomToast;
import project.helper.HelperView;
import project.structures.StructSystem;

public class FragmentSystemsList extends Fragment {
    private boolean granted = false;
    public static OnAccessPermissionListener permissionListener;
    private RecyclerView lstSystems;
    private ViewGroup loader;
    private TextView txtDesc;
    private AdapterSystemsRecycler adapter;
    private FusedLocationProviderClient client;
    private LocationRequest locationRequest;
    private ArrayList<StructSystem> systems ;
    private double myLat;
    private double myLng;
    private boolean validLocation;
    private ArrayList<Double> distances;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_systems_list, container, false);

        root.setFocusableInTouchMode(true);
        root.requestFocus();

        granted = false;
        validLocation = false;
        txtDesc = (TextView)root.findViewById(R.id.txtDesc);
        ActivityMain.mainToolbar.setVisibility(View.GONE);
        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar2);
        txtDesc.setVisibility(View.GONE);
        systems = new ArrayList<StructSystem>();
//        ((CAppCompatActivity) getActivity()).getSupportActionBar().hide();
//        ((CAppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        SearchView searchView = (SearchView) root.findViewById(R.id.searchView);
        setUpSearchView(searchView);
        TextView txtBack = (TextView) root.findViewById(R.id.txtBack);
        adapter = new AdapterSystemsRecycler(systems);
        lstSystems = (RecyclerView) root.findViewById(R.id.lstSystems);
        loader = (ViewGroup) root.findViewById(R.id.loader);

        findLocation();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String filter) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.getFilter().filter(filter);
                    }
                }).start();

                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelperView.hideKeyboard(App.getCurrentActivity());
                App.getCurrentActivity().onBackPressed();
            }
        });


        root.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                Fragment currentFragment = ActivityMain.fragmentManager.findFragmentById(R.id.fragmentContainer);
                if (currentFragment instanceof FragmentSystemsList) {
                    HelperView.hideKeyboard(App.getCurrentActivity());
                    ActivityMain.mainToolbar.setVisibility(View.VISIBLE);
                    App.getCurrentActivity().onBackPressed();
                }
                return true;
            }
        });

        return root;
    }

    private class RtlGridLayoutManager extends GridLayoutManager {

        public RtlGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        public RtlGridLayoutManager(Context context, int spanCount) {
            super(context, spanCount);
        }

        public RtlGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
        }

        @Override
        protected boolean isLayoutRTL() {
            return true;
        }
    }


    private void setUpSearchView(SearchView searchView) {
        TextView txt = searchView.findViewById(R.id.search_src_text);
        txt.setTypeface(App.persianFont);
        SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setTextColor(getResources().getColor(R.color.colorPrimary));
        searchAutoComplete.setHint(getString(R.string.search));
        searchAutoComplete.setHintTextColor(Color.parseColor("#555B8349"));

        ImageView searchIcon = searchView.findViewById(R.id.search_button);
        ImageView searchClose = searchView.findViewById(R.id.search_close_btn);
        searchIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.search));
        searchClose.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.close));

    }

    private void getSystems() {
        systems.clear();
        distances = new ArrayList<>();
        distances.clear();
        lstSystems.setVisibility(View.GONE);
        txtDesc.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    JSONArray systemsArray = new JSONArray(data);
                    for (int i = 0; i < systemsArray.length(); i++) {
                        JSONObject system = systemsArray.getJSONObject(i);
                        StructSystem item = new StructSystem();
                        item.isExpanded = false;
                        item.id = system.getInt("id");
                        item.coverUrl = system.getString("image");
                        item.name = system.getString("name");
                        item.address = system.getString("address");
                        item.state = system.getString("state");
                        item.cityName = system.getJSONObject("city").getString("name");
                        item.processing = false;
                        if (!validLocation) {
                            systems.add(item);
                        } else {
                            txtDesc.setVisibility(View.VISIBLE);
                            double lat = system.getDouble("lat");
                            double lng = system.getDouble("lng");
                            item.lat = lat;
                            item.lng = lng;
                            double distance = Math.sqrt(Math.pow((myLat - lat), 2) + Math.pow((myLng - lng), 2));
                            boolean inserted = false;
                            for (int index = 0; index < distances.size(); index++) {
                                if (distance < distances.get(index)){
                                    systems.add(index,item);
                                    distances.add(index,distance);
                                    inserted = true;
                                    break;
                                }
                            }
                            if(!inserted){
                                systems.add(item);
                                distances.add(distance);
                            }
                        }

                    }
                    adapter = new AdapterSystemsRecycler(systems);
                    //RtlGridLayoutManager gridLayoutManager = new RtlGridLayoutManager(App.getContext(), 3);
//                    lstSystems.setLayoutManager(gridLayoutManager);
                    lstSystems.setLayoutManager(new LinearLayoutManager(App.getContext()));
                    lstSystems.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    lstSystems.setVisibility(View.VISIBLE);
                    loader.setVisibility(View.GONE);
                } catch (JSONException e) {
                    CustomToast.showToast(getString(R.string.oldVersionError));
                    loader.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {
                CustomToast.showToast(getString(R.string.connectionError));
                loader.setVisibility(View.GONE);
            }
        }).getSystems();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ActivityMain.mainToolbar.setVisibility(View.VISIBLE);
        HelperView.hideKeyboard(App.getCurrentActivity());
    }

    private void findLocation() {
        validLocation = false;
        permissionListener = new OnAccessPermissionListener() {
            @Override
            public void onAccess(boolean isAccepted) {
                if(granted){
                    return;
                }
                if (isAccepted) {
                    granted = true;
                    checkLocation();
                    client = new FusedLocationProviderClient(App.getCurrentActivity());
                    locationRequest = new LocationRequest();
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    locationRequest.setFastestInterval(4000);
                    locationRequest.setInterval(10000);
                    LocationServices.getFusedLocationProviderClient(App.getCurrentActivity())
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    LocationServices.getFusedLocationProviderClient(App.getCurrentActivity())
                                            .removeLocationUpdates(this);
                                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                                        int lastIndex = locationResult.getLocations().size() - 1;
                                        myLat = locationResult.getLocations().get(lastIndex).getLatitude();
                                        myLng = locationResult.getLocations().get(lastIndex).getLongitude();
                                        validLocation = true;
                                    } else {
                                        CustomToast.showToast(App.getContext().getString(R.string.openLocation));
                                    }

                                    getSystems();
                                }
                            }, Looper.getMainLooper());

                }else{
                    getSystems();
                }
            }
        };
        new ActivityMain().requestAccessCoarseLocation();
    }

    public interface OnAccessPermissionListener {
        void onAccess(boolean isAccepted);
    }

    private void checkLocation(){
        LocationManager lm = (LocationManager)App.getContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(App.getContext())
                    .setTitle(R.string.gps_network_not_enabled)
                    .setMessage(R.string.app_need_gps)
                    .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                        }
                    })
                    .setPositiveButton(R.string.open_location_settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            App.getContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    getSystems();
                }
            }).show();


        }
    }

}
