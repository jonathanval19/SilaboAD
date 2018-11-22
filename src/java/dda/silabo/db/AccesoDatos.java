/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author
 */
public class AccesoDatos {

    private Connection con;
    private Exception error;
    private ResultSet rs;
    private PreparedStatement pr;

    public AccesoDatos() {
        this.con = null;
        this.error = null;
        this.pr = null;
    }

    /**
     * @return the error
     */
    public Exception getError() {
        return error;
    }

    /*0 signifiuca falso y cualkier otro valor es verdadero*/
    public ResultSet getRs() {
        return rs;
    }

    public Byte Connectar() {
        Byte result = 0;
        try {
            if (this.con == null) {
                Context ctx = new InitialContext();
                DataSource ds = (DataSource) ctx.lookup("jdbc/dda-silabo");
                con = ds.getConnection();
                con.setAutoCommit(false);
                result = 2;
            } else {
                result = 2;
            }

        } catch (SQLException e) {
            con.close();
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
            this.error = e;
        } finally {
            return result;
        }

    }

    public Byte Desconectar() {
        Byte result = 0;
        try {
            this.getCon().close();
            /*este null es el destructor porque en java no esxiste destructor*/
            result = 1;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("ERROR: " + e.getClass().getName() + "***" + e.getMessage());
            this.error = e;
        } finally {
            return result;
        }
    }

    public Byte EjecutarSQL(String SQL) {
        Byte result = 0;
        try {
            Statement smt = this.getCon().createStatement();
            this.rs = smt.executeQuery(SQL);
            result = 1;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("ERROR: " + e.getClass().getName() + " *** " + e.getMessage());

            this.error = e;
        } finally {
            return result;
        }
    }

    public Byte EjecutarSQLSeguro(String SQL) {
        Byte result = 0;
        try {
            setPr(this.getCon().prepareStatement(SQL));
            result = 1;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("ERROR: " + e.getClass().getName() + " *** " + e.getMessage());

            this.error = e;
        } finally {
            return result;
        }
    }

    /**
     * @return the con
     */
    public Connection getCon() {
        return con;
    }

    /**
     * @return the pr
     */
    public PreparedStatement getPr() {
        return pr;
    }

    /**
     * @param pr the pr to set
     */
    public void setPr(PreparedStatement pr) {
        this.pr = pr;
    }
}
