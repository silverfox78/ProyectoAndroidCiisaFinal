package cl.ciisa.movil.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import cl.ciisa.data.Usuario;
import cl.ciisa.data.dbHelper;
import cl.ciisa.transfer.TrUsuario;

import java.io.ByteArrayOutputStream;

public class RegistroActivity extends AppCompatActivity {

    final static int cons =0;
    private dbHelper dbhelper = new dbHelper(this);
    private Usuario usuario = new Usuario(this.dbhelper);
    Bitmap bmp;
    private String username;
    private String clave;

    private ImageView imageView;
    private EditText txtNewName;
    private EditText txtNewUsername;
    private EditText txtNewPassword;
    private Button btnFoto;
    private Button btnLimpiar;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        this.imageView = (ImageView)this.findViewById(R.id.imageView);
        this.txtNewName = (EditText) this.findViewById(R.id.txtNewName);
        this.txtNewUsername = (EditText) this.findViewById(R.id.txtNewUsername);
        this.txtNewPassword = (EditText) this.findViewById(R.id.txtNewPassword);
        this.btnFoto = (Button) this.findViewById(R.id.btnFoto);
        this.btnLimpiar = (Button) this.findViewById(R.id.btnLimpiar);
        this.btnGuardar = (Button) this.findViewById(R.id.btnGuardar);

        this.username = getIntent().getStringExtra("username");
        this.clave = getIntent().getStringExtra("clave");
        this.txtNewUsername.setText(username);

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, cons);

        this.btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i,cons);
            }
        });

        this.btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiar();
            }
        });

        this.btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grabarUsuario();
            }
        });
    }

    private void limpiar(){
        this.txtNewName.setText("");
        this.txtNewUsername.setText(username);
        this.txtNewPassword.setText("");
    }

    private void grabarUsuario(){
        try{
            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(1024);
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 , bytearrayoutputstream);
            byte[] blob = bytearrayoutputstream.toByteArray();

            TrUsuario informacion =
                    new TrUsuario(
                            0,
                            this.txtNewName.getText().toString(),
                            this.txtNewUsername.getText().toString(),
                            this.txtNewPassword.getText().toString(),
                            blob);

            this.usuario.GrabarUsuario(informacion);

            TrUsuario trusuario = this.usuario.BuscaUsuario(this.txtNewUsername.getText().toString());
            Intent intent = new Intent(RegistroActivity.this, PrincipalActivity.class);
            intent.putExtra("id", Integer.toString(trusuario.getId()));
            intent.putExtra("name", trusuario.getNombre());
            intent.putExtra("last_name", "");
            intent.putExtra("email", "");
            intent.putExtra("image_url", "");
            intent.putExtra("tipo", "LOCAL");
            startActivity(intent);
        } catch (Exception ex){
            Toast.makeText(RegistroActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bundle ext = data.getExtras();
            bmp = (Bitmap) ext.get("data");
            imageView.setImageBitmap(bmp);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }
}
