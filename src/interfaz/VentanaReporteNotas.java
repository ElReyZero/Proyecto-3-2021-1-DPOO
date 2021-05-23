package interfaz;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import IdentificadorUsuario.CoordinadorAcademico;
import IdentificadorUsuario.Estudiante;
import Sistema.systemMain;
import funcionalidades.reporteNotas;

@SuppressWarnings("serial")
public class VentanaReporteNotas extends JPanel implements ActionListener
{

    private JButton volver;
    private VentanaPrincipal ventanaMain;
    private systemMain sistema;
    private Estudiante estudiante;
    private boolean todo;
    private Estudiante respaldo;
    private boolean esCoordinador;
    private CoordinadorAcademico coordinador;
    private JLabel tipoPensum;

    public VentanaReporteNotas(VentanaPrincipal pVentanaMain, systemMain pSistema, Estudiante pEstudiante, boolean pTodo, Estudiante pRespaldo, boolean pEsCoordinador, CoordinadorAcademico pCoordinador, boolean pVolver, String tipoPensumString)
    {
        ventanaMain = pVentanaMain;
        sistema = pSistema;
        estudiante = pEstudiante;
        todo = pTodo;
        respaldo = pRespaldo;
        esCoordinador = pEsCoordinador;
        coordinador = pCoordinador;
        tipoPensum = new JLabel(tipoPensumString);
		setLayout(new BorderLayout());
        ///Botones y paneles
        add(PanelInformacion(estudiante), BorderLayout.NORTH);
        add(PanelMateriasVistas(estudiante),BorderLayout.CENTER);
        if(pVolver == true)
        {
            add(Volver(),BorderLayout.SOUTH);
        }
        setSize(1000, 900);
		setVisible(true);
    }
    public JPanel PanelInformacion(Estudiante estudiante)
    {
        JPanel panelInformacion = new JPanel();
        panelInformacion.setLayout(new BoxLayout(panelInformacion,BoxLayout.PAGE_AXIS));
        panelInformacion.add(PanelPGA(estudiante));
        panelInformacion.add(Box.createRigidArea(new Dimension(0,15)));
        return panelInformacion;
    }
    public JPanel PanelPGA(Estudiante estudiante)
    {
        JPanel panelPGA = new JPanel();
        JLabel textPGA = new JLabel("El PGA es de:     " + reporteNotas.promedioPGA(estudiante));
        JLabel textEA = new JLabel("El estado académico es:     " + reporteNotas.estadoAcademico(estudiante));
        JLabel textSSC = new JLabel("El semestre según créditos:     " + reporteNotas.semestreSegunCreditos(estudiante));
        panelPGA.setLayout(new BoxLayout(panelPGA,BoxLayout.PAGE_AXIS));
        panelPGA.add(tipoPensum);
        panelPGA.add(new JLabel(" "));
        panelPGA.add(textPGA);
        panelPGA.add(Box.createRigidArea(new Dimension(8,0)));
        panelPGA.add(textEA);
        panelPGA.add(Box.createRigidArea(new Dimension(8,0)));
        panelPGA.add(textSSC);
        panelPGA.add(Box.createRigidArea(new Dimension(8,0)));
        panelPGA.setAlignmentX(CENTER_ALIGNMENT);
        return panelPGA;
    }
    public JPanel PanelMateriasVistas(Estudiante estudiante)
    {
        JPanel panelMateriasVistas = new JPanel();
        panelMateriasVistas.setLayout(new BorderLayout());
        String[] parts = reporteNotas.reporteSemestre(estudiante).split("\n"); 
        JList<String> materiasVistas = new JList<String>(parts);
        JScrollPane sp = new JScrollPane(materiasVistas);
        JLabel vacio1 = new JLabel("                     ");
        JLabel vacio2 = new JLabel("                     ");
        JLabel vacio3 = new JLabel("                     ");
        panelMateriasVistas.add(sp, BorderLayout.CENTER);
        panelMateriasVistas.add(vacio1, BorderLayout.WEST);
        panelMateriasVistas.add(vacio2, BorderLayout.EAST);
        panelMateriasVistas.add(vacio3, BorderLayout.SOUTH);
        return panelMateriasVistas;
    }
    public JPanel Volver()
    {
      JPanel panelVolver = new JPanel();
      panelVolver.setLayout(new BoxLayout(panelVolver,BoxLayout.LINE_AXIS));
      volver = new JButton("Volver a menú principal");
      volver.addActionListener(this);
      panelVolver.add(volver);
      return panelVolver;
    }
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == volver)
		{
            if(todo == true)
            {
                if(esCoordinador == true)
                {
                    ventanaMain.actualizarMain(new VentanaCoordinador(coordinador, ventanaMain, sistema, estudiante, sistema.darPensum()));
                }
                else
                {
                    ventanaMain.actualizarMain(new VentanaEstudiante(estudiante.darNombre(), estudiante.darCodigo(), estudiante.darCodigo(), ventanaMain, sistema , estudiante));
                }
            }
			else
            {
                if(esCoordinador == true)
                {
                    ventanaMain.actualizarMain(new VentanaCoordinador(coordinador, ventanaMain, sistema, respaldo, sistema.darPensum()));
                }
                else
                {
                    ventanaMain.actualizarMain(new VentanaEstudiante(respaldo.darNombre(), respaldo.darCodigo(), respaldo.darCodigo(), ventanaMain, sistema , respaldo));
                }
            }
		}
	}
}
