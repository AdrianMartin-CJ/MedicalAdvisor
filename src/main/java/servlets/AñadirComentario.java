package servlets;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import domain.Centro;
import domain.Usuario;
import exceptions.DomainException;
import exceptions.ServiceException;
import servicios.ServicioAcceso;
import servicios.ServicioCentro;
import servicios.ServicioCita;
import servicios.ServicioComentario;
import servicios.ServicioEspecialidad;
import servicios.ServicioServicio;
import util.Fecha;


/**
 * Servlet implementation class A�adirCita
 */
@WebServlet("/A�adirComentario")
public class A�adirComentario extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public A�adirComentario() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//SESI�N 1- Iniciamos la sesi�n 
		HttpSession session = request.getSession();
		
		//Recuperamos el usuario
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		
		//Dispacher
		String salida = "";
		
		////0~Recuperamos los datos de la cita a a�adir (IdCentro, Usuario, Fecha y Mensaje) introducidos por el form en PerfilCentro.jsp
		String id = request.getParameter("inputIdCentro");
		String nombreUsuario = request.getParameter("inputUsuario");
		String fechaInput = request.getParameter("inputFecha");
		String mensaje = request.getParameter("inputMensaje");
		
		// - para a�adir la cita del Usuario de la BD
		ServicioComentario sComentario = new ServicioComentario();
	
		// - para recuperar todos los Centros de la BD
		ServicioCentro sCentro = new ServicioCentro();
		List<Centro> centros = new ArrayList<Centro>();
		
		// - para recuperar los detalles de todos los Centros
		ServicioAcceso sAcceso = new ServicioAcceso();
		ServicioEspecialidad sEspecialidad = new ServicioEspecialidad();
		ServicioServicio sServicio = new ServicioServicio();
		//ServicioComentario sComentario = new ServicioComentario();
		
		Date fecha = null;
		
		//Fecha
				try {
					fecha = Fecha.convertirADate(fechaInput, "yyyy-MM-dd");
					
				} catch (ParseException e) {
					salida=("/Fin.jsp");
					request.setAttribute("mensaje","Formato fecha err�neo.");//Error interno
				}
				
				int idCentro = Integer.parseInt(id);
	
				System.out.println("USUARIO RECUPERADO: " + nombreUsuario);
				System.out.println("FECHA RECUPERADA: " + fecha);
				System.out.println("MENSAJE RECUPERADO: " + mensaje);
		try {

			//Insertamos el comentario
			sComentario.a�adirComentario(idCentro, nombreUsuario, fecha, mensaje);

			//RECUPERAMOS LOS EXTRAS DE LOS CENTROS (PARA ACTUALIZAR LOS COMENTARIOS)
			try {
				
				//6a~Recuperamos todos los CENTROS
				//System.out.println("Entra bien por aqui = RECUPERAR CENTROS (BASE)");
				
				centros = sCentro.RecuperarCentros();
				
//				for (int i=0; i>centros.size(); i++) {
//					System.out.println("recuperado$$$$: " + centros.get(i).getNombre());
//				}
		
				
				try {
					//6b~Recuperamos los detalles de todos los CENTROS

//					System.out.println("Entra bien por aqui = RECUPERAR CENTROS (EXTRAS)");

					for (int i=0; i<centros.size(); i++) {

						centros.get(i).setAcceso(sAcceso.RecuperarAccesoPorIdCentro(centros.get(i).getIdCentro()));
						centros.get(i).setEspecialidades(sEspecialidad.RecuperarEspecialidadesPorIdCentro(centros.get(i).getIdCentro()));
						centros.get(i).setServicio(sServicio.RecuperarServicioPorIdCentro(centros.get(i).getIdCentro()));
						centros.get(i).setComentario(sComentario.RecuperarComentariosPorIdCentro(centros.get(i).getIdCentro()));
						
//						System.out.println("Acceso en Metro: " + centros.get(i).getAcceso().getMetro());
//						
//							for(int k=0; k<centros.get(i).getEspecialidades().size(); k++) {
//								
//								System.out.println("Especialidades: " + centros.get(i).getNombre() + " tiene la especialidad: " + centros.get(i).getEspecialidades().get(k).getEspecialidad());
//							}
//							
//						System.out.println("Servicio Restaurantes de Centro: " + centros.get(i).getServicio().getRestaurantes());
//						
//							for(int j=0; j<centros.get(i).getComentario().size(); j++) {
//							
//								System.out.println("Comentario: " + centros.get(i).getNombre() + " tiene el COMENTARIO: " + centros.get(i).getComentario().get(j).getMensaje());
//							}
						
					}
					
//					System.out.println("TODO GUT");

				}catch(ServiceException e) {
					request.setAttribute("centros", "No existen centros");
				}
					
			}catch(ServiceException e) {
				request.setAttribute("centros", "No existen centros");
			}
				
			
			//*Guardamos todos los CENTROS
			session.setAttribute("centros", centros);
			
			salida = ("/Inicio.jsp");
				
		} catch (DomainException | ServiceException e) {

			if(e.getCause()==null){
				salida=("/Fin.jsp"); //Poner en salida = "/Inicio.jsp"
				request.setAttribute("mensaje", e.getMessage() );
			}else{
				salida=("/Fin.jsp");
				request.setAttribute("mensaje","Error interno");//Error interno
			}
		}
		
		getServletContext().getRequestDispatcher(salida).forward(request, response);

	}

}
