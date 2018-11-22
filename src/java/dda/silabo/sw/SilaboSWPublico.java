/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.sw;

import dda.silabo.estructura.unidadinformacion.ad.EstructuraDesarrolloAD;
import dda.silabo.estructura.unidadinformacion.ln.EstructuraDesarrolloLN;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author Jorge Zaruma
 */
@WebService(serviceName = "SilaboSWPublico")
public class SilaboSWPublico {

    /**
     * This is a sample web service operation
     *
     * @param codCarrera
     * @param codPeriodo
     * @param codMateria
     * @return
     */
    ///servicios externos 
    @WebMethod(operationName = "SilaboContenidoAsignatura")
    public EstructuraDesarrolloAD SilaboContenidoAsignatura(@WebParam(name = "codCarrera") String codCarrera, @WebParam(name = "codPeriodo") String codPeriodo, @WebParam(name = "codMateria") String codMateria) {
        EstructuraDesarrolloAD result = null;
        try {
            EstructuraDesarrolloLN unidades = new EstructuraDesarrolloLN();
            result = unidades.SilaboContenidoAsignatura(codCarrera, codPeriodo, codMateria);
        } catch (Exception e) {

        }
        return result;
    }
}
