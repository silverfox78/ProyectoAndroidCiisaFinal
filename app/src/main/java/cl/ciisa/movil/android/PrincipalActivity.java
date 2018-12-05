package cl.ciisa.movil.android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import cl.ciisa.data.Recuerdo;
import cl.ciisa.data.Usuario;
import cl.ciisa.data.dbHelper;
import cl.ciisa.transfer.TrRecuerdo;
import cl.ciisa.transfer.TrUsuario;
import com.facebook.login.widget.ProfilePictureView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class PrincipalActivity extends AppCompatActivity {

    private TextView txtNombreUsuario;
    private TextView txtNombreLocal;
    private ImageView imageView;
    private LinearLayout divFacebook;
    private LinearLayout divLocal;

    private dbHelper dbhelper = new dbHelper(this);
    private Usuario usuario = new Usuario(this.dbhelper);
    private Recuerdo recuerdo = new Recuerdo(this.dbhelper);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        String id = getIntent().getStringExtra("id");
        String name = getIntent().getStringExtra("name");
        String tipo = getIntent().getStringExtra("tipo");

        this.txtNombreUsuario = (TextView) this.findViewById(R.id.txtNombreUsuario);
        this.txtNombreLocal = (TextView) this.findViewById(R.id.txtNombreLocal);
        this.imageView = (ImageView) this.findViewById(R.id.imageView);
        this.divFacebook = (LinearLayout) this.findViewById(R.id.divFacebook);
        this.divLocal = (LinearLayout) this.findViewById(R.id.divLocal);

        ProfilePictureView profilePictureView;
        profilePictureView = (ProfilePictureView) findViewById(R.id.friendProfilePicture);

        if(tipo.equals("LOCAL")){
            try{
                this.divFacebook.setVisibility(View.GONE);
                this.divLocal.setVisibility(View.VISIBLE);
                this.txtNombreLocal.setText("Bienvenido:\n" + name);
                TrUsuario informacion = this.usuario.BuscaUsuarioPorID(id);
                Bitmap bitmap = BitmapFactory.decodeByteArray(informacion.getImagen(), 0, informacion.getImagen().length);
                this.imageView.setImageBitmap(bitmap);
                this.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                this.imageView.setMinimumWidth(200);
                this.imageView.setMinimumHeight(200);
                this.imageView.setPadding(10, 10, 10, 10);
            } catch (Exception ex){
                Toast.makeText(PrincipalActivity.this, id + " - Error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            this.divFacebook.setVisibility(View.VISIBLE);
            this.divLocal.setVisibility(View.GONE);
            profilePictureView.setProfileId(id);
            this.txtNombreUsuario.setText("Bienvenido:\n" + name);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(PrincipalActivity.this, NuevoActivity.class));
            }
        });

        this.cargarGrilla();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        this.cargarGrilla();
    }

    private void cargarGrilla(){
        TableLayout tabla = (TableLayout) findViewById(R.id.tbl_datos);
        tabla.removeAllViews();
        boolean par = false;
        for (TrRecuerdo recuerdo: recuerdo.listar()) {
            String mensaje =
                    Integer.toString(recuerdo.getId()) + ".- " + recuerdo.getNombre() + " - [" + Double.toString(recuerdo.getValor()) +
                    "]\n" + recuerdo.getDescripcion() + "\n" +
                    recuerdo.getFecha();
            TableRow fila = new TableRow(PrincipalActivity.this);
            TextView textoFila = new TextView(PrincipalActivity.this);
            ImageView imagenFila = new ImageView(PrincipalActivity.this);

            Bitmap bitmap = BitmapFactory.decodeByteArray(recuerdo.getImagen(), 0, recuerdo.getImagen().length);
            imagenFila.setImageBitmap(bitmap);
            imagenFila.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imagenFila.setMinimumWidth(200);
            imagenFila.setMinimumHeight(200);
            imagenFila.setPadding(50, 10, 10, 10);

            textoFila.setText(mensaje);
            textoFila.setPadding(3, 3, 3, 8);
            textoFila.setGravity(Gravity.CENTER_VERTICAL);

            fila.addView(imagenFila, new TableRow.LayoutParams());
            fila.addView(textoFila, new TableRow.LayoutParams());
            fila.setBackgroundResource(par ? R.color.grisClaro : R.color.grisOscuro);
            fila.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    try {
                        TableRow t = (TableRow) view;
                        TextView firstTextView = (TextView) t.getChildAt(1);
                        String texto = firstTextView.getText().toString();
                        String idRecuerdo = texto.split(".-")[0];
                        Intent intentRecuerdo = new Intent(PrincipalActivity.this, DetalleActivity.class);
                        intentRecuerdo.putExtra("ID", idRecuerdo);
                        startActivity(intentRecuerdo);
                    } catch (Exception ex){
                        Toast.makeText(PrincipalActivity.this, "Error en la seleccion: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            par = !par;
            tabla.addView(fila, new TableLayout.LayoutParams());
            fila.requestLayout();
        }
    }
}
