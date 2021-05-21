package IdentificadorUsuario;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import Sistema.BannerException;
import Sistema.analizadorArchivo;
import curriculo.Materia;
import curriculo.MateriaEstudiante;
import curriculo.Pensum;
import funcionalidades.reporteNotas;
@SuppressWarnings("unchecked")

public class Estudiante extends Usuario implements Cloneable{

	//Atributos
	private String carrera;
	private ArrayList<MateriaEstudiante> cursosTomados;
	private ArrayList<String> cursosTomadosArrayString;
	private String tomadosString;
	private double credsSemestre;
	private double currentSemestre;
	
	//Constructor
	public Estudiante(String pNombre, String pCodigo, String pCarrera) 
	{
		super(pNombre, pCodigo);
		carrera = pCarrera;
		cursosTomados = new ArrayList<MateriaEstudiante>();
		cursosTomadosArrayString = new ArrayList<String>();
		tomadosString = "-----------------------------------\n";
		credsSemestre = 0;
		currentSemestre = 0;
	}

	//Métodos
	public int registrarMaterias(String codigo, double semestre, String nota, boolean tipoE, boolean epsilon, Pensum pensum, boolean CLE, double clecreds) throws BannerException
	{
		if (codigo.length() != 9 || !codigo.contains("-"))
		{
			throw new BannerException("El código de materia "+codigo+" no está escrito en un formato adecuado. Formato: AAAA-XXXX");
		}
		var listaMaterias = pensum.darMateriasPensum();
		String matString = pensum.darMateriasString();
		if(!tomadosString.contains(codigo))
		{	if(matString.contains(codigo))
			{
				for(Materia current:listaMaterias)
				{
					if(current.darCodigo().contains(codigo) && (revisarExtracreditacion(semestre, current.darCreditos()) == false))
					{
						throw new BannerException("Error al inscribir materias, se está sobrepasando el límite de créditos. Conflicto con: "+ current.darCodigo() + " Creds con esta materia: " + String.valueOf(current.darCreditos() + credsSemestre));
					}
					if (current.darCodigo().contains(codigo) && current.darNivel() >=3 && !codigo.equals("LENG-3999"))
					{
						for(int i = 0; pensum.darMateriasNivel1String().size()>i; i++)
						{
							if(!cursosTomadosArrayString.contains(pensum.darMateriasNivel1String().get(i)))
							{
								throw new BannerException("Para poder inscribir " + codigo + " necesitas haber inscrito todas las materias de nivel 1");
							}
						}	
					}
					if ((current.darCodigo().contains(codigo) && (current.darNivel() == 4| current.darCodigo().equals("ISIS-3007") && !codigo.equals("LENG-3999"))))
					{
						for(int i = 0; pensum.darMateriasNivel2String().size()>i; i++)
						{
							if(!cursosTomadosArrayString.contains(pensum.darMateriasNivel2String().get(i)))
							{
								throw new BannerException("Para poder inscribir " + codigo + " necesitas haber inscrito todas las materias de nivel 2");
							}
						}
					}
					if(current.darCodigo().contains(codigo) && current.darPreRequisitos().equals("N/A") && current.darRequisitos().equals("N/A"))
					{
						MateriaEstudiante agregada = revisarAprobado(current, nota, semestre);
						cursosTomados.add(agregada);
						tomadosString += current.darCodigo()+"\n";
						cursosTomadosArrayString.add(current.darCodigo());
						credsSemestre += current.darCreditos();
						currentSemestre = semestre;
						return 0;
						
					}
					else if(current.darCodigo().contains(codigo))
					{
						ArrayList<String> prerrequisitos = new ArrayList<String> (Arrays.asList(current.darPreRequisitos().split("&")));
						ArrayList<String> copiaPrerrequisitos = (ArrayList<String>) prerrequisitos.clone();
						ArrayList<String> correquisitos = new ArrayList<String> (Arrays.asList(current.darRequisitos().split("&")));
						ArrayList<String> copiaCorrequisitos = (ArrayList<String>) correquisitos.clone();
						if(!prerrequisitos.get(0).equals("N/A"))
						{
							for(MateriaEstudiante tomada:cursosTomados)
							{

								for(String codMateria : copiaPrerrequisitos)
								{
									try
									{
										Double grade = Double.parseDouble(tomada.darNota());
										if(codMateria.contains(tomada.darCodigo()) && (grade>=3.0))
										{
										prerrequisitos.remove(codMateria);
										}
									}
									catch (NumberFormatException e)
									{
										if(codMateria.contains(tomada.darCodigo()) && (tomada.darNota().equals("A")))
										{
											prerrequisitos.remove(codMateria);
										}
									}
								}
							}	
							if (prerrequisitos.size()!= 0)
							{
								throw new BannerException("Se está intentando registrar "+ codigo +" sin haber inscrito todos los prerrequisitos previamente. Prerequisitos(s) sin inscribir: " + String.join(", ", prerrequisitos));
							}

						}
						else 
						{
							prerrequisitos.remove(0);
						}
						if(!correquisitos.get(0).equals("N/A"))
						{
							{
								for(MateriaEstudiante tomada:cursosTomados)
								{
									for(String codMateria : copiaCorrequisitos)
								{
									try
									{
										Double grade = Double.parseDouble(tomada.darNota());
										if(codMateria.contains(tomada.darCodigo()) && (grade>=3.0))
										{
										correquisitos.remove(codMateria);
										}
									}
									catch (NumberFormatException e)
									{
										if(codMateria.contains(tomada.darCodigo()) && (tomada.darNota().equals("A")))
										{
											correquisitos.remove(codMateria);
										}
									}
								}
							}
								if (correquisitos.size()!= 0)
								{
									throw new BannerException("Se está intentando registrar "+ codigo +" sin haber inscrito todos los correquisitos previamente. Correquisitos(s) sin inscribir: " + String.join(", ", correquisitos));
								}
							}
						}
						else
						{
							correquisitos.remove(0);
						}
						if(correquisitos.size() == 0 && prerrequisitos.size() == 0)
						{
							MateriaEstudiante agregada = revisarAprobado(current, nota, semestre);
							cursosTomados.add(agregada);
							if(tipoE == true)
							{
								String tipo = agregada.darTipoMateria() + "- Tipo E";
								agregada.setType(tipo);
							}
							if(epsilon == true)
							{
								String tipo = agregada.darTipoMateria() + "- Curso Epsilon";
								agregada.setType(tipo);
							}
							tomadosString += current.darCodigo()+"\n";
							cursosTomadosArrayString.add(current.darCodigo());
							credsSemestre += current.darCreditos();
							currentSemestre = semestre;
							return 0;
						}
					}
				}
			}
		else if(codigo.contains("CB"))
		{
			Materia nuevaMateria = new Materia(codigo, codigo, "N/A", "N/A", 2,"Electiva CBU " +codigo.charAt(2)+codigo.charAt(3), 0, true, semestre);
			MateriaEstudiante agregada = revisarAprobado(nuevaMateria, nota, semestre);
			if(agregada.darCodigo().contains(codigo) && (revisarExtracreditacion(semestre, agregada.darCreditos()) == false))
			{
				throw new BannerException("Error al inscribir materias, se está sobrepasando el límite de créditos. Conflicto con: "+ agregada.darCodigo() + " Creds con esta materia: " + String.valueOf(agregada.darCreditos() + credsSemestre));
			}
			cursosTomados.add(agregada);
			if(tipoE == true)
				{
					String tipo = agregada.darTipoMateria() + "- Tipo E";
					agregada.setType(tipo);
				}
			if(epsilon == true)
				{
					String tipo = agregada.darTipoMateria() + "- Curso Epsilon";
					agregada.setType(tipo);
				}
			tomadosString += nuevaMateria.darCodigo()+"\n";
			cursosTomadosArrayString.add(nuevaMateria.darCodigo());
			credsSemestre += agregada.darCreditos();
			currentSemestre = semestre;
			return 0;
		}
		else if (codigo.equals("BIOL-3300")||codigo.equals("FISI-1038")||codigo.equals("FISI-1048")||codigo.equals("MATE-1107")||codigo.equals("MATE-2211")||codigo.equals("MATE-2301")||codigo.equals("MATE-2303")||codigo.equals("MATE-2411")||codigo.equals("MATE-3712")||codigo.equals("MBIO-2102")||codigo.equals("QUIM-1105")||codigo.equals("QUIM-1301")||codigo.equals("QUIM-1303")||codigo.equals("QUIM-1510")||codigo.equals("QUIM-2620"))
		{
			Materia nuevaMateria = new Materia(codigo, codigo, "N/A", "N/A", 3, "Electiva en Ciencias", 0, true, semestre);
			MateriaEstudiante agregada = revisarAprobado(nuevaMateria, nota, semestre);
			if(agregada.darCodigo().contains(codigo) && (revisarExtracreditacion(semestre, agregada.darCreditos()) == false))
			{
				throw new BannerException("Error al inscribir materias, se está sobrepasando el límite de créditos. Conflicto con: "+ agregada.darCodigo() + " Creds con esta materia: " + String.valueOf(agregada.darCreditos() + credsSemestre));
			}
			cursosTomados.add(agregada);
			if(tipoE == true)
				{
					String tipo = agregada.darTipoMateria() + "- Tipo E";
					agregada.setType(tipo);
				}
			if(epsilon == true)
				{
					String tipo = agregada.darTipoMateria() + "- Curso Epsilon";
					agregada.setType(tipo);
				}
			tomadosString += nuevaMateria.darCodigo()+"\n";
			cursosTomadosArrayString.add(nuevaMateria.darCodigo());
			credsSemestre += agregada.darCreditos();
			currentSemestre = semestre;
			return 0;
		}
		else if(codigo.equals("IBIO-2099")||codigo.equals("IBIO-2240")||codigo.equals("ICYA-1101")||codigo.equals("ICYA-1110")||codigo.equals("ICYA-1116")||codigo.equals("ICYA-1125")||codigo.equals("ICYA-2001")||codigo.equals("ICYA-2401")||codigo.equals("ICYA-2412")||codigo.equals("IELE-1002")||codigo.equals("IELE-1006")||codigo.equals("IELE-1010")||codigo.equals("IELE-1502")||codigo.equals("IELE-2010")||codigo.equals("IELE-2210")||codigo.equals("IELE-2300")||codigo.equals("IELE-2500")||codigo.equals("IIND-2103")||codigo.equals("IIND-2104")||codigo.equals("IIND-2107")||codigo.equals("IIND-2202")||codigo.equals("IIND-2301")||codigo.equals("IIND-2400")||codigo.equals("IMEC-1001")|codigo.equals("IMEC-1330")||codigo.equals("IMEC-1410")||codigo.equals("IMEC-1503")||codigo.equals("IQUI-2010")||codigo.equals("IQUI-2020")||codigo.equals("IQUI-2101")||codigo.equals("IQUI-2200"))
		{
				Materia nuevaMateria = new Materia(codigo, codigo, "N/A", "N/A", 3, "Electiva Ingeniería", 0, true, semestre);
				MateriaEstudiante agregada = revisarAprobado(nuevaMateria, nota, semestre);
				if(agregada.darCodigo().contains(codigo) && (revisarExtracreditacion(semestre, agregada.darCreditos()) == false))
				{
					throw new BannerException("Error al inscribir materias, se está sobrepasando el límite de créditos. Conflicto con: "+ agregada.darCodigo() + " Creds con esta materia: " + String.valueOf(agregada.darCreditos() + credsSemestre));
				}
				cursosTomados.add(agregada);
				if(tipoE == true)
				{
					String tipo = agregada.darTipoMateria() + "- Tipo E";
					agregada.setType(tipo);
				}
				if(epsilon == true)
				{
					String tipo = agregada.darTipoMateria() + "- Curso Epsilon";
					agregada.setType(tipo);
				}
				tomadosString += nuevaMateria.darCodigo()+"\n";
				cursosTomadosArrayString.add(nuevaMateria.darCodigo());
				credsSemestre += agregada.darCreditos();
				currentSemestre = semestre;
				return 0;
		}
		else if(codigo.equals("IIND-4115")||codigo.equals("IIND-4123")||codigo.equals("MATE-3133")||codigo.equals("IELE-4231")||codigo.equals("FISI-3024")||codigo.equals("IELE-3338")||codigo.equals("IELE-4014")||codigo.equals("MATE-3102")||codigo.equals("MATE-4527")||codigo.equals("IBIO-3470")||codigo.equals("MATE-3134")||codigo.equals("IBIO-4680")||codigo.equals("IBIO-4490") || codigo.equals("ISIS-3991") || codigo.equals("ARTI-4202") || codigo.equals("ARTI-4205") || codigo.equals("BCOM-4104") || codigo.equals("MBIT-4102") || codigo.equals("MBIT-4201") || codigo.equals("MBIT-4202") || codigo.equals("MBIT-4203") || codigo.equals("MBIT-4210") || codigo.equals("MBIT-4213") || codigo.equals("MBIT-4214") || codigo.equals("MSIN-4101") || codigo.equals("MSIN-4206") || codigo.equals("MINE-4102") || codigo.equals("MINE-4103") || codigo.equals("MINE-4201") || codigo.equals("MINE-4206") || codigo.equals("MISO-4101"))
		{
			for(int i = 0; pensum.darMateriasNivel1String().size()>i; i++)
				{
					if(!cursosTomadosArrayString.contains(pensum.darMateriasNivel1String().get(i)))
					{
						throw new BannerException("Hubo un problema inscribiendo materias. No se han visto todas las materias de Nivel 1. Error: (Restricción de Nivel)");
					}
				}
			for(int i = 0; pensum.darMateriasNivel2String().size()>i; i++)
			{
				if(!cursosTomadosArrayString.contains(pensum.darMateriasNivel2String().get(i)))
					{
						throw new BannerException("Hubo un problema inscribiendo materias. No se han visto todas las materias de Nivel 2. Error: (Restricción de Nivel)");
					}
			}
			double creds = 3;
			if(codigo.equals("ISIS-3991"))
			{
				creds = 6;
			}
			else if (codigo.equals("ARTI-4202") || codigo.equals("ARTI-4205") || codigo.equals("BCOM-4104") || codigo.equals("MBIT-4102") || codigo.equals("MBIT-4201") || codigo.equals("MBIT-4202") || codigo.equals("MBIT-4203") || codigo.equals("MBIT-4210") || codigo.equals("MBIT-4213") || codigo.equals("MBIT-4214") || codigo.equals("MSIN-4101") || codigo.equals("MSIN-4206") || codigo.equals("MINE-4102") || codigo.equals("MINE-4103") || codigo.equals("MINE-4201") || codigo.equals("MINE-4206") || codigo.equals("MISO-4101"))
			{
				creds = 4;
			}
			Materia nuevaMateria = new Materia(codigo, codigo, "N/A", "N/A", creds, "Electiva Profesional", 4, true, semestre);
			MateriaEstudiante agregada = revisarAprobado(nuevaMateria, nota, semestre);
			if(agregada.darCodigo().contains(codigo) && (revisarExtracreditacion(semestre, agregada.darCreditos()) == false))
			{
				throw new BannerException("Error al inscribir materias, se está sobrepasando el límite de créditos. Conflicto con: "+ agregada.darCodigo() + " Creds con esta materia: " + String.valueOf(agregada.darCreditos() + credsSemestre));
			}
			cursosTomados.add(agregada);
			if(tipoE == true)
			{
				String tipo = agregada.darTipoMateria() + "- Tipo E";
				agregada.setType(tipo);
			}
			if(epsilon == true)
			{
				String tipo = agregada.darTipoMateria() + "- Curso Epsilon";
				agregada.setType(tipo);
			}
			tomadosString += nuevaMateria.darCodigo()+"\n";
			cursosTomadosArrayString.add(nuevaMateria.darCodigo());
			credsSemestre += agregada.darCreditos();
			currentSemestre = semestre;
			return 0;
		}				
	else if(codigo.contains("ISIS-4"))
		{
			for(int i = 0; pensum.darMateriasNivel1String().size()>i; i++)
				{
					if(!cursosTomadosArrayString.contains(pensum.darMateriasNivel1String().get(i)))
					{
						throw new BannerException("Hubo un problema inscribiendo materias. No se han visto todas las materias de Nivel 1. Error: (Restricción de Nivel)");
					}
				}
			for(int i = 0; pensum.darMateriasNivel2String().size()>i; i++)
			{
				if(!cursosTomadosArrayString.contains(pensum.darMateriasNivel2String().get(i)))
					{
						throw new BannerException("Hubo un problema inscribiendo materias. No se han visto todas las materias de Nivel 2. Error: (Restricción de Nivel)");
					}
			}
			Materia nuevaMateria = new Materia(codigo, codigo, "N/A", "N/A", 2, "Electiva Profesional", 0, true, semestre);
			MateriaEstudiante agregada = revisarAprobado(nuevaMateria, nota, semestre);
			if(agregada.darCodigo().contains(codigo) && (revisarExtracreditacion(semestre, agregada.darCreditos()) == false))
			{
				throw new BannerException("Error al inscribir materias, se está sobrepasando el límite de créditos. Conflicto con: "+ agregada.darCodigo() + " Creds con esta materia: " + String.valueOf(agregada.darCreditos() + credsSemestre));
			}
			cursosTomados.add(agregada);
			if(tipoE == true)
			{
				String tipo = agregada.darTipoMateria() + "- Tipo E";
				agregada.setType(tipo);
			}
			if(epsilon == true)
			{
				String tipo = agregada.darTipoMateria() + "- Curso Epsilon";
				agregada.setType(tipo);
			}
			tomadosString += nuevaMateria.darCodigo()+"\n";
			cursosTomadosArrayString.add(nuevaMateria.darCodigo());
			credsSemestre += agregada.darCreditos();
			currentSemestre = semestre;
			return 0;
		}
		
		else if (CLE == true)
		{
			Materia nuevaMateria = new Materia(codigo, codigo, "N/A", "N/A", clecreds, "Curso de Libre Eleccion", 0, true, semestre);
			MateriaEstudiante agregada = revisarAprobado(nuevaMateria, nota, semestre);
			if(agregada.darCodigo().contains(codigo) && (revisarExtracreditacion(semestre, agregada.darCreditos()) == false))
			{
				throw new BannerException("Error al inscribir materias, se está sobrepasando el límite de créditos. Conflicto con: "+ agregada.darCodigo() + " Creds con esta materia: " + String.valueOf(agregada.darCreditos() + credsSemestre));
			}
			cursosTomados.add(agregada);
			if(tipoE == true)
			{
				String tipo = agregada.darTipoMateria() + "- Tipo E";
				agregada.setType(tipo);
			}
			if(epsilon == true)
		{
				String tipo = agregada.darTipoMateria() + "- Curso Epsilon";
				agregada.setType(tipo);
			}
			tomadosString += nuevaMateria.darCodigo()+"\n";					
			cursosTomadosArrayString.add(nuevaMateria.darCodigo());
			credsSemestre += agregada.darCreditos();
			currentSemestre = semestre;
			return 0;
		}
		else
		{
			throw new BannerException(codigo);
		}

    }	
	for (MateriaEstudiante mat : cursosTomados)
	{
		if (mat.darCodigo().contains(codigo))
		{
			String grade = mat.darNota();
			try 
			{
				Double gradeNum = Double.parseDouble(grade);
				Double notaNum = 0.0;
				try 
				{
					notaNum = Double.parseDouble(nota);
				}
				catch (NumberFormatException e)
				{
				}
				if (gradeNum<3.0 && (notaNum >=3.0 || nota.equals("A")))
				{
					double creds = 0;
					for (Materia mater : pensum.darMateriasPensum())
					{
						if(mater.darCodigo().contains(mat.darCodigo()))
						{
							creds = mater.darCreditos();
							break;
						}
					}
					MateriaEstudiante agregada = revisarAprobado(mat, nota, semestre);
					if(agregada.darCodigo().contains(codigo) && (revisarExtracreditacion(semestre, agregada.darCreditos()) == false))
					{
						throw new BannerException("Error al inscribir materias, se está sobrepasando el límite de créditos. Conflicto con: "+ agregada.darCodigo() + " Creds con esta materia: " + String.valueOf(agregada.darCreditos() + credsSemestre));
					}
					agregada.setCredits(creds);
					cursosTomados.add(agregada);
					tomadosString += agregada.darCodigo()+"\n";
					cursosTomadosArrayString.add(agregada.darCodigo());
					credsSemestre += agregada.darCreditos();
					currentSemestre = semestre;
					return 0;
				}
				else
				{
					throw new BannerException("No se puede repetir una materia que no haya sido perdida.");
				}

					} 
				catch (Exception e) 
				{
					Double notaNum = 0.0;
					try 
					{
						notaNum = Double.parseDouble(nota);
					}
					catch (NumberFormatException ex)
					{
					}
					if((grade.equals("R")||grade.equals("I")) && (notaNum >=3.0 || nota.equals("A")))
					{
						double creds = 0;
						for (Materia mater : pensum.darMateriasPensum())
						{
							if(mater.darCodigo().contains(mat.darCodigo()))
							{
								creds = mater.darCreditos();
								break;
							}
						}
						MateriaEstudiante agregada = revisarAprobado(mat, nota, semestre);
						if(agregada.darCodigo().contains(codigo) && (revisarExtracreditacion(semestre, agregada.darCreditos()) == false))
						{
							throw new BannerException("Error al inscribir materias, se está sobrepasando el límite de créditos. Conflicto con: "+ agregada.darCodigo() + " Creds con esta materia: " + String.valueOf(agregada.darCreditos() + credsSemestre));
						}
						agregada.setCredits(creds);
						cursosTomados.add(agregada);
						tomadosString += agregada.darCodigo()+"\n";
						cursosTomadosArrayString.add(agregada.darCodigo());
						credsSemestre += agregada.darCreditos();
						currentSemestre = semestre;
						return 0;
					}
					else
					{
						throw new BannerException("No se puede repetir una materia que no haya sido perdida.");
					}
			}
		}
	}
		throw new BannerException("Error al inscribir materias (Estudiante.java) line414");
	}


	public MateriaEstudiante revisarAprobado(Materia materia, String nota, double semestre)
	{
		try
		{
			Double notaNum = Double.parseDouble(nota);
			if(notaNum < 3)
			{
				MateriaEstudiante agregada = new MateriaEstudiante(materia, nota, semestre);
				agregada.setCredits(0);
				return agregada;
			}
		else
			{
				MateriaEstudiante agregada = new MateriaEstudiante(materia, nota, semestre);
				return agregada;
			}
		}
		catch (Exception e)
		{
			if(nota.equals("R"))
			{
				MateriaEstudiante agregada = new MateriaEstudiante(materia, nota, semestre);
				agregada.setCredits(0);
				return agregada;
			}
			else
			{
				MateriaEstudiante agregada = new MateriaEstudiante(materia, nota, semestre);
				return agregada;
			}
		}
		
	}
	public void guardarAvance(analizadorArchivo analizador, File archivo) throws FileNotFoundException, UnsupportedEncodingException
	{
		analizador.guardarAvanceEstudianteArchivo(archivo, nombre, codigo, carrera, cursosTomados);
		System.out.println("El archivo fue guardado en: " + archivo.getAbsolutePath());
	}

	public ArrayList<MateriaEstudiante> darCursosTomados()
	{
		return cursosTomados;
	}

	public String darCarrera()
	{
		return carrera;
	}
	public ArrayList<String> darCursosTomadosString()
	{
		return cursosTomadosArrayString;
	}

	public void setCursosTomados(ArrayList<MateriaEstudiante> pCursosTomados)
	{
		cursosTomados = pCursosTomados;
	}

	public void setCursosTomadosString(ArrayList<String> cursos)
	{
		cursosTomadosArrayString = cursos;
	}
	@Override
	public Estudiante clone() throws CloneNotSupportedException
	{
		Estudiante cloned = (Estudiante) super.clone();
		cloned.setCursosTomados((ArrayList<MateriaEstudiante>)cloned.darCursosTomados().clone());
		cloned.setCursosTomadosString((ArrayList<String>)cloned.darCursosTomadosString().clone());
		return cloned;
	}
	public void retirarMateria(MateriaEstudiante materia)
	{
		cursosTomados.remove(materia);
		cursosTomadosArrayString.remove(materia.darCodigo());
		tomadosString = tomadosString.replace(materia.darCodigo(), " ");
	}

	public String darTomadosString()
	{
		return tomadosString;
	}

	public double darCredsSemestre()
	{
		return credsSemestre;
	}

	public double darCurrentSemestre()
	{
		return currentSemestre;
	}

	public void borrarDatosEstudiante()
	{
		this.cursosTomados = null;
		this.cursosTomados = new ArrayList<>();
		this.tomadosString = "";
		this.cursosTomadosArrayString = null;
		this.cursosTomadosArrayString = new ArrayList<>();
		this.credsSemestre = 0;
		this.currentSemestre = 0;
	}

	public boolean revisarExtracreditacion(double semestreNuevo, double credsActual)
	{
		if (semestreNuevo != currentSemestre)
		{
			credsSemestre = 0;
		}
		String pga = reporteNotas.promedioPGA(this);
		Double doublePGA = Double.parseDouble(pga);
		if (credsActual+ credsSemestre > 25)
		{
			return false;
		}
		else if (doublePGA < 4 && (credsSemestre + credsActual > 20))
		{
			return false;
		}
		else
		{
			return true;
		}	
	}
}
