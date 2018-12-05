package cl.ciisa.util;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class Localizacion implements LocationListener {
    double longitud;
    double latitud;

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.longitud = location.getLongitude();
        this.latitud =  location.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        //tvMsj.setText("GPS Activado");
    }

    @Override
    public void onProviderDisabled(String provider) {
        //tvMsj.setText("GPS Apagado");
    }
}
