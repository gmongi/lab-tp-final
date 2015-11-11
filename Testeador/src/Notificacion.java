import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Mock
public class Notificacion {

	private int id;
	@MockTodayAttribute
	public Date fecha_envio;
	private Date fecha_recepcion;
	@MockStringAttribute({"Alegre", "Entusiasmado", "Molesto"})
	public String contenido;
	@MockStringAttribute({"Establo-Terapia", "Pista", "Hogar"})
	public String contexto;
	@MockStringAttribute({"<Predeterminada>", "Emociones", "Alimentos", "Actividades y Paseos"})
	public String categoria;
	@MockStringAttribute({"Juan", "Pedro", "Juana", "Manuela"})
	public String ninio;
	private ArrayList<String> etiquetas;

	public Date getFecha_envio() {
		return fecha_envio;
	}

	public void setFecha_envio(Date fecha_envio) {
		this.fecha_envio = fecha_envio;
	}

	public Date getFecha_recepcion() {
		return fecha_recepcion;
	}

	public void setFecha_recepcion(Date fecha_recepcion) {
		this.fecha_recepcion = fecha_recepcion;
	}

	public String getContenido() {
		return contenido;
	}

	public void setContenido(String contenido) {
		this.contenido = contenido;
	}

	public String getContexto() {
		return contexto;
	}

	public void setContexto(String contexto) {
		this.contexto = contexto;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getNinio() {
		return ninio;
	}

	public void setNinio(String ninio) {
		this.ninio = ninio;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<String> getEtiquetas() {
		return etiquetas;
	}

	public void setEtiquetas(ArrayList<String> etiquetas) {
		this.etiquetas = etiquetas;
	}
}
