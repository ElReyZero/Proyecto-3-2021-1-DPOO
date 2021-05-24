package interfaz;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import IdentificadorUsuario.Estudiante;
import Sistema.systemMain;
import curriculo.Pensum;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.BorderLayout;
import java.awt.Dimension;
@SuppressWarnings("unchecked")
public class VentanaReformaPensum extends JPanel implements ActionListener
{
    private Estudiante estudiante;
    private Estudiante copia;
    private systemMain sistema;
    private VentanaPrincipal ventanaMain;
    private JButton nuevo;
    private JButton viejo;
    private Pensum oldP;
    private Pensum nuevoP;


    public VentanaReformaPensum(VentanaPrincipal pVentanaMain, systemMain pSistema, Estudiante pEstudiante, Estudiante pCopia, Pensum viejoPensum, Pensum nuevoPensum)
    {
        estudiante = pEstudiante;
        copia = pCopia;
        ventanaMain = pVentanaMain;
        sistema = pSistema;
        oldP = viejoPensum;
        nuevoP = nuevoPensum;
        nuevo = new JButton("Acogerme al nuevo pensum");
        nuevo.addActionListener(this);
        viejo = new JButton("Quedarme con el pensum antiguo");
        viejo.addActionListener(this);
        setLayout(new BorderLayout());
        ///Botones y paneles
        add(panelOld(ventanaMain,sistema,estudiante), BorderLayout.WEST);
        add(panelNew(ventanaMain,sistema,copia),BorderLayout.EAST);
        add(PanelDesicion(nuevo,viejo),BorderLayout.SOUTH);
        //add(volver, BorderLayout.SOUTH);
        setSize(1000, 900);
        setVisible(true);
    }
    public JPanel PanelDesicion(JButton nuevo, JButton viejo)
    {
        JPanel panelDesicion = new JPanel();
        panelDesicion.setLayout(new BoxLayout(panelDesicion,BoxLayout.LINE_AXIS));
        panelDesicion.add(Box.createRigidArea(new Dimension(120,0)));
        panelDesicion.add(viejo);
        panelDesicion.add(Box.createRigidArea(new Dimension(280,0)));
        panelDesicion.add(nuevo);
        return panelDesicion;
    }
    public JPanel panelOld(VentanaPrincipal pVentanaMain, systemMain pSistema, Estudiante pEstudiante)
    {
        JPanel old = new VentanaReporteNotas(ventanaMain, sistema, estudiante, true, null, false, null, false, "Avance con pensum viejo:", null);
        return old;
    }
    public JPanel panelNew(VentanaPrincipal pVentanaMain, systemMain pSistema, Estudiante pCopia)
    {
        JPanel reformed = new VentanaReporteNotas(ventanaMain, sistema, copia, true, null, false, null, false, "Avance con pensum nuevo:", null);
        return reformed;
    }
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        if(e.getSource() == viejo)
        {
            int opcion = JOptionPane.showConfirmDialog(this, new JLabel("¿Estas seguro que quieres acogerte al viejo pensum?"), "Confirmación cambio de pensum", JOptionPane.ERROR_MESSAGE);
            if (opcion != 1) 
            {
                HashMap<Double, ArrayList<String>> mapa = (HashMap<Double, ArrayList<String>>) oldP.darCarteleras().clone();
                nuevoP.setCarteleras(mapa);
                ventanaMain.actualizarMain(new VentanaEstudiante(estudiante.darNombre(), estudiante.darCodigo(), estudiante.darCodigo(), ventanaMain, sistema , estudiante, nuevoP));
            }            
        }
        else if(e.getSource()== nuevo)
        {
            int opcion = JOptionPane.showConfirmDialog(this, new JLabel("¿Estas seguro que quieres acogerte al nuevo pensum?"), "Confirmación cambio de pensum", JOptionPane.ERROR_MESSAGE);
            if (opcion != 1) 
            {
                HashMap<Double, ArrayList<String>> mapa = (HashMap<Double, ArrayList<String>>) oldP.darCarteleras().clone();
                nuevoP.setCarteleras(mapa);
                ventanaMain.actualizarMain(new VentanaEstudiante(estudiante.darNombre(), estudiante.darCodigo(), estudiante.darCodigo(), ventanaMain, sistema , copia, nuevoP));
            }
           
        } 
    }
}