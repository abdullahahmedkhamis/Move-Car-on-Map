package commerce.amazoncommerce.traning.RegisterAndLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import commerce.amazoncommerce.traning.R;

public class LoginActivity extends AppCompatActivity {

    private Button mSendOtPBtn;
    private TextView processText;
    private EditText conutryCodeEdit, phoneNumberEdit;
    private FirebaseAuth auth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        mSendOtPBtn = findViewById( R.id.send_codebtn );
        processText = findViewById( R.id.textProccess );
        conutryCodeEdit = findViewById( R.id.inputCountryCode );
        phoneNumberEdit = findViewById( R.id.input_Phone );
        auth = FirebaseAuth.getInstance();

        mSendOtPBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String country_coude = conutryCodeEdit.getText().toString();
                String phone  = phoneNumberEdit.getText().toString();
                String phoneNumber = "+" + country_coude + "" + phone;
                if(!country_coude.isEmpty() || !phone.isEmpty())
                {
                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber( phoneNumber )
                            .setTimeout( 60L , TimeUnit.SECONDS )
                            .setActivity( LoginActivity.this )
                            .setCallbacks( mCallBacks )
                            .build();
                    PhoneAuthProvider.verifyPhoneNumber( options );
                }
                else
                {
                    processText.setText( "Please Enter Country Code ad Phone Number" );
                    processText.setTextColor( Color.RED );
                    processText.setVisibility( View.VISIBLE );
                }


            }
        } );

        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signIn( phoneAuthCredential );

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                processText.setText( e.getMessage() );
                processText.setTextColor( Color.RED );
                processText.setVisibility( View.VISIBLE );
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent( s, forceResendingToken );

                processText.setText( "OTP has been Sent" );
                processText.setVisibility( View.VISIBLE );
                 new Handler().postDelayed( new Runnable() {
                     @Override
                     public void run() {
                         Intent otpIntent = new Intent(LoginActivity.this, OtpActivity.class);
                         otpIntent.putExtra( "auth" , s );
                         startActivity( otpIntent );
                     }
                 },10000 );

            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            sendToMine();
        }
    }

    private void sendToMine(){
        Intent mainIntent = new Intent(LoginActivity.this,SignUp.class);
        startActivity( mainIntent );
        finish();
    }

    private void signIn(PhoneAuthCredential credential)
    {
        auth.signInWithCredential( credential ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    sendToMine();
                }else {
                    processText.setText( task.getException().getMessage() );
                    processText.setTextColor( Color.RED );
                    processText.setVisibility( View.VISIBLE );
                }
            }
        } );
    }
}