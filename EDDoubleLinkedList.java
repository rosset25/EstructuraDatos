package practica3;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;


public class EDDoubleLinkedList<T> implements List<T> {
	/** Implementation of the circular Double linked List
	 *
	 * @param <T> Base Type 
	 */
	
	// Node declaration
	protected class Node {
		public T data;
		public Node next;
		public Node prev;
		
		public Node(T element) {
			data = element;
			next = null;
			prev = null;
		}
		
		 Node(T element, Node prevRef, Node nextRef) {
			data = element;
			next = nextRef;
			prev = prevRef;
		}
	}
	
	// private data
	protected Node head = null;
	protected int size = 0;
	
	protected Node indexNode(int index) {
		// Search a node by index. Does not check index range
		Node ref = head;
		
		if (head != null) {
			int i; 
			if (index < (size/2))
				for (i=0; i < index; i++)
					ref = ref.next;
			else
				for (i=size; i > index; i--)
					ref = ref.prev;
		}
		return ref;
	}
	
	
	public EDDoubleLinkedList() {
		head = null;
		size = 0;
	}
	
	//Constructor copia
	public EDDoubleLinkedList(List<T> otherList) {
		
		if (otherList.isEmpty()) {
			head = null;
			size = 0;
		
		}else{
			
			for(T item : otherList) {
				add(item);	
			}
		}
		
	}

	public boolean add(T element) {
		
		if (element == null) {
			return false;
		}
		
		if (size == 0) {
			Node nuevo = new Node(element);
			head = nuevo;
			head.next = head.prev = head;
			/* Se debe tener en cuenta si sería el primer elemento
			 * de la lista y, en caso de serlo, asignarlo como la
			 * cabecera
			 */
		}else{
			Node nuevo = new Node(element,head.prev,head);
			nuevo.prev.next = nuevo;
			head.prev = nuevo;
			//Se añade al final de la lista
		}
		
		size++;
		
		return true;
	}

	
	public void add(int index, T element) {
		
		if (index > size || index < 0) {
			throw new IndexOutOfBoundsException();
		}
		
		if (index == size) {
			add(element);	
			/* Se llama directamente al método add(T elem) porque tanto 
			 * si no hay elementos como si es el último a añadir,
			 * add(element) puede hacer la operación correspondiente
			 */
		}else{
			Node nuevo = new Node(element);
			Node actual = indexNode(index);
			Node ant = actual.prev;
			ant.next = nuevo;
			nuevo.prev = ant;
			actual.prev = nuevo;
			nuevo.next = actual;
			size++;	/*	No se pone fuera porque si se añadiera el primer elemento
			 		 *  de la lista, se añadiría dos veces, ya que para ello se
			 		 *  llama al método add(T element)
			 		 */
			if (index == 0) { //Si es la posición es 0, el nuevo nodo será head
				head = nuevo;
			}
		}
	}

	//Borra la lista entera
	public void clear() {
		head = null;
		size = 0;
	}
	
	public boolean contains(Object element) {
		return indexOf(element) != -1;
	}

	public T get(int index) {
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException(Integer.toString(index));
		
		Node ref = indexNode(index);
		
		return ref.data;
	}

	
	//Busca el elemento en la lista
	public int indexOf(Object element) {
		
		int pos= -1;  //Devuelve -1 si no se encuentra el elemento
		Node actual = head;
		
		/*No hace falta comprobaer con contains(elem), ya que
		 * necesitamos devolver la posición del elemento
		 */
		for (int i=0; i< size; i++) {
			if (element == actual.data) {
				pos = i;
				break;
			}
			actual = actual.next;
		}
		
		return pos;		
	}

	public boolean isEmpty() {
		if (size == 0) {
			return true;
		}
		
		return false;
	}

	
	public boolean remove(Object element) {
		
		
		int i = indexOf(element);
		
		if (i != -1) {	// Comprobación de si el elemento está o no en la lista
			remove(i);
			return true;
		}

		return false;
		
		/* se podría optimizar más, puesto que se hacen muchas iteraciones
		 * para borrar un elemento, primero con el método indexOf(elem),
		 * y luego con remove (int index), que vuelve a recorrer la lista
		 * para volver a encontrar el elemento
		 */
	}

	
	public T remove(int index) {

		if (index >= size || index < 0) {	//Se comprueba que es un índice válido
			throw new IndexOutOfBoundsException();
		}
		
		Node actual = indexNode(index); /* Método que te devuelve el nodo que está en el
										 * índice pasado como parámetro
										 */
		if (size == 1) {
			head = null;
		
		}else{
			Node sig = actual.next;
			Node ant = actual.prev;
			ant.next = sig;
			sig.prev = ant;	
			if(index == 0) {
				head = sig;
			}
		}
		
		size--;
		return actual.data;	//Se devuelve el elemento borrado
	}
	

	public T set(int index, T element) {
		
		if (index < 0 || index >= size) {	//Se comprueba que el índice sea válido
			throw new IndexOutOfBoundsException();
		}
		
		Node cambiar = indexNode(index);
		T elemntAnterior = cambiar.data; /* Guardamos el elemento que había originalmente 
		 								  * antes de cambiarlo
		 								  */
		cambiar.data = element;
		
		return elemntAnterior;
	}

	
	public int size() {
		return size; 
	}
	
	public String toString() {
		
		String retVal = "[ ";
		
		Node current = head;
		
		if (current != null)
		do {
			retVal += current.data.toString();
			current = current.next;
			if (current != head)
				retVal += ", ";
			
		} while(current != head);
			
		retVal += " ]: " + size;
		
		return retVal;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		for (T item: c)
			add(item);
		
		return (!c.isEmpty());
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		ListIterator<T> iter2 = listIterator(index);
		for (T item: c) {
			iter2.add(item);
		}
		
		return (!c.isEmpty());
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		
		for (Object item : c) {
			if(! contains(item)) {
				return false;
			}
			/* Se comprueba que todos los elementos de collection c
			 * están en nuestra lista
			 */
		}
		
		return true;
	}


	@Override
	public int lastIndexOf(Object o) {
		Node ref = head;
		
		if (head == null)
			return -1;
		
		ref = ref.prev;
		for (int i = size-1; i >= 0; i--)
			if (ref.data.equals(o))
				return i;
		return -1;
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
				
		int talla = size;
		
		if(c.isEmpty()) {
			return true;
		}
		
		ListIterator<T> iter = listIterator();
		
		while(iter.hasNext()) {
			if(c.contains(iter.next())){	
				iter.remove();
			}
			/* Recorremos nuestra lista y comprobamos si el elemento
			 * está dentro de la colección c, si está, lo borramos
			 */
		}
		
		if(size != talla) {	//Devuelve true si ha borrado al menos un elemento
			return true;
		}
		
		return false;
	}

	
	@Override
	public boolean retainAll(Collection<?> c) {
		
		if(c.isEmpty()) {	
			clear();
			return true;
			/* Si la lista c está vacía entonces se borran 
			 * todos los elementos
			 */
		}
		
		ListIterator<T> iter = listIterator();
		
		while(iter.hasNext()) {
			if(! c.contains(iter.next())){
				iter.remove();
			}
			/* Recorremos nuestra lista y comprobamos si el elemento
			 * está dentro de la colección c, si NO está, lo borramos
			 */
		}
		
		return true;
	}

	
	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		if (fromIndex < 0 || fromIndex >= size || toIndex <= 0 || toIndex >= size || toIndex < fromIndex)
			throw new IndexOutOfBoundsException();
		
		EDDoubleLinkedList<T> newList = new EDDoubleLinkedList<T>();
		
		Node ref = indexNode(fromIndex);
		for (int i = fromIndex; i < toIndex; i++) {
			newList.add(ref.data);
			ref=ref.next;
		}
		
		return newList;
	}

	@Override
	public Object[] toArray() {
		Object[] newV = new Object[size];
		
		Node ref = head;
		for (int i =0; i < size; i++, ref = ref.next)
			newV[i] = ref.data;
			
		return newV;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		T[] newV = (T []) new Object[size];
		
		Node ref = (Node) head;
		for (int i =0; i < size; i++, ref = ref.next)
			newV[i] = (T) ref.data;
			
		return newV;
	}
	
	// Implementation of the iterators interface
		private class EDDoubleLinkedListIterator implements ListIterator<T>{
			/*
			 * Internal class that stores the related info of an iterator. This class does 
			 * not implement a fail safe mechanism. 
			 * 
			 * nextItem: The next item to be returned by next()
			 * lastItem: The last item read. Null if not next() or remove() has not been call
			 */
			private Node nextItem = head;
			private Node lastItem = null;
			private int index = 0;
			
			public EDDoubleLinkedListIterator(int id) {
				if (id < 0 || id > size) 
					throw new IndexOutOfBoundsException(Integer.toString(id));
				
				lastItem = null;
				
				nextItem = head;
				for (int i=0; i < id; i++)
					nextItem = nextItem.next;
				index = id;
			}
			
			
			public void add(T element) {
				
				Node n = new Node(element);
				if (nextItem == null) {
					head = n;
					nextItem = n.next = n.prev = n;
				} else {
					n.next = nextItem;
					n.prev = nextItem.prev;
					nextItem.prev= n;
					n.prev.next = n;
				}
					
				size++;
				lastItem = null;
				index++;
				
			}
			
			public boolean hasNext() {
				return index < size;
			}
			
			public boolean hasPrevious() {
				return index > 0;
			}
			
			public T next() throws NoSuchElementException {
				if (!hasNext())
					throw new NoSuchElementException();
				
				lastItem = nextItem;
				nextItem = nextItem.next;
				index++;
				return lastItem.data;
			}
			
			public int nextIndex() {
				return index;
			}
			
			public T previous() throws NoSuchElementException {
				if (!hasPrevious())
					throw new NoSuchElementException();
					
				lastItem = nextItem = nextItem.prev;	
				index--;
				return lastItem.data;
			}
			
			public int previousIndex() {
				return index-1;
			}
			public void remove() throws IllegalStateException{
				if (lastItem == null)
					throw new IllegalStateException();
				
				if (lastItem == nextItem) {
					nextItem = nextItem.next;
					index++;
				}
				
				lastItem.next.prev = lastItem.prev;
				lastItem.prev.next = lastItem.next;
				
				if (size == 1)
					nextItem = null;
				if (head == lastItem) 
					head = nextItem;
				
				lastItem = null;
				index--;
				size--;
				
			}
			
			public void set(T element) throws IllegalStateException {
				if (lastItem == null)
					throw new IllegalStateException();
				
				lastItem.data = element;
				lastItem = null;
			}
			
		}
		
		public ListIterator<T> listIterator() {
			return new EDDoubleLinkedListIterator(0);
		}

		public ListIterator<T> listIterator(int index) {
			return new EDDoubleLinkedListIterator(index);
		}
		
		public Iterator<T> iterator() {
			return (Iterator<T>) listIterator();
		}
}
