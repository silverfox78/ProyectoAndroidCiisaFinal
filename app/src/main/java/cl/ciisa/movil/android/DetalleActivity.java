package cl.ciisa.movil.android;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cl.ciisa.data.Recuerdo;
import cl.ciisa.data.dbHelper;
import cl.ciisa.transfer.TrRecuerdo;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

public class DetalleActivity extends AppCompatActivity {

    private dbHelper dbhelper = new dbHelper(this);
    private Recuerdo recuerdo = new Recuerdo(this.dbhelper);

    private TextView txtNombreRecuerdo;
    private ImageView imgRecuerdo;
    private TextView txtValorRecuerdo;
    private TextView txtDescripcionRecuerdo;
    private TextView txtFechaRecuerdo;
    private Button btnMapaRecuerdo;
    private Button btnCompartirRecuerdo;
    private Button btnEliminarRecuerdo;
    private String idRecuerdo;
    private double latitud;
    private double longitud;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        this.txtNombreRecuerdo = (TextView) this.findViewById(R.id.txtNombreRecuerdo);
        this.imgRecuerdo = (ImageView) this.findViewById(R.id.imgRecuerdo);
        this.txtValorRecuerdo = (TextView) this.findViewById(R.id.txtValorRecuerdo);
        this.txtDescripcionRecuerdo = (TextView) this.findViewById(R.id.txtDescripcionRecuerdo);
        this.txtFechaRecuerdo = (TextView) this.findViewById(R.id.txtFechaRecuerdo);
        this.btnMapaRecuerdo = (Button) this.findViewById(R.id.btnMapaRecuerdo);
        this.btnCompartirRecuerdo = (Button) this.findViewById(R.id.btnCompartirRecuerdo);
        this.btnEliminarRecuerdo = (Button) this.findViewById(R.id.btnEliminarRecuerdo);

        this.cargarInformacion();

        this.btnMapaRecuerdo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitud, longitud);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                } catch (Exception ex) {
                    Toast.makeText(DetalleActivity.this, "Error en el mapa: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        this.btnCompartirRecuerdo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    compartir(bitmap);
                } catch (Exception ex) {
                    Toast.makeText(DetalleActivity.this, "Error al compartir: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        this.btnEliminarRecuerdo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    elimiar();
                } catch (Exception ex) {
                    Toast.makeText(DetalleActivity.this, "Error al eliminar: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void elimiar(){
        recuerdo.eliminar(idRecuerdo);
        super.onBackPressed();
    }

    private void compartir(Bitmap bitmap) {
        String mensaje =
                "Articulo: " + this.txtNombreRecuerdo.getText() + "\n" +
                "Descripcion: " + this.txtDescripcionRecuerdo.getText() + "\n" +
                "Valor: " + this.txtValorRecuerdo.getText() + "\n" +
                "Ubicacion: https://maps.google.com/?q=" + Double.toString(this.latitud) + "," + Double.toString(this.longitud);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        Uri imageUri = Uri.parse(path);

        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("image/*");
        whatsappIntent.putExtra(android.content.Intent.EXTRA_STREAM, imageUri);
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(DetalleActivity.this, "Aplicacion no instalada", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarInformacion(){
        try{
            this.idRecuerdo = getIntent().getStringExtra("ID");
            TrRecuerdo informacion = this.recuerdo.buscarPorID(idRecuerdo);
            this.txtNombreRecuerdo.setText(informacion.getNombre());

            this.bitmap = BitmapFactory.decodeByteArray(informacion.getImagen(), 0, informacion.getImagen().length);
            this.imgRecuerdo.setImageBitmap(this.bitmap);
            this.imgRecuerdo.setScaleType(ImageView.ScaleType.CENTER_CROP);
            this.imgRecuerdo.setPadding(10, 10, 10, 10);
            this.txtValorRecuerdo.setText(Double.toString(informacion.getValor()));
            this.txtDescripcionRecuerdo.setText(informacion.getDescripcion());
            this.txtFechaRecuerdo.setText(informacion.getFecha());

            this.latitud = informacion.getLatitud();
            this.longitud = informacion.getLongitud();

        } catch(Exception ex) {
            Toast.makeText(DetalleActivity.this, "Error en la busqueda: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
