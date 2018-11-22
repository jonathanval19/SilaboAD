package dda.silabo.archivos.ln;

import com.google.gson.Gson;
import dda.panalitico.ws.ArchivoAD;
import dda.silabo.db.AccesoDatos;
import java.util.Base64;

public class ArchivoLN {

    AccesoDatos ad = new AccesoDatos();
    Gson gson = new Gson();

    public String getBYTEStoBASE64(byte[] bytes) {
        String base64 = "";
        if (bytes != null) {
            base64 = Base64.getEncoder().encodeToString(bytes);
        }
        return base64;
    }

    public String getLogoEntidad(String codEntidad, String tipoEntidad) {
        String result = null;
        try {
            if (ad.Connectar() != 0) {
                dda.silabo.archivos.ad.ArchivoAD archivoAD1 = new dda.silabo.archivos.ad.ArchivoAD();
                if (tipoEntidad.equals("Carrera")) {
                    codEntidad = archivoAD1.getCodigoFacultad(ad, codEntidad);
                    tipoEntidad = "Facultad";
                }

                ArchivoAD archivoAD = logosInstitucion(codEntidad, tipoEntidad);
                if (!archivoAD.getTipoEntidad().equals("LOGO")) {
                    result = getBYTEStoBASE64(archivoAD.getArchivo());
                }
            }
        } catch (Exception e) {
        } finally {
            ad.Desconectar();
        }
        return result;

    }

    private static ArchivoAD logosInstitucion(java.lang.String codEntidad, java.lang.String tipoEntidad) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.logosInstitucion(codEntidad, tipoEntidad);
    }
}
