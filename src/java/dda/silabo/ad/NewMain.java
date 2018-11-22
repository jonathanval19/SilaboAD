/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.ad;

import com.google.gson.Gson;
import com.sun.el.stream.Stream;
import dda.silabo.clases.unidos.AsignaturaUnidos;
import dda.silabo.clases.unidos.CampoFormacionUnidos;
import dda.silabo.db.AccesoDatos;
import static dda.silabo.db.Global.ipRedis;
import static dda.silabo.db.Global.portRedis;
import dda.silabo.entidad.ad.CampoFormacionAD;
import ec.edu.espoch.academico.ArrayOfMateria;
import ec.edu.espoch.academico.ArrayOfMateriaPensum;
import ec.edu.espoch.academico.Materia;
import ec.edu.espoch.academico.MateriaPensum;
import ec.edu.espoch.academico.Periodo;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import redis.clients.jedis.Jedis;
import unidos.sw.CarreraAD;
import unidos.sw.FacultadAD;

/**
 *
 * @author Jorge Zaruma
 */
public class NewMain {

//    List<CampoFormacionUnidos> campoFormacionUnidoses = new ArrayList<>();
////    /**
////     * @param args the command line arguments
////     */
//
//    public static void main(String[] args) throws SQLException, NamingException, CreateException {
////        // TODO code application logic here
//////        AccesoDatos ad = new AccesoDatos();
//////       
//////        if (ad.Connectar() != null) {
//////            String consulta = "180368940-3";
//////            String SQL = "SELECT id_docente, cedula, tipo_usuario, estado\n"
//////                    + "  FROM t_docente where cedula=?";
//////
//////            if (ad.EjecutarSQLSeguro(SQL) != 0) {
//////                PreparedStatement pr = ad.getPr();
//////                pr.setString(1, consulta);
//////                ResultSet rs = pr.executeQuery();
//////                while (rs.next()) {
//////                    System.err.println(rs.getString("id_docente"));
//////
//////                }
//////            }
//////        }
//////        try {
//////            FacultadAD facultadAD = docentesFacultad("{'codFacultad':'FIE'}");
//////            System.out.println("PRIMERA PETICION" + facultadAD.getDocentes().get(1).getApellidos());
//////        } catch (Exception e) {
//////            FacultadAD facultadAD = docentesFacultad("{'codFacultad':'FIE'}");
//////            System.out.println("SEGUNDA PETICION" + facultadAD.getDocentes().get(10).getCedula());
//////        }
////        updatePrice("postgres", "1234567");
//
//    }
    public String conexionBase() throws SQLException, NamingException {
        DataSource ds = null;
        Context ctx = null;
        ctx = new InitialContext();
        ds = (DataSource) ctx.lookup("jdbc/dda-silabo");
        Connection con = null;
        PreparedStatement pstmt;
        String result = "";
        try {
            con = ds.getConnection();
            con.setAutoCommit(false);
            pstmt = con.prepareStatement("select estado from t_silabo_estados");
            ResultSet rs = pstmt.executeQuery();
//            pstmt.setFloat(1, 2975);
//            pstmt.executeUpdate();
            while (rs.next()) {
                result += rs.getString("estado");
            }
            con.commit();
            pstmt.close();
        } catch (SQLException e) {
            con.rollback();
        } finally {
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public String conexionRedis() {
        String result = "result";
        try {
            Jedis jedis = new Jedis("172.17.102.143", 6379);
            jedis.auth("redisespoch2018");
            result = jedis.ping();
        } catch (Exception e) {
            result = "catch";
        }
        return result;
    }

    private static void verificar(String id, List<MateriaPensum> lista) {

        CampoFormacionUnidos c = new CampoFormacionAD();
        c.setCodCampoF(id);
        c.setAsignaturas(convertir(lista));
        Gson G = new Gson();
        System.out.println(G.toJson(c));
    }

    private static List< AsignaturaUnidos> convertir(List<MateriaPensum> lista) {
        List<AsignaturaUnidos> asignaturaUnidoses = new ArrayList<>();
        try {
            if (lista != null) {
                for (MateriaPensum materiaPensum : lista) {
                    AsignaturaUnidos asignaturaUnidos = new AsignaturaUnidos();
                    asignaturaUnidos.setCodMateria(materiaPensum.getCodMateria());
                    asignaturaUnidos.setNombreMateria(materiaPensum.getMateria());
                    asignaturaUnidos.setCodArea(materiaPensum.getCodArea());
                    asignaturaUnidoses.add(asignaturaUnidos);
                }
            }
        } catch (Exception e) {
        }
        return asignaturaUnidoses;
    }

    public List<CampoFormacionUnidos> obtenerCamposFormacionDocente(List<AsignaturaUnidos> materias) {
        List<CampoFormacionUnidos> result = new ArrayList<>();
        try {

        } catch (Exception e) {
        }
        return result;
    }

    private static ArrayOfMateriaPensum pensumVigente(java.lang.String jsonCarrera) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.pensumVigente(jsonCarrera);
    }

    private static ArrayOfMateria getMateriasDocente(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2) {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.getMateriasDocente(arg0, arg1, arg2);
    }

    private static CarreraAD asignaturasCarrera(java.lang.String jsonCarrera) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.asignaturasCarrera(jsonCarrera);
    }

    public String servicio() {
        return (unidadesAcademicasInstitucion(null) + new Gson().toJson(periodoActual()));
    }

    private static String unidadesAcademicasInstitucion(java.lang.String jsonDatos) {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.unidadesAcademicasInstitucion(jsonDatos);
    }

    private static Periodo periodoActual() {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.periodoActual();
    }
}
