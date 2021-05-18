package curriculo;

public class MateriaEstudiante extends Materia 
{
	//Atributos
	private String nota;
	private double semestre;
	private boolean materiaRetirada;

	public MateriaEstudiante(Materia materia, String pNota, double pSemestre) 
	{
		super(materia.darNombre(), materia.darCodigo(), materia.darPreRequisitos(), materia.darRequisitos(), materia.darCreditos(), materia.darTipoMateria(), materia.darNivel(), materia.darSemanas16(), materia.darSemestreSugerido());
		nota =  pNota;
		semestre = pSemestre;
		materiaRetirada = false;
	}
	
	public String darNota()
	{
		return nota;
	}
	public void cambiarNota(String pNota)
	{
		nota = pNota;
	}
	public double darSemestre()
	{
		return semestre;
	}
	public void setCredits(double credits)
	{
		super.creditos = credits;
	}

	public void setType(String tipo)
	{
		super.tipoMateria = tipo;
	}

	public boolean darInfoRetiro()
	{
		return materiaRetirada;
	}

	public void setRetiro(boolean decision)
	{
		materiaRetirada = decision;
	}

	public void cambiarSemestre(double text) 
	{
		semestre = text;
	}
}
