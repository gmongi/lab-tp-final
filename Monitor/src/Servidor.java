import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Properties;
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
			Properties p = new Properties();
			String archivo = "conf.properties";
			InputStream input = null;
			try {
				input = new FileInputStream(archivo);
			} catch (FileNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			try {
				p.load(input);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			server = HttpServer.create(new InetSocketAddress(Integer.parseInt(p.getProperty("port"))), 0);
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