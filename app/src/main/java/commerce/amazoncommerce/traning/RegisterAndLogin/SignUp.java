package commerce.amazoncommerce.traning.RegisterAndLogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import commerce.amazoncommerce.traning.R;

public class SignUp extends AppCompatActivity {

    private Button mLogOutBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sign_up );

        mLogOutBtn = findViewById( R.id.LogOutButton );
        mAuth = FirebaseAuth.getInstance();

        mLogOutBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity( new Intent(SignUp.this, LoginActivity.class) );
                finish();
            }
        } );
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            startActivity( new Intent(SignUp.this, LoginActivity.class) );
            finish();
        }
    }
}