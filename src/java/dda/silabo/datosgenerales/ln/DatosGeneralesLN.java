/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.datosgenerales.ln;

import com.google.gson.Gson;
import dda.silabo.datosgenerales.comunes.DatosGenerales;
import dda.silabo.db.Global;
import dda.silabo.silabo.comunes.Silabo;
import redis.clients.jedis.Jedis;

public class DatosGeneralesLN {

    Gson G = new Gson();
    Global global = new Global();
    String asignatura, escuela;
    DatosGenerales datosGenerales = null;

    public DatosGeneralesLN(String JSON) {
        Gson gson = new Gson();
        Silabo silabo = gson.fromJson(JSON, Silabo.class);
        this.asignatura = silabo.getCodMateria();
        this.escuela = silabo.getCodCarrera();

        datosGenerales = new DatosGenerales();
        datosGenerales.setCodigo_asignatura(this.asignatura);
        datosGenerales.setCodigoCampo(this.escuela);
    }

    private void matchDatosGenerales() {
        //ClasePADatosGenerales datosGenerales = new ClasePADatosGenerales(escuela, asignatura); 
        Thread hilo1 = new DatosGeneralesCarreraLN(datosGenerales);
        Thread hilo2 = new DatosGeneralesAsignaturaLN(datosGenerales);

        hilo1.start();
        hilo2.start();

        while (hilo1.isAlive() || hilo2.isAlive()) {
        };
    }

    public String getDatosGenerales() {
        Gson gson = new Gson();
        try {
            matchDatosGenerales();
        } catch (Exception e) {
        }
        return gson.toJson(datosGenerales);
    }
//    public String DatosGeneralesCargar(String jsonSilabo) {
//        DatosGeneralesAD datosgenerales = new DatosGeneralesAD();
//        String result = "";
//        Silabo silabo = G.fromJson(jsonSilabo, Silabo.class);
//        result = null;//jedis.get("DatosGenerales" + silabo.getCodCarrera() + silabo.getCodMateria());
//        if (result == null) {
//            try {
//
//                datosgenerales.DatosGeneralesCargar(silabo.getCodCarrera(), silabo.getCodMateria());
//                result = G.toJson(datosgenerales);
//                jedis.set("DatosGenerales" + silabo.getCodCarrera() + silabo.getCodMateria(), result);
//            } catch (JsonSyntaxException e) {
//
//            }
//        }
//        return result;
//    }

}
