package commerce.amazoncommerce.traning.RegisterAndLoginEmail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import commerce.amazoncommerce.traning.MapsActivity;
import commerce.amazoncommerce.traning.R;
import commerce.amazoncommerce.traning.RegisterAndLogin.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash );

        firebaseAuth = FirebaseAuth.getInstance();

        SystemClock.sleep( 1000 );
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if(currentUser == null)
        {
            Intent registerIntent = new Intent(SplashActivity.this, RegisterActivity.class);
            startActivity( registerIntent );
            finish();
        }
        else
        {
            Intent mainIntent = new Intent(SplashActivity.this, RegisterActivity.class);  // MainActivity
            startActivity( mainIntent );
            finish();
        }
    }
}