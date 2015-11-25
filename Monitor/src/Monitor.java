import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JLabel;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

@SuppressWarnings("serial")
public class Monitor extends JFrame implements Runnable {

	private JPanel contentPane;

	private JDatePickerImpl dateDesde;
	private JDatePickerImpl dateHasta;

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
	private JLabel lblCrearEtiqueta;
	private JLabel lblEliminar;
	private JLabel lblAsignarDesasginar;
	private JLabel lblRenombrar;

	private String filtroNinio = "";
	private String filtroCategoria = "";
	private String filtroContexto = "";
	private String filtroContenido = "";
	private String filtroEtiqueta = "";
	private String filtroDesde = "";
	private String filtroHasta = "";

	JButton btnFiltrar;

	JLabel lblMensajes;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedLookAndFeelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Monitor monitor = new Monitor();
				monitor.run();

				Servidor servidor = new Servidor(monitor);
				servidor.run();

			}
		});
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
			actualizarCombos();
		}

	}

	private void actualizarCombos() {
		// guardar valores seleccionados
		Object cn = comboNinio.getSelectedItem();
		Object cc = comboCategoria.getSelectedItem();
		Object ccx = comboContexto.getSelectedItem();
		Object ccd = comboContenido.getSelectedItem();
		Object ce = comboEtiqueta.getSelectedItem();

		conector.connect();
		cargarCombo(comboContenido, conector.getContenidos());
		cargarCombo(comboCategoria, conector.getCategorias());
		cargarCombo(comboContexto, conector.getContextos());
		cargarCombo(comboNinio, conector.getNinios());
		conector.close();

		actualizarCombosEtiqueta();

		// reponer valores
		if (cn != null) {
			comboNinio.setSelectedItem(cn);
			comboCategoria.setSelectedItem(cc);
			comboContexto.setSelectedItem(ccx);
			comboContenido.setSelectedItem(ccd);
			comboEtiqueta.setSelectedItem(ce);
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
			Object[] row = { format.format(n.getFecha_envio()), n.getContenido(), n.getContexto(), n.getCategoria(),
					n.getNinio(), etiquetas.toString(), n.getId() };
			((DefaultTableModel) table.getModel()).addRow(row);
		}
	}

	private class FiltradorTabla implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			clearTablaNotificaciones();

			// Cargar variables filtros para uso posterior
			filtroNinio = comboNinio.getSelectedItem().toString();
			filtroContenido = comboContenido.getSelectedItem().toString();
			filtroContexto = comboContexto.getSelectedItem().toString();
			filtroCategoria = comboCategoria.getSelectedItem().toString();
			filtroEtiqueta = comboEtiqueta.getSelectedItem().toString();
			filtroDesde = dateDesde.getJFormattedTextField().getText();
			filtroHasta = dateHasta.getJFormattedTextField().getText();

			// Obtener notificaciones filtradas
			notificaciones = notificacionesFiltradas();

			cargarTablaNotificaciones(notificaciones);
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
			actualizarNotificaciones();

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
			actualizarNotificaciones();

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
			actualizarNotificaciones();

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
			actualizarNotificaciones();
		}

	}

	private class MostradorTodo implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// resetear variables filtro
			filtroNinio = "";
			filtroCategoria = "";
			filtroContexto = "";
			filtroContenido = "";
			filtroEtiqueta = "";
			filtroDesde = "";
			filtroHasta = "";

			/*
			// resetear vista filtros
			comboNinio.setSelectedItem("");
			comboCategoria.setSelectedItem("");
			comboContexto.setSelectedItem("");
			comboContenido.setSelectedItem("");
			comboEtiqueta.setSelectedItem("");
			dateDesde.getJDateInstantPanel().getModel().setValue(null);
			dateHasta.getJDateInstantPanel().getModel().setValue(null);
			*/

			actualizarNotificaciones();
		}

	}

	private List<Notificacion> notificacionesFiltradas() {
		conector.connect();
		List<Notificacion> ret = conector.getNotificacionesFiltradas(filtroNinio, filtroContenido, filtroContexto,
				filtroCategoria, filtroEtiqueta, filtroDesde, filtroHasta);
		conector.close();
		return ret;
	}

	private void actualizarNotificaciones() {
		clearTablaNotificaciones();
		notificaciones = notificacionesFiltradas();
		cargarTablaNotificaciones(notificaciones);
	}

	private class DateLabelFormatter extends AbstractFormatter {

		private String datePattern = "yyyy-MM-dd";
		private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

		@Override
		public Object stringToValue(String text) throws ParseException {
			return dateFormatter.parseObject(text);
		}

		@Override
		public String valueToString(Object value) throws ParseException {
			if (value != null) {
				Calendar cal = (Calendar) value;
				return dateFormatter.format(cal.getTime());
			}

			return "";
		}

	}

	public Monitor() {

		// cargar notificaciones de la BD
		notificaciones = notificacionesFiltradas();

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
		panelFiltros.setLayout(null);

		comboContenido = new JComboBox<String>();
		comboContenido.setBounds(69, 21, 106, 20);
		panelFiltros.add(comboContenido);

		comboContexto = new JComboBox<String>();
		comboContexto.setBounds(69, 52, 106, 20);
		panelFiltros.add(comboContexto);

		comboNinio = new JComboBox<String>();
		comboNinio.setBounds(69, 83, 106, 20);
		panelFiltros.add(comboNinio);

		comboCategoria = new JComboBox<String>();
		comboCategoria.setBounds(263, 21, 106, 20);
		panelFiltros.add(comboCategoria);

		Properties p = new Properties();
		p.put("text.today", "Hoy");
		p.put("text.month", "Mes");
		p.put("text.year", "Año");
		UtilDateModel model = new UtilDateModel();
		JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
		dateDesde = new JDatePickerImpl(datePanel, new DateLabelFormatter());
		dateDesde.setBounds(79, 114, 145, 25);
		panelFiltros.add(dateDesde);

		UtilDateModel model2 = new UtilDateModel();
		JDatePanelImpl datePanel2 = new JDatePanelImpl(model2, p);
		dateHasta = new JDatePickerImpl(datePanel2, new DateLabelFormatter());
		dateHasta.setBounds(78, 145, 146, 25);
		panelFiltros.add(dateHasta);

		comboEtiqueta = new JComboBox<String>();
		comboEtiqueta.setBounds(263, 52, 106, 20);
		panelFiltros.add(comboEtiqueta);

		btnFiltrar = new JButton("Filtrar");
		btnFiltrar.setBounds(283, 165, 86, 23);
		btnFiltrar.addActionListener(new FiltradorTabla());
		panelFiltros.add(btnFiltrar);

		JLabel lblContenido = new JLabel("Contenido:");
		lblContenido.setBounds(10, 24, 61, 14);
		panelFiltros.add(lblContenido);

		JLabel lblContexto = new JLabel("Contexto:");
		lblContexto.setBounds(10, 55, 61, 14);
		panelFiltros.add(lblContexto);

		JLabel lblNi = new JLabel("Ni\u00F1@:");
		lblNi.setBounds(10, 86, 46, 14);
		panelFiltros.add(lblNi);

		JLabel lblEtiqueta = new JLabel("Etiqueta:");
		lblEtiqueta.setBounds(207, 55, 46, 14);
		panelFiltros.add(lblEtiqueta);

		JLabel lblNewLabel = new JLabel("Categoria:");
		lblNewLabel.setBounds(202, 24, 51, 14);
		panelFiltros.add(lblNewLabel);

		JLabel lblFechaDesde = new JLabel("Fecha Desde:");
		lblFechaDesde.setBounds(10, 120, 86, 14);
		panelFiltros.add(lblFechaDesde);

		JLabel lblFechaHasta = new JLabel("Fecha Hasta:");
		lblFechaHasta.setBounds(10, 151, 82, 14);
		panelFiltros.add(lblFechaHasta);

		JPanel panelEtiquetas = new JPanel();
		panelEtiquetas
				.setBorder(new TitledBorder(null, "Etiquetas", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelEtiquetas.setBounds(404, 12, 397, 149);
		contentPane.add(panelEtiquetas);
		panelEtiquetas.setLayout(null);

		txtCrear = new JTextField();
		txtCrear.setBounds(73, 20, 99, 20);
		panelEtiquetas.add(txtCrear);
		txtCrear.setColumns(10);

		JButton btnCrear = new JButton("Crear");
		btnCrear.setBounds(182, 19, 86, 23);
		btnCrear.addActionListener(new CreadorEtiqueta());
		panelEtiquetas.add(btnCrear);

		comboEliminar = new JComboBox<String>();
		comboEliminar.setBounds(73, 51, 99, 20);
		panelEtiquetas.add(comboEliminar);

		JButton btnEliminar = new JButton("Eliminar");
		btnEliminar.setBounds(182, 50, 86, 23);
		btnEliminar.addActionListener(new EliminadorEtiqueta());
		panelEtiquetas.add(btnEliminar);

		comboAsignar = new JComboBox<String>();
		comboAsignar.setBounds(73, 82, 99, 20);
		panelEtiquetas.add(comboAsignar);

		JButton btnAsignar = new JButton("Asignar / Desasig.");
		btnAsignar.setBounds(182, 84, 119, 23);
		btnAsignar.addActionListener(new AsignadorEtiqueta());
		panelEtiquetas.add(btnAsignar);

		comboRenombrar = new JComboBox<String>();
		comboRenombrar.setBounds(73, 115, 99, 20);
		panelEtiquetas.add(comboRenombrar);

		txtRenombrar = new JTextField();
		txtRenombrar.setBounds(182, 115, 86, 20);
		panelEtiquetas.add(txtRenombrar);
		txtRenombrar.setColumns(10);

		JButton btnRenombrar = new JButton("Renombrar");
		btnRenombrar.setBounds(278, 114, 109, 23);
		btnRenombrar.addActionListener(new RenombradorEtiqueta());
		panelEtiquetas.add(btnRenombrar);

		lblCrearEtiqueta = new JLabel("Crear:");
		lblCrearEtiqueta.setBounds(10, 25, 46, 14);
		panelEtiquetas.add(lblCrearEtiqueta);

		lblEliminar = new JLabel("Eliminar:");
		lblEliminar.setBounds(10, 56, 46, 14);
		panelEtiquetas.add(lblEliminar);

		lblAsignarDesasginar = new JLabel("Des/Asignar:");
		lblAsignarDesasginar.setBounds(10, 87, 110, 14);
		panelEtiquetas.add(lblAsignarDesasginar);

		lblRenombrar = new JLabel("Renombrar:");
		lblRenombrar.setBounds(10, 118, 69, 14);
		panelEtiquetas.add(lblRenombrar);

		JScrollPane panelNotificaciones = new JScrollPane();
		panelNotificaciones.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panelNotificaciones.setBounds(13, 223, 788, 266);
		contentPane.add(panelNotificaciones);

		table = new JTable();
		table.setAutoCreateRowSorter(true);
		panelNotificaciones.setViewportView(table);
		table.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Fecha/Hora env\u00EDo", "Contenido",
				"Contexto", "Categor\u00EDa", "Ni\u00F1@", "Etiquetas", "Id" }) {
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { String.class, String.class, String.class, String.class, String.class,
					String.class, Integer.class };

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});

		lblMensajes = new JLabel();
		lblMensajes.setBounds(402, 172, 271, 14);
		contentPane.add(lblMensajes);

		JButton btnNewButton = new JButton("Mostrar todo");
		btnNewButton.setBounds(681, 188, 106, 23);
		btnNewButton.addActionListener(new MostradorTodo());
		contentPane.add(btnNewButton);

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

	@Override
	public void run() {
		this.setVisible(true);
	}

	public void nuevasNotificaciones() {
		lblMensajes.setText("NUEVAS Notificaciones");

		actualizarNotificaciones();
		actualizarCombos();

		// Temporizador para desvanecer el mensaje
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				lblMensajes.setText("");
			}
		};

		timer.schedule(task, 20000);

	}
}
