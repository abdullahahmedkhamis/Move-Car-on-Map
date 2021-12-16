package commerce.amazoncommerce.traning.RegisterAndLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import commerce.amazoncommerce.traning.MapsActivity;
import commerce.amazoncommerce.traning.R;

public class SignIn extends AppCompatActivity {

    private Button btnLogin;
    private EditText etPhoneNumber;
    private static final String TAG = "SignIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sign_in );

        btnLogin = findViewById( R.id.btnlogin );
        etPhoneNumber = findViewById( R.id.editPhoneNumber );

        btnLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = etPhoneNumber.getText().toString();
                if(phoneNumber.isEmpty())
                Toast.makeText( SignIn.this, "Enter Your Phone Number ", Toast.LENGTH_SHORT ).show();
                {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber( "+2" + phoneNumber, 60, TimeUnit.SECONDS, SignIn.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    signInUser( phoneAuthCredential );
                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                  Log.d(TAG,"onVerificationFaild:" + e.getLocalizedMessage());
                                }

                                @Override
                                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent( verificationId, forceResendingToken );
                                    Dialog dialog = new Dialog( SignIn.this );
                                    dialog.setContentView( R.layout.verity_popup );
                                    EditText etVerifyCode = dialog.findViewById( R.id.etVerityCode );
                                    Button btnVerifyCode = dialog.findViewById( R.id.btnVerityOTp );
                                    btnVerifyCode.setOnClickListener( new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String verificaionCode = etVerifyCode.getText().toString();
                                            if(verificationId.isEmpty()) return;
                                           PhoneAuthCredential credential =PhoneAuthProvider.getCredential( verificationId ,verificaionCode);
                                            signInUser(credential);
                                        }
                                    } );
                                    dialog.show();

                                }
                            }
                    );
                }
            }
        } );
    }

    private void signInUser(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential( credential )
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
startActivity( new Intent(SignIn.this, MapsActivity.class ) );
finish();
                        }
                        else
                        {
                            Log.d(TAG, "onComplete: " + task.getException().getLocalizedMessage());
                        }
                    }
                } );
    }
}