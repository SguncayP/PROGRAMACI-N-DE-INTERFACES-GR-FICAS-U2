package controlador;

import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import vista.ventana;

/**
 * Controlador que gestiona la lógica de negocio, eventos de interfaz
 * y persistencia de datos para Palma Nexus Solutions.
 */
public class logica_ventana {

	private final ventana vista;

	public logica_ventana(ventana vista) {
		this.vista = vista;
		this.inicializarEventos();
		this.actualizarEstadisticas();
	}

	private void inicializarEventos() {
		// --- Gestión de botones ---
		this.vista.btn_add.addActionListener(e -> agregarContacto());
		this.vista.btn_modificar.addActionListener(e -> modificarContacto());
		this.vista.btn_eliminar.addActionListener(e -> eliminarContacto());
		this.vista.btn_exportar.addActionListener(e -> exportarCSV());

		// --- Atajo de teclado Ctrl + S para guardar ---
		KeyAdapter atajoGuardar = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
					agregarContacto();
				}
			}
		};
		vista.txt_nombres.addKeyListener(atajoGuardar);
		vista.txt_telefono.addKeyListener(atajoGuardar);
		vista.txt_email.addKeyListener(atajoGuardar);

		// --- Selección de tabla para cargar datos al formulario (Modificar) ---
		this.vista.tbl_contactos.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int f = vista.tbl_contactos.getSelectedRow();
				if (f >= 0) {
					vista.txt_nombres.setText(vista.modeloTabla.getValueAt(f, 0).toString());
					vista.txt_telefono.setText(vista.modeloTabla.getValueAt(f, 1).toString());
					vista.txt_email.setText(vista.modeloTabla.getValueAt(f, 2).toString());
					vista.cmb_categoria.setSelectedItem(vista.modeloTabla.getValueAt(f, 3).toString());
					String si = vista.bundle.getString("val_si");
					vista.chb_favorito.setSelected(vista.modeloTabla.getValueAt(f, 4).toString().equals(si));
				}
			}
		});

		// --- Eventos de Cambio de Idioma ---
		this.vista.btn_es.addActionListener(e -> cambiarIdioma("es"));
		this.vista.btn_en.addActionListener(e -> cambiarIdioma("en"));
		this.vista.btn_zh.addActionListener(e -> cambiarIdioma("fr")); // Configurado para Francés
	}

	private void agregarContacto() {
		if (validar()) {
			Object[] fila = {
					vista.txt_nombres.getText(),
					vista.txt_telefono.getText(),
					vista.txt_email.getText(),
					vista.cmb_categoria.getSelectedItem(),
					vista.chb_favorito.isSelected() ? vista.bundle.getString("val_si") : vista.bundle.getString("val_no")
			};
			vista.modeloTabla.addRow(fila);
			actualizarEstadisticas();
			limpiar();
			JOptionPane.showMessageDialog(vista, vista.bundle.getString("msg_guardado"));
		}
	}

	private void modificarContacto() {
		int f = vista.tbl_contactos.getSelectedRow();
		if (f >= 0 && validar()) {
			vista.modeloTabla.setValueAt(vista.txt_nombres.getText(), f, 0);
			vista.modeloTabla.setValueAt(vista.txt_telefono.getText(), f, 1);
			vista.modeloTabla.setValueAt(vista.txt_email.getText(), f, 2);
			vista.modeloTabla.setValueAt(vista.cmb_categoria.getSelectedItem(), f, 3);
			String fav = vista.chb_favorito.isSelected() ? vista.bundle.getString("val_si") : vista.bundle.getString("val_no");
			vista.modeloTabla.setValueAt(fav, f, 4);

			actualizarEstadisticas();
			limpiar();
			JOptionPane.showMessageDialog(vista, vista.bundle.getString("msg_modificado"));
		}
	}

	private void eliminarContacto() {
		int f = vista.tbl_contactos.getSelectedRow();
		if (f >= 0) {
			int confirm = JOptionPane.showConfirmDialog(vista, vista.bundle.getString("msg_confirm_eliminar"));
			if (confirm == JOptionPane.YES_OPTION) {
				vista.modeloTabla.removeRow(f);
				actualizarEstadisticas();
				limpiar();
			}
		}
	}

	private void exportarCSV() {
		JFileChooser selector = new JFileChooser();
		if (selector.showSaveDialog(vista) == JFileChooser.APPROVE_OPTION) {
			new Thread(() -> {
				try {
					File archivo = selector.getSelectedFile();
					if (!archivo.getName().toLowerCase().endsWith(".csv")) {
						archivo = new File(archivo.getAbsolutePath() + ".csv");
					}

					// Escritura con UTF-8 para soporte de tildes y caracteres especiales
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivo), StandardCharsets.UTF_8));

					int filas = vista.modeloTabla.getRowCount();
					for (int i = 0; i < filas; i++) {
						StringBuilder sb = new StringBuilder();
						for (int j = 0; j < vista.modeloTabla.getColumnCount(); j++) {
							sb.append(vista.modeloTabla.getValueAt(i, j));
							if (j < vista.modeloTabla.getColumnCount() - 1) sb.append(",");
						}
						bw.write(sb.toString());
						bw.newLine();

						// Actualización de la barra de progreso
						int progreso = (int) (((double) (i + 1) / filas) * 100);
						vista.progressBar.setValue(progreso);
					}
					bw.close();

					JOptionPane.showMessageDialog(vista, vista.bundle.getString("msg_exportar_exito"));
					vista.progressBar.setValue(0);

				} catch (IOException ex) {
					JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
				}
			}).start();
		}
	}

	private void cambiarIdioma(String lang) {
		// Preservación de datos antes de reiniciar la vista
		DefaultTableModel temp = vista.modeloTabla;
		vista.dispose();

		ventana nv = new ventana(Locale.forLanguageTag(lang));
		logica_ventana nc = new logica_ventana(nv);

		// Transferencia de filas a la nueva instancia
		for (int i = 0; i < temp.getRowCount(); i++) {
			Object[] f = new Object[temp.getColumnCount()];
			for (int j = 0; j < temp.getColumnCount(); j++) f[j] = temp.getValueAt(i, j);
			nv.modeloTabla.addRow(f);
		}

		nc.actualizarEstadisticas();
		nv.setVisible(true);
	}

	public void actualizarEstadisticas() {
		int t = vista.modeloTabla.getRowCount();
		int favs = 0;
		String si = vista.bundle.getString("val_si");

		for (int i = 0; i < t; i++) {
			if (vista.modeloTabla.getValueAt(i, 4).toString().equals(si)) favs++;
		}

		vista.lbl_totalContactos.setText(String.valueOf(t));
		vista.lbl_totalFavoritos.setText(String.valueOf(favs));
	}

	private boolean validar() {
		if (vista.txt_nombres.getText().trim().isEmpty() || vista.txt_telefono.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(vista, vista.bundle.getString("msg_error_campos"));
			return false;
		}
		return true;
	}

	private void limpiar() {
		vista.txt_nombres.setText("");
		vista.txt_telefono.setText("");
		vista.txt_email.setText("");
		vista.chb_favorito.setSelected(false);
		vista.tbl_contactos.clearSelection();
	}
}