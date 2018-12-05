package cl.ciisa.movil.android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cl.ciisa.data.Usuario;
import cl.ciisa.data.dbHelper;
import cl.ciisa.transfer.TrUsuario;
import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private AccessToken mAccessToken;
    private LoginButton btnLoginFB;
    private EditText txtUserLogin;
    private EditText txtClaveLogin;
    private Button btnLimpiarLogin;
    private Button btnIngresarLogin;

    private dbHelper dbhelper = new dbHelper(this);
    private Usuario usuario = new Usuario(this.dbhelper);

    private void getFbInfo() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            String id = object.getString("id");
                            String name = object.getString("name");
                            String last_name = object.getString("last_name");
                            //String birthday = object.getString("birthday");
                            String image_url = "http://graph.facebook.com/" + id + "/picture?type=large";

                            String email = "";
                            if (object.has("email")) {
                                email = object.getString("email");
                            }

                            Intent intent = new Intent(MainActivity.this, PrincipalActivity.class);
                            intent.putExtra("id", id);
                            intent.putExtra("name", name);
                            intent.putExtra("last_name", last_name);
                            intent.putExtra("email", email);
                            intent.putExtra("image_url", image_url);
                            intent.putExtra("tipo", "FACEBOOK");
                            startActivity(intent);

                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "Error al buscar la info... + " + e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(200),birthday,gender,first_name,last_name"); // id,first_name,last_name,email,gender,birthday,cover,picture.type(large)
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();

        this.btnLoginFB = (LoginButton)findViewById(R.id.btnLoginFB);
        this.btnLoginFB.setReadPermissions("email");
        this.txtUserLogin = (EditText) findViewById(R.id.txtUserLogin);
        this.txtClaveLogin = (EditText) findViewById(R.id.txtClaveLogin);
        this.btnLimpiarLogin = (Button) findViewById(R.id.btnLimpiarLogin);
        this.btnIngresarLogin = (Button) findViewById(R.id.btnIngresarLogin);

        this.txtUserLogin.setText("");
        this.txtClaveLogin.setText("");

        this.btnLimpiarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarDatos();
            }
        });

        this.btnIngresarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarAcceso();
            }
        });

        try{
            LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        try{
                            mAccessToken = loginResult.getAccessToken();
                            //getUserProfile(mAccessToken);
                            getFbInfo();
                        } catch (Exception ex){
                            Toast.makeText(MainActivity.this, "Error al identificarse contra Facebook.\n" + ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this, "Se ha cancelado el acceso con Facebook.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(MainActivity.this, "Se ocurrido un error en el acceso con Facebook.\n" + exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        } catch (Exception ex){
            Toast.makeText(MainActivity.this, "Error al identificarse con Facebook.\n" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        mAccessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = mAccessToken != null && !mAccessToken.isExpired();
        if (isLoggedIn) {
            this.getFbInfo();
        }
    }

    private void limpiarDatos(){
        this.txtUserLogin.setText("");
        this.txtClaveLogin.setText("");
    }

    private void validarAcceso(){
        String username = this.txtUserLogin.getText().toString();
        String clave = this.txtClaveLogin.getText().toString();

        if(!this.usuario.Existe(username)){
            this.registrarUsuario();
        } else {
            if(this.usuario.EsValido(username, clave)){
                this.accesoAprobado();
            } else {
                Toast.makeText(MainActivity.this, "Los datos ingresados son invalidos.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void registrarUsuario(){
        String username = this.txtUserLogin.getText().toString();
        String clave = this.txtClaveLogin.getText().toString();

        Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("clave", clave);
        startActivity(intent);
    }

    private void accesoAprobado(){
        String username = this.txtUserLogin.getText().toString();

        TrUsuario trusuario = this.usuario.BuscaUsuario(username);
        Intent intent = new Intent(MainActivity.this, PrincipalActivity.class);
        intent.putExtra("id", Integer.toString(trusuario.getId()));
        intent.putExtra("name", trusuario.getNombre());
        intent.putExtra("tipo", "LOCAL");
        startActivity(intent);
    }

    /*private void getUserProfile(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse  response) {
                        try {
                            String nombre = object.get("name").toString();
                            String email = object.get("email").toString();
                            Toast.makeText(MainActivity.this, "Datos.\nNombre: " + nombre + "\nCorreo: " + email, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(200),birthday,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode,  data);
    }
}
