import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class Servidor implements Runnable {

	private HttpServer server;

	@Override
	public void run() {
		try {
			server = HttpServer.create(new InetSocketAddress(55555), 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		server.createContext("/monitor", new NuevasNotificacionesHandler());
		server.setExecutor(null);
		server.start();
		System.out.println("Arranque");
	}

}