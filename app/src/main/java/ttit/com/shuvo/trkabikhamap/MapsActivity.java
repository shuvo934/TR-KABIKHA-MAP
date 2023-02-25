package ttit.com.shuvo.trkabikhamap;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    GeoJsonLayer layer = null;

    public ArrayList<CountCheck> countChecks;

    public static View decorView;
    public static Window window;
    public List<String> divisions;
    ArrayAdapter<String> spinnerArrayAdapter;
    public List<String> districts;
    ArrayAdapter<String> districtArrayAdapter;
    public List<String> upazillas;
    ArrayAdapter<String> upazilaArrayAdapter;

    public ArrayList<Division> allDivisions;
    public ArrayList<District> allDistricts;
    public ArrayList<Upazila> allUpazilas;

    public ArrayList<NameAndCount> nameAndCounts;

    WaitProgress waitProgress = new WaitProgress();

    private Boolean conn = false;
//    private Boolean connected = false;
//
//    private Boolean infoCon = false;
//    private Boolean infoConnected = false;
//
//    private Boolean disCon = false;
//    private Boolean disConeected = false;
//
//    private Boolean upaCon = false;
//    private Boolean upaConnected = false;
//
//    private Connection connection;

    CheckBox div, dis, upazila;
    LinearLayout divLay, disLay, upaLay, info;

    Spinner selectDiv, selectDis, selectUpa;

    TextView loc, pro;

    String divisonName = "";
    String districtName = "";
    String upazilaName = "";
    String Total_Count = "";

    String div_id = "";
    String dis_id = "";
    String upa_id = "";

//    String layerDiv_id = "";
//    String layerDis_id = "";
//    String layerUpa_id = "";

    List<Marker> markers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        selectDiv = findViewById(R.id.spinner_div);
        selectDis = findViewById(R.id.spinner_dis);
        selectUpa = findViewById(R.id.spinner_upaz);

        loc = findViewById(R.id.location_info);
        pro = findViewById(R.id.project_info);

        nameAndCounts = new ArrayList<>();


        countChecks = new ArrayList<>();
        window = getWindow();
        decorView = getWindow().getDecorView();

//        if (Build.VERSION.SDK_INT < 16) {
//
//            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        }
//        else {
//
//            // Hide Status Bar.
//            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//            decorView.setSystemUiVisibility(uiOptions);
//        }

        divLay = findViewById(R.id.division_layout);
        disLay = findViewById(R.id.distric_layout);
        upaLay = findViewById(R.id.upazila_layout);
        info = findViewById(R.id.linear_info);

        div = findViewById(R.id.division_checked);
        dis = findViewById(R.id.district_checked);
        upazila = findViewById(R.id.upazila_checked);

        allDivisions = new ArrayList<>();
        allDistricts = new ArrayList<>();
        allUpazilas = new ArrayList<>();

        divisions = new ArrayList<>();
        districts = new ArrayList<>();
        upazillas = new ArrayList<>();

        divisions.add("Select Division");
        districts.add("Select District");
        upazillas.add("Select Upazila");

        spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.item_country,divisions){
            @Override
            public boolean isEnabled(int position){
                // Disable the first item from Spinner
                // First item will be use for hint
                return position != 0;
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(R.id.tvCountry);
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(R.layout.item_country);
        selectDiv.setAdapter(spinnerArrayAdapter);



        // District Spinner
        districtArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.item_country,districts){
            @Override
            public boolean isEnabled(int position){
                // Disable the first item from Spinner
                // First item will be use for hint
                return position != 0;
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(R.id.tvCountry);
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        districtArrayAdapter.setDropDownViewResource(R.layout.item_country);
        selectDis.setAdapter(districtArrayAdapter);



        // Upazila Spinner
        upazilaArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.item_country,upazillas){
            @Override
            public boolean isEnabled(int position){
                // Disable the first item from Spinner
                // First item will be use for hint
                return position != 0;
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(R.id.tvCountry);
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        upazilaArrayAdapter.setDropDownViewResource(R.layout.item_country);
        selectUpa.setAdapter(upazilaArrayAdapter);


// Selecting Division
        selectDiv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //districts = new ArrayList<>();
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    divisonName = (String) parent.getItemAtPosition(position);
                    String name_id = "";
                    districts = new ArrayList<>();
                    upazillas = new ArrayList<>();
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + divisonName, Toast.LENGTH_SHORT)
                            .show();
                    for (int i = 0; i < allDivisions.size(); i++) {
                        if (divisonName.equals(allDivisions.get(i).getDiv_name())) {
                            name_id = allDivisions.get(i).getDiv_id();
                            div_id = allDivisions.get(i).getDiv_id();
                            System.out.println(div_id);
                        }
                    }

                    districts.add("Select District");
                    upazillas.add("Select Upazila");
                    for (int j = 0; j < allDistricts.size(); j++) {
                        if (name_id.equals(allDistricts.get(j).getDist_div_id())) {
                            districts.add(allDistricts.get(j).getDist_name());
                            System.out.println(allDistricts.get(j).getDist_name());
                            districtArrayAdapter.setNotifyOnChange(true);
                        }
                    }

                    System.out.println(districts.size());
                    districtArrayAdapter.clear();
                    districtArrayAdapter.addAll(districts);
                    districtArrayAdapter.notifyDataSetChanged();
                    selectDis.setSelection(0);

                    upazilaArrayAdapter.clear();
                    upazilaArrayAdapter.addAll(upazillas);
                    upazilaArrayAdapter.notifyDataSetChanged();
                    selectUpa.setSelection(0);

//                    new CheckInfo().execute();
                    getDivInfo(div_id);
                    String ddd = divisonName+" Division";
                    locationDiv(ddd);

//                    if (dis.isChecked() || upazila.isChecked()) {
//                        dis.setChecked(false);
//                        upazila.setChecked(false);
//                    }
//                    if (!div.isChecked()) {
//                        div.setChecked(true);
//                        divChecked();
//                    }


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        selectDis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    districtName = (String) parent.getItemAtPosition(position);
                    String name_id = "";
                    upazillas = new ArrayList<>();
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + districtName, Toast.LENGTH_SHORT)
                            .show();
                    for (int i = 0; i < allDistricts.size(); i++) {
                        if (districtName.equals(allDistricts.get(i).getDist_name())) {
                            name_id = allDistricts.get(i).getDist_id();
                            dis_id = allDistricts.get(i).getDist_id();
                            System.out.println(name_id);
                        }
                    }

                    upazillas.add("Select Upazila");
                    for (int j = 0; j < allUpazilas.size(); j++) {
                        if (name_id.equals(allUpazilas.get(j).getDd_dist_id())) {
                            upazilaArrayAdapter.setNotifyOnChange(true);
                            upazillas.add(allUpazilas.get(j).getThana_name());
                            System.out.println(allUpazilas.get(j).getThana_name());

                        }
                    }
                    System.out.println(upazillas.size());
                    upazilaArrayAdapter.clear();
                    upazilaArrayAdapter.addAll(upazillas);
                    upazilaArrayAdapter.notifyDataSetChanged();
                    selectUpa.setSelection(0);

//                    new CheckDisInfo().execute();
                    getDistInfo(dis_id);
                    String ddd = districtName+" District";
                    locationDis(ddd);

//                    if (div.isChecked() || upazila.isChecked()) {
//                        div.setChecked(false);
//                        upazila.setChecked(false);
//                    }
//                    if (!dis.isChecked()) {
//                        dis.setChecked(true);
//                        disChecked();
//                    }


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        selectUpa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                upazilaName = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + upazilaName, Toast.LENGTH_SHORT)
                            .show();
                    for (int i = 0; i < allUpazilas.size(); i++) {
                        if (upazilaName.equals(allUpazilas.get(i).getThana_name())) {
                            upa_id = allUpazilas.get(i).getDd_id();
                            System.out.println( "DDDDUPAY: "+upa_id);
                        }
                    }

//                    new CheckUpaInfo().execute();
                    getUpaInfo(upa_id);

                    String ddd = upazilaName+", "+ districtName;
                    locationUpa(ddd);


//                    if (div.isChecked() || dis.isChecked()) {
//                        dis.setChecked(false);
//                        div.setChecked(false);
//                    }
//                    if (!upazila.isChecked()) {
//                        upazila.setChecked(true);
//                        upaChecked();
//                    }

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getQuery();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.testtt));

            if (!success) {
                Log.i("Failed ", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("Style ", "Can't find style. Error: ", e);
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.6850, 90.3563), (float) 6.8));

        mMap.getUiSettings().setZoomControlsEnabled(true);

        layerOfBD();



        div.setOnClickListener(v -> {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.6850, 90.3563), (float) 6.8));
            divChecked();
        });


        dis.setOnClickListener(v -> {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.6850, 90.3563), (float) 6.8));
            disChecked();
        });

        upazila.setOnClickListener(v -> {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.6850, 90.3563), (float) 6.8));
            upaChecked();
        });


        layer.setOnFeatureClickListener(feature -> {
            if (feature.getProperty("ADM1_EN") != null && feature.getProperty("ADM0_EN") != null) {
                Toast.makeText(getApplicationContext(), feature.getProperty("ADM1_EN")+", "+feature.getProperty("ADM0_EN") ,Toast.LENGTH_SHORT).show();
            }

            if (feature.getProperty("Lat") != null && feature.getProperty("Long") != null) {
                LatLng latLng = new LatLng(Float.parseFloat(feature.getProperty("Lat")),Float.parseFloat(feature.getProperty("Long")));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 8.2));
            }

//                if(color != null) {
//                    polygonStyle.setFillColor(Color.parseColor(color));
//                }
        });

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("EXIT!")
                .setMessage("Do you want to Exit?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        System.exit(0);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void divChecked() {
        if (div.isChecked() && !dis.isChecked() && !upazila.isChecked()) {
            div.setChecked(true);
            mMap.clear();
            try {
                KmlLayer layer1  = new KmlLayer(mMap, R.raw.river, MapsActivity.this);
                layer1.addLayerToMap();
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }

            //layerOfDiv();
//            new LayerOfDivSetup().execute();
            layerOfDivSetup();
            divLay.setVisibility(View.VISIBLE);
            disLay.setVisibility(View.GONE);
            upaLay.setVisibility(View.GONE);
            info.setVisibility(View.GONE);
            selectDiv.setSelection(0);
            districts = new ArrayList<>();
            districts.add("Select District");
            districtArrayAdapter.clear();
            districtArrayAdapter.addAll(districts);
            districtArrayAdapter.notifyDataSetChanged();
            selectDis.setSelection(0);
            upazillas = new ArrayList<>();
            upazillas.add("Select Upazila");
            upazilaArrayAdapter.clear();
            upazilaArrayAdapter.addAll(upazillas);
            upazilaArrayAdapter.notifyDataSetChanged();
            selectUpa.setSelection(0);


            layer.setOnFeatureClickListener(feature -> {
                if (feature.getProperty("ADM1_EN") != null && feature.getProperty("ADM0_EN") != null) {
                    Toast.makeText(getApplicationContext(), feature.getProperty("ADM1_EN")+", "+feature.getProperty("ADM0_EN") ,Toast.LENGTH_SHORT).show();
                }

                if (feature.getProperty("Lat") != null && feature.getProperty("Long") != null) {
                    LatLng latLng = new LatLng(Float.parseFloat(feature.getProperty("Lat")),Float.parseFloat(feature.getProperty("Long")));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 8.2));
                }

//                if(color != null) {
//                    polygonStyle.setFillColor(Color.parseColor(color));
//                }
            });



        } else if (!div.isChecked() && !dis.isChecked() && !upazila.isChecked()){
            div.setChecked(false);
            mMap.clear();
            layerOfBD();
            divLay.setVisibility(View.VISIBLE);
            disLay.setVisibility(View.VISIBLE);
            upaLay.setVisibility(View.VISIBLE);
            info.setVisibility(View.GONE);
            selectDiv.setSelection(0);
            districts = new ArrayList<>();
            districts.add("Select District");
            districtArrayAdapter.clear();
            districtArrayAdapter.addAll(districts);
            districtArrayAdapter.notifyDataSetChanged();
            selectDis.setSelection(0);
            upazillas = new ArrayList<>();
            upazillas.add("Select Upazila");
            upazilaArrayAdapter.clear();
            upazilaArrayAdapter.addAll(upazillas);
            upazilaArrayAdapter.notifyDataSetChanged();
            selectUpa.setSelection(0);

        }
    }

    public void disChecked() {
        if (!div.isChecked() && dis.isChecked() && !upazila.isChecked()) {
            dis.setChecked(true);
            mMap.clear();

            //layerOfZilla();
//            new LayerOfDisSetup().execute();
            layerOfDisSetup();
            disLay.setVisibility(View.VISIBLE);
            divLay.setVisibility(View.GONE);
            upaLay.setVisibility(View.GONE);
            info.setVisibility(View.GONE);
            selectDiv.setSelection(0);
            districts = new ArrayList<>();
            districts.add("Select District");
            districtArrayAdapter.clear();
            districtArrayAdapter.addAll(districts);
            districtArrayAdapter.notifyDataSetChanged();
            selectDis.setSelection(0);
            upazillas = new ArrayList<>();
            upazillas.add("Select Upazila");
            upazilaArrayAdapter.clear();
            upazilaArrayAdapter.addAll(upazillas);
            upazilaArrayAdapter.notifyDataSetChanged();
            selectUpa.setSelection(0);
            layer.setOnFeatureClickListener(feature -> {
                Toast.makeText(getApplicationContext(), feature.getProperty("ADM2_EN")+", "+feature.getProperty("ADM1_EN")+", "+feature.getProperty("ADM0_EN") ,Toast.LENGTH_SHORT).show();
                String lat = feature.getProperty("Lat");
                String lon = feature.getProperty("Long");
                if (lat != null && lon != null) {
                    LatLng latLng = new LatLng(Float.parseFloat(lat),Float.parseFloat(lon));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 9.3));
                }
            });

        } else if (!div.isChecked() && !dis.isChecked() && !upazila.isChecked()){
            dis.setChecked(false);
            mMap.clear();
            layerOfBD();
            divLay.setVisibility(View.VISIBLE);
            disLay.setVisibility(View.VISIBLE);
            upaLay.setVisibility(View.VISIBLE);
            info.setVisibility(View.GONE);
            selectDiv.setSelection(0);
            districts = new ArrayList<>();
            districts.add("Select District");
            districtArrayAdapter.clear();
            districtArrayAdapter.addAll(districts);
            districtArrayAdapter.notifyDataSetChanged();
            selectDis.setSelection(0);
            upazillas = new ArrayList<>();
            upazillas.add("Select Upazila");
            upazilaArrayAdapter.clear();
            upazilaArrayAdapter.addAll(upazillas);
            upazilaArrayAdapter.notifyDataSetChanged();
            selectUpa.setSelection(0);

        }
    }

    public void upaChecked() {
        if (!div.isChecked() && !dis.isChecked() && upazila.isChecked()) {
            upazila.setChecked(true);
            mMap.clear();


//            new CheckLogin().execute();
            layerOfUpaSetup();
            upaLay.setVisibility(View.VISIBLE);
            divLay.setVisibility(View.GONE);
            disLay.setVisibility(View.GONE);
            info.setVisibility(View.GONE);
            selectDiv.setSelection(0);
            districts = new ArrayList<>();
            districts.add("Select District");
            districtArrayAdapter.clear();
            districtArrayAdapter.addAll(districts);
            districtArrayAdapter.notifyDataSetChanged();
            selectDis.setSelection(0);
            upazillas = new ArrayList<>();
            upazillas.add("Select Upazila");
            upazilaArrayAdapter.clear();
            upazilaArrayAdapter.addAll(upazillas);
            upazilaArrayAdapter.notifyDataSetChanged();
            selectUpa.setSelection(0);
            layer.setOnFeatureClickListener(feature -> {
                String place = feature.getProperty("ADM3_EN")+", "+ feature.getProperty("ADM2_EN");
                Toast.makeText(getApplicationContext(), feature.getProperty("ADM3_EN")+", "+ feature.getProperty("ADM2_EN")+", "+feature.getProperty("ADM1_EN")+", "+feature.getProperty("ADM0_EN") ,Toast.LENGTH_SHORT).show();
//                String lat = feature.getProperty("Lat");
//                String lon = feature.getProperty("Long");

                locationUpa(place);
//                    if (lat != null && lon != null) {
//                        LatLng latLng = new LatLng(Float.parseFloat(lat),Float.parseFloat(lon));
//                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 9.3));
//                    }
            });


        } else if (!div.isChecked() && !dis.isChecked() && !upazila.isChecked()){
            upazila.setChecked(false);
            mMap.clear();
            layerOfBD();
            divLay.setVisibility(View.VISIBLE);
            disLay.setVisibility(View.VISIBLE);
            upaLay.setVisibility(View.VISIBLE);
            info.setVisibility(View.GONE);
            selectDiv.setSelection(0);
            districts = new ArrayList<>();
            districts.add("Select District");
            districtArrayAdapter.clear();
            districtArrayAdapter.addAll(districts);
            districtArrayAdapter.notifyDataSetChanged();
            selectDis.setSelection(0);
            upazillas = new ArrayList<>();
            upazillas.add("Select Upazila");
            upazilaArrayAdapter.clear();
            upazilaArrayAdapter.addAll(upazillas);
            upazilaArrayAdapter.notifyDataSetChanged();
            selectUpa.setSelection(0);

        }
    }

    public void layerOfBD() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.6850, 90.3563), (float) 6.8));
        try {
            layer = new GeoJsonLayer(mMap, R.raw.whole_bangladesh, this);

            System.out.println(0);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        GeoJsonPolygonStyle polygonStyle = layer.getDefaultPolygonStyle();
        polygonStyle.setStrokeColor(Color.BLACK);


        polygonStyle.setStrokeWidth(2);
        layer.addLayerToMap();

        for (GeoJsonFeature feature : layer.getFeatures()) {
            // Do something to the feature
            String color = feature.getProperty("color");
            GeoJsonPolygonStyle ppp = new GeoJsonPolygonStyle();
            //List<LatLng> pp = new ArrayList<>();

            if(color != null) {
                ppp.setFillColor(Color.parseColor(color));
                ppp.setStrokeColor(Color.BLACK);
                ppp.setStrokeWidth(2);
                feature.setPolygonStyle(ppp);
            }


        }

        try {
            KmlLayer layer1  = new KmlLayer(mMap, R.raw.river, MapsActivity.this);
            layer1.addLayerToMap();
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

//    public class LayerOfDivSetup extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            waitProgress.show(getSupportFragmentManager(),"WaitBar");
//            waitProgress.setCancelable(false);
//
//
//            try {
//                layer = new GeoJsonLayer(mMap, R.raw.small_bangladesh_divisions_bibhags, MapsActivity.this);
//
//                System.out.println(0);
//
//            } catch (JSONException | IOException e) {
//                e.printStackTrace();
//            }
//
//            GeoJsonPolygonStyle polygonStyle = layer.getDefaultPolygonStyle();
//            polygonStyle.setStrokeColor(Color.BLACK);
//
//
//            polygonStyle.setStrokeWidth(2);
//            layer.addLayerToMap();
//
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            countChecks = new ArrayList<>();
//            for (GeoJsonFeature feature : layer.getFeatures()) {
//                // Do something to the feature
//
//
//                if (feature.getId() != null) {
//                    layerDiv_id = feature.getId();
//                    System.out.println("ID OF LAYER: "+layerDiv_id);
//                    String count = "";
//
//                    Boolean ddd = false;
//                    try {
//                        MapsActivity.this.connection = createConnection();
//                        //    Toast.makeText(MainActivity.this, "Connected",Toast.LENGTH_SHORT).show();
//
//                        Statement stmt = connection.createStatement();
//                        StringBuffer stringBuffer = new StringBuffer();
//                        StringBuffer stringBuffer1 = new StringBuffer();
//
//
//                        ResultSet rs=stmt.executeQuery("SELECT\n" +
//                                "    COUNT(project_creation_mst.pcm_id) AS \"Count_PCM_ID\"\n" +
//                                "FROM\n" +
//                                "    project_creation_mst,\n" +
//                                "    project_creation_upozila\n" +
//                                "   \n" +
//                                "WHERE\n" +
//                                "        project_creation_mst.pcm_id = project_creation_upozila.pcu_pcm_id\n" +
//                                "    \n" +
//                                "    AND project_creation_mst.pcm_proj_evaluation_flag = 1\n" +
//                                "    AND project_creation_upozila.pcu_div_id = "+layerDiv_id+"\n");
//
//
//
//                        while(rs.next()) {
//                            stringBuffer.append("Count: " +rs.getString(1)+ "\n");
//
//                            count = rs.getString(1);
//                            countChecks.add(new CountCheck(layerDiv_id,count));
//
//                        }
//
//                        ddd = true;
//
//
//                        System.out.println(stringBuffer);
//
//                        connection.close();
//
//                    }
//                    catch (Exception e) {
//
//                        //   Toast.makeText(MainActivity.this, ""+e,Toast.LENGTH_LONG).show();
////                        Log.i("ERRRRR", e.getLocalizedMessage());
//                        e.printStackTrace();
//                        System.out.println(e.getLocalizedMessage());
//                    }
//                    System.out.println(ddd);
//
//
//                }
//
//
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//
//            for (GeoJsonFeature feature : layer.getFeatures()) {
//                String color = feature.getProperty("color");
//                GeoJsonPolygonStyle ppp = new GeoJsonPolygonStyle();
//                //List<LatLng> pp = new ArrayList<>();
//
////                if (feature.getProperty("ADM1_EN").equals("Dhaka")) {
//                    if(color != null) {
//                        ppp.setFillColor(Color.parseColor(color));
//                        ppp.setStrokeColor(Color.BLACK);
//                        ppp.setStrokeWidth(2);
//                        feature.setPolygonStyle(ppp);
//
//                        if (feature.getProperty("Lat") != null && feature.getProperty("Long") != null) {
//                            LatLng latLng = new LatLng(Float.parseFloat(feature.getProperty("Lat")),Float.parseFloat(feature.getProperty("Long")));
//                            IconGenerator iconGenerator = new IconGenerator(MapsActivity.this);
//                            iconGenerator.setBackground(AppCompatResources.getDrawable(MapsActivity.this,R.drawable.bg_custom_marker));
//                            View inflatedView = View.inflate(MapsActivity.this, R.layout.marker_custom, null);
//                            TextView textView = (TextView) inflatedView.findViewById(R.id.test_text);
//                            if (feature.getProperty("ADM1_EN") != null && feature.getProperty("ADM0_EN") != null) {
//                                String count = "";
//                                for (int i = 0; i < countChecks.size(); i++) {
//                                    if (feature.getId().equals(countChecks.get(i).getId())) {
//                                        count = countChecks.get(i).getCount();
//                                    }
//                                }
//
//                                if (count.equals("0")) {
//                                    textView.setText("0 Project");
//                                } else {
//                                    textView.setText(count +" Projects");
//                                }
//                                iconGenerator.setContentView(inflatedView);
//                                //Marker marker;
//                                mMap.addMarker(
//                                                new MarkerOptions()
//                                                        .position(latLng)
//                                                        .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())))
//                                        .setTitle(feature.getProperty("ADM1_EN"));
//
//                                //Toast.makeText(getApplicationContext(), feature.getProperty("ADM1_EN")+", "+feature.getProperty("ADM0_EN") ,Toast.LENGTH_SHORT).show();
//                            }
//
//                        }
//                    }
////                }
//
//            }
//
//
//            waitProgress.dismiss();
//        }
//    }

    // Division Layer Design
    public void layerOfDivSetup() {
        waitProgress.show(getSupportFragmentManager(),"WaitBar");
        waitProgress.setCancelable(false);
        conn = false;

        try {
            layer = new GeoJsonLayer(mMap, R.raw.small_bangladesh_divisions_bibhags, MapsActivity.this);

            System.out.println(0);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        GeoJsonPolygonStyle polygonStyle = layer.getDefaultPolygonStyle();
        polygonStyle.setStrokeColor(Color.BLACK);


        polygonStyle.setStrokeWidth(2);
        layer.addLayerToMap();

        countChecks = new ArrayList<>();

        String project_count = "http://103.56.208.123:8086/terrain/tr_kabikha/tr_kabikha_map/project_count_by_div";

        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);

        StringRequest divInfoReq = new StringRequest(Request.Method.GET, project_count, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray jsonArray = new JSONArray(items);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject upazilaObject = jsonArray.getJSONObject(i);
                        String div_id = upazilaObject.getString("div_id");
                        String p_count = upazilaObject.getString("p_count");

                        countChecks.add(new CountCheck(div_id,p_count));
                    }

                }
                conn = true;
                designMapWithDiv();

            } catch (JSONException e) {
                e.printStackTrace();
                conn = false;
                designMapWithDiv();
            }
        }, error -> {
            conn = false;
            designMapWithDiv();
        });

        requestQueue.add(divInfoReq);
    }

    public void designMapWithDiv() {
        if (conn) {
            for (GeoJsonFeature feature : layer.getFeatures()) {
                String color = feature.getProperty("color");
                GeoJsonPolygonStyle ppp = new GeoJsonPolygonStyle();
                //List<LatLng> pp = new ArrayList<>();

//                if (feature.getProperty("ADM1_EN").equals("Dhaka")) {
                if(color != null) {
                    ppp.setFillColor(Color.parseColor(color));
                    ppp.setStrokeColor(Color.BLACK);
                    ppp.setStrokeWidth(2);
                    feature.setPolygonStyle(ppp);

                    if (feature.getProperty("Lat") != null && feature.getProperty("Long") != null) {
                        LatLng latLng = new LatLng(Float.parseFloat(feature.getProperty("Lat")),Float.parseFloat(feature.getProperty("Long")));
                        IconGenerator iconGenerator = new IconGenerator(MapsActivity.this);
                        iconGenerator.setBackground(AppCompatResources.getDrawable(MapsActivity.this,R.drawable.bg_custom_marker));
                        View inflatedView = View.inflate(MapsActivity.this, R.layout.marker_custom, null);
                        TextView textView = (TextView) inflatedView.findViewById(R.id.test_text);
                        if (feature.getProperty("ADM1_EN") != null && feature.getProperty("ADM0_EN") != null) {
                            String count = "";
                            for (int i = 0; i < countChecks.size(); i++) {
                                if (feature.getId().equals(countChecks.get(i).getId())) {
                                    count = countChecks.get(i).getCount();
                                }
                            }

                            if (count.equals("0")) {
                                String text = "0 Project";
                                textView.setText(text);
                            } else {
                                String text = count +" Projects";
                                textView.setText(text);
                            }
                            iconGenerator.setContentView(inflatedView);
                            //Marker marker;
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon()))
                                    .title(feature.getProperty("ADM1_EN"))
                                    .anchor((float) 0.5, (float) 0.5));

                            //Toast.makeText(getApplicationContext(), feature.getProperty("ADM1_EN")+", "+feature.getProperty("ADM0_EN") ,Toast.LENGTH_SHORT).show();
                        }

                    }
                }
//                }

            }
            conn = false;
            waitProgress.dismiss();
        }
        else {
            waitProgress.dismiss();
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            AlertDialog dialog = new AlertDialog.Builder(MapsActivity.this)
                    .setMessage("Please Check Your Internet Connection")
                    .setPositiveButton("Retry", null)
                    .show();

            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {

               layerOfDivSetup();
               dialog.dismiss();
            });
        }

    }

//    public class LayerOfDisSetup extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            waitProgress.show(getSupportFragmentManager(),"WaitBar");
//            waitProgress.setCancelable(false);
//            try {
//                KmlLayer layer1  = new KmlLayer(mMap, R.raw.river, MapsActivity.this);
//                layer1.addLayerToMap();
//            } catch (XmlPullParserException | IOException e) {
//                e.printStackTrace();
//            }
//
//            try {
//                layer = new GeoJsonLayer(mMap, R.raw.small_bangladesh_districts_zillas, MapsActivity.this);
//
//                System.out.println(0);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            GeoJsonPolygonStyle polygonStyle = layer.getDefaultPolygonStyle();
//            polygonStyle.setStrokeColor(Color.BLACK);
//
//
//            polygonStyle.setStrokeWidth(2);
//            layer.addLayerToMap();
//
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            countChecks = new ArrayList<>();
//
//            String count = "";
//
//            boolean ddd = false;
//            try {
//                MapsActivity.this.connection = createConnection();
//                //    Toast.makeText(MainActivity.this, "Connected",Toast.LENGTH_SHORT).show();
//
//                Statement stmt = connection.createStatement();
//                StringBuffer stringBuffer = new StringBuffer();
//
//
//
//                ResultSet rs=stmt.executeQuery("SELECT\n" +
//                        "    COUNT(project_creation_mst.pcm_id) AS \"Count_PCM_ID\",\n" +
//                        "    district.dist_id,\n" +
//                        "    district.dist_name,\n" +
//                        "    division.div_name\n" +
//                        "FROM\n" +
//                        "    project_creation_mst,\n" +
//                        "    project_creation_upozila,\n" +
//                        "    district_dtl,\n" +
//                        "    district,\n" +
//                        "    division\n" +
//                        "WHERE\n" +
//                        "        project_creation_mst.pcm_id = project_creation_upozila.pcu_pcm_id\n" +
//                        "    AND project_creation_upozila.pcu_dd_id = district_dtl.dd_id\n" +
//                        "    AND project_creation_upozila.pcu_dist_id = district.dist_id\n" +
//                        "    AND project_creation_upozila.pcu_div_id = division.div_id\n" +
//                        "    AND project_creation_mst.pcm_proj_evaluation_flag = 1\n" +
//                        "GROUP BY\n" +
//                        "    district.dist_id,\n" +
//                        "    district.dist_name,\n" +
//                        "    division.div_name");
//
//
//
//                while(rs.next()) {
//                    stringBuffer.append("Count: ").append(rs.getString(1)).append("\n");
//
//                    count = rs.getString(1);
//                    countChecks.add(new CountCheck(rs.getString(2),count));
//
//                }
//
//                ddd = true;
//
//
//                System.out.println(stringBuffer);
//
//                connection.close();
//
//            }
//            catch (Exception e) {
//
//                //   Toast.makeText(MainActivity.this, ""+e,Toast.LENGTH_LONG).show();
////                        Log.i("ERRRRR", e.getLocalizedMessage());
//                e.printStackTrace();
//                System.out.println(e.getLocalizedMessage());
//            }
//            System.out.println(ddd);
//
////            for (GeoJsonFeature feature : layer.getFeatures()) {
////                // Do something to the feature
////                //String color = feature.getProperty("color");
////                if (feature.getId() != null) {
////                    layerDis_id = feature.getId();
////                    System.out.println("ID OF LAYER: "+layerDis_id);
////
////
////
////
////                }
//
////            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            Random rand = new Random();
//
//            for (GeoJsonFeature feature : layer.getFeatures()) {
//                String color = feature.getProperty("color");
//                GeoJsonPolygonStyle ppp = new GeoJsonPolygonStyle();
//                //List<LatLng> pp = new ArrayList<>();
//
//                float r = (float) (rand.nextFloat() );
//                float g = (float) (rand.nextFloat() );
//                float b = (float) (rand.nextFloat() );
//
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    //Color randomColor = Color.valueOf(Color.pack(r,g,b));
//                    ppp.setFillColor(Color.rgb(r,g,b));
//                    ppp.setStrokeColor(Color.BLACK);
//                    ppp.setStrokeWidth(2);
//                    feature.setPolygonStyle(ppp);
//                }
//
//                if (feature.getProperty("Lat") != null && feature.getProperty("Long") != null) {
//                    LatLng latLng = new LatLng(Float.parseFloat(feature.getProperty("Lat")),Float.parseFloat(feature.getProperty("Long")));
////                        IconGenerator iconGenerator = new IconGenerator(MapsActivity.this);
////                        iconGenerator.setBackground(MapsActivity.this.getDrawable(R.drawable.bg_custom_marker));
////                        View inflatedView = View.inflate(MapsActivity.this, R.layout.marker_custom, null);
////                        TextView textView = (TextView) inflatedView.findViewById(R.id.test_text);
//                    if (feature.getProperty("ADM2_EN") != null && feature.getProperty("ADM1_EN") != null) {
//                        String count = "";
//                        for (int i = 0; i < countChecks.size(); i++) {
//                            if (feature.getId().equals(countChecks.get(i).getId())) {
//                                count = countChecks.get(i).getCount();
//                                System.out.println(countChecks.get(i).getId()+": "+count);
//                                break;
//                            } else {
//                                count = "0";
//                            }
//                        }
//
////                            if (count.equals("0")) {
////                                textView.setText("No Project Found");
////                            } else {
////                                textView.setText(count +" Project");
////                            }
////                            iconGenerator.setContentView(inflatedView);
//                        //Marker marker;
////                            mMap.addMarker(
////                                    new MarkerOptions()
////                                            .position(latLng)
////                                            .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())))
////                                    .setTitle(feature.getProperty("ADM2_EN")+", "+feature.getProperty("ADM1_EN"));
//                        mMap.addMarker(new MarkerOptions()
//                                .position(latLng)
//                                .title(feature.getProperty("ADM2_EN")+", "+feature.getProperty("ADM1_EN")).snippet(count+" Project")
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_white_16x16))
//                                .anchor((float) 0.5, (float) 0.5));
////                            mMap.addMarker(new MarkerOptions().position(latLng).title(feature.getProperty("ADM2_EN")+", "+feature.getProperty("ADM1_EN")).snippet(count+" Project"));
//
//                        //Toast.makeText(getApplicationContext(), feature.getProperty("ADM1_EN")+", "+feature.getProperty("ADM0_EN") ,Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//
//            }
//            waitProgress.dismiss();
//        }
//    }

    public void layerOfDisSetup() {
        waitProgress.show(getSupportFragmentManager(),"WaitBar");
        waitProgress.setCancelable(false);
        conn = false;
        try {
            KmlLayer layer1  = new KmlLayer(mMap, R.raw.river, MapsActivity.this);
            layer1.addLayerToMap();
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        try {
            layer = new GeoJsonLayer(mMap, R.raw.small_bangladesh_districts_zillas, MapsActivity.this);

            System.out.println(0);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        GeoJsonPolygonStyle polygonStyle = layer.getDefaultPolygonStyle();
        polygonStyle.setStrokeColor(Color.BLACK);


        polygonStyle.setStrokeWidth(2);
        layer.addLayerToMap();

        countChecks = new ArrayList<>();

        String project_count = "http://103.56.208.123:8086/terrain/tr_kabikha/tr_kabikha_map/project_count_by_dist";

        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);

        StringRequest distInfoReq = new StringRequest(Request.Method.GET, project_count, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray jsonArray = new JSONArray(items);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject upazilaObject = jsonArray.getJSONObject(i);
                        String dist_id = upazilaObject.getString("dist_id");
                        String p_count = upazilaObject.getString("p_count");

                        countChecks.add(new CountCheck(dist_id,p_count));
                    }

                }
                conn = true;
                designMapWithDist();

            } catch (JSONException e) {
                e.printStackTrace();
                conn = false;
                designMapWithDist();
            }
        }, error -> {
            conn = false;
            designMapWithDist();
        });

        requestQueue.add(distInfoReq);
    }

    public void designMapWithDist() {

        if (conn) {
            Random rand = new Random();

            for (GeoJsonFeature feature : layer.getFeatures()) {
//                String color = feature.getProperty("color");
                GeoJsonPolygonStyle ppp = new GeoJsonPolygonStyle();

                float r = (float) (rand.nextFloat() );
                float g = (float) (rand.nextFloat() );
                float b = (float) (rand.nextFloat() );


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ppp.setFillColor(Color.rgb(r,g,b));
                    ppp.setStrokeColor(Color.BLACK);
                    ppp.setStrokeWidth(2);
                    feature.setPolygonStyle(ppp);
                }

                if (feature.getProperty("Lat") != null && feature.getProperty("Long") != null) {
                    LatLng latLng = new LatLng(Float.parseFloat(feature.getProperty("Lat")),Float.parseFloat(feature.getProperty("Long")));

                    if (feature.getProperty("ADM2_EN") != null && feature.getProperty("ADM1_EN") != null) {
                        String count = "";
                        for (int i = 0; i < countChecks.size(); i++) {
                            if (feature.getId().equals(countChecks.get(i).getId())) {
                                count = countChecks.get(i).getCount();
                                System.out.println(countChecks.get(i).getId()+": "+count);
                                break;
                            } else {
                                count = "0";
                            }
                        }
                        if (!count.equals("0")) {
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(feature.getProperty("ADM2_EN")+", "+feature.getProperty("ADM1_EN")).snippet(count+" Project")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_green_16x16))
                                    .anchor((float) 0.5, (float) 0.5));
                        }
                        else {
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(feature.getProperty("ADM2_EN") + ", " + feature.getProperty("ADM1_EN")).snippet(count + " Project")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_white_16x16))
                                    .anchor((float) 0.5, (float) 0.5));
                        }
                    }
                }

            }
            conn = false;
            waitProgress.dismiss();
        }
        else {
            waitProgress.dismiss();
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            AlertDialog dialog = new AlertDialog.Builder(MapsActivity.this)
                    .setMessage("Please Check Your Internet Connection")
                    .setPositiveButton("Retry", null)
                    .show();

            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {

                layerOfDisSetup();
                dialog.dismiss();
            });
        }
    }

//    public class CheckLogin extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            waitProgress.show(getSupportFragmentManager(),"WaitBar");
//            waitProgress.setCancelable(false);
//
//            try {
//                KmlLayer layer1  = new KmlLayer(mMap, R.raw.river, MapsActivity.this);
//                layer1.addLayerToMap();
//            } catch (XmlPullParserException | IOException e) {
//                e.printStackTrace();
//            }
//
//            try {
//                layer = new GeoJsonLayer(mMap, R.raw.small_bangladesh_upozila, MapsActivity.this);
//
//                System.out.println(0);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            GeoJsonPolygonStyle polygonStyle = layer.getDefaultPolygonStyle();
//            polygonStyle.setStrokeColor(Color.BLACK);
//
//
//            polygonStyle.setStrokeWidth(2);
//            layer.addLayerToMap();
//
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            countChecks = new ArrayList<>();
//
//            String count = "";
//
//            boolean ddd = false;
//
//            try {
//                MapsActivity.this.connection = createConnection();
//                //    Toast.makeText(MainActivity.this, "Connected",Toast.LENGTH_SHORT).show();
//
//                Statement stmt = connection.createStatement();
//                StringBuffer stringBuffer = new StringBuffer();
//
//
//                ResultSet rs=stmt.executeQuery("SELECT\n" +
//                        "    COUNT(project_creation_mst.pcm_id) AS \"Count_PCM_ID\",\n" +
//                        "    district_dtl.dd_id,\n" +
//                        "    district_dtl.dd_thana_name,\n" +
//                        "    district.dist_name,\n" +
//                        "    division.div_name\n" +
//                        "FROM\n" +
//                        "    project_creation_mst,\n" +
//                        "    project_creation_upozila,\n" +
//                        "    district_dtl,\n" +
//                        "    district,\n" +
//                        "    division\n" +
//                        "WHERE\n" +
//                        "        project_creation_mst.pcm_id = project_creation_upozila.pcu_pcm_id\n" +
//                        "    AND project_creation_upozila.pcu_dd_id = district_dtl.dd_id\n" +
//                        "    AND project_creation_upozila.pcu_dist_id = district.dist_id\n" +
//                        "    AND project_creation_upozila.pcu_div_id = division.div_id\n" +
//                        "    AND project_creation_mst.pcm_proj_evaluation_flag = 1\n" +
//                        "GROUP BY\n" +
//                        "    district_dtl.dd_id,\n" +
//                        "    district_dtl.dd_thana_name,\n" +
//                        "    district.dist_name,\n" +
//                        "    division.div_name");
//
//
//
//                while(rs.next()) {
//                    stringBuffer.append("Count: ").append(rs.getString(1)).append("\n");
//
//                    count = rs.getString(1);
//                    countChecks.add(new CountCheck(rs.getString(2),count));
//
//                }
//
//                ddd = true;
//
//
//                System.out.println(stringBuffer);
//
//                connection.close();
//
//            }
//            catch (Exception e) {
//
//                //   Toast.makeText(MainActivity.this, ""+e,Toast.LENGTH_LONG).show();
////                        Log.i("ERRRRR", e.getLocalizedMessage());
//                e.printStackTrace();
//                System.out.println(e.getLocalizedMessage());
//            }
//            System.out.println(ddd);
//
//
////            for (GeoJsonFeature feature : layer.getFeatures()) {
////                // Do something to the feature
////                //String color = feature.getProperty("color");
////                if (feature.getId() != null) {
////                    layerUpa_id = feature.getId();
////                    System.out.println("ID OF LAYER: "+layerUpa_id);
////
////
////                }
////
////            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//
//
//
//            Random rand = new Random();
//
//            for (GeoJsonFeature feature : layer.getFeatures()) {
//                // Do something to the feature
//                //String color = feature.getProperty("color");
//                GeoJsonPolygonStyle ppp = new GeoJsonPolygonStyle();
//
//                float r = (float) (rand.nextFloat() );
//                float g = (float) (rand.nextFloat() );
//                float b = (float) (rand.nextFloat() );
////                float r = (float) (rand.nextFloat() / 2f + 0.5);
////                float g = (float) (rand.nextFloat() / 2f + 0.5);
////                float b = (float) (rand.nextFloat() / 2f + 0.5);
//
////                final float hue = rand.nextFloat();
////                final float saturation = (rand.nextInt(2000) + 1000) / 10000f;//1.0 for brilliant, 0.0 for dull
////                final float luminance = 0.9f;
////
////                float[] hsv = new float[3];
////                hsv[0] = hue;
////                hsv[1] = saturation;
////                hsv[2] = luminance;
//
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    //Color randomColor = Color.valueOf(Color.pack(r,g,b));
//                    //ppp.setFillColor(Color.HSVToColor(hsv));
//                    ppp.setFillColor(Color.rgb(r,g,b));
//                    ppp.setStrokeColor(Color.BLACK);
//                    ppp.setStrokeWidth(2);
//                    feature.setPolygonStyle(ppp);
//                }
//
////                String place = feature.getProperty("ADM3_EN")+", "+ feature.getProperty("ADM2_EN");
////                LatLng latLng = location(place);
//
//
//                if (feature.getProperty("ADM3_EN") != null && feature.getProperty("ADM2_EN") != null) {
//                    String count = "";
////                    for (int i = 0; i < countChecks.size(); i++) {
////                        if (feature.getId().equals(countChecks.get(i).getId())) {
////                            count = countChecks.get(i).getCount();
////                        }
////                    }
//
//                    if (feature.getProperty("Lat") != null && feature.getProperty("Long") != null) {
//                        LatLng latLng = new LatLng(Float.parseFloat(feature.getProperty("Lat")), Float.parseFloat(feature.getProperty("Long")));
//
//                        for (int i = 0; i < countChecks.size(); i++) {
//                            if (feature.getId().equals(countChecks.get(i).getId()) && !countChecks.get(i).getCount().equals("0")) {
//                                count = countChecks.get(i).getCount();
//                            }
//                        }
//
//                        if (count.isEmpty()) {
//                            count = "0";
//                            mMap.addMarker(new MarkerOptions()
//                                    .position(latLng)
//                                    .title(feature.getProperty("ADM3_EN")+", "+feature.getProperty("ADM2_EN")).snippet(count+" Project")
//                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_white_16x16))
//                                    .anchor((float) 0.5, (float) 0.5));
//                        } else {
//                            mMap.addMarker(new MarkerOptions()
//                                    .position(latLng)
//                                    .title(feature.getProperty("ADM3_EN")+", "+feature.getProperty("ADM2_EN")).snippet(count+" Projects")
//                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_white_16x16))
//                                    .anchor((float) 0.5, (float) 0.5));
//                        }
//
//
//                    }
//
//
//                    //Marker marker;
////                   mMap.addMarker(
////                                  new MarkerOptions()
////                                            .position(latLng)
////                                            .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())))
////                                    .setTitle(feature.getProperty("ADM2_EN")+", "+feature.getProperty("ADM1_EN"));
////                    mMap.addMarker(new MarkerOptions()
////                            .position(latLng)
////                            .title(feature.getProperty("ADM3_EN")+", "+feature.getProperty("ADM2_EN")).snippet(count+" Project")
////                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_white_16x16))
////                            .anchor((float) 0.5, (float) 0.5));
//
//                    //Toast.makeText(getApplicationContext(), feature.getProperty("ADM1_EN")+", "+feature.getProperty("ADM0_EN") ,Toast.LENGTH_SHORT).show();
//                }
//
//
//
//
//
//            }
//            waitProgress.dismiss();
//
//
//
//        }
//    }

    public void layerOfUpaSetup() {
        waitProgress.show(getSupportFragmentManager(),"WaitBar");
        waitProgress.setCancelable(false);
        conn = false;

        try {
            KmlLayer layer1  = new KmlLayer(mMap, R.raw.river, MapsActivity.this);
            layer1.addLayerToMap();
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        try {
            layer = new GeoJsonLayer(mMap, R.raw.small_bangladesh_upozila, MapsActivity.this);

            System.out.println(0);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        GeoJsonPolygonStyle polygonStyle = layer.getDefaultPolygonStyle();
        polygonStyle.setStrokeColor(Color.BLACK);

        polygonStyle.setStrokeWidth(2);
        layer.addLayerToMap();

        countChecks = new ArrayList<>();

        String project_count = "http://103.56.208.123:8086/terrain/tr_kabikha/tr_kabikha_map/project_count_by_upa";

        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);

        StringRequest upaInfoReq = new StringRequest(Request.Method.GET, project_count, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray jsonArray = new JSONArray(items);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject upazilaObject = jsonArray.getJSONObject(i);
                        String dd_id = upazilaObject.getString("dd_id");
                        String p_count = upazilaObject.getString("p_count");

                        countChecks.add(new CountCheck(dd_id,p_count));
                    }

                }
                conn = true;
                designMapWithUpa();

            } catch (JSONException e) {
                e.printStackTrace();
                conn = false;
                designMapWithUpa();
            }
        }, error -> {
            conn = false;
            designMapWithUpa();
        });

        requestQueue.add(upaInfoReq);

    }

    public void designMapWithUpa() {
        if (conn) {
            Random rand = new Random();

            for (GeoJsonFeature feature : layer.getFeatures()) {
                // Do something to the feature
                GeoJsonPolygonStyle ppp = new GeoJsonPolygonStyle();

                float r = (float) (rand.nextFloat() );
                float g = (float) (rand.nextFloat() );
                float b = (float) (rand.nextFloat() );
//                float r = (float) (rand.nextFloat() / 2f + 0.5);
//                float g = (float) (rand.nextFloat() / 2f + 0.5);
//                float b = (float) (rand.nextFloat() / 2f + 0.5);

//                final float hue = rand.nextFloat();
//                final float saturation = (rand.nextInt(2000) + 1000) / 10000f;//1.0 for brilliant, 0.0 for dull
//                final float luminance = 0.9f;
//
//                float[] hsv = new float[3];
//                hsv[0] = hue;
//                hsv[1] = saturation;
//                hsv[2] = luminance;


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //Color randomColor = Color.valueOf(Color.pack(r,g,b));
                    //ppp.setFillColor(Color.HSVToColor(hsv));
                    ppp.setFillColor(Color.rgb(r,g,b));
                    ppp.setStrokeColor(Color.BLACK);
                    ppp.setStrokeWidth(2);
                    feature.setPolygonStyle(ppp);
                }

                if (feature.getProperty("ADM3_EN") != null && feature.getProperty("ADM2_EN") != null) {
                    String count = "";
                    if (feature.getProperty("Lat") != null && feature.getProperty("Long") != null) {
                        LatLng latLng = new LatLng(Float.parseFloat(feature.getProperty("Lat")), Float.parseFloat(feature.getProperty("Long")));

                        for (int i = 0; i < countChecks.size(); i++) {
                            if (feature.getId().equals(countChecks.get(i).getId()) && !countChecks.get(i).getCount().equals("0")) {
                                count = countChecks.get(i).getCount();
                            }
                        }

                        if (count.isEmpty()) {
                            count = "0";
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(feature.getProperty("ADM3_EN")+", "+feature.getProperty("ADM2_EN")).snippet(count+" Project")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_white_16x16))
                                    .anchor((float) 0.5, (float) 0.5));
                        } else {
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(feature.getProperty("ADM3_EN")+", "+feature.getProperty("ADM2_EN")).snippet(count+" Projects")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_green_16x16))
                                    .anchor((float) 0.5, (float) 0.5));
                        }
                    }
                }
            }
            waitProgress.dismiss();
        }
        else {
            waitProgress.dismiss();
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            AlertDialog dialog = new AlertDialog.Builder(MapsActivity.this)
                    .setMessage("Please Check Your Internet Connection")
                    .setPositiveButton("Retry", null)
                    .show();

            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {

                layerOfUpaSetup();
                dialog.dismiss();
            });
        }
    }

    public void locationUpa(String location) {

        Log.i("Hobe ", location);

        List<Address> addressList;

        if (!location.isEmpty()) {
            Log.i("Hobe na  ", "hobe dekhi");
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
                System.out.println(addressList);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
                return;
            }

            if (addressList.size() == 0) {
                Toast.makeText(getApplicationContext(), "Location Not Found", Toast.LENGTH_SHORT).show();
            } else {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                //mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
            }
        }
    }

    public LatLng location(String location) {

        Log.i("Hobe ", location);
        LatLng latLng = new LatLng(0,0);

        List<Address> addressList;

        if (!location.isEmpty()) {
            Log.i("Hobe na  ", "hobe dekhi");
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
                System.out.println(addressList);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
                return latLng;
            }

            if (addressList.size() == 0) {
                Toast.makeText(getApplicationContext(), "Location Not Found", Toast.LENGTH_SHORT).show();
            } else {
                Address address = addressList.get(0);
                latLng = new LatLng(address.getLatitude(), address.getLongitude());
                //mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
            }
        }
        return latLng;
    }

    public void locationDiv(String location) {

        Log.i("Hobe ", location);

        List<Address> addressList;

        if (!location.isEmpty()) {
            Log.i("Hobe na  ", "hobe dekhi");
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
                System.out.println(addressList);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
                return;
            }

            if (addressList.size() == 0) {
                Toast.makeText(getApplicationContext(), "Location Not Found in Map", Toast.LENGTH_SHORT).show();
            } else {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                //mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 8.2));
            }
        }
    }

    public void locationDis(String location) {

        Log.i("Hobe ", location);

        List<Address> addressList;

        if (!location.isEmpty()) {
            Log.i("Hobe na  ", "hobe dekhi");
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
                System.out.println(addressList);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
                return;
            }

            if (addressList.size() == 0) {
                Toast.makeText(getApplicationContext(), "Location Not Found in Map", Toast.LENGTH_SHORT).show();
            } else {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                //mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 9.3));
            }
        }
    }

//    public boolean isConnected() {
//
//        boolean connected = false;
//        boolean isMobile = false;
//        try {
//            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo nInfo = cm.getActiveNetworkInfo();
//            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
//            isMobile = nInfo.getType() == ConnectivityManager.TYPE_MOBILE;
//            return connected;
//        } catch (Exception e) {
//            Log.e("Connectivity Exception", e.getMessage());
//        }
//        return connected;
//    }
//
//    public boolean isOnline() {
//
//        Runtime runtime = Runtime.getRuntime();
//        try {
//            //Process ipProcess = runtime.exec("/system/bin/ping -c 1 192.168.1.5");
//            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
//            int     exitValue = ipProcess.waitFor();
//            return (exitValue == 0);
//        }
//        catch (IOException | InterruptedException e)          { e.printStackTrace(); }
//
//        return false;
//    }

////    public class CheckInfo extends AsyncTask<Void, Void, Void> {
////
////        @Override
////        protected void onPreExecute() {
////            super.onPreExecute();
////
////
////        }
////
////        @Override
////        protected Void doInBackground(Void... voids) {
////            if (isConnected()) {
////
////                gettingInfoDiv(div_id);
////                if (infoConnected) {
////                    infoCon = true;
////                    message= "Internet Connected";
////                }
////
////            } else {
////                infoCon = false;
////                message = "Not Connected";
////            }
////
////            return null;
////        }
////
////        @Override
////        protected void onPostExecute(Void aVoid) {
////            super.onPostExecute(aVoid);
////
////            if (infoCon) {
////
////                info.setVisibility(View.VISIBLE);
////
////                if (Total_Count.isEmpty()) {
////                    //Toast.makeText(getApplicationContext(), "No Project Found", Toast.LENGTH_SHORT).show();
////                    loc.setText(divisonName+", Bangladesh");
////                    pro.setText("No Project Found");
////
////                } else {
////                    //Toast.makeText(getApplicationContext(), "Total Project: "+Total_Count, Toast.LENGTH_SHORT).show();
////
////                    loc.setText(divisonName+", Bangladesh");
////                    pro.setText("Total Project: "+Total_Count);
////                }
////                infoCon = false;
////                infoConnected = false;
////                Total_Count = "";
////
////                if (markers.size() != 0) {
////
////                    for (int i = 0; i < markers.size(); i++) {
////                        Marker marker = markers.get(i);
////                        marker.remove();
////                    }
////                    markers = new ArrayList<Marker>();
////                }
////                for (int i = 0; i < nameAndCounts.size(); i++) {
////                    String name = nameAndCounts.get(i).getName();
////                    name = name+", "+ nameAndCounts.get(i).getNamebefore();
////                    String cc = nameAndCounts.get(i).getCount();
////
////                    LatLng latLng = location(name);
////
////
////
////                    Marker marker = mMap.addMarker(new MarkerOptions()
////                            .position(latLng)
////                            .title(name)
////                            .snippet(cc+" Project"));
////
////                    markers.add(marker);
////
////                }
////                System.out.println(markers.size());
////
////            }else {
////                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
////                AlertDialog dialog = new AlertDialog.Builder(MapsActivity.this)
////                        .setMessage("Please Check Your Internet Connection")
////                        .setPositiveButton("Retry", null)
////                        .show();
////
////                dialog.setCancelable(false);
////                dialog.setCanceledOnTouchOutside(false);
////                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
////                positive.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////
////                        getQuery();
////                        dialog.dismiss();
////                    }
////                });
////            }
////        }
////    }
//
//    public class CheckDisInfo extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            waitProgress.show(getSupportFragmentManager(),"WaitBar");
//            waitProgress.setCancelable(false);
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            if (isConnected()) {
//
//                gettingInfoDis(dis_id);
//                if (disConeected) {
//                    disCon = true;
//                    message= "Internet Connected";
//                }
//
//            } else {
//                disCon = false;
//                message = "Not Connected";
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//            waitProgress.dismiss();
//            if (disCon) {
//
//                info.setVisibility(View.VISIBLE);
//
//                if (Total_Count.isEmpty()) {
//                    //Toast.makeText(getApplicationContext(), "No Project Found", Toast.LENGTH_SHORT).show();
//                    loc.setText(districtName+", "+divisonName+", Bangladesh");
//                    pro.setText("No Project Found");
//
//                } else {
//                    //Toast.makeText(getApplicationContext(), "Total Project: "+Total_Count, Toast.LENGTH_SHORT).show();
//
//                    loc.setText(districtName+", "+divisonName+", Bangladesh");
//                    pro.setText("Total Project: "+Total_Count);
//                }
//                infoCon = false;
//                infoConnected = false;
//                Total_Count = "";
//
//                if (markers.size() != 0) {
//
//                    for (int i = 0; i < markers.size(); i++) {
//                        Marker marker = markers.get(i);
//                        marker.remove();
//                    }
//                    markers = new ArrayList<Marker>();
//                }
//                for (int i = 0; i < nameAndCounts.size(); i++) {
//                    String name = nameAndCounts.get(i).getName();
//                    name = name+", "+ nameAndCounts.get(i).getNamebefore();
//                    String cc = nameAndCounts.get(i).getCount();
//
//                    LatLng latLng = location(name);
//
//
//
//                    Marker marker = mMap.addMarker(new MarkerOptions()
//                            .position(latLng)
//                            .title(name)
//                            .snippet(cc+" Project"));
//
//                    markers.add(marker);
//
//                }
//                System.out.println(markers.size());
//
//            }else {
//                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
//                AlertDialog dialog = new AlertDialog.Builder(MapsActivity.this)
//                        .setMessage("Please Check Your Internet Connection")
//                        .setPositiveButton("Retry", null)
//                        .show();
//
//                dialog.setCancelable(false);
//                dialog.setCanceledOnTouchOutside(false);
//                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                positive.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        getQuery();
//                        dialog.dismiss();
//                    }
//                });
//            }
//        }
//    }

//    public class CheckUpaInfo extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            waitProgress.show(getSupportFragmentManager(),"WaitBar");
//            waitProgress.setCancelable(false);
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            if (isConnected()) {
//
//                gettingInfoUpa(upa_id);
//                if (upaConnected) {
//                    upaCon = true;
//                    message= "Internet Connected";
//                }
//
//            } else {
//                upaCon = false;
//                message = "Not Connected";
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//            waitProgress.dismiss();
//            if (upaCon) {
//
//                info.setVisibility(View.VISIBLE);
//
//                if (Total_Count.isEmpty()) {
//                    //Toast.makeText(getApplicationContext(), "No Project Found", Toast.LENGTH_SHORT).show();
//                    loc.setText(upazilaName+", "+districtName+", "+divisonName+", Bangladesh");
//                    pro.setText("No Project Found");
//
//                } else {
//                    //Toast.makeText(getApplicationContext(), "Total Project: "+Total_Count, Toast.LENGTH_SHORT).show();
//
//                    loc.setText(upazilaName+", "+districtName+", "+divisonName+", Bangladesh");
//                    pro.setText("Total Project: "+Total_Count);
//                }
//                infoCon = false;
//                infoConnected = false;
//                Total_Count = "";
//
//                if (markers.size() != 0) {
//
//                    for (int i = 0; i < markers.size(); i++) {
//                        Marker marker = markers.get(i);
//                        marker.remove();
//                    }
//                    markers = new ArrayList<Marker>();
//                }
//                for (int i = 0; i < nameAndCounts.size(); i++) {
//                    String name = nameAndCounts.get(i).getName();
//                    name = name+", "+ nameAndCounts.get(i).getNamebefore();
//                    String cc = nameAndCounts.get(i).getCount();
//
//                    LatLng latLng = location(name);
//
//
//
//                    Marker marker = mMap.addMarker(new MarkerOptions()
//                            .position(latLng)
//                            .title(name)
//                            .snippet(cc+" Project"));
//
//                    markers.add(marker);
//
//                }
//                System.out.println(markers.size());
//
//            }else {
//                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
//                AlertDialog dialog = new AlertDialog.Builder(MapsActivity.this)
//                        .setMessage("Please Check Your Internet Connection")
//                        .setPositiveButton("Retry", null)
//                        .show();
//
//                dialog.setCancelable(false);
//                dialog.setCanceledOnTouchOutside(false);
//                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                positive.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        getQuery();
//                        dialog.dismiss();
//                    }
//                });
//            }
//        }
//    }


    //    --------------------------Updating UI with Necessary Data-----------------------------
    public void getQuery() {
        waitProgress.show(getSupportFragmentManager(), "WaitBar");
        waitProgress.setCancelable(false);

        allDivisions = new ArrayList<>();
        allDistricts = new ArrayList<>();
        allUpazilas = new ArrayList<>();

        conn = false;

        String div_url = "http://103.56.208.123:8086/terrain/tr_kabikha/tr_kabikha_map/division_lists";

        String dist_url = "http://103.56.208.123:8086/terrain/tr_kabikha/tr_kabikha_map/district_lists";

        String upa_url = "http://103.56.208.123:8086/terrain/tr_kabikha/tr_kabikha_map/upazila_lists";

        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);


        StringRequest upazilaRequest = new StringRequest(Request.Method.GET, upa_url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray jsonArray = new JSONArray(items);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject upazilaObject = jsonArray.getJSONObject(i);
                        String dd_dist_id = upazilaObject.getString("dd_dist_id");
                        String dd_id = upazilaObject.getString("dd_id");
                        String dd_thana_name = upazilaObject.getString("dd_thana_name");

                        dd_thana_name = transformText(dd_thana_name);

                        allUpazilas.add(new Upazila(dd_dist_id,dd_id,dd_thana_name));

                    }

                }
                conn = true;
                updateUI();

            } catch (JSONException e) {
                e.printStackTrace();
                conn = false;
                updateUI();
            }
        }, error -> {
            conn = false;
            updateUI();
        });

        StringRequest districtRequest = new StringRequest(Request.Method.GET, dist_url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray jsonArray = new JSONArray(items);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject distObject = jsonArray.getJSONObject(i);
                        String dist_div_id = distObject.getString("dist_div_id");
                        String dist_id = distObject.getString("dist_id");
                        String dist_name = distObject.getString("dist_name");

                        dist_name = transformText(dist_name);

                        allDistricts.add(new District(dist_div_id,dist_id,dist_name));
                    }

                }
                requestQueue.add(upazilaRequest);

            } catch (JSONException e) {
                e.printStackTrace();
                conn = false;
                updateUI();
            }
        }, error -> {
            conn = false;
            updateUI();
        });

        StringRequest divRequest = new StringRequest(Request.Method.GET, div_url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray jsonArray = new JSONArray(items);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject divObject = jsonArray.getJSONObject(i);
                        String p_div_id = divObject.getString("div_id");
                        String div_name = divObject.getString("div_name");

                        div_name = transformText(div_name);

                        allDivisions.add(new Division(p_div_id,div_name));
                    }
                }
                requestQueue.add(districtRequest);
            } catch (JSONException e) {
                e.printStackTrace();
                conn = false;
                updateUI();
            }
        }, error -> {
            conn = false;
            updateUI();
        });



        requestQueue.add(divRequest);
    }

    public void updateUI() {
        waitProgress.dismiss();
        if (conn) {

            for (int i = 0; i < allDivisions.size(); i++) {
                divisions.add(allDivisions.get(i).getDiv_name());
            }
            spinnerArrayAdapter.notifyDataSetChanged();
            conn = false;
        }else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            AlertDialog dialog = new AlertDialog.Builder(MapsActivity.this)
                    .setMessage("Please Check Your Internet Connection")
                    .setPositiveButton("Retry", null)
                    .show();

            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {

                getQuery();
                dialog.dismiss();
            });
        }

    }

//    public void gettingInfoDiv(String id) {
//
//        //Division Data
//        try {
//            this.connection = createConnection();
//            //    Toast.makeText(MainActivity.this, "Connected",Toast.LENGTH_SHORT).show();
//
//            Statement stmt = connection.createStatement();
//
//            ResultSet rs=stmt.executeQuery("SELECT\n" +
//                    "    COUNT(project_creation_mst.pcm_id) AS Count_PCM_ID\n" +
//                    "FROM\n" +
//                    "    project_creation_mst,\n" +
//                    "    project_creation_upozila\n" +
//                    "WHERE\n" +
//                    "        project_creation_mst.pcm_id = project_creation_upozila.pcu_pcm_id\n" +
//                    "    AND project_creation_mst.pcm_proj_evaluation_flag = 1\n" +
//                    "    AND project_creation_upozila.pcu_div_id = "+id);
//
//
//
//            while(rs.next()) {
//
//                Total_Count = rs.getString(1);
//
//            }
//
//            nameAndCounts = new ArrayList<>();
//
//            ResultSet resultSet = stmt.executeQuery("SELECT\n" +
//                    "    COUNT(project_creation_mst.pcm_id) AS \"Count_PCM_ID\",\n" +
//                    "    district_dtl.dd_thana_name,\n" +
//                    "    district.dist_name,\n" +
//                    "    division.div_name\n" +
//                    "FROM\n" +
//                    "    project_creation_mst,\n" +
//                    "    project_creation_upozila,\n" +
//                    "    district_dtl,\n" +
//                    "    district,\n" +
//                    "    division\n" +
//                    "WHERE\n" +
//                    "        project_creation_mst.pcm_id = project_creation_upozila.pcu_pcm_id\n" +
//                    "    AND project_creation_upozila.pcu_dd_id = district_dtl.dd_id\n" +
//                    "    AND project_creation_upozila.pcu_dist_id = district.dist_id\n" +
//                    "    AND project_creation_upozila.pcu_div_id = division.div_id\n" +
//                    "    AND project_creation_mst.pcm_proj_evaluation_flag = 1\n" +
//                    "    AND project_creation_upozila.pcu_div_id = "+id+"\n" +
//                    "GROUP BY\n" +
//                    "    district_dtl.dd_thana_name,\n" +
//                    "    district.dist_name,\n" +
//                    "    division.div_name");
//
//            while (resultSet.next()) {
//                nameAndCounts.add(new NameAndCount(resultSet.getString(3),resultSet.getString(4),resultSet.getString(1)));
//            }
//
//
//            infoConnected = true;
//
//            connection.close();
//
//        }
//        catch (Exception e) {
//
//            //   Toast.makeText(MainActivity.this, ""+e,Toast.LENGTH_LONG).show();
//            Log.i("ERRRRR", e.getLocalizedMessage());
//            e.printStackTrace();
//        }
//
//    }

    public void getDivInfo(String id) {
        waitProgress.show(getSupportFragmentManager(), "WaitBar");
        waitProgress.setCancelable(false);
        conn = false;

        nameAndCounts = new ArrayList<>();

        String div_url = "http://103.56.208.123:8086/terrain/tr_kabikha/tr_kabikha_map/div_info?div_id="+id;

        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);

        StringRequest divInfoReq = new StringRequest(Request.Method.GET, div_url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray jsonArray = new JSONArray(items);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject upazilaObject = jsonArray.getJSONObject(i);
                        String count_pcm_id = upazilaObject.getString("count_pcm_id");
                        String dist_name = upazilaObject.getString("dist_name");
                        String div_name = upazilaObject.getString("div_name");
                        String dd_thana_name = upazilaObject.getString("dd_thana_name");

                        dd_thana_name = transformText(dd_thana_name);
                        dist_name = transformText(dist_name);
                        div_name = transformText(div_name);

                        nameAndCounts.add(new NameAndCount(dist_name,div_name,count_pcm_id));
                        Total_Count = count_pcm_id;
                    }

                }
                conn = true;
                updateDivInfo();

            } catch (JSONException e) {
                e.printStackTrace();
                conn = false;
                updateDivInfo();
            }
        }, error -> {
            conn = false;
            updateDivInfo();
        });

        requestQueue.add(divInfoReq);

    }

    public void updateDivInfo() {
        waitProgress.dismiss();
        if (conn) {
            info.setVisibility(View.VISIBLE);

            if (Total_Count.isEmpty()) {
                //Toast.makeText(getApplicationContext(), "No Project Found", Toast.LENGTH_SHORT).show();
                String locText = divisonName+", Bangladesh";
                loc.setText(locText);
                String proText = "No Project Found";
                pro.setText(proText);

            } else {
                //Toast.makeText(getApplicationContext(), "Total Project: "+Total_Count, Toast.LENGTH_SHORT).show();

                String locText = divisonName+", Bangladesh";
                loc.setText(locText);
                String proText = "Total Project: "+Total_Count;
                pro.setText(proText);
            }
            conn = false;
            Total_Count = "";

            if (markers.size() != 0) {

                for (int i = 0; i < markers.size(); i++) {
                    Marker marker = markers.get(i);
                    marker.remove();
                }
                markers = new ArrayList<>();
            }
            for (int i = 0; i < nameAndCounts.size(); i++) {
//                 name = nameAndCounts.get(i).getName();
                String name = nameAndCounts.get(i).getNamebefore();
                String cc = nameAndCounts.get(i).getCount();

                LatLng latLng = location(name);

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(name)
                        .snippet(cc+" Project"));

                markers.add(marker);

            }

        }
        else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            AlertDialog dialog = new AlertDialog.Builder(MapsActivity.this)
                    .setMessage("Please Check Your Internet Connection")
                    .setPositiveButton("Retry", null)
                    .show();

            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {

                getDivInfo(div_id);
                dialog.dismiss();
            });
        }
    }

//    public void gettingInfoDis( String id) {
//
//        //District Data
//        try {
//            this.connection = createConnection();
//            //    Toast.makeText(MainActivity.this, "Connected",Toast.LENGTH_SHORT).show();
//
//            Statement stmt = connection.createStatement();
//            StringBuffer stringBuffer = new StringBuffer();
//
//
//            ResultSet rs=stmt.executeQuery("SELECT\n" +
//                    "    COUNT(project_creation_mst.pcm_id) AS \"Count_PCM_ID\"\n" +
//                    "FROM\n" +
//                    "    project_creation_mst,\n" +
//                    "    project_creation_upozila\n" +
//                    "   \n" +
//                    "WHERE\n" +
//                    "        project_creation_mst.pcm_id = project_creation_upozila.pcu_pcm_id\n" +
//                    "    \n" +
//                    "    AND project_creation_mst.pcm_proj_evaluation_flag = 1\n" +
//                    "    AND project_creation_upozila.pcu_dist_id= "+id);
//
//
//
//            while(rs.next()) {
//                stringBuffer.append("Count: ").append(rs.getString(1)).append("\n");
//
//                Total_Count = rs.getString(1);
//
//            }
//
//            nameAndCounts = new ArrayList<>();
//
//            ResultSet resultSet = stmt.executeQuery("SELECT\n" +
//                    "    COUNT(project_creation_mst.pcm_id) AS \"Count_PCM_ID\",\n" +
//                    "    district_dtl.dd_thana_name,\n" +
//                    "    district.dist_name,\n" +
//                    "    division.div_name\n" +
//                    "FROM\n" +
//                    "    project_creation_mst,\n" +
//                    "    project_creation_upozila,\n" +
//                    "    district_dtl,\n" +
//                    "    district,\n" +
//                    "    division\n" +
//                    "WHERE\n" +
//                    "        project_creation_mst.pcm_id = project_creation_upozila.pcu_pcm_id\n" +
//                    "    AND project_creation_upozila.pcu_dd_id = district_dtl.dd_id\n" +
//                    "    AND project_creation_upozila.pcu_dist_id = district.dist_id\n" +
//                    "    AND project_creation_upozila.pcu_div_id = division.div_id\n" +
//                    "    AND project_creation_mst.pcm_proj_evaluation_flag = 1\n" +
//                    "    AND project_creation_upozila.pcu_dist_id = "+id+"\n" +
//                    "GROUP BY\n" +
//                    "    district_dtl.dd_thana_name,\n" +
//                    "    district.dist_name,\n" +
//                    "    division.div_name");
//
//            while (resultSet.next()) {
//                nameAndCounts.add(new NameAndCount(resultSet.getString(2),resultSet.getString(3),resultSet.getString(1)));
//            }
//
//
//            System.out.println(stringBuffer);
//
//
//            disConeected = true;
//
//            connection.close();
//
//        }
//        catch (Exception e) {
//
//            //   Toast.makeText(MainActivity.this, ""+e,Toast.LENGTH_LONG).show();
//            Log.i("ERRRRR", e.getLocalizedMessage());
//            e.printStackTrace();
//        }
//
//    }

    public void getDistInfo(String id) {
        waitProgress.show(getSupportFragmentManager(), "WaitBar");
        waitProgress.setCancelable(false);
        conn = false;

        nameAndCounts = new ArrayList<>();

        String div_url = "http://103.56.208.123:8086/terrain/tr_kabikha/tr_kabikha_map/dist_info?dist_id="+id;

        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);

        StringRequest distInfoReq = new StringRequest(Request.Method.GET, div_url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray jsonArray = new JSONArray(items);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject upazilaObject = jsonArray.getJSONObject(i);
                        String count_pcm_id = upazilaObject.getString("count_pcm_id");
                        String dist_name = upazilaObject.getString("dist_name");
                        String div_name = upazilaObject.getString("div_name");
                        String dd_thana_name = upazilaObject.getString("dd_thana_name");

                        dd_thana_name = transformText(dd_thana_name);
                        dist_name = transformText(dist_name);
                        div_name = transformText(div_name);

                        nameAndCounts.add(new NameAndCount(dist_name,div_name,count_pcm_id));
                        Total_Count = count_pcm_id;
                    }

                }
                conn = true;
                updateDistInfo();

            } catch (JSONException e) {
                e.printStackTrace();
                conn = false;
                updateDistInfo();
            }
        }, error -> {
            conn = false;
            updateDistInfo();
        });

        requestQueue.add(distInfoReq);
    }

    public void updateDistInfo() {
        waitProgress.dismiss();
        if (conn) {

            info.setVisibility(View.VISIBLE);

            if (Total_Count.isEmpty()) {
                //Toast.makeText(getApplicationContext(), "No Project Found", Toast.LENGTH_SHORT).show();
                String locText = districtName+", "+divisonName+", Bangladesh";
                loc.setText(locText);
                String proText = "No Project Found";
                pro.setText(proText);

            } else {
                //Toast.makeText(getApplicationContext(), "Total Project: "+Total_Count, Toast.LENGTH_SHORT).show();

                String locText = districtName+", "+divisonName+", Bangladesh";
                loc.setText(locText);
                String proText = "Total Project: "+Total_Count;
                pro.setText(proText);
            }
            conn = false;
            Total_Count = "";

            if (markers.size() != 0) {

                for (int i = 0; i < markers.size(); i++) {
                    Marker marker = markers.get(i);
                    marker.remove();
                }
                markers = new ArrayList<>();
            }
            for (int i = 0; i < nameAndCounts.size(); i++) {
                String name = nameAndCounts.get(i).getName();
                name = name+", "+ nameAndCounts.get(i).getNamebefore();
                String cc = nameAndCounts.get(i).getCount();

                LatLng latLng = location(name);

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(name)
                        .snippet(cc+" Project"));

                markers.add(marker);

            }
            System.out.println(markers.size());

        }
        else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            AlertDialog dialog = new AlertDialog.Builder(MapsActivity.this)
                    .setMessage("Please Check Your Internet Connection")
                    .setPositiveButton("Retry", null)
                    .show();

            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {

                getDistInfo(dis_id);
                dialog.dismiss();
            });
        }
    }

//    public void gettingInfoUpa( String id) {
//
//        //District Data
//        try {
//            this.connection = createConnection();
//            //    Toast.makeText(MainActivity.this, "Connected",Toast.LENGTH_SHORT).show();
//
//            Statement stmt = connection.createStatement();
//            StringBuffer stringBuffer = new StringBuffer();
//
//
//            ResultSet rs=stmt.executeQuery("SELECT\n" +
//                    "    COUNT(project_creation_mst.pcm_id) AS \"Count_PCM_ID\",\n" +
//                    "    district_dtl.dd_thana_name,\n" +
//                    "    district.dist_name,\n" +
//                    "    division.div_name\n" +
//                    "FROM\n" +
//                    "    project_creation_mst,\n" +
//                    "    project_creation_upozila,\n" +
//                    "    district_dtl,\n" +
//                    "    district,\n" +
//                    "    division\n" +
//                    "WHERE\n" +
//                    "        project_creation_mst.pcm_id = project_creation_upozila.pcu_pcm_id\n" +
//                    "    AND project_creation_upozila.pcu_dd_id = district_dtl.dd_id\n" +
//                    "    AND project_creation_upozila.pcu_dist_id = district.dist_id\n" +
//                    "    AND project_creation_upozila.pcu_div_id = division.div_id\n" +
//                    "    AND project_creation_mst.pcm_proj_evaluation_flag = 1\n" +
//                    "    AND  project_creation_upozila.pcu_dd_id = "+id+"\n" +
//                    "GROUP BY\n" +
//                    "    district_dtl.dd_thana_name,\n" +
//                    "    district.dist_name,\n" +
//                    "    division.div_name");
//
//
//
//            while(rs.next()) {
//                stringBuffer.append("Count: ").append(rs.getString(1)).append("\n");
//
//                Total_Count = rs.getString(1);
//
//            }
//
//
//            System.out.println(stringBuffer);
//
//            nameAndCounts = new ArrayList<>();
//
//            ResultSet resultSet = stmt.executeQuery("SELECT\n" +
//                    "    COUNT(project_creation_mst.pcm_id) AS \"Count_PCM_ID\",\n" +
//                    "    district_dtl.dd_thana_name,\n" +
//                    "    district.dist_name,\n" +
//                    "    division.div_name\n" +
//                    "FROM\n" +
//                    "    project_creation_mst,\n" +
//                    "    project_creation_upozila,\n" +
//                    "    district_dtl,\n" +
//                    "    district,\n" +
//                    "    division\n" +
//                    "WHERE\n" +
//                    "        project_creation_mst.pcm_id = project_creation_upozila.pcu_pcm_id\n" +
//                    "    AND project_creation_upozila.pcu_dd_id = district_dtl.dd_id\n" +
//                    "    AND project_creation_upozila.pcu_dist_id = district.dist_id\n" +
//                    "    AND project_creation_upozila.pcu_div_id = division.div_id\n" +
//                    "    AND project_creation_mst.pcm_proj_evaluation_flag = 1\n" +
//                    "    AND project_creation_upozila.pcu_dd_id = "+id+"\n" +
//                    "GROUP BY\n" +
//
//                    "    district_dtl.dd_thana_name,\n" +
//                    "    district.dist_name,\n" +
//                    "    division.div_name");
//
//            while (resultSet.next()) {
//                nameAndCounts.add(new NameAndCount(resultSet.getString(2),resultSet.getString(3),resultSet.getString(1)));
//            }
//
//
//            upaConnected = true;
//
//            connection.close();
//
//        }
//        catch (Exception e) {
//
//            //   Toast.makeText(MainActivity.this, ""+e,Toast.LENGTH_LONG).show();
//            Log.i("ERRRRR", e.getLocalizedMessage());
//            e.printStackTrace();
//        }
//
//    }

    public void getUpaInfo(String id) {
        waitProgress.show(getSupportFragmentManager(), "WaitBar");
        waitProgress.setCancelable(false);
        conn = false;

        nameAndCounts = new ArrayList<>();

        String div_url = "http://103.56.208.123:8086/terrain/tr_kabikha/tr_kabikha_map/upa_info?dd_id="+id;

        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);

        StringRequest upaInfoReq = new StringRequest(Request.Method.GET, div_url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray jsonArray = new JSONArray(items);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject upazilaObject = jsonArray.getJSONObject(i);
                        String count_pcm_id = upazilaObject.getString("count_pcm_id");
                        String dist_name = upazilaObject.getString("dist_name");
                        String div_name = upazilaObject.getString("div_name");
                        String dd_thana_name = upazilaObject.getString("dd_thana_name");

                        dd_thana_name = transformText(dd_thana_name);
                        dist_name = transformText(dist_name);
                        div_name = transformText(div_name);

                        nameAndCounts.add(new NameAndCount(dd_thana_name,dist_name,count_pcm_id));
                        Total_Count = count_pcm_id;
                    }

                }
                conn = true;
                updateUpaInfo();

            } catch (JSONException e) {
                e.printStackTrace();
                conn = false;
                updateUpaInfo();
            }
        }, error -> {
            conn = false;
            updateUpaInfo();
        });

        requestQueue.add(upaInfoReq);
    }

    public void updateUpaInfo() {
        waitProgress.dismiss();
        if (conn) {

            info.setVisibility(View.VISIBLE);

            if (Total_Count.isEmpty()) {
                //Toast.makeText(getApplicationContext(), "No Project Found", Toast.LENGTH_SHORT).show();
                String locText = upazilaName+", "+districtName+", "+divisonName+", Bangladesh";
                loc.setText(locText);
                String proText = "No Project Found";
                pro.setText(proText);

            } else {
                //Toast.makeText(getApplicationContext(), "Total Project: "+Total_Count, Toast.LENGTH_SHORT).show();
                String locText = upazilaName+", "+districtName+", "+divisonName+", Bangladesh";
                loc.setText(locText);
                String proText = "Total Project: "+Total_Count;
                pro.setText(proText);
            }
            conn = false;
            Total_Count = "";

            if (markers.size() != 0) {

                for (int i = 0; i < markers.size(); i++) {
                    Marker marker = markers.get(i);
                    marker.remove();
                }
                markers = new ArrayList<>();
            }
            for (int i = 0; i < nameAndCounts.size(); i++) {
                String name = nameAndCounts.get(i).getName();
                name = name+", "+ nameAndCounts.get(i).getNamebefore();
                String cc = nameAndCounts.get(i).getCount();

                LatLng latLng = location(name);

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(name)
                        .snippet(cc+" Project"));

                markers.add(marker);

            }
            System.out.println(markers.size());

        }
        else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            AlertDialog dialog = new AlertDialog.Builder(MapsActivity.this)
                    .setMessage("Please Check Your Internet Connection")
                    .setPositiveButton("Retry", null)
                    .show();

            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {

                getUpaInfo(upa_id);
                dialog.dismiss();
            });
        }
    }

    //    --------------------------Transforming Bangla Text-----------------------------
    private String transformText(String text) {
        byte[] bytes = text.getBytes(ISO_8859_1);
        return new String(bytes, UTF_8);
    }
}
