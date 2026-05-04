package vista;

import java.awt.*;
import java.awt.event.*;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class ventana extends JFrame {
	public JTabbedPane tabbedPane;
	public JPanel pnl_gestion, pnl_estadisticas;
	public JTextField txt_nombres, txt_telefono, txt_email, txt_buscar;
	public JComboBox<String> cmb_categoria;
	public JCheckBox chb_favorito;
	public JButton btn_add, btn_modificar, btn_eliminar, btn_exportar, btn_es, btn_en, btn_zh;
	public JTable tbl_contactos;
	public DefaultTableModel modeloTabla;
	public JProgressBar progressBar;
	public JLabel lbl_totalContactos, lbl_totalFavoritos;
	public ResourceBundle bundle;

	private final Color CLR_ACCENT = new Color(34, 197, 94), CLR_INFO = new Color(59, 130, 246), CLR_BG = new Color(244, 244, 245);

	public ventana(Locale locale) {
		// Control personalizado para evitar símbolos extraños en Español/Francés
		this.bundle = ResourceBundle.getBundle("idiomas.mensajes", locale, new ResourceBundle.Control() {
			@Override
			public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IOException {
				String resourceName = toResourceName(toBundleName(baseName, locale), "properties");
				try (InputStream is = loader.getResourceAsStream(resourceName)) {
					if (is != null) return new java.util.PropertyResourceBundle(new InputStreamReader(is, StandardCharsets.UTF_8));
				}
				try { return super.newBundle(baseName, locale, format, loader, reload); }
				catch (Exception e) { return null; }
			}
		});

		configurarVentana();
		inicializarComponentes();
		construirPestañaGestion();
		construirPestañaEstadisticas();
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
	}

	private void configurarVentana() {
		setTitle(bundle.getString("titulo"));
		setSize(1150, 850);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
	}

	private void inicializarComponentes() {
		tabbedPane = new JTabbedPane();
		tabbedPane.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		modeloTabla = new DefaultTableModel(new String[]{bundle.getString("col_nombre"), bundle.getString("col_telefono"), bundle.getString("col_email"), bundle.getString("col_categoria"), bundle.getString("col_favorito")}, 0);
		tbl_contactos = new JTable(modeloTabla);
		tbl_contactos.setRowHeight(40);
		progressBar = new JProgressBar(0, 100);
		progressBar.setForeground(CLR_ACCENT);
		lbl_totalContactos = new JLabel("0"); lbl_totalFavoritos = new JLabel("0");
		btn_es = new JButton("ES"); btn_en = new JButton("EN"); btn_zh = new JButton("FR");
	}

	private void construirPestañaGestion() {
		pnl_gestion = new JPanel(new BorderLayout(25, 25));
		pnl_gestion.setBackground(CLR_BG);
		pnl_gestion.setBorder(new EmptyBorder(25, 25, 25, 25));

		JPanel formCard = new JPanel(null);
		formCard.setPreferredSize(new Dimension(350, 0));
		formCard.setBackground(Color.WHITE);
		formCard.setBorder(new LineBorder(new Color(228, 228, 231), 1, true));

		// Selector Idioma
		btn_es.setBounds(30, 30, 55, 30); btn_en.setBounds(95, 30, 55, 30); btn_zh.setBounds(160, 30, 55, 30);
		formCard.add(btn_es); formCard.add(btn_en); formCard.add(btn_zh);

		agregarInput(formCard, bundle.getString("lbl_nombres"), txt_nombres = new JTextField(), 80);
		agregarInput(formCard, bundle.getString("lbl_telefono"), txt_telefono = new JTextField(), 160);
		agregarInput(formCard, bundle.getString("lbl_email"), txt_email = new JTextField(), 240);

		cmb_categoria = new JComboBox<>(new String[]{"Trabajo", "Familia", "Amigos", "Otros"});
		cmb_categoria.setBounds(30, 330, 290, 40); formCard.add(cmb_categoria);

		chb_favorito = new JCheckBox(bundle.getString("lbl_favorito"));
		chb_favorito.setBounds(30, 380, 200, 30); chb_favorito.setBackground(Color.WHITE); formCard.add(chb_favorito);

		btn_add = crearBoton(bundle.getString("btn_agregar"), CLR_ACCENT); btn_add.setBounds(30, 430, 290, 45); formCard.add(btn_add);
		btn_modificar = crearBoton(bundle.getString("btn_modificar"), new Color(63, 63, 70)); btn_modificar.setBounds(30, 490, 290, 45); formCard.add(btn_modificar);
		btn_eliminar = crearBoton(bundle.getString("btn_eliminar"), new Color(244, 63, 94)); btn_eliminar.setBounds(30, 550, 290, 45); formCard.add(btn_eliminar);

		JPanel pnlDer = new JPanel(new BorderLayout(0, 15)); pnlDer.setOpaque(false);
		txt_buscar = new JTextField(); txt_buscar.setPreferredSize(new Dimension(0, 45));
		setPlaceholder(txt_buscar, bundle.getString("lbl_buscar"));
		btn_exportar = crearBoton(bundle.getString("btn_exportar"), CLR_INFO); btn_exportar.setPreferredSize(new Dimension(140, 45));

		JPanel pnlTop = new JPanel(new BorderLayout(10, 0)); pnlTop.setOpaque(false);
		pnlTop.add(txt_buscar, BorderLayout.CENTER); pnlTop.add(btn_exportar, BorderLayout.EAST);
		pnlDer.add(pnlTop, BorderLayout.NORTH); pnlDer.add(new JScrollPane(tbl_contactos), BorderLayout.CENTER); pnlDer.add(progressBar, BorderLayout.SOUTH);

		pnl_gestion.add(formCard, BorderLayout.WEST); pnl_gestion.add(pnlDer, BorderLayout.CENTER);
		tabbedPane.addTab(bundle.getString("pestana_lista"), pnl_gestion);
	}

	private void construirPestañaEstadisticas() {
		pnl_estadisticas = new JPanel(new GridBagLayout()); pnl_estadisticas.setBackground(CLR_BG);
		pnl_estadisticas.add(crearTarjetaKPI(bundle.getString("lbl_total_contactos"), lbl_totalContactos, CLR_INFO, "👤"));
		pnl_estadisticas.add(crearTarjetaKPI(bundle.getString("lbl_total_favoritos"), lbl_totalFavoritos, CLR_ACCENT, "⭐"));
		tabbedPane.addTab(bundle.getString("pestana_estadisticas"), pnl_estadisticas);
	}

	private JPanel crearTarjetaKPI(String t, JLabel v, Color c, String i) {
		JPanel p = new JPanel(new BorderLayout()); p.setPreferredSize(new Dimension(300, 200)); p.setBackground(Color.WHITE);
		p.setBorder(new LineBorder(new Color(228, 228, 231), 1, true));
		JLabel icon = new JLabel(i); icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
		v.setFont(new Font("Segoe UI Bold", Font.PLAIN, 60)); v.setForeground(c); v.setHorizontalAlignment(0);
		p.add(icon, BorderLayout.WEST); p.add(new JLabel(t, 0), BorderLayout.NORTH); p.add(v, BorderLayout.CENTER);
		return p;
	}

	private void setPlaceholder(JTextField f, String p) {
		f.setText(p); f.setForeground(Color.GRAY);
		f.addFocusListener(new FocusAdapter() {
			@Override public void focusGained(FocusEvent e) { if(f.getText().equals(p)){f.setText(""); f.setForeground(Color.BLACK);}}
			@Override public void focusLost(FocusEvent e) { if(f.getText().isEmpty()){f.setText(p); f.setForeground(Color.GRAY);}}
		});
	}

	private void agregarInput(JPanel p, String t, JTextField f, int y) {
		JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12)); l.setBounds(30, y, 200, 20);
		f.setBounds(30, y + 25, 290, 35); f.setBorder(new CompoundBorder(new LineBorder(new Color(228, 228, 231)), new EmptyBorder(0, 10, 0, 10)));
		p.add(l); p.add(f);
	}

	private JButton crearBoton(String t, Color bg) {
		JButton b = new JButton(t); b.setBackground(bg); b.setForeground(Color.WHITE); b.setFocusPainted(false); b.setBorder(null); b.setCursor(new Cursor(12));
		return b;
	}
}