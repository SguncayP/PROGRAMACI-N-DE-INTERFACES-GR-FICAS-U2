package modelo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class personaDAO {

	private File archivo;
	private persona persona;

	public personaDAO(persona persona) {
		this.persona = persona;
		// Se recomienda verificar que esta carpeta exista en su disco C:
		archivo = new File("c:/gestionContactos");
		prepararArchivo();
	}

	private void prepararArchivo() {
		if (!archivo.exists()) {
			archivo.mkdir();
		}

		archivo = new File(archivo.getAbsolutePath(), "datosContactos.csv");

		if (!archivo.exists()) {
			try {
				archivo.createNewFile();
				// Escribimos el encabezado inicial
				String encabezado = "NOMBRE;TELEFONO;EMAIL;CATEGORIA;FAVORITO";
				escribir(encabezado);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void escribir(String texto) {
		try (FileWriter fw = new FileWriter(archivo.getAbsolutePath(), true);
			 BufferedWriter bw = new BufferedWriter(fw)) {
			bw.write(texto);
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean escribirArchivo() {
		escribir(persona.datosContacto());
		return true;
	}

	/**
	 * Lee todos los contactos del archivo CSV.
	 * Salta la primera línea (encabezados) para evitar filas vacías en la tabla.
	 */
	public List<persona> leerArchivo() throws IOException {
		List<persona> personas = new ArrayList<>();

		if (!archivo.exists()) return personas;

		try (BufferedReader br = new BufferedReader(new FileReader(archivo.getAbsolutePath()))) {
			String linea;
			boolean esPrimeraLinea = true;

			while ((linea = br.readLine()) != null) {
				// 1. Saltamos la fila de títulos (Encabezado)
				if (esPrimeraLinea) {
					esPrimeraLinea = false;
					continue;
				}

				// 2. Procesamos solo líneas que no estén vacías
				if (!linea.trim().isEmpty()) {
					String[] datos = linea.split(";");

					// 3. Validamos que la línea tenga todas las columnas necesarias
					if (datos.length >= 5) {
						persona p = new persona();
						p.setNombre(datos[0]);
						p.setTelefono(datos[1]);
						p.setEmail(datos[2]);
						p.setCategoria(datos[3]);
						p.setFavorito(Boolean.parseBoolean(datos[4]));
						personas.add(p);
					}
				}
			}
		}
		return personas;
	}

	/**
	 * Sobreescribe el archivo con la lista actualizada de contactos.
	 * Útil para las funciones de Eliminar y Modificar.
	 */
	public void actualizarContactos(List<persona> listaPersonas) throws IOException {
		// 1. Borramos o sobreescribimos el archivo creando uno nuevo con el encabezado
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo.getAbsolutePath(), false))) {
			bw.write("NOMBRE;TELEFONO;EMAIL;CATEGORIA;FAVORITO");
			bw.newLine();

			// 2. Escribimos cada persona de la lista actualizada
			for (persona p : listaPersonas) {
				bw.write(p.datosContacto());
				bw.newLine();
			}
		}
	}
}