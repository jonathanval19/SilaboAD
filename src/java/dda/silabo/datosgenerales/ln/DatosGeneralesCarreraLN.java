package dda.silabo.datosgenerales.ln;

import dda.silabo.datosgenerales.comunes.DatosGenerales;
import ec.edu.espoch.academico.ArrayOfEscuelaEntidad;
import ec.edu.espoch.academico.EscuelaEntidad;
import ec.edu.espoch.academico.ParamCarrera;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatosGeneralesCarreraLN extends Thread {
    
    String asignatura, escuela;
    DatosGenerales datosGenerales;
    
    @Override
    public void run() {
        try {
            this.llenarDatosGeneralesCarrera();
        } catch (SQLException ex) {
            Logger.getLogger(DatosGeneralesCarreraLN.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public DatosGeneralesCarreraLN(DatosGenerales datosGenerales2) {
        this.asignatura = datosGenerales2.getCodigo_asignatura();
        this.escuela = datosGenerales2.getCodigoCampo();
        this.datosGenerales = datosGenerales2;
    }
    
    private void llenarDatosGeneralesCarrera() throws SQLException {
        
        List<EscuelaEntidad> listaEscuelas = arrayOfEscuelaEntidad().getEscuelaEntidad();
        EscuelaEntidad escu = listaEscuelas.stream().filter(es -> es.getCodCarrera().equals(escuela)).findFirst().orElse(null);
        if (escu != null) {
            datosGenerales.setNombre_facultad(escu.getFacultad());
            datosGenerales.setNombre_escuela(escu.getEscuela());
            datosGenerales.setNombre_carrera(escu.getCarrera());
            datosGenerales.setTitulo("Datos Generales de Asignatura");
            datosGenerales.setAyuda("En esta seccion se presenta la informacion general de la asignatura que se esta desarrollando el Silabo");
            try {
                datosGenerales.setNombre_sede(getParametrosCarrera(escuela).getResidencia());
                datosGenerales.setModalidad(getParametrosCarrera(escuela).getModalidad());
            } catch (Exception e) {
                datosGenerales.setNombre_sede("");
                datosGenerales.setModalidad("");
            }
        }
    }
    
    public DatosGenerales getDatosGenerales() {
        try {
            llenarDatosGeneralesCarrera();
        } catch (Exception e) {
            datosGenerales = null;
        }
        return datosGenerales;
    }
    
    private static ArrayOfEscuelaEntidad arrayOfEscuelaEntidad() {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.arrayOfEscuelaEntidad();
    }
    
    private static ParamCarrera getParametrosCarrera(java.lang.String arg0) {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.getParametrosCarrera(arg0);
    }
    
}
