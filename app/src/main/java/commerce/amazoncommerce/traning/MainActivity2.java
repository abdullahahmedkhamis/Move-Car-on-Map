package commerce.amazoncommerce.traning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity2 extends AppCompatActivity {

    EditText etSource, etDestination;
    Button btTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main2 );

        etSource = findViewById( R.id.et_source );
        etDestination = findViewById( R.id.et_destionation );
        btTrack = findViewById( R.id.bt_track );

        btTrack.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sSource = etSource.getText().toString().trim();
                String sDestionation = etSource.getText().toString().trim();

                if (sSource.equals( "" ) && sDestionation.equals( "" )) {
                    Toast.makeText( MainActivity2.this, "Enter both location", Toast.LENGTH_SHORT ).show();
                } else {
                    DisplayTrack( sSource, sDestionation );
                }

            }
        } );
    }

    private void DisplayTrack(String sSource, String sDestionation) {

        try {
            Uri uri = Uri.parse( "https://www.google.co.in/maps/dir" + sSource + "/"
                    + sDestionation );

            Intent intent = new Intent( Intent.ACTION_VIEW, uri );

            intent.setPackage( "com.google.android.apps.maps" );


            startActivity( intent );

        } catch (ActivityNotFoundException e) {

            Uri uri = Uri.parse( "https://play.google.com/store/apps/details?id=com.google.android.apps.maps" );

            Intent intent = new Intent( Intent.ACTION_VIEW, uri );

            startActivity( intent );
        }
    }
}
