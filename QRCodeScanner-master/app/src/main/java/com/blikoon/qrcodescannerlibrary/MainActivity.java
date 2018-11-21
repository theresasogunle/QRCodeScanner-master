package com.blikoon.qrcodescannerlibrary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.blikoon.qrcodescanner.QrCodeActivity;

import javax.annotation.Nonnull;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private static final int REQUEST_CODE_QR_SCAN = 101;
    private final String LOGTAG = "QRCScanner-MainActivity";
    Activity activity=MainActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        checkConnection();
        button = (Button) findViewById(R.id.button_start_scan);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start the qr scan activity
                Intent i = new Intent(MainActivity.this,QrCodeActivity.class);
                startActivityForResult( i,REQUEST_CODE_QR_SCAN);
            }
        });


    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if(resultCode != Activity.RESULT_OK)
        {


            Log.d(LOGTAG,"COULD NOT GET A GOOD RESULT.");
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if( result!=null)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                alertDialog.show();
            }
            return;

        }
        if(requestCode == REQUEST_CODE_QR_SCAN)
        {

            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            Log.d(LOGTAG,"Have scan result in your app activity :"+ result);




            getTickets(result);

        }
    }

    private  void getTickets(final String id){
        final ProgressDialog progress = new ProgressDialog(MainActivity.this);
        progress.setTitle("Checking");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        MyApolloClient.getApolloClient()
                .query(AllTicketsQuery.builder().id(id).build())
                .enqueue(new ApolloCall.Callback<AllTicketsQuery.Data>() {
                    @Override
                    public void onResponse(@Nonnull final Response<AllTicketsQuery.Data> response) {
                        Log.d("LOGGG", response.errors().toString());


activity.runOnUiThread(new Runnable() {



    @Override
    public void run() {


        if (response.errors().isEmpty()) {

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setIcon(R.drawable.error);

            alertDialog.setMessage("Access denied, check Barcode ");

            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            progress.dismiss();
                        }
                    });
            alertDialog.show();
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Access Granted");
            alertDialog.setIcon(R.drawable.greentick);

            alertDialog.setMessage("Access Granted");

            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            progress.dismiss();
                        }
                    });
            alertDialog.show();

        }

        }
});

                            update(id);

                    }
                    @Override
                    public void onFailure(@Nonnull ApolloException e) {

                    }
                });


    }

    public  void update(String id){
        MyApolloClient.getApolloClient().mutate(UpdateTicketMutation.builder().id(id).build())
                .enqueue(new ApolloCall.Callback<UpdateTicketMutation.Data>() {
            @Override
            public void onResponse(@Nonnull Response<UpdateTicketMutation.Data> response) {

            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {

            }
        });
    }

    public boolean checkConnection(){
        if(isOnline()){
            Toast.makeText(MainActivity.this, "You are connected to Internet", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this, "You are not connected to Internet", Toast.LENGTH_SHORT).show();
        }
        return  true;
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}
