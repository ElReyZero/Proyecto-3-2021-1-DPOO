package Sistema;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import IdentificadorUsuario.CoordinadorAcademico;
import IdentificadorUsuario.Estudiante;
import curriculo.Pensum;

///Hecho por: Juan Andrés Romero C - 202013449
/// Luccas Rojas - 201923052

public class systemMain 
{
    analizadorArchivo analizador;

    public systemMain()
    {
        analizador = new analizadorArchivo();
    }

    public Pensum cargarPensumAnalizador(File archivo)
    {
        return analizador.cargarPensum(archivo);
    }

    public Pensum darPensum()
    {
        return analizador.darPensum();
    }

    public int cargarAvanceEstudiante(File archivo, Estudiante estudiante) throws BannerException
    {
        return analizador.cargarAvanceEstudiante(archivo, estudiante);
    }

    public int guardarAvanceEstudiante(File archivo, Estudiante estudiante) throws FileNotFoundException, UnsupportedEncodingException
    {
        return analizador.guardarAvanceEstudianteArchivo(archivo, estudiante.darNombre(), estudiante.darCodigo(), estudiante.darCarrera(), estudiante.darCursosTomados());
    }

    public int guardarPlan(File archivo, String plan, Estudiante estudiante) throws FileNotFoundException, UnsupportedEncodingException
    {
        return analizador.guardarPlaneación(archivo, plan, estudiante);
    }

    public int cargarAvanceCoordinador(File archivo, CoordinadorAcademico coordinador) throws BannerException
    {
        return analizador.cargarAvanceCoordinador(archivo, coordinador);
    }
    
    public void escribirLog(String error)
    {
    	analizador.escribirLog(error);
    }
    
    public void escribirException(Exception e)
    {
    	analizador.escribirErrorLog(e);
    }

    public Estudiante cargarReglasPrograma(File reglas, Estudiante estudiante, Pensum nuevoPensum) throws BannerException, IOException, CloneNotSupportedException
    {
        return analizador.cargarReglasPrograma(reglas, estudiante, nuevoPensum);
    }

    public ArrayList<String> cargarCartelera(File cartelera) throws IOException
    {
        return analizador.cargarCartelera(cartelera);
    }
}

