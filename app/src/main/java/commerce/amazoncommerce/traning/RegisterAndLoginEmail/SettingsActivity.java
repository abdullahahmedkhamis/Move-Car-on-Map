package commerce.amazoncommerce.traning.RegisterAndLoginEmail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.mbms.MbmsErrors;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

import commerce.amazoncommerce.traning.MapsActivity;
import commerce.amazoncommerce.traning.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button UpdateAccountSettings;
    private EditText userName, userStatus;
    private ImageView userProfileImage;

    private String currenUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private ImageView profilePic;
    public Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_settings );

        mAuth = FirebaseAuth.getInstance();
        currenUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        userProfileImage = findViewById( R.id.set_profile_imgage );
        userProfileImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        } );


        InitializaFields();


        UpdateAccountSettings.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSetting();
            }
        } );
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType( "image/*" );
        intent.setAction( Intent.ACTION_GET_CONTENT );
        startActivityForResult( intent,1 );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if(requestCode==1 && resultCode==RESULT_OK && data !=null && data.getData()!=null){
               imageUri = data.getData();
               profilePic.setImageURI( imageUri );
               uploadPicture();

        }
    }

    private void uploadPicture() {

        final ProgressDialog pd =new ProgressDialog( this );
        pd.setTitle( "Uploading Image..." );
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
        StorageReference riversRef = storageReference.child("images/" + randomKey);

        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       pd.dismiss();
                        Snackbar.make( findViewById( android.R.id.content ) , "Image Uploaded.", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                           pd.dismiss();
                        Toast.makeText( getApplicationContext(), "Failde to Upload.", Snackbar.LENGTH_LONG ).show();
                    }

    }).addOnProgressListener( new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot)
            {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage( "Percentage: " + (int) progressPercent + "%");

            }
        } );
    }

    private void InitializaFields() {

        UpdateAccountSettings = (Button) findViewById( R.id.update_setting_profile );
        userName = (EditText) findViewById( R.id.set_user_name );
        userStatus = (EditText) findViewById( R.id.set_profile_status );
        userProfileImage = (ImageView) findViewById( R.id.set_profile_imgage );
    }


    private void UpdateSetting() {
        String setUserName = userName.getText().toString();
        String setUserStatus = userStatus.getText().toString();

        if (TextUtils.isEmpty( setUserName )) {
            Toast.makeText( this, "please write your username first...", Toast.LENGTH_SHORT ).show();
        }
        if (TextUtils.isEmpty( setUserStatus )) {
            Toast.makeText( this, "please write your status...", Toast.LENGTH_SHORT ).show();
        } else {
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put( "uid", currenUserID );
            profileMap.put( "name", setUserName );
            profileMap.put( "setus", setUserStatus );

            RootRef.child( "Users" ).child( currenUserID ).setValue( profileMap )
                    .addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                SendUserMapsActivity();
                                Toast.makeText( SettingsActivity.this, "Profile update successfully...", Toast.LENGTH_SHORT ).show();
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText( SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT ).show();
                            }
                        }
                    } );
        }

    }

    private void SendUserMapsActivity() {
        Intent intent = new Intent( SettingsActivity.this, MapsActivity.class );
        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity( intent );
        finish();
    }

}