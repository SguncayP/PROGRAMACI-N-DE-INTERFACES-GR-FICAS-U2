package modelo;

/**
 * Clase que representa la entidad Persona (Contacto) en el sistema.
 * Contiene los atributos básicos y métodos de formateo para persistencia y visualización.
 */
public class persona {

	// Atributos privados para cumplir con el encapsulamiento
	private String nombre, telefono, email, categoria;
	private boolean favorito;

	// Constructor por defecto
	public persona() {
		super();
		this.nombre = "";
		this.telefono = "";
		this.email = "";
		this.categoria = "";
		this.favorito = false;
	}

	// Constructor con parámetros para inicialización completa
	public persona(String nombre, String telefono, String email, String categoria, boolean favorito) {
		super();
		this.nombre = nombre;
		this.telefono = telefono;
		this.email = email;
		this.categoria = categoria;
		this.favorito = favorito;
	}

	// --- Métodos Getters y Setters ---

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public boolean isFavorito() {
		return favorito;
	}

	public void setFavorito(boolean favorito) {
		this.favorito = favorito;
	}

	/**
	 * Devuelve los datos del contacto en formato de arreglo de objetos.
	 * Ideal para insertar directamente como una fila en un JTable.
	 */
	public Object[] toArray() {
		return new Object[]{
				nombre,
				telefono,
				email,
				categoria,
				favorito ? "Sí" : "No" // Conversión visual del booleano
		};
	}

	/**
	 * Formato delimitado por punto y coma (;) para persistencia en archivos CSV.
	 */
	public String datosContacto() {
		return String.format("%s;%s;%s;%s;%s", nombre, telefono, email, categoria, favorito);
	}

	/**
	 * Formato de cadena con espaciado fijo para visualización en JList (Legacy).
	 */
	public String formatoLista() {
		return String.format("%-40s%-40s%-40s%-40s", nombre, telefono, email, categoria);
	}
}