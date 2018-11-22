package dda.silabo.datosgenerales.ln;

import dda.silabo.datosgenerales.comunes.DatosGenerales;
import ec.edu.espoch.academico.ArrayOfMateriaPensum;
import ec.edu.espoch.academico.MateriaPensum;
import ec.edu.espoch.academico.Periodo;
import java.util.ArrayList;
import java.util.List;
import javax.xml.ws.Holder;

public class DatosGeneralesAsignaturaLN extends Thread {

    String asignatura, escuela;
    DatosGenerales datosGenerales;

    @Override
    public void run() {
        llenarDatosGeneralesAsignatura();
    }

    public DatosGeneralesAsignaturaLN(DatosGenerales datosGenerales2) {
        this.asignatura = datosGenerales2.getCodigo_asignatura();
        this.escuela = datosGenerales2.getCodigoCampo();

        this.datosGenerales = datosGenerales2;
    }

    private void llenarDatosGeneralesAsignatura() {
        Holder<String> p = new Holder<>();    //Pensum
        Holder<ArrayOfMateriaPensum> m = new Holder<>();      //Malla
        getMallaCurricularPensumVigente(escuela, p, m);

        Integer nMaterias = m.value.getMateriaPensum().size();
        Boolean existe = false;
        Integer contador = 0;
        while (existe == false && contador < nMaterias) {
            MateriaPensum mat = m.value.getMateriaPensum().get(contador);
            String codMat = mat.getCodMateria();

            if (codMat.equals(asignatura)) {
                existe = true;
                datosGenerales.setNivel(mat.getNivel());
                Float f = mat.getCreditos();
                datosGenerales.setNumero_creditos(f.doubleValue());
                datosGenerales.setHoras_semanales(mat.getHorasPracticas() + mat.getHorasTeoricas());
                datosGenerales.setSilabo_materia(mat.getMateria());
                datosGenerales.setCampo(mat.getArea());
                datosGenerales.setCodigo_asignatura(mat.getCodMateria());
                datosGenerales.setCodigoCampo(mat.getCodArea());

                String listaPrerreq = "";
                String listaCorreq = "";
                try {
                    for (int i = 0; i < mat.getRequisitos().getMateriaRequisito().size(); i++) {
                        if (mat.getRequisitos().getMateriaRequisito().get(i).getTipo().equals("PRERREQUISITO")) {
                            listaPrerreq += (mat.getRequisitos().getMateriaRequisito().get(i).getCodMateria()) + "  ";
                        }
                        if (mat.getRequisitos().getMateriaRequisito().get(i).getTipo().equals("CORREQUISITO")) {
                            listaCorreq += (mat.getRequisitos().getMateriaRequisito().get(i).getCodMateria()) + "  ";
                        }
                    }
                    if (listaPrerreq.equals("")) {
                        listaPrerreq = "NINGUNO";
                    }
                    if (listaCorreq.equals("")) {
                        listaCorreq = "NINGUNO";
                    }
                    datosGenerales.setPrerequisitos(listaPrerreq);
                    datosGenerales.setCorrequisitos(listaCorreq);
                    Periodo periodo = periodoActual();
                    datosGenerales.setPeriodo_academico(periodo.getDescripcion());
                } catch (Exception e) {
                }
            }
            contador++;
        }
    }

    public DatosGenerales getDatosGenerales() {
        try {
            llenarDatosGeneralesAsignatura();
        } catch (Exception e) {
            datosGenerales = null;
        }
        return datosGenerales;
    }

    private static void getMallaCurricularPensumVigente(java.lang.String arg0, javax.xml.ws.Holder<java.lang.String> arg1, javax.xml.ws.Holder<ec.edu.espoch.academico.ArrayOfMateriaPensum> arg2) {
        si.sw.locales.SwUnidosSI_Service service = new si.sw.locales.SwUnidosSI_Service();
        si.sw.locales.SwUnidosSI port = service.getSwUnidosSIPort();
        port.getMallaCurricularPensumVigente(arg0, arg1, arg2);
    }

    private static Periodo periodoActual() {
        unidos.sw.WsUnidosEspoch_Service service = new unidos.sw.WsUnidosEspoch_Service();
        unidos.sw.WsUnidosEspoch port = service.getWsUnidosEspochPort();
        return port.periodoActual();
    }

}
