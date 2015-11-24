import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class NuevasNotificacionesHandler implements HttpHandler {

	private Servidor servidor;
	
	public NuevasNotificacionesHandler(Servidor s) {
		this.servidor = s;
	}
	
	@Override
	public void handle(HttpExchange req) throws IOException {
		JSONLoader(IOUtils.toString(req.getRequestBody()));
		servidor.nuevasNotificaciones();
	}

	private void JSONLoader(String notifs) {
		Conector con = new Conector();

		ObjectMapper mapper = new ObjectMapper();

		JsonNode notificaciones;
		try {
			notificaciones = mapper.readTree(notifs);

			con.connect();
			int count = 0;
			for (JsonNode i : notificaciones) {
				con.nuevaNotificacion(i.get("fecha").asText(), i.get("contenido").asText(), i.get("contexto").asText(),
						i.get("categoria").asText(), i.get("ninio").asText());
				count++;
			}
			System.out.println("Se cargaron notifs: "+count);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			con.close();
		}
	}
}
