package com.example.carrent.carrent.com.example.carrent.carrent.commons;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class EventoItem {
    public Date fecha;
    public String accion;
    public String mensaje;

    public EventoItem() {
    }

    public static EventoItem Parse(String registro) {
        try {
            EventoItem item = new EventoItem();
            String[] data = registro.split("#");

            switch (data[1].trim()) {
                case "PrenderLuces":
                    item.accion = "Encender luces";
                    item.mensaje = "";
                    break;
                case "ExcesoVelocidad":
                    item.accion = "Exceso de velocidad";
                    item.mensaje = data[5].trim();
                    break;
                case "HumoOn":
                    item.accion = "Detección de humo en habitáculo";
                    item.mensaje = "";
                    break;
                case "HumoOff":
                    item.accion = "Normalización de humo en habitáculo";
                    item.mensaje = "";
                    break;
                default:
                    return null;
            }

            if (data.length > 2) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy H:mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
                item.fecha = formatter.parse(data[2] + ' ' + data[3]);
            }

            return  item;
        } catch(Exception e){
            return  null;
        }
    }
}
