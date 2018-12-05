package cl.ciisa.movil.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import cl.ciisa.data.Recuerdo;
import cl.ciisa.data.dbHelper;
import cl.ciisa.transfer.TrRecuerdo;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NuevoActivity extends AppCompatActivity {

    private dbHelper dbhelper = new dbHelper(this);
    private Recuerdo recuerdo = new Recuerdo(this.dbhelper);

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imgFotoRecuerdo;
    private Button btnFoto;
    private Button btnLimpiar;
    private Button btnGuardar;

    private EditText txtNombre;
    private EditText txtDescripcion;
    private  EditText txtPrecio;

    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    final static int cons = 0;
    Bitmap bmp;

    private LocationManager locationManager;
    private String provider;
    private MyLocationListener mylistener;
    private Criteria criteria;

    private double latitud;
    private double longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo);

        this.latitud = 0;
        this.longitud = 0;
        this.recuperaGPS();

        this.btnLimpiar = (Button) this.findViewById(R.id.btnLimpiar);
        this.btnFoto = (Button) this.findViewById(R.id.btnFoto);
        this.btnGuardar = (Button) this.findViewById(R.id.btnGuardar);

        this.imgFotoRecuerdo = (ImageView)this.findViewById(R.id.imgFotoRecuerdo);
        this.txtNombre = (EditText) this.findViewById(R.id.txtNombre);
        this.txtDescripcion = (EditText) this.findViewById(R.id.txtDescripcion);
        this.txtPrecio = (EditText) this.findViewById(R.id.txtPrecio);

        this.limpiar();

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, cons);

        this.btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiar();
            }
        });

        this.btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grabarRecuerdo();
            }
        });

        this.btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i,cons);
            }
        });
    }

    private void limpiar(){
        this.txtNombre.setText("");
        this.txtDescripcion.setText("");
        this.txtPrecio.setText("");
    }

    private void grabarRecuerdo(){
        try{
            Bitmap bitmap = ((BitmapDrawable)imgFotoRecuerdo.getDrawable()).getBitmap();
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(1024);
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 , bytearrayoutputstream);
            byte[] blob = bytearrayoutputstream.toByteArray();

            TrRecuerdo informacion =
                    new TrRecuerdo(
                            0,
                            this.txtNombre.getText().toString(),
                            this.txtDescripcion.getText().toString(),
                            Double.parseDouble(this.txtPrecio.getText().toString()),
                            this.latitud,
                            this.longitud,
                            new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime()),
                            blob);

            this.recuerdo.Grabar(informacion);

            super.onBackPressed();
        } catch (Exception ex){
            Toast.makeText(NuevoActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void recuperaGPS(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setCostAllowed(false);
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);
        mylistener = new MyLocationListener();
        if (location != null) {
            mylistener.onLocationChanged(location);
        } else {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        locationManager.requestLocationUpdates(provider, 200, 1, mylistener);
        this.latitud = location.getLatitude();
        this.longitud = location.getLongitude();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bundle ext = data.getExtras();
            bmp = (Bitmap) ext.get("data");
            this.imgFotoRecuerdo.setImageBitmap(bmp);
            this.imgFotoRecuerdo.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            /*Toast.makeText(NuevoActivity.this,  ""+location.getLatitude()+location.getLongitude(),
                    Toast.LENGTH_SHORT).show();*/
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            /*Toast.makeText(NuevoActivity.this, provider + "'s status changed to "+status +"!",
                    Toast.LENGTH_SHORT).show();*/
        }

        @Override
        public void onProviderEnabled(String provider) {
            /*Toast.makeText(NuevoActivity.this, "Provider " + provider + " enabled!",
                    Toast.LENGTH_SHORT).show();*/
        }

        @Override
        public void onProviderDisabled(String provider) {
            /*Toast.makeText(NuevoActivity.this, "Provider " + provider + " disabled!",
                    Toast.LENGTH_SHORT).show();*/
        }
    }
}
