package controlador;

import java.awt.event.*;
import java.io.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import vista.ventana;
import modelo.*;

/**
 * Controlador principal: Gestiona la lógica de negocio, eventos y persistencia.
 * Implementa el patrón MVC para desacoplar la vista de los datos.
 */
public class logica_ventana implements ActionListener {
	private ventana vista;
	private List<persona> contactos;
	private TableRowSorter<DefaultTableModel> sorter;

	public logica_ventana(ventana vista) {
		this.vista = vista;

		configurarTabla();
		cargarDatosConProgreso();
		asignarEventos();

		// Estado inicial de la interfaz
		this.vista.btn_modificar.setEnabled(false);
	}

	private void configurarTabla() {
		// Implementación de JTable con soporte para ordenamiento y filtrado
		sorter = new TableRowSorter<>(vista.modeloTabla);
		vista.tbl_contactos.setRowSorter(sorter);
	}

	private void asignarEventos() {
		// Registro de ActionListeners para botones
		this.vista.btn_add.addActionListener(this);
		this.vista.btn_modificar.addActionListener(this);
		this.vista.btn_eliminar.addActionListener(this);
		this.vista.btn_exportar.addActionListener(this);

		// --- EVENTOS DE TECLADO (Atajo Global Ctrl + S) ---
		JComponent rootPane = vista.getRootPane();
		KeyStroke ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
		rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlS, "guardarAction");
		rootPane.getActionMap().put("guardarAction", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (vista.btn_add.isEnabled()) registrarContacto();
			}
		});

		// --- SELECCIÓN DE FILA (Sincronización segura) ---
		this.vista.tbl_contactos.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				cargarContactoSeleccionado();
			}
		});

		// --- BÚSQUEDA Y FILTRADO (Previene errores de índice) ---
		this.vista.txt_buscar.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String texto = vista.txt_buscar.getText().trim();
				// Limpiamos selección antes de filtrar para evitar desfases del sorter
				vista.tbl_contactos.clearSelection();

				if (texto.isEmpty()) {
					sorter.setRowFilter(null);
				} else {
					// Filtrado por columna 0 (Nombres)
					sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 0));
				}
			}
		});

		// --- EVENTO DE MOUSE (Clic derecho / Menú Contextual) ---
		this.vista.tbl_contactos.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					int r = vista.tbl_contactos.rowAtPoint(e.getPoint());
					if (r >= 0 && r < vista.tbl_contactos.getRowCount()) {
						vista.tbl_contactos.setRowSelectionInterval(r, r);
					}
					JPopupMenu menu = new JPopupMenu();
					JMenuItem itemEliminar = new JMenuItem("Eliminar contacto");
					itemEliminar.addActionListener(al -> eliminarContacto());
					menu.add(itemEliminar);
					menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}

	private void cargarContactoSeleccionado() {
		int filaVista = vista.tbl_contactos.getSelectedRow();

		// Validación crítica: Comprobar límites de la vista para evitar advertencias del sorter
		if (filaVista != -1 && filaVista < vista.tbl_contactos.getRowCount()) {
			try {
				int filaModelo = vista.tbl_contactos.convertRowIndexToModel(filaVista);
				persona p = contactos.get(filaModelo);

				vista.txt_nombres.setText(p.getNombre());
				vista.txt_telefono.setText(p.getTelefono());
				vista.txt_email.setText(p.getEmail());
				vista.cmb_categoria.setSelectedItem(p.getCategoria());
				vista.chb_favorito.setSelected(p.isFavorito());

				vista.btn_add.setEnabled(false);
				vista.btn_modificar.setEnabled(true);
			} catch (Exception ex) {
				// Sincronización silenciosa durante cambios rápidos de filtro
			}
		}
	}

	private void registrarContacto() {
		String nom = vista.txt_nombres.getText().trim();
		String tel = vista.txt_telefono.getText().trim();
		String mail = vista.txt_email.getText().trim();
		String cat = vista.cmb_categoria.getSelectedItem().toString();
		boolean fav = vista.chb_favorito.isSelected();

		if (!nom.isEmpty() && !tel.isEmpty() && !cat.equals("Elija una Categoria")) {
			persona nueva = new persona(nom, tel, mail, cat, fav);
			if (new personaDAO(nueva).escribirArchivo()) {
				JOptionPane.showMessageDialog(vista, "¡Contacto Guardado!");
				limpiarFormulario();
				cargarDatosConProgreso();
			}
		} else {
			JOptionPane.showMessageDialog(vista, "Llene los campos Nombre, Teléfono y Categoría.");
		}
	}

	private void modificarContacto() {
		int filaVista = vista.tbl_contactos.getSelectedRow();
		if (filaVista != -1 && filaVista < vista.tbl_contactos.getRowCount()) {
			int filaModelo = vista.tbl_contactos.convertRowIndexToModel(filaVista);
			persona p = contactos.get(filaModelo);

			p.setNombre(vista.txt_nombres.getText().trim());
			p.setTelefono(vista.txt_telefono.getText().trim());
			p.setEmail(vista.txt_email.getText().trim());
			p.setCategoria(vista.cmb_categoria.getSelectedItem().toString());
			p.setFavorito(vista.chb_favorito.isSelected());

			try {
				new personaDAO(new persona()).actualizarContactos(contactos);

				// Actualización directa del modelo para mantener la integridad visual
				vista.modeloTabla.setValueAt(p.getNombre(), filaModelo, 0);
				vista.modeloTabla.setValueAt(p.getTelefono(), filaModelo, 1);
				vista.modeloTabla.setValueAt(p.getEmail(), filaModelo, 2);
				vista.modeloTabla.setValueAt(p.getCategoria(), filaModelo, 3);
				vista.modeloTabla.setValueAt(p.isFavorito() ? "Sí" : "No", filaModelo, 4);

				JOptionPane.showMessageDialog(vista, "Datos actualizados correctamente.");
				limpiarFormulario();
				actualizarEstadisticas();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(vista, "Error al guardar cambios.");
			}
		}
	}

	private void eliminarContacto() {
		int filaVista = vista.tbl_contactos.getSelectedRow();
		if (filaVista != -1) {
			int confirm = JOptionPane.showConfirmDialog(vista, "¿Desea eliminar este registro?");
			if (confirm == JOptionPane.YES_OPTION) {
				int filaModelo = vista.tbl_contactos.convertRowIndexToModel(filaVista);
				contactos.remove(filaModelo);
				try {
					new personaDAO(new persona()).actualizarContactos(contactos);
					limpiarFormulario();
					cargarDatosConProgreso();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void exportarCSV() {
		if (contactos == null || contactos.isEmpty()) return;

		SwingWorker<Void, Integer> worker = new SwingWorker<>() {
			@Override
			protected Void doInBackground() throws Exception {
				try (BufferedWriter bw = new BufferedWriter(new FileWriter("contactos_exportados.csv"))) {
					bw.write("NOMBRE;TELEFONO;EMAIL;CATEGORIA;FAVORITO");
					bw.newLine();
					for (int i = 0; i < contactos.size(); i++) {
						bw.write(contactos.get(i).datosContacto());
						bw.newLine();
						// Indicador de JProgressBar durante exportación
						int progreso = (int) (((double) (i + 1) / contactos.size()) * 100);
						publish(progreso);
						Thread.sleep(15);
					}
				}
				return null;
			}
			@Override
			protected void process(List<Integer> chunks) {
				vista.progressBar.setValue(chunks.get(chunks.size() - 1));
			}
			@Override
			protected void done() {
				JOptionPane.showMessageDialog(vista, "Exportación finalizada (contactos_exportados.csv)");
				Timer t = new Timer(1000, e -> vista.progressBar.setValue(0));
				t.setRepeats(false);
				t.start();
			}
		};
		worker.execute();
	}

	private void cargarDatosConProgreso() {
		vista.progressBar.setValue(0);
		SwingWorker<Void, Integer> worker = new SwingWorker<>() {
			@Override
			protected Void doInBackground() throws Exception {
				personaDAO dao = new personaDAO(new persona());
				contactos = dao.leerArchivo();
				vista.modeloTabla.setRowCount(0);

				for (int i = 0; i < contactos.size(); i++) {
					vista.modeloTabla.addRow(contactos.get(i).toArray());
					// Barra de progreso (JProgressBar) para carga de contactos
					int porc = (int) (((double) (i + 1) / (contactos.isEmpty() ? 1 : contactos.size())) * 100);
					publish(porc);
					Thread.sleep(10);
				}
				return null;
			}
			@Override
			protected void process(List<Integer> chunks) {
				vista.progressBar.setValue(chunks.get(chunks.size() - 1));
			}
			@Override
			protected void done() {
				actualizarEstadisticas();
				vista.progressBar.setValue(0);
			}
		};
		worker.execute();
	}

	private void actualizarEstadisticas() {
		int total = contactos.size();
		long favs = contactos.stream().filter(persona::isFavorito).count();
		// Organización en pestañas de contactos y estadísticas
		vista.lbl_totalContactos.setText("Total de contactos: " + total);
		vista.lbl_totalFavoritos.setText("Contactos Favoritos: " + favs);
	}

	private void limpiarFormulario() {
		vista.txt_nombres.setText("");
		vista.txt_telefono.setText("");
		vista.txt_email.setText("");
		vista.chb_favorito.setSelected(false);
		vista.cmb_categoria.setSelectedIndex(0);

		// Limpiamos selección de tabla para resetear estados del listener
		vista.tbl_contactos.clearSelection();

		vista.btn_add.setEnabled(true);
		vista.btn_modificar.setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == vista.btn_add) registrarContacto();
		if (e.getSource() == vista.btn_modificar) modificarContacto();
		if (e.getSource() == vista.btn_eliminar) eliminarContacto();
		if (e.getSource() == vista.btn_exportar) exportarCSV();
	}
}