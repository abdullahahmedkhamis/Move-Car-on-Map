package commerce.amazoncommerce.traning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import commerce.amazoncommerce.traning.RegisterAndLoginEmail.SplashActivity;
import io.paperdb.Paper;

// https://console.firebase.google.com/u/1/project/cars-map-ed2e2/authentication/users
// https://www.youtube.com/watch?v=GT-Br9iIqC0&list=PLdHg5T0SNpN3GBUmpGqjiKGMcBaRT2A-m&index=3
// https://www.youtube.com/watch?fbclid=IwAR3mATnz_rejOOU2EMdD0xRBVDjwa7Oc-nbb6Efb_vs88z9CcKWBikFhSPQ&v=zhaAQG4Fcp8&feature=youtu.be
public class MapCars extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener,GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener,GoogleMap.OnMapLongClickListener{

    private static final String TAG = "MapCars";
    private GoogleMap mMap;
    private Geocoder geocoder;
    DrawerLayout drawerLayout;

    NavigationView nav;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    private int ACCESS_LOCATION_REQUEST_CODE = 10001;

    String currentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.cars_map );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );
        geocoder = new Geocoder( this );

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar3 );
        NavigationView navigationView = (NavigationView) findViewById( R.id.nav_view );
        navigationView=(NavigationView) findViewById( R.id.navmenu3 );
        drawerLayout = (DrawerLayout)findViewById( R.id.drawer3 );
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener( toggle );
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener( new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {

                switch (item.getItemId())

                {
                    case R.id.nav_home:
                        Toast.makeText( MapCars.this, "Home", Toast.LENGTH_SHORT ).show();
                        drawerLayout.closeDrawer( GravityCompat.START );
                        break;

                    case R.id.nav_setting:
                        Toast.makeText( MapCars.this, "Phone", Toast.LENGTH_SHORT ).show();
                        drawerLayout.closeDrawer( GravityCompat.START );
                        break;

                    case R.id.nav_sign_out:

                        Paper.book().destroy();


                        Intent intent = new Intent( MapCars.this, SplashActivity.class );
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                }
                return true;
            }
        } );
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
        mMap.setMapType( GoogleMap.MAP_TYPE_NORMAL);

        mMap.setOnMapLongClickListener( this );


        try {
            List<Address> addresses = geocoder.getFromLocationName( "abc.xyz",1 );
if(addresses.size() > 0){
    Address address = addresses.get( 0 );
    LatLng latLng = new LatLng( address.getLatitude(),address.getLongitude() );
    MarkerOptions markerOptions = new MarkerOptions()
            .position( latLng)
            .title( address.getLocality() );
    mMap.addMarker( markerOptions );
    mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( latLng,16 ) );
}
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add a marker in Sydney and move the camera
//        LatLng latLng1 = new LatLng( 27.1751, 78.0421 );
//        MarkerOptions markerOptions1 = new MarkerOptions().position( latLng1 ).title( "My Location" ).snippet( "Welcome" );
//        mMap.addMarker( markerOptions1 );
//        CameraUpdate cameraUpdate1 = CameraUpdateFactory.newLatLngZoom( latLng1,16 );
//        mMap.animateCamera( cameraUpdate1 );




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.activity_main_drawer, menu );
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
    public void pickImage(View v)
    {
        Intent myIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult( myIntent,120 );
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat( "yyyyMMdd_HHmmss" ).format( new Date() );
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir( Environment.DIRECTORY_PICTURES );
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;

    }


    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }
}