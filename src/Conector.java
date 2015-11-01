import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Conector {
	String url = "monitor.db";
	Connection connect;

	String selectCont = "SELECT id FROM contenidos WHERE texto = ?";
	String selectContx = "SELECT id FROM contextos WHERE texto = ?";
	String selectCat = "SELECT id FROM categorias WHERE texto = ?";
	String selectNinio = "SELECT id FROM ninios WHERE nombre = ?";

	String nuevaNotificacion = "INSERT INTO notificaciones(fecha_envio,fecha_recepcion,contenido_id,contexto_id,categoria_id,ninio_id) "
			+ "VALUES (?, ?, ?, ?, ?, ?)";

	public void connect() {
		try {
			connect = DriverManager.getConnection("jdbc:sqlite:" + url);
			if (connect != null) {
				System.out.println("Conectado");
			}
		} catch (SQLException ex) {
			System.err.println("No se ha podido conectar a la base de datos\n" + ex.getMessage());
		}
	}

	public void close() {
		try {
			connect.close();
		} catch (SQLException ex) {
			Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void nuevaNotificacion(String fecha, String contenido, String contexto, String categoria, String ninio) {
		PreparedStatement st = null;
		try {
			st = connect.prepareStatement(nuevaNotificacion);
			st.setString(1, fecha);
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			st.setString(2, format.format(new Date()));
			st.setInt(3, getIdContenido(contenido));
			st.setInt(4, getIdContexto(contexto));
			st.setInt(5, getIdCategoria(categoria));
			st.setInt(6, getIdNinio(ninio));
			st.executeUpdate();
			st.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getIdContenido(String texto) {
		Statement st;
		int ret = 0;
		try {
			st = connect.createStatement();
			ResultSet rs = st.executeQuery("SELECT id FROM contenidos WHERE texto = '" + texto + "'");
			rs.next();
			ret = rs.getInt("id");
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	public int getIdContexto(String texto) {
		Statement st;
		int ret = 0;
		try {
			st = connect.createStatement();
			ResultSet rs = st.executeQuery("SELECT id FROM contextos WHERE texto = '" + texto + "'");
			rs.next();
			ret = rs.getInt("id");
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	public int getIdCategoria(String texto) {
		Statement st;
		int ret = 0;
		try {
			st = connect.createStatement();
			ResultSet rs = st.executeQuery("SELECT id FROM categorias WHERE texto = '" + texto + "'");
			if (rs.next()) {
				ret = rs.getInt("id");
			}
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	public int getIdNinio(String nombre) {
		Statement st;
		int ret = 0;
		try {
			st = connect.createStatement();
			ResultSet rs = st.executeQuery("SELECT id FROM ninios WHERE nombre = '" + nombre + "'");
			rs.next();
			ret = rs.getInt("id");
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	public int getIdEtiqueta(String texto) {
		Statement st;
		int ret = 0;
		try {
			st = connect.createStatement();
			ResultSet rs = st.executeQuery("SELECT id FROM etiquetas WHERE texto = '" + texto + "'");
			rs.next();
			ret = rs.getInt("id");
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	private Notificacion armarNotificacion(ResultSet rs) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Notificacion n = new Notificacion();
		try {
			n.setId(rs.getInt("id"));
			n.setFecha_envio(format.parse(rs.getString("fecha_envio")));
			n.setFecha_recepcion(format.parse(rs.getString("fecha_recepcion")));
			n.setContenido(rs.getString("contenido"));
			n.setContexto(rs.getString("contexto"));
			n.setNinio(rs.getString("ninio"));

			Statement st2 = connect.createStatement();
			ResultSet rs2;
			// agregar categoria
			if (rs.getInt("categoria_id") != 0) {
				rs2 = st2.executeQuery("SELECT texto FROM categorias WHERE id = " + rs.getInt("categoria_id"));
				rs2.next();
				n.setCategoria(rs2.getString("texto"));
			} else {
				n.setCategoria("<Predeterminado>");
			}
			
			// agregar etiquetas
			ArrayList<String> etiquetas = new ArrayList<String>();
			rs2 = st2.executeQuery("SELECT * FROM notificacion_etiqueta, etiquetas WHERE notificacion_id = "+n.getId());
			while (rs2.next()){
				etiquetas.add(rs2.getString("texto"));
			}
			
			n.setEtiquetas(etiquetas);
			
		} catch (SQLException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return n;
	};

	public List<Notificacion> getNotificaciones() {
		List<Notificacion> ret = new ArrayList<Notificacion>();
		String sql = "SELECT notificaciones.id, fecha_envio, fecha_recepcion, categoria_id, contextos.texto as contexto, contenidos.texto as contenido, ninios.nombre as ninio "
				+ "FROM notificaciones, contenidos, contextos, ninios "
				+ "WHERE contenido_id = contenidos.id AND contexto_id = contextos.id AND ninio_id = ninios.id";

		Statement st;
		ResultSet rs;
		try {
			st = connect.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next()) {
				ret.add(armarNotificacion(rs));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	public List<Notificacion> getNotificacionesFiltradas(String ninio, String contenido, String contexto, String categoria,
			String etiqueta) {
		List<Notificacion> ret = new ArrayList<Notificacion>();
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT notificaciones.id, fecha_envio, fecha_recepcion, categoria_id, contextos.texto as contexto, contenidos.texto as contenido, ninios.nombre as ninio "
						+ "FROM notificaciones, categorias, contenidos, contextos, ninios " + "WHERE ");
		if (ninio.equals("") && contenido.equals("") && contexto.equals("") && categoria.equals("") && etiqueta.equals("")) {
			return getNotificaciones();
		}
		;

		if (!ninio.equals("")) {
			sql.append("");
		}
		if (!ninio.equals("")) {

		}
		if (!ninio.equals("")) {

		}
		if (!ninio.equals("")) {

		}
		if (!ninio.equals("")) {

		}

		return ret;
	}

	public void nuevaEtiqueta(String text) {
		try {
			Statement st = connect.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM etiquetas WHERE texto = '" + text + "'");
			if (!rs.next()) {
				st.executeUpdate("INSERT INTO etiquetas (texto) VALUES ('" + text + "')");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public List<String> getEtiquetas() {
		ArrayList<String> ret = new ArrayList<String>();
		try {
			Statement st = connect.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM etiquetas ORDER BY texto ASC");
			while (rs.next()) {
				ret.add(rs.getString("texto"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	public void eliminarEtiqueta(String string) {
		try {
			Statement st = connect.createStatement();
			int id = getIdEtiqueta(string);
			
			// Eliminar Etiqueta
			st.executeUpdate("DELETE FROM etiquetas WHERE id = "+id);
			
			// Elinimar Relaciones
			st.executeUpdate("DELETE FROM notificacion_etiqueta WHERE etiqueta_id = "+id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void renombrarEtiqueta(String original, String nuevo) {
		try {
			Statement st = connect.createStatement();
			st.executeUpdate("UPDATE etiquetas SET texto = '"+nuevo+"' WHERE texto = '"+original+"'");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void asignarEtiqueta(String string, int id_notificacion) {
		try {
			Statement st = connect.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM etiquetas WHERE texto = '"+string+"'");
			rs.next();
			int id_etiqueta = rs.getInt("id");
			
			rs = st.executeQuery("SELECT * FROM notificacion_etiqueta WHERE notificacion_id = "+id_notificacion+" AND etiqueta_id = "+id_etiqueta);
			if (rs.next()){
				st.executeUpdate("DELETE FROM notificacion_etiqueta WHERE notificacion_id = "+id_notificacion+" AND etiqueta_id = "+id_etiqueta);
			}else{
				st.executeUpdate("INSERT INTO notificacion_etiqueta VALUES ("+id_notificacion+", "+id_etiqueta+")");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
