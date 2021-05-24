package interfaz;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import IdentificadorUsuario.CoordinadorAcademico;
import IdentificadorUsuario.Estudiante;
import Sistema.BannerException;
import Sistema.systemMain;
import curriculo.MateriaEstudiante;
import curriculo.Pensum;
import funcionalidades.planeador;

@SuppressWarnings({"unchecked", "rawtypes", "serial"})
public class VentanaPlaneador extends JPanel implements ActionListener
{
    private JButton registrarMateria;
    private JButton editarMateria;
    private JButton guardar;
    private JButton volver;
    private VentanaPrincipal ventanaMain;
    private systemMain sistema;
    private Estudiante estudiante;
    private Pensum pensum;
    private ArrayList<String> parts;
    private String lastSubject;
    private Estudiante copia;
    private String plan;
    private boolean esCoordinador;
    private CoordinadorAcademico coordinador;

    public VentanaPlaneador(Estudiante pEstudiante, VentanaPrincipal pVentanaMain, systemMain pSistema, Pensum pPensum, Estudiante pCopia, ArrayList<String> pParts, boolean pEsCoordinador, CoordinadorAcademico pCoordinador)
    {
        copia = pCopia;
        estudiante = pEstudiante;
        pensum = pPensum;
        parts = pParts;
        plan = "";     
        ventanaMain = pVentanaMain;
        sistema = pSistema;
        esCoordinador = pEsCoordinador;
        coordinador = pCoordinador;
		setLayout(new BorderLayout());
        ///Botones y paneles
        add(PanelOpciones(),BorderLayout.CENTER);
        add(PanelPlan(estudiante),BorderLayout.EAST);
        add(Volver(),BorderLayout.SOUTH);
        setSize(1000, 900);
		setVisible(true);
    }
    public JPanel PanelOpciones()
    {
        JPanel panelOpciones = new JPanel();
        panelOpciones.setLayout(new BoxLayout(panelOpciones,BoxLayout.PAGE_AXIS));
        registrarMateria = new JButton("Planear semestre");
        registrarMateria.addActionListener(this);
        editarMateria = new JButton("Editar materia del plan");
        editarMateria.addActionListener(this);
        guardar = new JButton("Guardar plan actual");
        guardar.addActionListener(this);
        panelOpciones.add(Box.createRigidArea(new Dimension(0,40)));
        panelOpciones.add(registrarMateria);
        panelOpciones.add(Box.createRigidArea(new Dimension(0,8)));
        panelOpciones.add(editarMateria);
        panelOpciones.add(Box.createRigidArea(new Dimension(0,8)));
        panelOpciones.add(guardar);
        registrarMateria.setAlignmentX(CENTER_ALIGNMENT);
        editarMateria.setAlignmentX(CENTER_ALIGNMENT);
        guardar.setAlignmentX(CENTER_ALIGNMENT);
        return panelOpciones;
    }

    public JPanel PanelPlan(Estudiante estudiante)
    {
        JPanel panelMateriasPlaneadas = new JPanel();
        JList <String> materiasVistas = new JList(parts.toArray());
        JScrollPane scroll = new JScrollPane(materiasVistas);
        panelMateriasPlaneadas.add(scroll);
        return panelMateriasPlaneadas;
    }
    public JPanel Volver()
    {
      JPanel panelVolver = new JPanel();
      panelVolver.setLayout(new BoxLayout(panelVolver,BoxLayout.LINE_AXIS));
      volver = new JButton("Volver al menú inicial");
      volver.addActionListener(this);
      panelVolver.add(volver);
      return panelVolver;
    }

    public String registroMateriasPlaneador(Estudiante estudiante, String codigoMateria, double semester, String grade, boolean esE, boolean esEpsilon, Pensum pensum, boolean esCle, double credsCle, String plan)
    {
        try 
        {
            lastSubject = planeador.crearPlaneacion(estudiante, pensum, codigoMateria, semester, grade, esE, esEpsilon, esCle, credsCle);
            plan += lastSubject;
            int respuesta = planeador.darError();
            if(respuesta == 0)
            {
                JOptionPane.showMessageDialog(this, new JLabel(codigoMateria+" Registrada Satisfactoriamente!"), null, JOptionPane.INFORMATION_MESSAGE);
                actualizarLista();
            }
        } 
        catch (BannerException e) 
        {
            sistema.escribirException(e);
            JOptionPane.showMessageDialog(this, new JLabel(e.getMessage()), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }       
        return plan;
    }

    public String registroClePlaneador(Estudiante estudiante, String codMateria, double semestre, String nota, boolean esE, boolean epsilon, Pensum pensum, String plan) throws BannerException
    {
        JTextField creds = new JTextField();
        final JComponent[] inputsCLE = new JComponent[] 
        {
            new JLabel("¿De cuántos créditos es el Curso de Libre Elección?"),
            creds,
        };
        int result2 = JOptionPane.showConfirmDialog(this, inputsCLE, "Créditos CLE", JOptionPane.PLAIN_MESSAGE);
        if(result2 == JOptionPane.OK_OPTION && (creds.getText().equals("")))
        {
            JOptionPane.showMessageDialog(this, new JLabel("Tienes que completar todos los datos."), "Error", JOptionPane.ERROR_MESSAGE);
            return "";
        }
        else if(result2 == -1)
        {
            JOptionPane.showMessageDialog(this, new JLabel("No se registró la materia. (Número de Créditos Faltantes)"), "Error", JOptionPane.ERROR_MESSAGE);
            return "";
        }
        else
        {
            try
            {
                double creditsCle = Double.parseDouble(creds.getText());
                plan += registroMateriasPlaneador(estudiante, codMateria, semestre, nota, esE, epsilon, pensum, true, creditsCle, plan);
                return plan;
            }
            catch (NumberFormatException exa)
            {
                JOptionPane.showMessageDialog(this, new JLabel("No se registró la materia. (Formato inválido de créditos)"), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return plan;
        }
    }





	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == volver)
		{
            if(esCoordinador == true)
            {
                ventanaMain.actualizarMain(new VentanaCoordinador(coordinador, ventanaMain, sistema, estudiante, pensum));
            }
            else
            {
                ventanaMain.actualizarMain(new VentanaEstudiante(estudiante.darNombre(), estudiante.darCodigo(), estudiante.darCodigo(), ventanaMain, sistema , estudiante, pensum));
            }
		}
        else if (e.getSource() == registrarMateria)
        {
            JTextField codMateria = new JTextField();
            JTextField semestre = new JTextField();
            JCheckBox tipoE = new JCheckBox("Tipo E");
            JCheckBox epsilon = new JCheckBox("Tipo Épsilon");
            JCheckBox cle = new JCheckBox("Curso de Libre Elección");
            final JComponent[] inputs = new JComponent[] 
            {
            new JLabel("Materia a Registrar:"),
            codMateria,
            new JLabel("Semestre:"),
            semestre,
            new JLabel("¿El curso es de tipo especial?"),
            tipoE,
            epsilon,
            cle
            };
            int result = JOptionPane.showConfirmDialog(this, inputs, "Planear materia", JOptionPane.PLAIN_MESSAGE);
            if(result == JOptionPane.OK_OPTION && (codMateria.getText().equals("")) || semestre.getText().equals(""))
            {
                JOptionPane.showMessageDialog(this, new JLabel("Tienes que completar todos los datos."), "Error", JOptionPane.ERROR_MESSAGE);
            }
            else if(result == -1)
            {
            }
            else
            {
                int error = 1;
                try
                {
                    Double.parseDouble(semestre.getText());
                }
                catch (NumberFormatException ex)
                {
                    error = -1;
                    sistema.escribirException(ex);
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, new JLabel("Solo puedes ingresar números en semestre."), "Error", JOptionPane.ERROR_MESSAGE);
                }
                if (error != -1)
                {
                    if (copia == null)
                    {
                        JOptionPane.showMessageDialog(this, new JLabel("Error, no se encuentra la copia"), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    if(cle.isSelected())
                    {
                        try 
                        {
                            plan += registroClePlaneador(copia, codMateria.getText(), Double.parseDouble(semestre.getText()), "A", tipoE.isSelected(), epsilon.isSelected(), pensum, plan);
                        } 
                        catch (NumberFormatException e1) 
                        {
                            sistema.escribirException(e1);
                            JOptionPane.showMessageDialog(this, new JLabel(e1.getMessage()), "Error", JOptionPane.ERROR_MESSAGE);
                            e1.printStackTrace();
                        } catch (BannerException be) 
                        {
                            sistema.escribirException(be);
                            JOptionPane.showMessageDialog(this, new JLabel(be.getMessage()), "Error", JOptionPane.ERROR_MESSAGE);
                            be.printStackTrace();
                        }
                    }
                    else
                    {
                        try 
                        {
                            plan += registroMateriasPlaneador(copia, codMateria.getText(), Double.parseDouble(semestre.getText()), "A", tipoE.isSelected(), epsilon.isSelected(), pensum, false, 0, plan);
                        } 
                        catch (NumberFormatException e1) 
                        {
                            JOptionPane.showMessageDialog(this, new JLabel(e1.getMessage()), "Error", JOptionPane.ERROR_MESSAGE);
                            sistema.escribirException(e1);
                            e1.printStackTrace();
                        }
                    }
                }
        }            
        }
        else if (e.getSource() == editarMateria)
        {
            if(copia.darCursosTomados().isEmpty())
            {
                JOptionPane.showMessageDialog(this, new JLabel("Tienes que empezar una planeación antes de editar las materias"), "Error", JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                JTextField codEditar = new JTextField();
                final JComponent[] materia = new JComponent[] 
                {
                    new JLabel("Escriba el código del curso a editar:"),
                    codEditar
                };
                int result = JOptionPane.showConfirmDialog(this, materia, "Editar curso", JOptionPane.PLAIN_MESSAGE);
                
                if(result == JOptionPane.OK_OPTION && codEditar.getText().equals(""))
                {
                    JOptionPane.showMessageDialog(this, new JLabel("Tienes que completar todos los datos."), "Error", JOptionPane.ERROR_MESSAGE);
                }
                else if(result == JOptionPane.OK_OPTION)
                {
                    boolean encontrar = false;
                    MateriaEstudiante editar = null;
                    for(MateriaEstudiante materiaAEditar : copia.darCursosTomados())
                    {
                        if (materiaAEditar.darCodigo().contains(codEditar.getText()))
                        {
                            encontrar = true;
                            editar = materiaAEditar;
                            break;
                        }
                    }
    
                    if(encontrar == false)
                    {
                        JOptionPane.showMessageDialog(this, new JLabel("La materia no fue encontrada en los cursos inscritos."), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {
                        JComboBox<String> estado = new JComboBox<String>();
                        estado.addItem("Inscrita");
                        estado.addItem("Retirada");
                        estado.addActionListener(this);
                        JTextField semestre = new JTextField();
                        final JComponent[] edicion = new JComponent[] 
                        {
                            new JLabel("Estado de la materia:"),
                            estado,
                            new JLabel("Semestre a cambiar:"),
                            semestre
                        };
                        int resultado = JOptionPane.showConfirmDialog(this, edicion, "Editar curso", JOptionPane.PLAIN_MESSAGE);
                        if(resultado == JOptionPane.OK_OPTION && semestre.getText().equals(""))
                        {
                            JOptionPane.showMessageDialog(this, new JLabel("Tienes que completar todos los datos."), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        else if (resultado == JOptionPane.OK_OPTION)
                        {
                            try
                            {
                                    int cont = 0;
                                    editar.cambiarSemestre(Double.parseDouble(semestre.getText()));
                                    String est = estado.getSelectedItem().toString();
                                    if(est.equals("Inscrita"))
                                    {   
                                        editar.setRetiro(false);
                                        for (String part : parts)
                                        {
                                            if (part.contains(editar.darCodigo()))
                                            {
                                                parts.set(cont, editar.darCodigo()+"      "+String.valueOf(editar.darSemestre())+"\n");
                                            }
                                            cont += 1;
                                        }
                                        actualizarLista();
                                    }
                                    else if(est.equals("Retirada"))
                                    {
                                        editar.setRetiro(true);
                                        copia.retirarMateria(editar);
                                        ArrayList<String> partcopy = (ArrayList<String>) parts.clone();
                                        for (String part : partcopy)
                                        {
                                            if (part.contains(editar.darCodigo()))
                                            {
                                                parts.remove(cont);
                                            }
                                            cont += 1;
                                        }
                                        actualizarLista();
                                    }
                                    if(editar.darInfoRetiro() == false)
                                    {
                                        
                                        JOptionPane.showMessageDialog(this, new JLabel("Semestre cambiado satisfactoriamente a: " + editar.darSemestre()+" Estado de la materia cambiado a: Inscrita"), null, JOptionPane.INFORMATION_MESSAGE);
                                    }
                                    else
                                    {
                                        JOptionPane.showMessageDialog(this, new JLabel("Semestre cambiado satisfactoriamente a: " + editar.darSemestre()+" Estado de la materia cambiado a: Retirada" ), null, JOptionPane.INFORMATION_MESSAGE);
                                    }
                            }
                            catch (NumberFormatException exa)
                            {
                                JOptionPane.showMessageDialog(this, new JLabel("Inserta un semestre válido"), "Error", JOptionPane.ERROR_MESSAGE);
                                sistema.escribirException(exa);
                                exa.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        else if (e.getSource() == guardar)
        {
            ArrayList<String> cop = (ArrayList<String>) parts.clone();
            cop.remove(0);
            cop.remove(0);
            if(cop.isEmpty())
            {
                JOptionPane.showMessageDialog(this, new JLabel("No has realizado ninguna planeación."), "Error", JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                ArrayList<String> partscopy = (ArrayList<String>) parts.clone();
                partscopy.remove(0);
                partscopy.remove(0);
                int cont = 0;
                for(String part : partscopy)
                {
                    if(part.contains("\n\n"));                    
                    {
                        String part2 = part.substring(0, part.length()-1);
                        partscopy.set(cont, part2);
                    }
                    cont += 1;
                }
                String guardarPlan = String.join("\n", partscopy);
                System.out.println(guardarPlan);
                File archivo = null;
		        JFileChooser fc = new JFileChooser();
    		    fc.setDialogTitle("Selecciona dónde guardar el plan.");
	    		fc.setFileFilter(new FiltroCSV());
                int response = fc.showSaveDialog(this);
                {
                    if (response == JFileChooser.APPROVE_OPTION)
                    {
                        archivo = fc.getSelectedFile();
                        try {
                            planeador.guardarPlaneación(guardarPlan, sistema, estudiante, archivo);
                        } 
                        catch (FileNotFoundException e1) 
                        {
                            JOptionPane.showMessageDialog(this, new JLabel("El archivo no fue encontrado"), "Error", JOptionPane.ERROR_MESSAGE);
                            e1.printStackTrace();
                            sistema.escribirException(e1);
                        } 
                        catch (UnsupportedEncodingException e2) 
                        {
                            JOptionPane.showMessageDialog(this, new JLabel("Hubo un problema con el encoding del archivo"), "Error", JOptionPane.ERROR_MESSAGE);
                            e2.printStackTrace();
                            sistema.escribirException(e2);
                        }
                    }
                }
            }
        }
	}

    public void actualizarLista()
    {
        parts.add(lastSubject);
        ventanaMain.actualizarMain(new VentanaPlaneador(estudiante, ventanaMain, sistema, pensum, copia, parts, esCoordinador, coordinador));
    }
}
