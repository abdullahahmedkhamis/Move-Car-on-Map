package commerce.amazoncommerce.traning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.multidex.MultiDex;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commerce.amazoncommerce.traning.ReAndLog.Common;
import commerce.amazoncommerce.traning.ReAndLog.Utils.UserUtils;
import commerce.amazoncommerce.traning.RegisterAndLoginEmail.SplashActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener,
        NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMapLongClickListener{

    private static final String TAG = "MapsActivity";
    private static final int PICK_IMAGE_REQUEST = 7172;
    private GoogleMap mMap;
    private Geocoder geocoder;
    private int ACCESS_LOCATION_REQUEST_CODE = 10001;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentPhotoPath;


    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    Marker userLocatiomMarker;
    Circle userLocationAccuracyCircle;

    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;

    Button logout;


    private Uri imageUri;
    private ImageView image;
    GeoFire geoFire;
    SupportMapFragment mapFragment;
    DatabaseReference onlineRef,curentUserRef,driversLocationRef;
    private AlertDialog waitingDialog;
    private StorageReference storageReference;

    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private DatabaseReference userIdRef;




    ValueEventListener onlineValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
if(snapshot.exists())
{
    curentUserRef.onDisconnect().removeValue();
}
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
Snackbar.make( mapFragment.getView(),error.getMessage(),Snackbar.LENGTH_LONG ).show();
        }
    };

    @Override
    protected void onDestroy() {
        fusedLocationProviderClient.removeLocationUpdates( locationCallback );
        geoFire.removeLocation( FirebaseAuth.getInstance().getCurrentUser().getUid() );
        onlineRef.removeEventListener( onlineValueEventListener );
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerOnlineSystem();
    }

    private void registerOnlineSystem() {
        onlineRef.addValueEventListener( onlineValueEventListener );

    }

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private static final int GalleryPick = 1;
    CircleImageView avatarIv,profileImageViwe;
    TextView nameTv, emailTv,phoneTv;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_maps );
        MultiDex.install(this);

        profileImageViwe = (CircleImageView) findViewById( R.id.set_profile_imgage );
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();



        logout = findViewById( R.id.nav_sign_out );

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );

        nav=(NavigationView) findViewById( R.id.navmenu );
        drawerLayout = (DrawerLayout)findViewById( R.id.drawer );

        toggle = new ActionBarDrawerToggle (this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener( toggle );
        toggle.syncState();



        NavigationView navigationView = (NavigationView) findViewById( R.id.nav_view );


      init();



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );
        geocoder = new Geocoder( this );

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( this );

        locationRequest = LocationRequest.create();
        locationRequest.setInterval( 500 );
        locationRequest.setFastestInterval( 500 );
        locationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK)
        {
            if(data != null && data.getData() != null )
            {
                imageUri = data.getData();
                image.setImageURI( imageUri );

                showDialogUpload();

            }
        }
    }

    private void showDialogUpload() {
AlertDialog.Builder builder = new AlertDialog.Builder( MapsActivity.this );
builder.setTitle( "Change Image" )
        .setMessage( "Do you want to change Image" )
        .setNegativeButton( "Cancel" , (dialog, which) -> dialog.dismiss() )
    .setNegativeButton( "Upload",  (dialog, which) ->{
      if(imageUri != null)
      {
          waitingDialog.setMessage( "Uploading..." );
          waitingDialog.show();
          String unique_name = FirebaseAuth.getInstance().getCurrentUser().getUid();
          StorageReference Folder = storageReference.child( "image/"+ unique_name);
          Folder.putFile( imageUri )
                  .addOnFailureListener( new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                      waitingDialog.dismiss();
                          Snackbar.make( drawerLayout, e.getMessage(),Snackbar.LENGTH_LONG).show();
                      }
                  } ).addOnCompleteListener( new OnCompleteListener<UploadTask.TaskSnapshot>() {
              @Override
              public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                  if(task.isSuccessful())
                  {
                      Folder.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                          @Override
                          public void onSuccess(Uri uri) {
                              Map<String,Object> updateData = new HashMap<>();
                              updateData.put( "image", uri.toString() );

                              UserUtils.updateUser( drawerLayout,updateData );
                          }
                      } );
                  }
                  waitingDialog.dismiss();  // That was not here
              }

          } ).addOnProgressListener( new OnProgressListener<UploadTask.TaskSnapshot>() {
              @Override
              public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                  double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                  waitingDialog.setMessage( new StringBuilder("Uploading...").append( progress ).append( "%" ) );
              }
          } );

//          FirebaseAuth.getInstance().signOut();
//          Intent intent = new Intent(MapsActivity.this, SplashActivity.class);        // That was made wrong
//          intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
//          startActivity( intent );
//          finish();
      }
    } )
        .setCancelable( false );
AlertDialog dialog = builder.create();
dialog.setOnShowListener( dialogInterface -> {
    dialog.getButton( AlertDialog.BUTTON_POSITIVE )
            .setTextColor( getResources().getColor( android.R.color.holo_red_dark ) );
    dialog.getButton( AlertDialog.BUTTON_NEGATIVE )
            .setTextColor( getResources().getColor( R.color.colorAccent ) );
} );
dialog.show();
    }

    private void init() {
        onlineRef = FirebaseDatabase.getInstance().getReference().child( ".info/connected" );
        driversLocationRef = FirebaseDatabase.getInstance().getReference(Common.DRIVER_LOCATION_REFERENCE);
        curentUserRef = FirebaseDatabase.getInstance().getReference(Common.DRIVER_LOCATION_REFERENCE)
                .child( FirebaseAuth.getInstance().getCurrentUser().getUid() );
        geoFire = new GeoFire( driversLocationRef );
        registerOnlineSystem();


        waitingDialog = new AlertDialog.Builder( this )
                .setCancelable( false )
                .setMessage( "Waitting..." )
                .create();
        storageReference = FirebaseStorage.getInstance().getReference();


        nav.setNavigationItemSelectedListener( new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {

                switch (item.getItemId())

                {
                    case R.id.nav_home:
                        Toast.makeText( MapsActivity.this, "Home", Toast.LENGTH_SHORT ).show();
                        drawerLayout.closeDrawer( GravityCompat.START );
                        break;

                    case R.id.nav_setting:
                        Toast.makeText( MapsActivity.this, "Phone", Toast.LENGTH_SHORT ).show();
                        drawerLayout.closeDrawer( GravityCompat.START );
                        break;


                    case R.id.nav_sign_out:

                        Paper.book().destroy();


                        Intent intent = new Intent( MapsActivity.this, SplashActivity.class );
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                }
                return true;
            }
        } );

        nav.setNavigationItemSelectedListener( new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.nav_sign_out)
                {
                   AlertDialog.Builder builder = new AlertDialog.Builder( MapsActivity.this ) ;
                   builder.setTitle( "Sign Out" )
                           .setMessage( "Do you really want sign out?" )
                           .setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   dialog.dismiss();
                               }
                           } ).setPositiveButton( "Sign out", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
FirebaseAuth.getInstance().signOut();
Intent intent = new Intent(MapsActivity.this,SplashActivity.class);
intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
startActivity( intent );
finish();
                       }
                   } ).setCancelable( false );
                   AlertDialog dialog = builder.create();
                   dialog.setOnShowListener( dialog1 -> {
                       dialog.getButton( AlertDialog.BUTTON_POSITIVE )
                               .setTextColor( getResources().getColor( android.R.color.holo_red_dark ) );
                       dialog.getButton( AlertDialog.BUTTON_NEGATIVE )
                               .setTextColor( getResources().getColor( R.color.black ) );
                   } );
                   dialog.show();
                }
                return true;
            }

        } );

        View header = nav.getHeaderView( 0 );
        TextView name = (TextView) header.findViewById( R.id.txt_name );
        TextView phone = (TextView) header.findViewById( R.id.txt_phone );
        TextView star = (TextView) header.findViewById( R.id.txt_star);
        image = (ImageView) header.findViewById( R.id.set_profile_imgage );


        name.setText( Common.buildWelcomeMessage() );
        name.setText( Common.currentUser !=null ?  Common.currentUser.getFirstname()  : "Programmer Abdullah" );
        phone.setText( Common.currentUser != null ? Common.currentUser.getPhoneNumber() : "00201150629997" );
        star.setText( Common.currentUser != null ? String.valueOf( Common.currentUser.getRating() ) : "0.0" );

       image.setOnClickListener( v -> {
     Intent intent =new Intent();
     intent.setType( "image/*" );
     intent.setAction( Intent.ACTION_GET_CONTENT );
     startActivityForResult( intent, PICK_IMAGE_REQUEST );
       } );

        if(Common.currentUser != null && Common.currentUser.getAvatar() != null &&
        !TextUtils.isEmpty( Common.currentUser.getAvatar() ))
        {
            Glide.with( this )
                    .load( Common.currentUser.getAvatar() )
                    .into( image );

        }

    }



//    public void pickImage(View v)
//    {
//        Intent myIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult( myIntent,120 );
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if(requestCode == 120 && resultCode == RESULT_OK && data!=null)
//        {
//            Uri selectedImageUri = data.getData();
//            String[] filePath = {MediaStore.Images.Media.DATA};
//            Cursor cursor = getContentResolver().query( selectedImageUri,filePath,null,null,null );
//            cursor.moveToFirst();
//
//            int columnIndex = cursor.getColumnIndex( filePath[0] );
//            String myPath = cursor.getString( columnIndex );
//            cursor.close();
//
//            Bitmap bitmap = BitmapFactory.decodeFile( myPath );
//           profileImageViwe.setImageBitmap( bitmap );
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            profileImageViwe.setImageBitmap(imageBitmap);
//        }
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType( GoogleMap.MAP_TYPE_NORMAL ); // MAP_TYPE_SATELLITE

        mMap.setOnMapClickListener( this );
        mMap.setOnMarkerDragListener( this );

        if(ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION )  == PackageManager
                .PERMISSION_GRANTED)
        {

        }else {
            if (ActivityCompat.shouldShowRequestPermissionRationale( this,Manifest.permission.ACCESS_FINE_LOCATION )){
                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_LOCATION_REQUEST_CODE );
            }else
            {
                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_LOCATION_REQUEST_CODE );
            }
        }


        try {
            List<Address> addresses = geocoder.getFromLocationName( "abc.xyz", 1 );
            if (addresses.size() > 0) {
                Address address = addresses.get( 0 );
                LatLng lodon = new LatLng( address.getLatitude(), address.getLongitude() );
                MarkerOptions markerOptions = new MarkerOptions()
                        .position( lodon )
                        .title( address.getLocality() );
                mMap.addMarker( markerOptions );
                mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( lodon, 1 ) );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult( locationResult );
            Log.d(TAG, "onLocationResult: " + locationResult.getLastLocation());

            if (mMap != null)
            {
                setUserLocationMarker( locationResult.getLastLocation() );
            }
        }
    };

    private void setUserLocationMarker(Location location)
    {
        LatLng latLng = new LatLng( location.getLatitude(),location.getLongitude() );
        if(userLocatiomMarker == null)
        {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position( latLng );
            markerOptions.icon( BitmapDescriptorFactory.fromResource( R.drawable.car ) );
            markerOptions.rotation( location.getBearing() );
            markerOptions.anchor( (float)  0.5 , (float) 0.5);
            userLocatiomMarker = mMap.addMarker( markerOptions );
            mMap.animateCamera( CameraUpdateFactory.newLatLngZoom( latLng,17 ) );
        }
        else
        {
            userLocatiomMarker.setPosition( latLng );
            userLocatiomMarker.setRotation( location.getBearing() );
            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( latLng,17 ) );
        }

        if(userLocationAccuracyCircle == null)
        {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center( latLng );
            circleOptions.strokeWidth( 4 );
            circleOptions.strokeColor( Color.argb( 255,255,0,0 ) );
            circleOptions.fillColor( Color.argb( 32,255 ,0,0) );
            circleOptions.radius( location.getAccuracy() );
            userLocationAccuracyCircle = mMap.addCircle( circleOptions );
        }
        else
        {
            userLocationAccuracyCircle.setCenter( latLng );
            userLocationAccuracyCircle.setRadius( location.getAccuracy() );
        }
    }


    private void startLocationUpdates()
    {
        fusedLocationProviderClient.requestLocationUpdates( locationRequest, locationCallback, Looper.getMainLooper() );

    }

    private void stopLocationUpdates()
    {
        fusedLocationProviderClient.removeLocationUpdates( locationCallback );
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            startLocationUpdates();
        }
        else
        {

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    private void enableUserLocation()
    {
        mMap.setMyLocationEnabled( true );

    }

    private void zoomToUserLocation(){
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener( new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location)
            {
                LatLng latLng = new LatLng( location.getLatitude(), location.getLongitude() );
                mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( latLng,20 ) );

            }
        } );

    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d( TAG, "onMapLongClick: " + latLng.toString() );
        try {
            List<Address> addresses = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1 );
            if (addresses.size() > 0) {
                Address address = addresses.get( 0 );
                String streetAddress = address.getAddressLine( 0 );
                mMap.addMarker( new MarkerOptions().position( latLng ).title( streetAddress ).draggable( true ) );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Log.d( TAG, "onMarkerDragStart: " );
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Log.d( TAG, "onMarkerDrag: " );
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Log.d( TAG, "onMarkerDragEnd: " );
        LatLng latLng = marker.getPosition();
        try {
            List<Address> addresses = geocoder.getFromLocationName( "abc.xyz", 1 );
            if (addresses.size() > 0) {
                Address address = addresses.get( 0 );
                LatLng streetAddress = new LatLng( address.getLatitude(), address.getLongitude() );
                marker.setTitle( String.valueOf( streetAddress ) );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(requestCode == ACCESS_LOCATION_REQUEST_CODE)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                enableUserLocation();
                zoomToUserLocation();
            }else {

            }
        }

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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

    @Override
    public void onMapLongClick(LatLng latLng) {    // That is method was not here beacous was marker not show
        Log.d(TAG, "onMapLongClick: " + latLng.toString());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(streetAddress)
                        .draggable(true)
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        try {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        } catch (ActivityNotFoundException e) {
//            // display error state to the user
//        }
//    }

//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.example.android.fileprovider",
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//            }
//        }
//    }


//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir( Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        currentPhotoPath = image.getAbsolutePath();
//        return image;
//    }

}