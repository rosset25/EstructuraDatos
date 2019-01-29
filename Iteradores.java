package practica2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * @author morales
 *
 */
public class Iteradores {

	/**
	 * Toma dos conjuntos de enteros y redistribuye los elementos entre ellos para que en 
	 * en primero terminen todos los que tienen valor par, y en el segundo todos los qu tienen
	 * valor impar. 
	 * @param pares		Conjunto en el que terminarÃ¡n todos los elementos pares
	 * @param impares	Conjunto que el que terminarÃ¡n todos los elementos imapres.
	 */
	static public void partir(Set<Integer> pares, Set<Integer> impares) {
		
		int num;
		
		Iterator<Integer> iter1 = pares.iterator();	//iterador lista pares
		while (iter1.hasNext()) {
			num = iter1.next();
			if(num%2 != 0) {
				impares.add(num);
				iter1.remove();
				/*Todos aquellos números que no sean pares, se añaden
				 *a la lista de impares y se borran de la lista de pares
				 */
				
			}
		}
		
		Iterator<Integer> iter2 = impares.iterator(); //iterador lista impares
		while(iter2.hasNext()) {
			num = iter2.next();
			if (num%2 == 0) {
				pares.add(num);
				iter2.remove();
				/*Todos aquellos números que no sean impares, se añaden
				 *a la lista de pares y se borran de la lista de impares
				 */
			}
		}
		
		/* NOTA: Los respectivos iteradores se deben inicializar cuando se vayan a usar,
		 * ya que si hay modificaciones en la lista una vez inicializados, dará un error
		 * ya que la información del iterador será distinta a la realidad.
		 */
	}
	
	
	/**
	 * Intercambio los elementos de una lista de interos. De forma que los que ocupan la 
	 * posiciÃ³n i se intercambiÃ¡n pos los que ocupan i+1, siendo i un nÃºmero par. En el caso de 
	 * que la lista tenga talla impar el Ãºltimo elemento permanece inalterado.
	 * 
	 * <p> Toma como pÃ¡ramatero  un iterador que puede estar en cualquier posiciÃ³n de la lista. 
	 * 
	 * @param iter 	Un iterador de enteros que puede estar en cualquier posiciÃ³n de la lista
	 * @return 		El tamaÃ±o de la lista
	 */
	static public int intercambio(ListIterator<Integer> iter) {
		
		while (iter.hasPrevious()){	//El iterador se coloca al principio de la lista
			iter.previous();
		}
		
		
		while(iter.hasNext()) {	//Recorre la lista
			int num = iter.next();
			
			if(iter.hasNext()) {	
				iter.remove();
				iter.next();
				iter.add(num);
				/* En caso de ser una lista con un número
				 * de posiciones impar, no alteramos el 
				 * último de ellos, ya que no tiene pareja
				 * con la que intercambiarse
				 */
			}
		}
		
		return iter.nextIndex();
		
	}	
	
	
	/**
	 * Traslada todos los elmentos que ocupan posiciÃ³n mÃºltiplo de tres en 
	 * una lista de enteros al final de Ã©sta. Los elementos trasladados mantienen el
	 * orden en el que estaban en la lista antes del traslado. El primer elementos de la list ocupa 
	 * la posisicÃ³n 1.
	 * 
	 * <p> Toma como pÃ¡ramatero  un iterador que puede estar en cualquier posiciÃ³n de la lista. 
	 * @param iter	Un iterador de enteros que puede estar en cualquier posiciÃ³n de la lista
	 * @return		El nÃºmero de elementos trasladados. 
	 */
	static public int trespatras(ListIterator<Integer> iter) {
		
		List<Integer> multiplos = new LinkedList<Integer>();	//Lista auxiliar
		int i = 1;	//contador
		
		while (iter.hasPrevious()){	//El iterador se coloca al principio de la lista
				iter.previous();
		}
		
		while(iter.hasNext()) {	//Recorre la lista
			
			int num = iter.next();
			if(i%3 == 0) {
				multiplos.add(num);
				iter.remove();	
				/* Si el contador, que marca las posiciones de la lista, es múltiplo
				 * de 3, el elemento de la posición se almacena en la lista auxiliar
				 * multiplos y lo borra de la original
				 */
			}
			
			i++;
		}
		
		for (int j: multiplos) {	//Recorre la lista auxiliar multiplos
			iter.add(j);	
			//Añade todos los elementos al final de la lista original
		}
		
		return multiplos.size();
		// Se devuelve la cantidad de posiciones de múltiplos de 3 encontrada
	}
 }
