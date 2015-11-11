import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MockGenerator {

	public static void main(String[] args) {
		List<Notificacion> l = MockGenerator.createMockInstances(Notificacion.class, 3);
	}

	public static <T> List<T> createMockInstances(Class<T> clase, int cant) {
		List<T> lista = new ArrayList<T>();

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
		} catch (

		InstantiationException e1)

		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (

		IllegalAccessException e1)

		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return lista;
	}
}