package commerce.amazoncommerce.traning.ReAndLog;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import commerce.amazoncommerce.traning.MapsActivity;
import commerce.amazoncommerce.traning.R;

public class MainActivity4 extends AppCompatActivity  {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView( R.layout.activity_main4);
        super.onCreate( savedInstanceState );



        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    sleep( 1000 );
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    Intent intent = new Intent(MainActivity4.this, MapsActivity.class);

                }
            }
        };
    }
}
