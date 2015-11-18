import java.text.SimpleDateFormat;
import java.util.Date;

@Mock
public class Notificacion {

	@MockTodayAttribute
	public Date fecha;
	@MockStringAttribute({"Alegre", "Entusiasmado", "Molesto"})
	public String contenido;
	@MockStringAttribute({"Establo-Terapia", "Pista", "Hogar"})
	public String contexto;
	@MockStringAttribute({"<Predeterminada>", "Emociones", "Alimentos", "Actividades y Paseos"})
	public String categoria;
	@MockStringAttribute({"Juan", "Pedro", "Juana", "Manuela"})
	public String ninio;

	public String getFecha() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(fecha);
	}

	public void setFecha(Date fecha_envio) {
		this.fecha = fecha_envio;
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

}
