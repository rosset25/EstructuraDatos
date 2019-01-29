package practica4;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;


/**
 * <p>Clase genérica que implementa la interface <code>Set<T></code> usando una tabla 
 * de dispersión con resolución de colisiones mediante direccionamiento abierto. 
 * 
 * <p>No admite elementos repetidos.
 * 
 * <p>Permite incluir elementos con valor <code>null</code>.
 * 
 * <p>Implementa iteradores sín ningún tipo control de las modificaciones concurrente.
 *
 * @param <T> Clase de los elementos contenidos en el conjunto.
 */
public class EDHashSet<T> implements Set<T> {
	private T[] table;				// Tabla de dispersión
	private boolean[] used;			// Elementos usados de la tabla
	private int size;				// Número elementos contenidos en el conjunto
	private int rehashThreshold;		// Umbral para realizar una redispersión.
	private int dirty;				// Cantidad de casillas de la tabla usadas
	private boolean containsNull;	// Indicador de que null está en el conjunto. 

	// Capacidades y umbrales por defecto
	private static int DEFAULT_CAPACITY = 10;
	private static int DEFAULT_THRESHOLD = 7;

	
	/** Calcula el código de dispersión ajusntado al tamaño de la tablas
	 * 
	 * <p> El valor devuelto estará entre <code>0</code> y <code>table.length</code>.
	 * 
	 * @param item 	Valor a dispersar.
	 * @return		Código de dispersión
	 */
	private int hash(T item) {
		return (item.hashCode() & Integer.MAX_VALUE) % table.length;
	}

	/**
	 * Realiza la redispersión de todos los elementos de table.
	 * 
	 * <p>Para ello doblará el tamaño de <code>table, used</code> y del umbral 
	 * de dispersión y resispersará todos los elementos contenidos en el conjnuto.
	 *  La condición para redispersión es <code>dirty >= rehashThreshold</code> 
	 */
	private void rehash() {
		
		T tableAux[] = table;
		table = (T[]) new Object[tableAux.length*2];
		rehashThreshold *= 2; //También podría ser tableAux.length
		
		//Reseteo de las posiciones del vector usadas
		if(containsNull) {
			dirty = size-1; //containsNull no incrementa dirty, pero sí size
		}else{
			dirty = size;
		}
		
		used = new boolean[table.length];
		
		//Recorremos toda la tabla original
		for(int i=0; i < tableAux.length; i++) {
			T elem = tableAux[i];
			/* Se guarda la información de la posición i del vector y,
			 * en caso de ser null, pasamos a la siguiente posición del vector
			 */
			if(elem != null) {
				int code = hash(elem);
				while (used[code]) {
					code = (code+1) % table.length;
				}
				used[code] = true;
				table[code] = elem;		
			}
		}
	}

	/**
	 * Constructor por defecto del conjunto. 
	 * 
	 * <p> Construye un conjunto vacío. 
	 */
	public EDHashSet() {
		table = (T[]) new Object[DEFAULT_CAPACITY];
		used = new boolean[DEFAULT_CAPACITY];
		size = dirty = 0;
		rehashThreshold = DEFAULT_THRESHOLD;
		containsNull = false;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	public int dirty() {
		return dirty;
	}

	public T[] getTable() {
		return table;
	}

	public boolean[] getUsed() {
		return used;
	}

	public int getThrshold() {
		return rehashThreshold;
	}


	@Override
	public boolean contains(Object o) {
		
		if( o == null) { //Se comprueba si el elemento o es null
			if(containsNull) {
				return true;
			}
			return false;
		}
		
		int code = hash((T)o);
		
		while(used[code]) {
			if(o.equals(table[code])){
			//Otra posible forma de comparar y su explicación:
			//**** if(elem != null && table[code].equals((T) o)) {	
				/* Se debe revisar otra vez si hay algún elemento o no (null) 
				 * en la posición donde nos encontramos, además de comparar
				 * si el elemento encontrado (en caso de que no sea null) está
				 * contenido en nuestra tabla
				 */
				return true;
			}
			code = (code+1) % table.length;
		}
		
		return false;
	}

	
	@Override
	public boolean add(T e) {
		
		if(e == null) { //Se comprueba si el elemento e es null
			/* En caso de serlo, debemos comprobar si ya hay
			 * un null contenido en nuestra tabla
			 */
			if(! containsNull) { 
				/*dirty++; No se añade, ya que realmente null no 
				 * está dentro del vector
				 */
				containsNull = true;
				size++;
				return true;
			}
			return false; //En caso de que ya haya un elemento null
		}
		
		int code = hash(e);
		int pos = -1; /* Esta variable servirá para guardarnos una posición 
					   * en la que anteriormente haya habido un elemento pero 
					   * se haya borrado, ya que así el nuevo elemento no estará
					   * tan lejos de su posición original (la dada por el
					   * método hash())
					   */
		
		while (used[code]) { //Mientras (used[code] == true) usada/usada alguna vez
			
			T elemActual = table[code];
			
			//Comprobaciones:
			if(e.equals(elemActual)){ //if ( e == table[code]) { 
				return false;
				//Se comprueba si el elemento ya está
			}	
			if(elemActual == null) {
				pos = code;
				/* Nos guargamos la posición del lugar donde antes de ser borrado
				 * había un elemento, para así guardar el nuestro después de las 
				 * comprobaciones
				 */		
			}	
			code = (code+1) % table.length;
			/* Si se llega al final de la tabla, se vuelve al principio de la misma,
			 * para ello se obtiene el resto ej. 99%100=99 -> 0%100=0 -> 1%100=1
			 */
		}
		
		if(pos == -1) {
			pos = code;
			/* Se comprueba si había algún hueco libre antes de finalizar el bucle y, 
			 * en caso de no ahberlo, se le asigna la posición dada por la variable code
			 */
		}
		
		table[pos] = e;
		used[pos] = true;
		size++;
		dirty++;
		
		//Comprobación para ver si es necesario ampliar la tabla
		if(dirty >= rehashThreshold){
			rehash();
		}
		
		return true;
	}

	
	@Override
	public boolean remove(Object o) {
		
		if(o == null) {  //Se comprueba si el elemento e es null
			if(containsNull) {
				size--;
				containsNull = false;
				return true;
			}
			return false;
		}
		
		int code = hash((T) o); 
	
		while(used[code]) {
			
			//Lo mismo que en el contains(o), otra forma de hacerlo:
			//if( table[code] != null && table[code].equals((T)o)) { 
			if(o.equals(table[code])) {
				table[code] = null;
				size--;
				if(size == 1 && containsNull || size == 0){
					/* En caso de borrar un elemento y quedarnos con la lista
					 * vacía, se puede hacer un clear() y reutilizarla desde
					 * el principio
					 */
					clear();
				}
				return true;
			}	
			code = (code+1) % table.length;
			/* La posición siempre se incrementará dividiéndola por la longitud
			 * de la tabla para que cuando se llegue al final vuelva a la posición 
			 * 0 y nunca se salga de la misma
			 */
		}
		
		return false;	
	}

	
	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object item : c)
			if (!contains(item))
				return false;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		int oldS = size;
		for (T item : c)
			add(item);

		return size != oldS;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		int oldS = size;

		Iterator<T> iter = iterator();
		while (iter.hasNext()) {
			T d = iter.next();
			if (!c.contains(d))
				remove(d);
		}

		return size != oldS;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		int oldS = size;

		Iterator<T> iter = iterator();
		while (iter.hasNext()) {
			T d = iter.next();
			if (c.contains(d))
				remove(d);
		}

		return size != oldS;
	}

	@Override
	public void clear() {
		containsNull = false;
		size = 0;
		dirty = 0;
		for (int i = 0; i < table.length; i++) {
			table[i] = null;
			used[i] = false;
		}
	}

	@Override
	public Object[] toArray() {
		Object v[] = new Object[size];
		int i = 0;
		for (T item : this)
			v[i++] = item;

		return v;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		if (a.length < size)
			return (T[]) toArray();

		Iterator<T> iter = (Iterator<T>) iterator();
		int i = 0;
		while (iter.hasNext())
			a[i++] = iter.next();

		return a;
	}

	public String toString() {
		StringBuilder str = new StringBuilder("[ ");
		if (containsNull)
			str.append("null, ");
		for (int row = 0; row < table.length; row++) {
			if (table[row] != null) {
				str.append("{" + row + ": ");
				str.append(table[row]);
				str.append("} ");
			}
		}

		str.append("] (size: " + size + ", capacity: " + table.length + ")");
		return str.toString();
	}

	// IMPLEMENTACION DE ITERADORES
	private class LocalIterator implements Iterator<T> {
		int next;
		int last;

		LocalIterator() {
			last = -2;
			if (containsNull)
				next = -1;
			else {
				next = 0;
				while (next < table.length && table[next] == null)
					next++;
			}
		}

		@Override
		public boolean hasNext() {
			return next != table.length;
		}

		@Override
		public T next() {
			if (!hasNext())
				throw new NoSuchElementException();

			last = next;
			do {
				next++;
			} while (next < table.length && table[next] == null);
			return last == -1 ? null : table[last];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public Iterator<T> iterator() {
		return new LocalIterator();
	}

}
