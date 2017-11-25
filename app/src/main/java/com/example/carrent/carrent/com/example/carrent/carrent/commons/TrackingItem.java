package com.example.carrent.carrent.com.example.carrent.carrent.commons;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TrackingItem {
    public Date fecha;
    public String posicion;
    public String velocidad;
    public String altura;
    public String grados;
    public String satelites;

    public TrackingItem() {
    }

    public static TrackingItem Parse(String registro) {
        // #-34.67020,-58.56442#22/11/2017#0:54:27.0#64.70m |267.82g |9S#0kmph*
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy H:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            TrackingItem item = new TrackingItem();
            String[] data = registro.split("#");
            item.posicion = data[1];
            item.fecha = formatter.parse(data[2] + ' ' + data[3]);
            item.velocidad = data[5].trim();
            data = data[4].split("\\|");
            item.altura = data[0].trim();
            item.grados = data[1].trim();
            item.satelites = data[2].trim();
            // Fix datos de posicion, se agregá simbolos '-'
            data = item.posicion.split(",");
            item.posicion = "-" + data[0] + ",-" + data[1];
            return  item;
        } catch(Exception e){
            return  null;
        }
    }
}
