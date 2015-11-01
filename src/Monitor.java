import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.sqlite.date.DateFormatUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

@SuppressWarnings("serial")
public class Monitor extends JFrame {

	private JPanel contentPane;
	private JTextField txtDesde;
	private JTextField txtHasta;
	private JTextField txtCrear;
	private JTextField txtRenombrar;
	private JTable table;
	private JPanel panelFiltros;
	private JComboBox<String> comboNinio;
	private JComboBox<String> comboContenido;
	private JComboBox<String> comboContexto;
	private JComboBox<String> comboCategoria;
	private JComboBox<String> comboEtiqueta;
	private JComboBox<String> comboEliminar;
	private JComboBox<String> comboAsignar;
	private JComboBox<String> comboRenombrar;

	private List<Notificacion> notificaciones;
	private Conector conector = new Conector();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				// JSONLoader();
				Monitor frame = new Monitor();
				frame.setVisible(true);
			}
		});
	}

	private static void JSONLoader() {
		Conector con = new Conector();

		ObjectMapper mapper = new ObjectMapper();

		JsonNode notificaciones;
		try {
			notificaciones = mapper.readTree(new File("notificaciones.json"));

			List<ArrayList<String>> lista = new ArrayList<ArrayList<String>>();

			con.connect();
			int count = 1;
			for (JsonNode i : notificaciones) {
				con.nuevaNotificacion(i.get("fecha").asText(), i.get("contenido").asText(), i.get("contexto").asText(),
						i.get("categoria").asText(), i.get("ninio").asText());
				System.out.println(count);
				count++;
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		con.close();
	}

	private class InicializadorVentana implements WindowListener {

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowClosing(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowOpened(WindowEvent e) {
			cargarTablaNotificaciones(notificaciones);

			cargarComboColumna(1, comboContenido);
			cargarComboColumna(2, comboContexto);
			cargarComboColumna(3, comboCategoria);
			cargarComboColumna(4, comboNinio);

			actualizarCombosEtiqueta();
		}

		private void cargarComboColumna(int columna, JComboBox<String> combo) {
			Set<String> set = new HashSet<String>();
			for (int i = 1; i < table.getModel().getRowCount(); i++) {
				set.add((String) table.getModel().getValueAt(i, columna));
			}

			ArrayList<String> arr = new ArrayList<String>();
			arr.addAll(set);
			Collections.sort(arr);

			combo.addItem("");
			for (String s : arr) {
				combo.addItem(s);
			}
		}

	}

	private void clearTablaNotificaciones() {
		((DefaultTableModel) table.getModel()).setRowCount(0);
	}

	private void cargarTablaNotificaciones(List<Notificacion> notifs) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (Notificacion n : notifs) {
			StringBuilder etiquetas = new StringBuilder();
			for (String s : n.getEtiquetas()) {
				etiquetas.append(s + ", ");
			}
			Object[] row = { format.format(n.getFecha_envio()), n.getContenido(), n.getContexto(), n.getCategoria(), n.getNinio(),
					etiquetas.toString(), n.getId() };
			((DefaultTableModel) table.getModel()).addRow(row);
		}
	}

	private class FiltradorTabla implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			clearTablaNotificaciones();

			ArrayList<Notificacion> auxList = new ArrayList<Notificacion>(notificaciones);
			Notificacion auxNot = null;
			int correccion = 0;
			int i = 0;
			Boolean borrado = false;
			for (Notificacion n : notificaciones) {
				auxNot = notificaciones.get(i);
				String nombre = auxNot.getNinio();
				Boolean resultado = auxNot.getNinio().equals(comboNinio.getSelectedItem().toString());

				if (!borrado && !comboNinio.getSelectedItem().toString().equals("")
						&& !auxNot.getNinio().equals(comboNinio.getSelectedItem().toString())) {
					auxList.remove(i - correccion);
					borrado = true;
				}
				if (!borrado && !comboContenido.getSelectedItem().toString().equals("")
						&& !auxNot.getContenido().equals(comboContenido.getSelectedItem().toString())) {
					auxList.remove(i - correccion);
					borrado = true;
				}
				if (!borrado && !comboCategoria.getSelectedItem().toString().equals("")
						&& !auxNot.getCategoria().equals(comboCategoria.getSelectedItem().toString())) {
					auxList.remove(i - correccion);
					borrado = true;
				}
				if (!borrado && !comboContexto.getSelectedItem().toString().equals("")
						&& !comboContexto.getSelectedItem().toString().equals("<Predeterminado>")
						&& !auxNot.getContexto().equals(comboContexto.getSelectedItem().toString())) {
					auxList.remove(i - correccion);
					borrado = true;
				}

				// FECHAS
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

				if (!borrado && !txtDesde.getText().equals("")) {
					Date desde = null;
					try {
						desde = format.parse(txtDesde.getText());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (desde.after(auxNot.getFecha_envio())) {
						auxList.remove(i - correccion);
						borrado = true;
					}
				}

				if (!borrado && !txtHasta.getText().equals("")) {
					Date hasta = null;
					try {
						hasta = format.parse(txtHasta.getText());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					Calendar c = Calendar.getInstance();
					c.setTime(hasta);
					c.add(Calendar.DATE, 1); // number of days to add
					hasta = c.getTime();

					if (hasta.before(auxNot.getFecha_envio())) {
						auxList.remove(i - correccion);
						borrado = true;
					}
				}

				// IMPLEMENTAR ETIQUETADOS

				if (!borrado && !comboEtiqueta.getSelectedItem().toString().equals("")
						&& !auxNot.getEtiquetas().contains(comboEtiqueta.getSelectedItem())) {
					auxList.remove(i - correccion);
					borrado = true;
				}

				// set for next loop
				if (borrado) {
					correccion++;
				}
				;
				borrado = false;
				i++;
			}

			cargarTablaNotificaciones(auxList);
		}

	}

	private class CreadorEtiqueta implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!txtCrear.getText().equals("")) {
				conector.connect();
				conector.nuevaEtiqueta(txtCrear.getText());
				conector.close();

				actualizarCombosEtiqueta();
			}
		}

	}

	private void actualizarCombosEtiqueta() {
		conector.connect();
		List<String> etiquetas = conector.getEtiquetas();
		conector.close();

		cargarCombo(comboEtiqueta, etiquetas);
		cargarCombo(comboEliminar, etiquetas);
		cargarCombo(comboAsignar, etiquetas);
		cargarCombo(comboRenombrar, etiquetas);
	}

	private void cargarCombo(JComboBox<String> combo, List<String> list) {
		combo.removeAllItems();
		combo.addItem("");
		for (String s : list) {
			combo.addItem(s);
		}
	}

	private class EliminadorEtiqueta implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!comboEliminar.getSelectedItem().toString().equals("")) {
				conector.connect();
				conector.eliminarEtiqueta(comboEliminar.getSelectedItem().toString());
				conector.close();

				actualizarCombosEtiqueta();
			}
		}

	}

	private class RenombradorEtiqueta implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!comboRenombrar.getSelectedItem().toString().equals("")) {
				conector.connect();
				conector.renombrarEtiqueta(comboRenombrar.getSelectedItem().toString(), txtRenombrar.getText());
				conector.close();

				actualizarCombosEtiqueta();
			}
		}

	}

	private class AsignadorEtiqueta implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			int row = table.getSelectedRow();
			if (comboAsignar.getSelectedItem().toString() != "" && row > -1) {
				int id = (int) table.getValueAt(row, 6);

				conector.connect();
				conector.asignarEtiqueta(comboAsignar.getSelectedItem().toString(), id);
				conector.close();

				actualizarNotificaciones();
			}
		}

	}

	private void actualizarNotificaciones() {
		clearTablaNotificaciones();
		conector.connect();
		notificaciones = conector.getNotificaciones();
		conector.close();
		cargarTablaNotificaciones(notificaciones);
	}

	public Monitor() {
		// cargar notificaciones de la BD
		conector.connect();
		notificaciones = conector.getNotificaciones();
		conector.close();

		this.addWindowListener(new InicializadorVentana());

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 813, 524);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		panelFiltros = new JPanel();
		panelFiltros.setBorder(new TitledBorder(null, "Filtros", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelFiltros.setBounds(13, 12, 379, 199);
		contentPane.add(panelFiltros);

		comboContenido = new JComboBox<String>();
		panelFiltros.add(comboContenido);

		comboContexto = new JComboBox<String>();
		panelFiltros.add(comboContexto);

		comboNinio = new JComboBox<String>();
		panelFiltros.add(comboNinio);

		comboCategoria = new JComboBox<String>();
		panelFiltros.add(comboCategoria);

		txtDesde = new JTextField();
		panelFiltros.add(txtDesde);
		txtDesde.setColumns(10);

		txtHasta = new JTextField();
		panelFiltros.add(txtHasta);
		txtHasta.setColumns(10);

		comboEtiqueta = new JComboBox<String>();
		panelFiltros.add(comboEtiqueta);

		JButton btnFiltrar = new JButton("Filtrar");
		btnFiltrar.addActionListener(new FiltradorTabla());
		panelFiltros.add(btnFiltrar);

		JPanel panelEtiquetas = new JPanel();
		panelEtiquetas.setBorder(new TitledBorder(null, "Etiquetas", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelEtiquetas.setBounds(404, 12, 397, 199);
		contentPane.add(panelEtiquetas);

		txtCrear = new JTextField();
		panelEtiquetas.add(txtCrear);
		txtCrear.setColumns(10);

		JButton btnCrear = new JButton("Crear");
		btnCrear.addActionListener(new CreadorEtiqueta());
		panelEtiquetas.add(btnCrear);

		comboEliminar = new JComboBox<String>();
		panelEtiquetas.add(comboEliminar);

		JButton btnEliminar = new JButton("Eliminar");
		btnEliminar.addActionListener(new EliminadorEtiqueta());
		panelEtiquetas.add(btnEliminar);

		comboAsignar = new JComboBox<String>();
		panelEtiquetas.add(comboAsignar);

		JButton btnAsignar = new JButton("Asignar / Desasig.");
		btnAsignar.addActionListener(new AsignadorEtiqueta());
		panelEtiquetas.add(btnAsignar);

		comboRenombrar = new JComboBox<String>();
		panelEtiquetas.add(comboRenombrar);

		txtRenombrar = new JTextField();
		panelEtiquetas.add(txtRenombrar);
		txtRenombrar.setColumns(10);

		JButton btnRenombrar = new JButton("Renombrar");
		btnRenombrar.addActionListener(new RenombradorEtiqueta());
		panelEtiquetas.add(btnRenombrar);

		JScrollPane panelNotificaciones = new JScrollPane();
		panelNotificaciones.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panelNotificaciones.setBounds(13, 223, 788, 266);
		contentPane.add(panelNotificaciones);

		table = new JTable();
		table.setAutoCreateRowSorter(true);
		panelNotificaciones.setViewportView(table);
		table.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { "Fecha/Hora env\u00EDo", "Contenido", "Contexto", "Categor\u00EDa", "Ni\u00F1@", "Etiquetas", "Id" }) {
			Class[] columnTypes = new Class[] { String.class, String.class, String.class, String.class, String.class, String.class,
					Integer.class };

			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		table.getColumnModel().getColumn(0).setResizable(true);
		table.getColumnModel().getColumn(0).setPreferredWidth(115);
		table.getColumnModel().getColumn(1).setResizable(true);
		table.getColumnModel().getColumn(2).setResizable(true);
		table.getColumnModel().getColumn(3).setResizable(true);
		table.getColumnModel().getColumn(4).setResizable(true);
		table.getColumnModel().getColumn(5).setResizable(true);
		table.getColumnModel().getColumn(6).setMinWidth(0);
		table.getColumnModel().getColumn(6).setMaxWidth(0);
		table.getColumnModel().getColumn(6).setWidth(0);
	}
}
