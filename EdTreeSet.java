package practica6;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;

public class EdTreeSet<E extends Comparable <E>> implements Set<E>{

	protected class BinaryNode {
		protected E data;
		protected BinaryNode left;
		protected BinaryNode right;
		
		BinaryNode(E data){
			this.data = data;
		}
		BinaryNode(E data, BinaryNode lnode, BinaryNode rnode) {
			this.data = data;
			this.left = lnode;
			this.right = rnode;
		}
	}
	
	private BinaryNode root;
	private Comparator<? super E> comparator;
	private int size; //number of elements
	protected boolean insertReturn; //Return value for the public insert method
	protected boolean removeReturn; //Return value for the public remove method

	

	public EdTreeSet() {
		root = null;
		comparator = null;
		size = 0;
	}
	
	public EdTreeSet(Comparator<? super E> comp) {
		root = null;
		comparator = comp;
		size = 0;
	}
	
	public EdTreeSet(Collection<? extends E> c) {
		this();
		addAll(c);
	}
	
	public EdTreeSet(SortedSet<E> s) {
		this(s.comparator());
		addAll(s);
	}
	
	private int compare(E left, E right) {
		if (comparator != null) { //A comparator is defined
			return comparator.compare(left,right);
		}
		else {
			return (((Comparable<E>) left).compareTo(right));
		}
	}
	
	/**
	 * E first()
	 * Returns the first (lowest) element currently in this set.
	Throws:
	NoSuchElementException - if this set is empty
	*/
	public E first() {
		
		if(isEmpty()) {
			return null;
		}
		
		E item = minimo(root);

		return item;
	}

   
	/**
	 * E last()
	*Returns the last (highest) element currently in this set.
	*@Return:
	* the last (highest) element currently in this set
	* Throws:
	*NoSuchElementException - if this set is empty
	 */
	public E last() {
		
		if( root == null) {
			return null;
		}
		
		BinaryNode aux = root;
		while(aux.right != null) {
			aux = aux.right;
		}
		return aux.data;
	
	}
	
	
	/** boolean contains(Object o)
	  * Returns true if this set contains the specified element.
	  * Parameters:
		o - element whose presence in this set is to be tested
	  * Returns:
		true if this set contains the specified element
	*/
	@Override
	public boolean contains(Object item) {	//Recursivo
		
		if( item == null){ 
			return false;
		}
		
		boolean resultado = contains(root,(E) item);
		return resultado;
	}
	
	private boolean contains(BinaryNode r, E item) {  //método privado
		
		if(r == null){  //caso base (si no se ha encontrado el elemento)
			return false;
		} 
		
		//Si el elemento es más grande, se avanza a la derecha
		if(compare(item, r.data) > 0){ 
			return contains(r.right, item);
		}
		//Si el elemento es más pequeño, se avanza a la izquierda
		if(compare(item, r.data) < 0){
			return contains(r.left, item);
		}
		//Si hemos encontrado el elemento
		return true;
	}
	
	
	@Override
	/**
	 * boolean add(E e)
	Adds the specified element to this set if it is not already present
 	Returns:
	true if this set did not already contain the specified element
	 */
	public boolean add(E item) { //Recursivo
		
		if (item == null){
			return false;
		}
		
		root = add(root, item);
		return insertReturn;

	}
	
	
	private BinaryNode add(BinaryNode r, E item) { //método privado
		
		if(r == null) { //caso base, se añade el elemento
				r = new BinaryNode(item);
				size++;
				insertReturn = true;
		}
		
		/* Resto de casos (se avanzará a la derecha o la izquierda en función del valor del 
		 * elemento frente a los nodos visitados
		 */
		else if (compare(item, r.data) < 0){
			r.left = add(r.left, item);
			/* Si el elemento es más pequeño, se desplaza a la 
			 * izquierda, se devuelve el nodo original implementando
			 * los posibles cambios que se hayan realizado (ya que en 
			 * Java los datos se pasan pro valor, no referencia)
			 */
		}
		else if(compare(item, r.data) > 0) {
			r.right = add(r.right, item);
			// Si el elemento es más grande, se desplaza a la derecha
		}
		else{ //son iguales, elemento repetido, por tanto no puede insertarse
			insertReturn = false;
		}
		
		return r;
	}

	
	/** boolean remove(Object o)
	 * Removes the specified element from this set if it is present (optional operation). More formally, removes an element e such that (o==null ? e==null : o.equals(e)), if this set contains such an element. Returns true if this set contained the element (or equivalently, if this set changed as a result of the call). (This set will not contain the element once the call returns.)
	 * Parameters:
		o - object to be removed from this set, if present
	 * Returns:
		true if this set contained the specified element
	*/
	@Override
	public boolean remove(Object item) {	//Recursivo
		
		if (item == null) {
			return false;
		}
		
		root = remove(root,(E)item);
		
		return removeReturn;
		
	}
	
	private BinaryNode remove(BinaryNode r, E item){
		
		if( r == null) { //caso base, no se ha encontrado el elemento
			removeReturn = false;
		
		//Resto de casos: mientras no se encuentre
		}else if(compare(item, r.data) < 0){
			r.left = remove(r.left, item);
		}else if(compare(item, r.data) > 0) {
			r.right = remove(r.right, item);
		
		}else{ //son iguales, se ha encontrado el elemento a borrar
			
			if( r.left != null && r.right != null){ //tiene los dos hijos
				E elem = minimo(r.right); 
				/* Se guarda el elemento que sustituirá al actual,
				 * que será el mínimo de su árbol derecho (o bien se podría
				 * haber hecho con el máximo de su árbol izquierdo)
				 */
				r.data = elem; //se sustituye el nuevo elemento
				r.right = removeMin(r.right); 
				/* Se borra el elemento por el cual hemos sustituido al anterior,
				 * es decir, el mínimo del árbol derecho
				 */
				
			}else if( r.left != null) { //sólo tiene hijo izquierdo
				r = r.left;
				
			}else{ //sólo tiene hijo derecho o es nodo hoja
				r = r.right;
			}
			
			size--;	
			removeReturn = true;
		}
		
		return r;
	}
	
	private E minimo(BinaryNode r) {  //método privado auxiliar 
		//Iterativo
		
		BinaryNode aux = r;
		
		while(aux.left != null){
			aux = aux.left; //se avanza lo más a la izquierda posible
		}
		
		E item = aux.data;
		return item;
	}
	
	private BinaryNode removeMin(BinaryNode r){ //método privado auxiliar
		//Recursivo
		//Nota: el nodo a borrar NUNCA tendrá dos hijos o hijo izquierdo
		if(r.left == null && r.right == null){ 
			// si el nodo es hoja, para borrarlo se referencia a null
			r = null;
			
		}else if(r.left == null && r.right != null) {
			// si tiene hijo derecho, el nodo ahora es su hijo derecho
			r = r.right;
			
		}else{ //si el nodo tiene dos hijos se sigue avanzando a la izquierda
			r.left = removeMin(r.left);
		}
		
		return r;
	}
	
	/** 
	 * E ceiling(E e)
	 * Returns the least element in this set greater than or equal to the given element, 
	 * or null if there is no such element.
	 */
	public E ceiling(E item) {  //Recursivo
		
		if(root == null){
			return null;
		}
		
		E itemAprox = ceiling(root, item);
		
		return itemAprox;
	}
	
	private E ceiling(BinaryNode r, E item) {  //método privado
		
		/* No hace falta, ya que nunca llamará a la función una vez
		 * llegado al caso base, el cual mira si es nodo hoja
		 * 
		 *  if(r == null){
		 *		return null;
		 *  }
		 */
		
		E itemAprox = null; 
		/* Se inicializa a null, ya que si item es un elemento más grande
		 * que el mayor de la lista se devuelve null y sino tomará diferentes
		 * valores a lo largo de las recursiones
		 */
		
		int comparacion = compare(item, r.data);
		
		if(comparacion == 0){ //si es igual
			return item;
		
		}else{
			
			if(r.left == null && r.right == null){ //caso base, si es nodo hoja
				if(comparacion < 0){
					return r.data;
					 /* En caso de ser un elemento menor al nodo mirado
					  * se devuelve ese mismo nodo, ya que no habrá otro 
					  * que se aproxime más
					  */
				}
				
				return null; // o bien: return itemAprox
				/* En caso contrario (item dado más grande que el nodo), se devuelve null, 
				 * no se ha encontrado un elemento aproximado idóneo
				 */
			}
			
			if(comparacion < 0){
				itemAprox = r.data;
				E ceil = null;
				
				if(r.left != null) {
					ceil = ceiling(r.left, item);
				}
				if( ceil != null){
					/* Si se ha encontrado un valor más idóneo que el actual, 
					 * se le asigna el encontrado (por el método ceil anterior)
					 */
					itemAprox = ceil;
				}
				
			}else if(r.right != null && comparacion > 0){
				/* Si el elemento es mayor que el nodo actual, se sigue buscando pero
				 * no se modifica directamente itemAprox, irá sólo en función de lo 
				 * que devuelva el ceiling, puediendo así llegar a devolver incluso null
				 * si item es más grande que el mayor de los nodos
				 */
				itemAprox = ceiling(r.right, item);
			}
		}
		
		return itemAprox;
	}
	

	@Override
	public boolean addAll(Collection<? extends E> arg0) {
		if (arg0==null) throw new NullPointerException();
		boolean changed = false;
		for (E e: arg0) {
			boolean res=add(e);
			if (!changed && res) changed = true;
		}
		return (changed);
	}

	@Override
	public void clear() {
		root = null; 
		size = 0;
	}

	
	

	@Override
	public boolean containsAll(Collection<?> arg0) {
		if (arg0 == null) throw new NullPointerException();
		boolean cont=true;
		Iterator<?> it=arg0.iterator();
		while (it.hasNext() && cont) {
			cont = contains(it.next());
		}
		return cont;
	}

	@Override
	public boolean isEmpty() {
		return (size==0);
	}



	@Override
	public boolean removeAll(Collection<?> arg0) {
		if (arg0==null) throw new NullPointerException();
		int originalSize=size();
		int newSize = originalSize;
		Object [] v = this.toArray();
		for (int i=0; i<v.length; i++) {
			if (arg0.contains(v[i])) {
				remove(v[i]);
				newSize--;
			}
		}
		return (originalSize!=newSize);
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		if (arg0==null) throw new NullPointerException();
		int originalS = size();
		int newS = originalS;
		Object[] v = this.toArray();
		for (int i=0; i<v.length; i++) {
			if (!arg0.contains(v[i])) {
				remove(v[i]);
				newS--;
			}
		}
		return (originalS!=newS);
		
	}


	@Override
	public int size() {
		return this.size;
	}

	@Override
	public Object[] toArray() {
		
		Object[] v = new Object[size()];
		toArray(0,root,v);
		
		return v;
	}

	private int toArray (int pos, BinaryNode r, Object[] v) {
		if (r!=null) {
			if (r.left!=null) pos = toArray(pos,r.left, v);
			//System.out.println("toArray pos-> "+pos +" data--> "+r.data);
			v[pos] = r.data;
			pos++;
			if (r.right!=null) pos =toArray(pos,r.right,v);
		}
		return pos;
	}
	
	@Override
	public <T> T[] toArray(T[] arg0) {
		if (arg0 == null) throw new NullPointerException();
		int n=size();
		if (n > arg0.length) 
			arg0=(T[]) new Object[n];
		toArray(0,root,arg0);
		
		return arg0;
	}

	
	/**Returns an String with the data in the nodes
	 * in inorder
	 */
	 public String toString() {
		 return toString(root);
	 }
	 
	 private String toString(BinaryNode r) {
		 String s="";
		 if (r != null) {
			 String sl=toString(r.left);
			 String sd=toString(r.right);
			 if (sl.length() >0) 
				 s = sl + ", ";
			 s = s + r.data;
			 if (sd.length() > 0)
				 s = s + ", " + sd;
		 }
	     return s;
	 }
	
	 //Implementaci�n de Iteradores
	 private class EdTreeSetIterator implements Iterator<E>{
			/*
			 * Internal class that stores the related info of an iterator. This class does 
			 * not implement a fail safe mechanism. 
			 * 
			 * nextItem: The next item to be returned by next()
			 * lastItem: The last item read. Null if not next() or remove() has not been call
			 */
			private E nextItem;
			private E lastItem;
			private int index;
			
			 
			public EdTreeSetIterator() {
				if (root!=null) nextItem = root.data;
				lastItem=null;
				index=0;
			}
				

			public boolean hasNext() {
				
				Object v[] = toArray();
				if (index <v.length) return true;
				return false;
			}

			public E next() {
				
				Object v[] = toArray();
				if (!hasNext()) throw new NoSuchElementException();
				lastItem = nextItem;
				index++;
				if (index < v.length) nextItem = (E) v[index];
				return lastItem;
			}
			
			public void remove() {
				throw new UnsupportedOperationException();
			}
	    }

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return new EdTreeSetIterator();
	}

}
