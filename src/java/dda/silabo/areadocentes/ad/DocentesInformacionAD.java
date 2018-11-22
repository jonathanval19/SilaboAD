/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.areadocentes.ad;

import dda.silabo.ad.SilaboAD;
import dda.silabo.areadocentes.comunes.DocentesInformacion;
import dda.silabo.db.AccesoDatos;
import dda.silabo.entidad.ad.CarrerasAD;
import dda.silabo.entidad.ad.RolUsuarioAD;
import dda.silabo.roles.ad.RolAD;
import ec.edu.espoch.academico.ArrayOfMateria;
import ec.edu.espoch.academico.Materia;
import ec.edu.espoch.academico.Persona;
import ec.edu.espoch.academico.RolCarrera;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Jorge Zaruma
 */
public class DocentesInformacionAD extends DocentesInformacion {

    public void agregarDatosDocente(Persona objPersona) {
        try {
            this.nombres = objPersona.getNombres();
            this.apellidos = objPersona.getApellidos();
            this.cedula = objPersona.getCedula();
            this.email = objPersona.getEmail();
        } catch (Exception e) {

        }
    }

    public void agregarRoles(String codPeriodo, String cedula, List<RolCarrera> objCarreras, AccesoDatos ad) throws SQLException {
        SilaboAD silaboad = new SilaboAD();
        RolUsuarioAD rolad = new RolUsuarioAD();

        int idcedula = 0;
        if (silaboad.idDocenteSelect(ad, cedula) == 0) {
            idcedula = silaboad.idDocenteInsert(ad, cedula, "OASIS");
        } else {
            idcedula = silaboad.idDocenteSelect(ad, cedula);
        }
        if (rolad.getRolUsuarioID(idcedula, ad) == 0) {
            for (RolCarrera carrera : objCarreras) {
                List<Materia> objMaterias = getMateriasDocente(carrera.getCodigoCarrera(), cedula, codPeriodo).getMateria();
                if (objMaterias.size() > 0) {
                    String descRol = "";
                    if (carrera.getNombreRol().equals("DOC")) {
                        descRol = "DOCENTE";
                    }
                    rolad.agregarRoles(descRol, idcedula, ad);
                }
            }
        }
        RolesDocenteAsignar(idcedula, ad);

    }

    private void RolesDocenteAsignar(int idcedula, AccesoDatos ad) throws SQLException {
        String SQL = "select trd.id_rol, tru.descripcion \n"
                + "from t_rol_docente as trd\n"
                + "inner join \n"
                + "t_rol_usuario as tru \n"
                + "on trd.id_rol= tru.id_rol\n"
                + "where (id_docente=?)";

        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setInt(1, idcedula);
            ResultSet rsRoles = ps.executeQuery();
            while (rsRoles.next()) {
                RolUsuarioAD rolad = new RolUsuarioAD();
                rolad.agregarRolDocente(rsRoles);
                this.getRoles().add(rolad);
            }
            ad.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

    public void agregarRolCarrera(String codigoCarrera, String nombreRol) {
        CarrerasAD objCarrera = new CarrerasAD();
        objCarrera.setCodCarrera(codigoCarrera);
        if (nombreRol != null) {
            RolAD rolAD = new RolAD();
            if (nombreRol.equals("DOC")) {
                nombreRol = ("Docente");
            } else if (nombreRol.equals("DIRCAR")) {
                nombreRol = ("Director de Carrera");
            } else {
                nombreRol = "Indefinido";
            }
            rolAD.setDescRol(nombreRol);
            objCarrera.setRoles(rolAD);
        }
        this.carreras.add(objCarrera);

    }

    private static ArrayOfMateria getMateriasDocente(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2) {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        return port.getMateriasDocente(arg0, arg1, arg2);
    }
}
