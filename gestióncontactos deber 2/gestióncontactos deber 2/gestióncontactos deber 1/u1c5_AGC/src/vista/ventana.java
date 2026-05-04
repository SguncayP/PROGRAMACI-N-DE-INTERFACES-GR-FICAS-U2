package vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ventana extends JFrame {

	public JPanel contentPane;
	// Componentes de entrada
	public JTextField txt_nombres, txt_telefono, txt_email, txt_buscar;
	public JCheckBox chb_favorito;
	public JComboBox<String> cmb_categoria;

	// Botones
	public JButton btn_add, btn_modificar, btn_eliminar, btn_exportar;

	// Componentes de visualización avanzada
	public JTabbedPane tabbedPane;
	public JTable tbl_contactos;
	public DefaultTableModel modeloTabla;
	public JProgressBar progressBar;

	// Componentes de estadísticas
	public JLabel lbl_totalContactos, lbl_totalFavoritos;

	public ventana() {
		setTitle("GESTIÓN DE CONTACTOS - VERSIÓN PRO (MVC)");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setBounds(100, 100, 1040, 750);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		// 1. Implementación de JTabbedPane
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		// --- PESTAÑA 1: GESTIÓN DE CONTACTOS ---
		JPanel pnl_gestion = new JPanel();
		pnl_gestion.setLayout(null);
		tabbedPane.addTab("Lista de Contactos", null, pnl_gestion, "Ver y editar contactos");

		// Etiquetas y Campos de texto
		JLabel lbl_nombres = new JLabel("NOMBRES:");
		lbl_nombres.setFont(new Font("Tahoma", Font.BOLD, 13));
		lbl_nombres.setBounds(25, 25, 80, 25);
		pnl_gestion.add(lbl_nombres);

		txt_nombres = new JTextField();
		txt_nombres.setBounds(110, 25, 300, 25);
		pnl_gestion.add(txt_nombres);

		JLabel lbl_telefono = new JLabel("TELÉFONO:");
		lbl_telefono.setFont(new Font("Tahoma", Font.BOLD, 13));
		lbl_telefono.setBounds(25, 65, 80, 25);
		pnl_gestion.add(lbl_telefono);

		txt_telefono = new JTextField();
		txt_telefono.setBounds(110, 65, 300, 25);
		pnl_gestion.add(txt_telefono);

		JLabel lbl_email = new JLabel("EMAIL:");
		lbl_email.setFont(new Font("Tahoma", Font.BOLD, 13));
		lbl_email.setBounds(430, 25, 80, 25);
		pnl_gestion.add(lbl_email);

		txt_email = new JTextField();
		txt_email.setBounds(490, 25, 300, 25);
		pnl_gestion.add(txt_email);

		chb_favorito = new JCheckBox("CONTACTO FAVORITO");
		chb_favorito.setFont(new Font("Tahoma", Font.PLAIN, 13));
		chb_favorito.setBounds(430, 65, 180, 25);
		pnl_gestion.add(chb_favorito);

		cmb_categoria = new JComboBox<>();
		cmb_categoria.setModel(new DefaultComboBoxModel<>(new String[] {"Elija una Categoria", "Familia", "Amigos", "Trabajo"}));
		cmb_categoria.setBounds(620, 65, 170, 25);
		pnl_gestion.add(cmb_categoria);

		// Botones de Acción
		btn_add = new JButton("AGREGAR");
		btn_add.setBounds(820, 20, 150, 35);
		pnl_gestion.add(btn_add);

		btn_modificar = new JButton("MODIFICAR");
		btn_modificar.setBounds(820, 60, 150, 35);
		pnl_gestion.add(btn_modificar);

		btn_exportar = new JButton("EXPORTAR CSV");
		btn_exportar.setBackground(new Color(200, 255, 200));
		btn_exportar.setBounds(820, 105, 150, 35);
		pnl_gestion.add(btn_exportar);

		btn_eliminar = new JButton("ELIMINAR");
		btn_eliminar.setForeground(Color.BLACK); // CAMBIO: Texto en negro para mejor lectura
		btn_eliminar.setBackground(new Color(255, 102, 102)); // Un rojo más suave
		btn_eliminar.setBounds(820, 150, 150, 35);
		pnl_gestion.add(btn_eliminar);

		// 3. Implementación de JTable para visualizar contactos
		String[] columnas = {"Nombre", "Teléfono", "Email", "Categoría", "Favorito"};
		modeloTabla = new DefaultTableModel(null, columnas) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Evita edición directa en la tabla
			}
		};
		tbl_contactos = new JTable(modeloTabla);
		tbl_contactos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tbl_contactos.getTableHeader().setReorderingAllowed(false);

		JScrollPane scrollPane = new JScrollPane(tbl_contactos);
		scrollPane.setBounds(25, 200, 960, 350);
		pnl_gestion.add(scrollPane);

		// 3. Barra de progreso (JProgressBar)
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setBounds(25, 560, 960, 20);
		pnl_gestion.add(progressBar);

		JLabel lbl_buscar = new JLabel("BUSCAR POR NOMBRE:");
		lbl_buscar.setFont(new Font("Tahoma", Font.BOLD, 13));
		lbl_buscar.setBounds(25, 600, 160, 25);
		pnl_gestion.add(lbl_buscar);

		txt_buscar = new JTextField();
		txt_buscar.setBounds(185, 600, 800, 25);
		pnl_gestion.add(txt_buscar);

		// --- PESTAÑA 2: ESTADÍSTICAS ---
		JPanel pnl_estadisticas = new JPanel();
		pnl_estadisticas.setBackground(Color.WHITE);
		pnl_estadisticas.setLayout(null);
		tabbedPane.addTab("Estadísticas", null, pnl_estadisticas, "Resumen de contactos");

		lbl_totalContactos = new JLabel("Total de contactos: 0");
		lbl_totalContactos.setFont(new Font("Tahoma", Font.BOLD, 18));
		lbl_totalContactos.setBounds(50, 50, 400, 30);
		pnl_estadisticas.add(lbl_totalContactos);

		lbl_totalFavoritos = new JLabel("Contactos Favoritos: 0");
		lbl_totalFavoritos.setFont(new Font("Tahoma", Font.BOLD, 18));
		lbl_totalFavoritos.setForeground(new Color(0, 102, 204));
		lbl_totalFavoritos.setBounds(50, 100, 400, 30);
		pnl_estadisticas.add(lbl_totalFavoritos);

		// El controlador se instancia fuera para cumplir con el patrón MVC estricto
	}
}