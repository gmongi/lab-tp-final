import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MockGenerator {

	public static void main(String[] args) throws JsonProcessingException {
		List<Notificacion> lista = MockGenerator.createMockInstances(Notificacion.class, 3);
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(mapper.writeValueAsString(lista));
	}

	public static <T> List<T> createMockInstances(Class<T> clase, int cant) {
		List<T> lista = new ArrayList<T>();
		if (clase.isAnnotationPresent(Mock.class)) {
			T n = null;
			try {
				for (int i = 0; i < cant; i++) {
					n = clase.newInstance();
					for (Field f : clase.getDeclaredFields()) {

						if (f.isAnnotationPresent(MockStringAttribute.class)) {
							MockStringAttribute a = f.getAnnotation(MockStringAttribute.class);
							f.set(n, a.value()[new Random().nextInt(a.value().length)]);
						}
						if (f.isAnnotationPresent(MockTodayAttribute.class)) {							
							f.set(n, new Date());
						}

					}
					lista.add(n);
				}
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return lista;
	}
}