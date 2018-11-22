/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.ad;

import dda.silabo.db.AccesoDatos;
import dda.silabo.silabo.comunes.Silabo;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Zaruma
 */
public class SilaboAD extends Silabo {

    public Integer getIdsilabo(int idDocAsgPrd, AccesoDatos ad) {

        if (idSilaboSelect(idDocAsgPrd, ad) != 0) {
            return idSilaboSelect(idDocAsgPrd, ad);
        } else {
            return idsilaboInsert(idDocAsgPrd, ad);
        }
    }

    public int idSilaboSelect(int idDocAsgPrd, AccesoDatos ad) {
        int id = 0;
        try {
            String SQL = "Select id_silabo from t_silabo where doc_asg_per='" + idDocAsgPrd + "'";
            if (ad.EjecutarSQL(SQL) != 0) {
                ResultSet rsSilabo = ad.getRs();
                while (rsSilabo.next()) {
                    id = rsSilabo.getInt("id_silabo");
                }
            }
        } catch (SQLException e) {
            Logger.getLogger("SilaboLN").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return id;
    }

    private Integer idsilaboInsert(int idDocAsgPrd, AccesoDatos ad) {
        int id = 200;
        try {
            String SQL = "insert into t_silabo (doc_asg_per) VALUES ('" + idDocAsgPrd + "')";
            Statement ps = ad.getCon().createStatement();
            ps.executeUpdate(SQL);
            id = idSilaboSelect(idDocAsgPrd, ad);
        } catch (SQLException e) {
            Logger.getLogger("SilaboLN").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return id;
    }
//*************************************************************OBTENER ID ASIGNATURA **********************************************************

    public Integer getIdAsignaturaDocentePerido(int idDocente, int idAsignatura, int idPeriodo, AccesoDatos ad) {

        if (idAsignaturaDocentePeridoSelect(idAsignatura, idPeriodo, ad) != 0) {
            return idAsignaturaDocentePeridoSelect(idAsignatura, idPeriodo, ad);
        } else {
            return idAsignaturaDocentePeridoInsert(idDocente, idAsignatura, idPeriodo, ad);
        }
    }

    public int idAsignaturaDocentePeridoSelect(int idAsignatura, int idPeriodo, AccesoDatos ad) {
        int id = 0;
        try {
            String SQL = "Select id_doc_asg_per from t_docente_asig_periodo where asignatura='" + idAsignatura + "'and periodo='" + idPeriodo + "'";
            if (ad.EjecutarSQL(SQL) != 0) {
                ResultSet rsSilabo = ad.getRs();
                while (rsSilabo.next()) {
                    id = rsSilabo.getInt("id_doc_asg_per");
                }
            }
        } catch (SQLException e) {
            Logger.getLogger("SilaboLN").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return id;
    }

    private Integer idAsignaturaDocentePeridoInsert(int idDocente, int idAsignatura, int idPeriodo, AccesoDatos ad) {
        int id = 0;
        try {
            String SQL = "insert into t_docente_asig_periodo (asignatura,docente,periodo)VALUES ('" + idAsignatura + "','" + idDocente + "','" + idPeriodo + "')";
            Statement ps = ad.getCon().createStatement();
            ps.executeUpdate(SQL);
            id = idAsignaturaDocentePeridoSelect(idAsignatura, idPeriodo, ad);
        } catch (SQLException e) {
            Logger.getLogger("SilaboLN").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return id;
    }
//*************************************************************OBTENER ID ASIGNATURA **********************************************************

    public Integer getIdAsignatura(AccesoDatos ad, Integer idEntidad) {
        int idAsignatura = 0;
        if (idAsignaturaSelect(ad) != 0) {
            idAsignatura = idAsignaturaSelect(ad);
        } else {
            idAsignatura = idAsignaturaInsert(ad, idEntidad);
        }
        return idAsignatura;
    }

    public int idAsignaturaReporte(AccesoDatos ad, String codAsignatura) {
        int id = 0;
        try {
            String SQL = "Select id_asignatura from t_asignatura where codigo='" + codAsignatura + "'";
            if (ad.EjecutarSQL(SQL) != 0) {
                ResultSet rsSilabo = ad.getRs();
                while (rsSilabo.next()) {
                    id = rsSilabo.getInt("id_asignatura");
                }
            }
        } catch (SQLException e) {
            Logger.getLogger("SilaboLN").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return id;
    }

    public int idAsignaturaSelect(AccesoDatos ad) {
        int id = 0;
        try {
            String SQL = "Select id_asignatura from t_asignatura where codigo='" + this.getCodMateria() + "'";
            if (ad.EjecutarSQL(SQL) != 0) {
                ResultSet rsSilabo = ad.getRs();
                while (rsSilabo.next()) {
                    id = rsSilabo.getInt("id_asignatura");
                }
            }
        } catch (SQLException e) {
            Logger.getLogger("SilaboLN").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return id;
    }

    private Integer idAsignaturaInsert(AccesoDatos ad, Integer idEntidad) {
        int id = 0;
        try {
            String SQL = "insert into t_asignatura (codigo,id_entidad)VALUES ('" + this.getCodMateria() + "','" + idEntidad + "')";
            Statement ps = ad.getCon().createStatement();
            ps.executeUpdate(SQL);
            id = idAsignaturaSelect(ad);
        } catch (SQLException e) {
            Logger.getLogger("SilaboLN").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return id;
    }

//*************************************************************OBTENER ID PERIODO **********************************************************
    public Integer getIdPeriodo(AccesoDatos ad) {
        int idPeriodo = 0;
        if (idPeriodoSelect(ad) != 0) {
            idPeriodo = idPeriodoSelect(ad);
        } else {
            idPeriodo = idPeriodoInsert(ad);
        }
        return idPeriodo;
    }

    public int idPeriodoSelect(AccesoDatos ad) {
        int id = 0;
        try {
            String SQL = "Select id_periodo from t_periodo_academico where codigo='" + this.getPeriodo() + "'";
            if (ad.EjecutarSQL(SQL) != 0) {
                ResultSet rsSilabo = ad.getRs();
                while (rsSilabo.next()) {
                    id = rsSilabo.getInt("id_periodo");
                }
            }
        } catch (SQLException e) {
            Logger.getLogger("SilaboLN").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return id;
    }

    private Integer idPeriodoInsert(AccesoDatos ad) {
        int id = 0;
        try {
            String SQL = "insert into t_periodo_academico (codigo)VALUES ('" + this.getPeriodo() + "')";
            Statement ps = ad.getCon().createStatement();
            ps.executeUpdate(SQL);
            id = idPeriodoSelect(ad);
        } catch (SQLException e) {
            id = -2;
        }
        return id;
    }

//*************************************************************OBTENER ID DOCENTE **********************************************************
    public Integer getIdDocente(AccesoDatos ad, String cedula) {
        int idDocente = idDocenteSelect(ad, cedula);
        if (idDocente == 0) {
            idDocente = idDocenteInsert(ad, cedula, "OASIS");
        }
        return idDocente;
    }

    public int idDocenteSelect(AccesoDatos ad, String cedula) {
        int id = 0;
        try {
            String SQL = "Select id_docente from t_docente where cedula='" + cedula + "'";
            if (ad.EjecutarSQL(SQL) != 0) {
                ResultSet rsSilabo = ad.getRs();
                while (rsSilabo.next()) {
                    id = rsSilabo.getInt("id_docente");
                }
            }
        } catch (SQLException e) {
            Logger.getLogger("SilaboLN").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return id;
    }

    public Integer idDocenteInsert(AccesoDatos ad, String cedula, String tipo) {
        int id = 0;
        try {
            String SQL = "insert into t_docente (cedula,tipo_usuario,estado)VALUES ('" + cedula + "','" + tipo + "','H')";
            Statement ps = ad.getCon().createStatement();
            ps.executeUpdate(SQL);
            id = idDocenteSelect(ad, cedula);
        } catch (SQLException e) {
            Logger.getLogger("SilaboLN").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return id;
    }
    /////////////////////////////////////////////////----------------------------------------------------///////////////////////////////////////////////////

    public void asignarSecciones(Integer idSilabo, AccesoDatos ad) {
        for (int i = 1; i < 6; i++) {
            try {
                String SQL = "insert into t_secciones (id_silabo,id_descripcion,estado) values('" + idSilabo + "','" + i + "','Inicio')";
                Statement ps = ad.getCon().createStatement();
                ps.executeUpdate(SQL);
            } catch (SQLException e) {
                Logger.getLogger("SilaboLN").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
                System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
            }
        }
    }

    public void asignarSubsecciones(Integer idSilabo, int numUnidades, AccesoDatos ad) {
        for (int sub = 1; sub < 8; sub++) {
            for (int un = 0; un < numUnidades; un++) {
                try {
                    String SQL = "insert into t_unidad_subsecciones (id_unidad,id_silabo,id_descripcion,estado) values('" + (un + 1) + "','" + idSilabo + "','" + sub + "','Inicio')";
                    Statement ps = ad.getCon().createStatement();
                    ps.executeUpdate(SQL);
                } catch (SQLException e) {
                    Logger.getLogger("SilaboLN").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
                    System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
                }
            }
        }
    }

    public int existeSilaboSeccion(Integer idSilabo, AccesoDatos ad) {
        int result = 0;
        try {
            String SQL = "Select id_silabo from t_secciones where id_silabo='" + idSilabo + "'";
            if (ad.EjecutarSQL(SQL) != 0) {
                ResultSet rsSilabo = ad.getRs();
                while (rsSilabo.next()) {
                    result = rsSilabo.getInt("id_silabo");
                }
            }
        } catch (SQLException e) {
            Logger.getLogger("SilaboLN").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    public int existeSilaboSubseccion(Integer idSilabo, AccesoDatos ad) {
        int result = 0;
        try {
            String SQL = "Select id_silabo from t_unidad_subsecciones where id_silabo='" + idSilabo + "'";
            if (ad.EjecutarSQL(SQL) != 0) {
                ResultSet rsSilabo = ad.getRs();
                while (rsSilabo.next()) {
                    result = rsSilabo.getInt("id_silabo");
                }
            }
        } catch (SQLException e) {
            Logger.getLogger("SilaboLN").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    public Integer existeEstadoSilabo(Integer idSilabo, AccesoDatos ad) {
        int result = 0;
        try {
            String SQL = "Select id_silabo from t_silabo_estados where id_silabo='" + idSilabo + "'";
            if (ad.EjecutarSQL(SQL) != 0) {
                ResultSet rsSilabo = ad.getRs();
                while (rsSilabo.next()) {
                    result = rsSilabo.getInt("id_silabo");
                }
            }
        } catch (SQLException e) {
            Logger.getLogger("SilaboLN").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    public void agregarEstadoSilabo(Integer idSilabo, AccesoDatos ad) {
        try {
            String SQL = "insert into t_silabo_estados (id_silabo,estado) values('" + idSilabo + "','Inicio')";
            Statement ps = ad.getCon().createStatement();
            ps.executeUpdate(SQL);
        } catch (SQLException e) {
            Logger.getLogger("SilaboLN").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public String getEstadoSilabo(Integer idSilabo, AccesoDatos ad) {
        String result = "";
        try {
            String SQL = "Select estado from t_silabo_estados where id_silabo='" + idSilabo + "'";
            if (ad.EjecutarSQL(SQL) != 0) {
                ResultSet rsSilabo = ad.getRs();
                while (rsSilabo.next()) {
                    result = rsSilabo.getString("estado");
                }
            }
        } catch (SQLException e) {
            Logger.getLogger("SilaboLN").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    public Object getEstadoSubseccion(Silabo silabo, AccesoDatos ad, Integer idSubseccion) {
        String result = "";
        String SQL = "select estado from t_unidad_subsecciones WHERE (id_descripcion='" + idSubseccion + "'and id_silabo='" + silabo.getIdSilabo() + "'and id_unidad='" + silabo.getIdUnidad() + "')";
        try {
            if (ad.EjecutarSQL(SQL) != 0) {
                ResultSet rsSilabo = ad.getRs();
                while (rsSilabo.next()) {
                    result = rsSilabo.getString("estado");
                }
            }
        } catch (SQLException e) {
            Logger.getLogger("ObservacionAD").log(java.util.logging.Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    public Object getEstadoSeccion(Silabo silabo, AccesoDatos ad, Integer idSubseccion) {
        String result = "";
        String SQL = "select estado from t_secciones WHERE (id_descripcion='" + idSubseccion + "'and id_silabo='" + silabo.getIdSilabo() + "')";
        try {
            if (ad.EjecutarSQL(SQL) != 0) {
                ResultSet rsSilabo = ad.getRs();
                while (rsSilabo.next()) {
                    result = rsSilabo.getString("estado");
                }
            }
        } catch (SQLException e) {
            Logger.getLogger("ObservacionAD").log(java.util.logging.Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
        return result;
    }

    public void asignarEstadoSilabo(Integer idSilaboActual, AccesoDatos ad) {
        try {
            String SQL = "UPDATE t_silabo_estados SET estado='Revision' WHERE (id_silabo='" + idSilaboActual + "')";
            Statement ps = ad.getCon().createStatement();
            ps.executeUpdate(SQL);
        } catch (SQLException e) {
            Logger.getLogger("SilaboLN").log(java.util.logging.Level.SEVERE, "dda.silabo.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public void IngresarPeriodoCarrera(String codCarrera, String periodo, AccesoDatos ad) {
        Integer cont = 0;
        String SQL = "select periodo from t_periodos_carreras WHERE (periodo='" + periodo + "'and carrera='" + codCarrera + "')";
        try {
            if (ad.EjecutarSQL(SQL) != 0) {
                ResultSet rsSilabo = ad.getRs();
                while (rsSilabo.next()) {
                    cont++;
                }
            }
            if (cont == 0) {
                SQL = "insert into t_periodos_carreras (periodo,carrera) values('" + periodo + "','" + codCarrera + "')";
                Statement ps = ad.getCon().createStatement();
                ps.executeUpdate(SQL);
            }
        } catch (SQLException e) {
            Logger.getLogger("ObservacionAD").log(java.util.logging.Level.SEVERE, "dda.silabo.estructura.unidad.estrategias.ln", e.getClass().getName() + "*****" + e.getMessage());
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
        }
    }

    public Integer getIdSilaboBibliografias(String codMateria, String codPeriodo, AccesoDatos ad) {
        Integer result = 0;
        this.setCodMateria(codMateria);
        this.setPeriodo(codPeriodo);
        Integer idMateria = idAsignaturaSelect(ad);
        Integer idPeriodo = idPeriodoSelect(ad);
        if (idMateria != 0 && idPeriodo != 0) {
            Integer idAsigPeriodo = idAsignaturaDocentePeridoSelect(idMateria, idPeriodo, ad);
            result = idSilaboSelect(idAsigPeriodo, ad);
        }
        return result;
    }

    public Integer getUsuarioEntidad(AccesoDatos AD, String user, String codigo) throws SQLException {
        Integer result = 0;
        String SQL = "select tue.id_usuario_entidad from \n"
                + "t_usuario_entidad AS tue \n"
                + "JOIN t_docente AS td\n"
                + "on td.id_docente=tue.id_usuario and td.cedula=?\n"
                + "JOIN t_periodo_academico AS tp\n"
                + "on tp.id_periodo = tue.id_periodo and tp.codigo=?";
        try {
            PreparedStatement ps = AD.getCon().prepareStatement(SQL);
            ps.setString(1, user);
            ps.setString(2, codigo);

            ResultSet rsEntidad = ps.executeQuery();
            while (rsEntidad.next()) {
                result = rsEntidad.getInt("id_usuario_entidad");

            }
            AD.getCon().commit();
            ps.close();
        } catch (SQLException e) {
            AD.getCon().rollback();
            AD.Desconectar();
        }
        return result;
    }
    ///servicos externos 

    public Integer existeAsignaturaPeriodo(String codMateria, String codPeriodo, AccesoDatos ad) throws SQLException {
        Integer result = 0;
        String SQL = "SELECT tda.id_doc_asg_per FROM t_docente_asig_periodo AS tda \n"
                + "JOIN t_asignatura AS ta \n"
                + "ON ta.id_asignatura=tda.asignatura AND ta.codigo=? \n"
                + "JOIN t_periodo_academico AS tp\n"
                + "ON tp.id_periodo=tda.periodo AND tp.codigo=?";
        try {
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, codMateria);
            ps.setString(2, codPeriodo);
            ResultSet rsAsignatura = ps.executeQuery();
            while (rsAsignatura.next()) {
                result = rsAsignatura.getInt("id_doc_asg_per");
            }
            ps.close();
            ad.getCon().commit();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
        return result;
    }

    public String SilaboGenerarSelect(Integer idSilabo) {
        return "SELECT archivo FROM t_silabo_estados WHERE id_silabo='" + idSilabo + "' and estado='Aprobado'";
    }

    public void EstructuraCurricularSilaboID(AccesoDatos ad) throws SQLException {
        this.setIdSilabo(0);
        try {
            String SQL = "Select id_silabo from t_silabo as ts \n"
                    + "join t_docente_asig_periodo  as tdap on ts.doc_asg_per = tdap.id_doc_asg_per\n"
                    + "join t_asignatura as ta on ta.id_asignatura = tdap.asignatura and ta.codigo = ?\n"
                    + "join t_periodo_academico as tpa on tdap.periodo = tpa.id_periodo and tpa.codigo=? ";
            PreparedStatement ps = ad.getCon().prepareStatement(SQL);
            ps.setString(1, this.getCodMateria());
            ps.setString(2, this.getPeriodo());
            ResultSet silabo = ps.executeQuery();
            while (silabo.next()) {
                this.setIdSilabo(silabo.getInt("id_silabo"));
            }
            ad.getCon().commit();
        } catch (SQLException e) {
            ad.getCon().rollback();
            ad.Desconectar();
        }
    }

}
