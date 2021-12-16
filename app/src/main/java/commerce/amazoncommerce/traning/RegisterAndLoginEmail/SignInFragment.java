package commerce.amazoncommerce.traning.RegisterAndLoginEmail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.CredentialsOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import commerce.amazoncommerce.traning.MapCars;
import commerce.amazoncommerce.traning.MapsActivity;
import commerce.amazoncommerce.traning.R;
import io.paperdb.Paper;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignInFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Object Prevalent;

    public SignInFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignInFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignInFragment newInstance(String param1, String param2) {
        SignInFragment fragment = new SignInFragment();
        Bundle args = new Bundle();
        args.putString( ARG_PARAM1, param1 );
        args.putString( ARG_PARAM2, param2 );
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        if (getArguments() != null) {
            mParam1 = getArguments().getString( ARG_PARAM1 );
            mParam2 = getArguments().getString( ARG_PARAM2 );
        }
    }

    private TextView dontHaveAnAccount;
    private FrameLayout parentFrameLayout;


    private TextView forogotPassword;

    private ProgressBar progressBar;

    private ImageButton closeBtn;


    private FirebaseAuth firebaseAuth;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    private FrameLayout frameLayout;
    public static boolean onResetPasswordFragment = true;

    private CheckBox checkBoxRemmberme;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;
    private EditText email;
    private EditText password;
    private Button signInBtn;

    CredentialsClient mCredentialsClient;

    int LOCATION_REQUEST_CODE = 10001;
    int RC_SAVE = 10002;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate( R.layout.fragment_sign_in, container, false );

        checkBoxRemmberme = (CheckBox) view.findViewById( R.id.checkBoxRemmberMe );


        dontHaveAnAccount = view.findViewById( R.id.tv_dont_have_an_account );
        parentFrameLayout = getActivity().findViewById( R.id.register_framelayout );

        forogotPassword = view.findViewById( R.id.sign_in_forgot_password );


        email = view.findViewById( R.id.sign_in_email );
        password = view.findViewById( R.id.sign_in_password );

        progressBar = view.findViewById( R.id.sign_in_progress_bar );

        signInBtn = view.findViewById( R.id.sign_in_btn );

        closeBtn = view.findViewById( R.id.sign_in_close_btn );


        checkBoxRemmberme = view.findViewById( R.id.checkBoxRemmberMe );
        Paper.init( getContext() );

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences( getContext() );
        //sharedPreferences = getActivity().getSharedPreferences( "tabian.com.shareedpreferencestest", MODE_PRIVATE );
        mEditor = sharedPreferences.edit();
        checkSheardPreferance();
        signInBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBoxRemmberme.isChecked())
                {
                    mEditor.putString( getString( R.string.checkbok ), "True" );
                    mEditor.commit();

                    String email2 = email.getText().toString();
                    mEditor.putString( getString( R.string.name ) , email2);
                    mEditor.commit();

                    String password2 = password.getText().toString();
                    mEditor.putString( getString( R.string.password ), password2 );
                    mEditor.commit();
                }
                else
                {

                    mEditor.putString( getString( R.string.checkbok ), "False" );
                    mEditor.commit();


                    mEditor.putString( getString( R.string.name ) , "");
                    mEditor.commit();

                    mEditor.putString( getString( R.string.password ), "" );
                    mEditor.commit();

                }
            }
        } );




        firebaseAuth = FirebaseAuth.getInstance();


        CheckBox cbGeslacht = (CheckBox) view.findViewById( R.id.checkBoxRemmberMe );
        cbGeslacht.setOnClickListener( this );
        Button reset = (Button) view.findViewById( R.id.sign_in_btn );
        reset.setOnClickListener( this );
        TextView punt = (TextView) view.findViewById( R.id.textView2 );
        TextView perc = (TextView) view.findViewById( R.id.tv_dont_have_an_account );




        return view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );



        CheckBox stayInCheckBox = (CheckBox) view.findViewById(R.id.checkBoxRemmberMe);
        SharedPreferences sharedpreferences = PreferenceManager  .getDefaultSharedPreferences(getApplicationContext());


        SharedPreferences preferences = getActivity().getSharedPreferences( "checked", MODE_PRIVATE );
        String checkbox = preferences.getString( "remember", "" );
        if (checkbox.equals( "true" )) {

            Intent intent = new Intent( getActivity().getApplication(), MapsActivity.class );
            startActivity( intent );
        } else if (checkbox.equals( "false" )) {
            Toast.makeText( getContext(), "Please Sign In", Toast.LENGTH_SHORT ).show();
        }




        sharedPreferences = getActivity().getSharedPreferences( "MYSP", MODE_PRIVATE );
        final String nm = sharedPreferences.getString( "uname", "" );
        final String ps = sharedPreferences.getString( "upass", "" );
        final boolean saveLogin = sharedPreferences.getBoolean( "save", false );
        final boolean logout = sharedPreferences.getBoolean( "logout", false );
        final boolean[] isChecked = new boolean[1];

        checkBoxRemmberme.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBoxRemmberme.isChecked()) {
                    if (isChecked) {
                        email.setText( nm );
                        password.setText( ps );
                        Toast.makeText( getContext(), "Checkbox Remember me is " + String.valueOf( isChecked ), Toast.LENGTH_LONG ).show();
                    } else {
                        Toast.makeText( getContext(), "Checkbox Remember me is " + String.valueOf( isChecked ), Toast.LENGTH_SHORT ).show();
                    }
                }
            }
        } );

        Credential credential = new Credential.Builder( String.valueOf( email ) ).setPassword( String.valueOf( password ) ).build();

//        GoogleSignInAccount gsa = getActivity().getSupportFragmentManager().getResult();
//        Credential credential1 = new Credential.Builder(gsa.getEmail())
//                .setAccountType( IdentityProviders.GOOGLE)
//                .setName(gsa.getDisplayName())
//                .setProfilePictureUri(gsa.getPhotoUrl())
//                .build();

//        mCredentialsClient.save(credential).addOnCompleteListener(
//                new OnCompleteListener() {
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "SAVE: OK");
//                            Toast.makeText(getActivity(), "Credentials saved", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        Exception e = task.getException();
//                        if (e instanceof ResolvableApiException) {
//                            // Try to resolve the save request. This will prompt the user if
//                            // the credential is new.
//                            ResolvableApiException rae = (ResolvableApiException) e;
//                            try {
//                                rae.startResolutionForResult(getActivity(), RC_SAVE);
//                            } catch (IntentSender.SendIntentException exception) {
//                                // Could not resolve the request
//                                Log.e(TAG, "Failed to send resolution.", exception);
//                                Toast.makeText(getContext(), "Save failed", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            // Request has no resolution
//                            Toast.makeText(getContext(), "Save failed", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });


        CredentialsOptions options = new CredentialsOptions.Builder()
                .forceEnableSaveDialog()
                .build();
        mCredentialsClient = Credentials.getClient( getActivity(), options );


        SharedPreferences preferences1 = getActivity().getSharedPreferences( "checked", MODE_PRIVATE );
        String checkbox1 = preferences1.getString( "remember", "" );
        if (checkbox.equals( "true" )) {

            Intent intent = new Intent( getActivity().getApplication(), MapsActivity.class );
            startActivity( intent );
        } else if (checkbox1.equals( "false" )) {
            Toast.makeText( getContext(), "Please Sign In", Toast.LENGTH_SHORT ).show();
        }

        checkBoxRemmberme.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkBoxRemmberme.isChecked(  )){
                    checkBoxRemmberme.setChecked( true );

                }

                else
                {
                    checkBoxRemmberme.setChecked( false );
                }
            }
        } );

        checkBoxRemmberme.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    SharedPreferences preferences = getActivity().getSharedPreferences( "checkBox", MODE_PRIVATE );
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString( "remember", "true" );
                    editor.apply();
                    Toast.makeText( getContext(), "Checked", Toast.LENGTH_SHORT ).show();


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        password.setImportantForAutofill( View.IMPORTANT_FOR_AUTOFILL_NO );
                    }

                } else if (!buttonView.isChecked()) {

                    SharedPreferences preferences = getActivity().getSharedPreferences( "checkBox", MODE_PRIVATE );
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString( "remember", "false" );
                    editor.apply();
                    Toast.makeText( getContext(), "Unchecked", Toast.LENGTH_SHORT ).show();
                }
            }
        } );

        dontHaveAnAccount.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setFragment( new SignUpFragment() );
            }
        } );

        closeBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainIntent();
            }
        } );


        forogotPassword.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onResetPasswordFragment = true;
                setFragment( new ResetPasswordFragment() );
                                                }
                                            }
        );

        email.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );
        password.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );
        signInBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailAndPassword();

            }
        } );

//        SharedPreferences shared = getActivity().getSharedPreferences( "shared", MODE_PRIVATE );
//        if (shared.contains( "username" ) && shared.contains( "password" )) {
//            getActivity().finish();
//        } else {
//            saveInformation(  );
//        }
    }


//    public void saveInformation(String username, String password) {
//        SharedPreferences shared = getActivity().getSharedPreferences( "shared", MODE_PRIVATE );
//        SharedPreferences.Editor editor = shared.edit();
//        editor.putString( "username", username );
//        editor.putString( "password", password );
//        editor.commit();
//    }


    private void setFragment(Fragment fragment) {


        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations( R.anim.slide_from_right, R.anim.slideout_from_left );
        fragmentTransaction.replace( parentFrameLayout.getId(), fragment );
        fragmentTransaction.commit();

    }

    private void checkInputs() {
        if (!TextUtils.isEmpty( email.getText() )) {
            if (!TextUtils.isEmpty( password.getText() )) {
                signInBtn.setEnabled( true );
                signInBtn.setTextColor( Color.rgb( 255, 255, 255 ) );
            } else {
                signInBtn.setEnabled( false );
                signInBtn.setTextColor( Color.argb( 50, 255, 255, 255 ) );
            }
        } else {
            signInBtn.setEnabled( false );
            signInBtn.setTextColor( Color.argb( 50, 255, 255, 255 ) );
        }
    }

    private void checkEmailAndPassword() {

        if (email.getText().toString().matches( emailPattern )) {
            if (password.length() >= 8) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();


                progressBar.setVisibility( View.VISIBLE );
                signInBtn.setEnabled( false );
                signInBtn.setTextColor( Color.argb( 50, 255, 255, 255 ) );

                firebaseAuth.signInWithEmailAndPassword( email.getText().toString(), password.getText().toString() )
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    mainIntent();
                                } else {
                                    progressBar.setVisibility( View.INVISIBLE );
                                    signInBtn.setEnabled( true );
                                    signInBtn.setTextColor( Color.rgb( 255, 255, 255 ) );
                                    String error = task.getException().getMessage();
                                    Toast.makeText( getActivity(), error, Toast.LENGTH_SHORT ).show();
                                }
                            }
                        } );

            } else {
                Toast.makeText( getActivity(), "Incorrect email or password !", Toast.LENGTH_SHORT ).show();
            }
        } else {
            Toast.makeText( getActivity(), "Incorrect email or password !", Toast.LENGTH_SHORT ).show();
        }
    }




    private void mainIntent() {
        Intent mainIntent = new Intent( getActivity(), MapsActivity.class );
        startActivity( mainIntent );
        getActivity().finish();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );


        if (requestCode == RC_SAVE) {
            if (resultCode == RESULT_OK) {
                Log.d( TAG, "SAVE: OK" );
                Toast.makeText( getContext(), "Credentials saved", Toast.LENGTH_SHORT ).show();
            } else {
                Log.e( TAG, "SAVE: Canceled by user" );
            }
        }


    }

    @Nullable

    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {

            view.findViewById( R.id.checkBoxRemmberMe );
        }


        CheckBox cb = (CheckBox) view.findViewById( R.id.checkBoxRemmberMe );


        return super.getView();
    }


    public void reset(View view) {

        CheckBox geslacht = (CheckBox) view.findViewById(R.id.checkBoxRemmberMe);

//        TextView punt = (TextView) view.findViewById(R.id.tvPunt);
//        TextView perc = (TextView) view.findViewById(R.id.tvPercentage);
//        TextView advies = (TextView) view.findViewById(R.id.tvToelicht);

        geslacht.setChecked(false);

        double score = 0;
        //manPl = 0;

        //punt.setText(Double.toString(score));
        //perc.setText(R.string.pt4);
       // perc.setBackgroundColor(getResources().getColor(holo_green_light));
        //advies.setText(R.string.pt4a);

    }

    public void update(View view) {

//        TextView punt = (TextView) view.findViewById(R.id.tvPunt);
//        TextView perc = (TextView) view.findViewById(R.id.tvPercentage);
//        TextView advies = (TextView) view.findViewById(R.id.tvToelicht);

//        score = manPl;
//        punt.setText(Double.toString(score));
//
//        if (score <= 4) {
//            perc.setText(R.string.pt4);
//            perc.setBackgroundColor(getResources().getColor(holo_green_light));
//            advies.setText(R.string.pt4a);
//        } else if (score < 8) {
//            perc.setText(R.string.pt48);
//            perc.setBackgroundColor(getResources().getColor(holo_orange_light));
//            advies.setText(R.string.pt48a);
        //} else {
            //perc.setText(R.string.pt8);
            //perc.setBackgroundColor(getResources().getColor(holo_red_light));
            //advies.setText(R.string.pt8a);
       // }

   }


    @Override
    public void onClick(View v) {

        CheckBox geslacht = (CheckBox) v.findViewById( R.id.checkBoxRemmberMe );

        switch (v.getId()) {
            case R.id.checkBoxRemmberMe:
                if (geslacht.isChecked()) {

                } else {

                }
                update( v);
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(checkBoxRemmberme.equals( true )){
            checkBoxRemmberme.setChecked( true );
        }else {
            checkBoxRemmberme.setChecked( false );
        }
    }

    public void saveCheckBoxState(boolean isChecked){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("key",isChecked);
        editor.commit();
    }

    public boolean getCheckBoxState(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return prefs.getBoolean("key", false);
    }

// https://stackoverflow.com/questions/22927032/remember-me-in-android-app

    private void checkSheardPreferance(){
        String checkbox = sharedPreferences.getString(getString( R.string.checkbok ),"False");
        String email1 = sharedPreferences.getString(getString( R.string.name ),"");
        String password1 = sharedPreferences.getString(getString( R.string.password ),"");

        email.setText( email1 );
        password.setText( password1 );

        if(checkbox.equals( "True" )){
            checkBoxRemmberme.setChecked( true );
        }else {
            checkBoxRemmberme.setChecked( false );
        }

    }
}