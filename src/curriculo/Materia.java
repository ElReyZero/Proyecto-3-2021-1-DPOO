package curriculo;
public class Materia
 {
    protected String nombre;
    protected String preRequisitos;
    protected String requisitos;
    protected double creditos;
    protected String tipoMateria;
    protected boolean semanas16;
    protected String codigo;
    protected int nivel;
    protected double semestreSugerido;

    public Materia(String pnombre,String pcodigo, String ppreRequisitos, String prequisitos, double pcreditos, String ptipoMateria, int pNivel, boolean psemanas16, double pSemestreSugerido)

    {
        nombre = pnombre;
        codigo = pcodigo;
        preRequisitos = ppreRequisitos;
        requisitos = prequisitos;
        creditos = pcreditos;
        tipoMateria = ptipoMateria;
        semanas16 = psemanas16;
        nivel = pNivel;
        semestreSugerido = pSemestreSugerido;
    }

public String darNombre()
{
    return nombre;
}
public String darCodigo()
{
    return codigo;
}
public String darPreRequisitos()
{
    return preRequisitos;
}
public String darRequisitos()
{
   return requisitos; 
}
public double darCreditos()
{
    return creditos;
}
public String darTipoMateria()
{
    return tipoMateria;
}
public boolean darSemanas16()
{
    return semanas16;
}

public int darNivel()
{
    return nivel;
}

public double darSemestreSugerido()
{
    return semestreSugerido;
}

public void setType(String pTipo)
{
    tipoMateria = pTipo;
}

}
