package in.ac.srmuniv.srm_uploadfileremoteservice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class SRM_FileUploadClientActivity extends Activity {
    protected static final String LOG_TAG = "DisplayClient";
    private Button disconnectButton;
    private Button uploadButton;
    private EditText edittext;

    private IRemoteFileUploadService IRservice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        disconnectButton = (Button) findViewById(R.id.disconnectButton);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        edittext = (EditText) findViewById(R.id.editText1);
        disconnectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnectService();
            }
        });

        uploadButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        uploadFilesServiceCall();

                    }
                };
                Handler h = new Handler();
                h.post(r);
            }
        });
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            IRservice = IRemoteFileUploadService.Stub.asInterface(service);

        }

        public void onServiceDisconnected(ComponentName className) {
            IRservice = null;

        }
    };

    public void connectService() {
        if (IRservice == null) {
            Intent bindIntent = new Intent(
                    "srmuniv.intent.action.bindMessageService");
            bindIntent.setClassName("in.ac.srmuniv.srm_uploadfileremoteservice",
                    "in.ac.srmuniv.srm_uploadfileremoteservice"
                            + ".SRM_FileUploadRemoteService");
            bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);
            Log.d(LOG_TAG,
                    "The Service will be connected soon (asynchronus call)!");
        }
    }

    public void disconnectService() {
        if (IRservice != null) {
            IRservice = null;
            unbindService(mConnection);
            Log.d(LOG_TAG, "The connection to the service was closed.!");
            uploadButton.setText("Bind");
        }

    }

    public void uploadFilesServiceCall() {
        Log.d(LOG_TAG, "Uploading file from the Service.");
        if (IRservice == null) { // if the service is null the //connection is not established.
            Log.d(LOG_TAG, "The service was not connected -> connecting.");
            connectService();
            uploadButton.setText("Upload");

        } else {
            Log.d(LOG_TAG, "The Service is already connected Uploading file.");
            try {
                String message = IRservice.uploadFile(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/"
                        + edittext.getText().toString());
                Toast toast = Toast.makeText(getApplicationContext(), message,
                        Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            } catch (RemoteException e) {
                Log.e(LOG_TAG, "An error occured during the service call.");
            }
        }
    }
}
