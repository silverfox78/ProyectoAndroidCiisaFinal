package cl.ciisa.transfer;

public class TrUsuario {
    private int id;
    private String nombre;
    private String username;
    private String clave;
    private byte[] imagen;

    public TrUsuario(int id, String nombre, String username, String clave, byte[] imagen) {
        this.id = id;
        this.nombre = nombre;
        this.username = username;
        this.clave = clave;
        this.imagen = imagen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }
}
