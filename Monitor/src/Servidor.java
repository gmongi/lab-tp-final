import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

public class Servidor implements Runnable {

	private HttpServer server;
	private Monitor monitor;
	
	public Servidor(Monitor monitor) {
		this.monitor = monitor;
	}
	
	@Override
	public void run() {
		try {
			server = HttpServer.create(new InetSocketAddress(55555), 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		server.createContext("/monitor", new NuevasNotificacionesHandler(this));		
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
	}

	public synchronized void nuevasNotificaciones(){
		monitor.nuevasNotificaciones();
		notify();
	}

}