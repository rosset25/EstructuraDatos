package polinomio;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import polinomio.Cero;

public class Polinomio {

	// La lista de monomios
	private List<Monomio> datos = new LinkedList<Monomio>();

	/**
	 * Constructor por defecto. La lista de monomios estÃ¡ vacÃ­a
	 */
	public Polinomio() {
	};

	/**
	 * Constructor a partir de un vector. Toma los coeficientes de los monomios
	 * de los valores almacenados en el vector, y los exponentes son las
	 * posiciones dentro del vector. Si <code>v[i]</code> contiene
	 * <code>a</code> el monomio contruido serÃ¡ aX^i. <br>
	 * 
	 * Por ejemplo: <br>
	 * 
	 * v = [-1, 0, 2] -> 2X^2 -1X^0
	 * 
	 * @param v
	 */
	public Polinomio(double v[]) {
		
		//se recorre el vector v
		for (int i=0; i < v.length; i++) {
			if (! Cero.esCero(v[i])) {	//si no es 0, el monomio se añadirá a la lista
				Monomio m = new Monomio(v[i], i);
				datos.add(m);	//automáticamente los va poniendo uno detrás de otro
			}
		}
		
	}

	/**
	 * Constructor copia
	 * 
	 * @param otro
	 * @throws <code>NullPointerException</code>
	 *             si el parÃ¡metro es nulo
	 */
	public Polinomio(Polinomio otro) {
		if (otro == null)
			throw new NullPointerException();

		for (Monomio item : otro.datos)
			datos.add(new Monomio(item));
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();

		

		boolean primero = true;

		for (int i = 0; i <datos.size(); i++) {
			Monomio item = datos.get(i);

			if (item.coeficiente < 0) {
				str.append('-');
				if (!primero)
					str.append(' ');
			} else if (!primero)
				str.append("+ ");

			str.append(Math.abs(item.coeficiente));
			if (item.exponente > 0)
				str.append('X');
			if (item.exponente > 1)
				str.append("^" + item.exponente);

			if (i < datos.size()-1)
				str.append(' ');

			primero = false;
		}
		if (primero)
			str.append("0.0");

		return str.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		Polinomio other = (Polinomio) obj;

		if (this.datos.size() != other.datos.size())
			return false;

		Iterator<Monomio> iter1 = this.datos.iterator();
		Iterator<Monomio> iter2 = other.datos.iterator();

		while (iter1.hasNext())
			if (!(iter1.next().equals(iter2.next())))
				return false;

		return true;
	}

	/**
	 * Devuelve la lista de monomios
	 *
	 */
	public List<Monomio> monomios() {
		return datos;
	}

	/**
	 * Suma un polinomio sobre <code>this</code>, es decir, modificando el
	 * polinomio local. Debe permitir la auto autosuma, es decir,
	 * <code>polinomio.sumar(polinomio)</code> debe dar un resultado correcto.
	 *
	 * @param otro
	 * @return <code>this<\code>
	 * @throws <code>NullPointeExcepction</code> en caso de que el parÃ¡metro sea <code>null</code>.
	 */
	public void sumar(Polinomio otro) {

		//Primero se comprueba si this.datos está vacío
		if(this.datos.isEmpty() && !otro.monomios().isEmpty()) {
			this.datos.addAll(otro.monomios()); 
			//si es así se le suma directamente todo el Polinomio otro
			
		}else{ 
			
			ListIterator<Monomio> iter1 = this.datos.listIterator();	//iterador datos
			ListIterator<Monomio> iter2 = otro.monomios().listIterator(); //iterador otro
			Monomio m1 = null;
			Monomio m2 = null;
		
			while(iter1.hasNext() && iter2.hasNext()) {	//mientras alguna de las listas no se termine
				
				 m1 = iter1.next();
				 m2 = iter2.next();
			
				if ( m1.exponente < m2.exponente) {
					m2 = iter2.previous();
					/*si el exponente del monomio de datos es menor, 
					 *sólo avanzamos el puntero de datos
					 */
				}else{
					if (m2.exponente < m1.exponente) {
						iter1.previous();
						iter1.add(m2);
						/*si el exponente del monomio de otro es menor,
						 * sólo avanzamos el puntero de otro y
						 * añadimos el monomio a datos
						 */
					}
				}
				
				if(m1.exponente == m2.exponente) {
					double result = m1.coeficiente + m2.coeficiente;
					m1.coeficiente = result;
					/*en caso de que ambos exponentes sean iguales,
					 * se hace la operación sobre el monomio de datos
					 */
					
					if(Cero.esCero(result)) {
						iter1.remove();
						/*en caso de que dicha operación de como
						 * resultado 0, se elimina de la lista
						 * datos el monomio
						 */
					}
				}
			}
			
			while(iter2.hasNext()) {
				m2 = iter2.next();
				this.datos.add(m2);
				/*nos aseguramos de que no se ha quedado nada
				 * del Polinomio otro por sumar
				 */
			}
				
		}
	}
	

	/** Multiplica el polinomio <code>this</code> por un monomio. 
	 * @param mono
	 */
	public void multiplicarMonomio(Monomio mono) {
		
		//primero se comprueba si el monomio es 0
		if(!Cero.esCero(mono.coeficiente)) {	
		
			//se crea un iterador para recorrer la lista datos
			ListIterator<Monomio> iter = this.datos.listIterator();
			
			while(iter.hasNext()) { //se recorre datos para poder multiplicar
				Monomio m = iter.next();
				double resultCoef = m.coeficiente * mono.coeficiente; //multiplicación de coeficientes
				int resultExpo = m.exponente + mono.exponente; //suma de sus exponentes
				Monomio resultFinal = new Monomio(resultCoef, resultExpo);
				iter.set(resultFinal);
			}
			
		}else{	//si es 0, todo sería 0, así que no habría nada en datos
			datos.clear();
		}
	}
}
