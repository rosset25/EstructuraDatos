package practica5;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class indexador {
	
	public static void printIndice(Map<String, List<Integer>> indice) {
		System.out.println("El índice contiene " + indice.size() + " palabras distintas");
		for (String palabra:indice.keySet()) 
			System.out.println(" " + palabra + ": " + indice.get(palabra));
	}	
			
	/**
	 * <p>Carga el diccionario desde un fichero. El fichero constará de un listado de palabras válidas. 
	 * <p> Al leer las palbras les asignará a cada una un índice, comenzando desde el cero, de forma consecutiva, de uno en uno. 
	 * 
	 * @param fichero Nombre del fichero que contiene el diccionario.
	 * 
	 * @return Un <code>HashMap</code> donde la clave es la palabra, y el valor el índice numérico de esa palabra. 
	 * 
	 * @throws FileNotFoundException 
	 */
	public static HashMap<String, Integer> leerDiccionario(String fichero) throws FileNotFoundException{
		
		Scanner input = null;
		try {
		input = new Scanner(new FileInputStream(fichero));
		}
		catch (FileNotFoundException e) {
		System.out.println("File could not be opened");
		System.exit(0);
		}
		
		int i = 0;  //contador de palabras
		
		//Mapa donde se guardan las palabras y sus índices
		HashMap<String, Integer> diccionario = new HashMap<String,Integer>(); 
		
		while(input.hasNext()) {
			diccionario.put(input.nextLine(),i);
			i++;
		}
		
		return diccionario;
	}
	
	/** Toma un fichero conteniendo un texto e indexa todas las palabras que apercen en él.
	 * <p> Para realizar está operación numera las posiciones de todas las palabras que aperecen en el texto, en orden, 
	 * comenzando desde 0 y contándolas de una en una. En esta numeración no se descarta ninguna palabra. A continuación crea un índice 
	 * tal que para cada palabra que aparece en el fichero, crea una lista con las posiciones en las que aparece esa palabra.
	 * En este índice solo se incluirán las palabras válidas, es decir las que aparecen en el diccionario.
	 * 
	 * @param fichero 	   Nombre del fichero que contiene el texto. 
	 * @param diccionario  Conjunto con todas las palabras del diccionario. 
	 * 
	 * @return Un <code>Map</code> en el que la clave será la palabra y el valor, una lista con las posiciones en las 
	 * 		  que aparece la palabra en el fichero de texto. En estas listas las posicioens están ordenadas de menor a mayor.
	 * 
	 * @throws FileNotFoundException Si alguno de los ficheros no podido ser abierto.
	 */
	public static Map<String, List<Integer>> indexar(String fichero, Set<String> diccionario) throws FileNotFoundException {
		
		Scanner input = null;	
		try {
		input = new Scanner(new FileInputStream(fichero));
		}
		catch (FileNotFoundException e) {
		System.out.println("File could not be opened");
		System.exit(0);
		}
		
		TreeMap<String, List<Integer>> palabras = new TreeMap<String,List<Integer>>();
		/* Mapa donde almacenaremos las palabras ordenadas alfabéticamente y sus 
		 * diferentes apariciones en el fichero proporcionado
		 */
		int i=0;
		while(input.hasNext()) {
			String word = input.next().toLowerCase();
			if(diccionario.contains(word)){ 
				/*Todas las palabras que no formen parte del diccionario 
				 * (incluyendo números y signos de puntuación), quedarán excluidas
				 */
				if(!palabras.containsKey(word)) { //Si la palabra aún no se ha guardado
					palabras.put(word, new ArrayList<Integer>());
				}
		
				palabras.get(word).add(i);
				/* Se busca la palabra ya guardada alfabéticamente y se guarda
				 * el índice dónde ha aparecido/vuelto a apareer
				 */
				i++; //Sólo se sumará el índice cuando la palabra sea válida
			}			
		}
		
		input.close();
		
		return palabras;
	}
	
	/** Escribe el indice en un fichero en formato binario.
	 * 
	 * <p> El fichero binario consistirá en una secuancia de <code>Intger</code> conel siguiente formato:
	 * <ol> 
	 * <li> El primer <code>Intger</code> del fichero será el numero de palabras en el índice.  
	 * <li> Por cada palabra del índice se guardarán dos <codeIntger</code>: el índice de la palabra y el 
	 * número de apariciones de esa palabra en el fichero de texto (longitud de la lista de posiciones). 
	 * Los pares de enteros se ordenarán por orden del índice de las palabras.
	 * <li> Por cada palabra y en el mismo orden que en el bloque anterior, un <code>Integer</code>por cada 
	 * posición de esa palabra en el fichero de texto.  Las posiciones se guardarán en orden 
	 * de menor a mayor. 
	 * </ol>
	 * 
	 * @param fichero 	Nombre del fichero binario que se creará
	 * @param indice		Un <code>Map</code> en el que la clave será la palabra y el valor, una lista con las posiciones en las 
	 * 		  que aparece la palabra.
	 * @param diccionario Un <code>HashMap</code> donde la clave es la palabra, y el valor el índice numérico de esa palabra. 
	 * 
	 * @return El número de bytes escritos en el fichero.
	 * 
	 * @throws IOException Si se produce cualquier fallo de apertura, escritura. 
	 */
	public static long escribirIndice(String fichero,  Map<String,List<Integer>> indice, Map<String, Integer> diccionario) throws IOException {
		
		RandomAccessFile io = null;
		try{
			io = new RandomAccessFile(fichero, "rw");
		}catch (IOException e) {
			System.out.println("File could not be opened");
			System.exit(0);
		}
		
		//1 BLOQUE:
		int indicesTotales = indice.size(); //Escribe el total de palabras que hay
		io.writeInt(indicesTotales); 
		
		// Variable donde se guardarán los bytes totales del fichero
		int totalBytes = Integer.BYTES;
		
		Set<String> palabras = indice.keySet(); //Lista de palabras a guardar
		
		//2 BLOQUE:
		for(String word: palabras){
			io.writeInt(diccionario.get(word)); //Escribe el índice de la palabra
			io.writeInt(indice.get(word).size());  //Cantidad de veces que ha aparecido la palabra
			
			//Se suman el número de bytes que lleva escrito el fichero
			totalBytes = totalBytes + (Integer.BYTES*2); 
		}
		
		//3 BLOQUE:
		for(String word: palabras){
			for(int i : indice.get(word)){
				//Se escriben las posiciones de las diversas apariciones de una palabra del texto
				io.writeInt(i);
				totalBytes = totalBytes + Integer.BYTES;
			}
		}
		
		io.close();
		
		return totalBytes;
	}
		
	/** Busca los datos de una plabra en el fichero binario.
	 *
	 * Busca una palabra en un fichero binario con la estructura indicada en el método <code>escribirIndice</code>. Usa 
	 * el diccionario para averiguar el indice de esa palabra. Descarta las palabras que no se encuentran en el diccionario.
	 * 
	 * @param fichero 	Nombre del fichero bianrio
	 * @param palabra	Palabra a buscar en el fichero binario.
	 * @param diccionario  Un <code>HashMap</code> donde la clave es la palabra, y el valor el índice numérico de esa palabra.
	 * 
	 * @return Una <code>List</code> con las posiciones de la palabra, o <code>null</code> si no ha se encuentra en el índice.
	 * 
	 * @throws IOException Si se produce algún error en la apertura/lectura del fichero binario. 
	 */
	public static List<Integer> buscaPalabra(String fichero, String palabra, Map<String,Integer> diccionario) throws IOException {
		
		RandomAccessFile io = null;
		try{
			io = new RandomAccessFile(fichero, "r");
		}catch (IOException e) {
			System.out.println("File could not be opened");
			System.exit(0);
		}
		
		//Lista a devolver con las posiciones de las apariciones de la palabra buscada en el texto
		List<Integer> apariciones = new LinkedList<Integer>(); 
		
		int indicePalabra = diccionario.get(palabra); //índice de la palabra a buscar
		
		int totalPalabras = (io.readInt()*Integer.BYTES); 
		/* Lee el primer parámetro del fichero (1 BLOQUE), el cual indica el número 
		 * total de palabras que tiene el texto
		 */
		
		int bytesLeidos = Integer.BYTES; 
		int cantidadRepTotales = 0; 
		/* Servirá para avanzar el puntero en el 3 BLOQUE, es decir irá contando la cantidad
		 * de repeticiones que tiene cada palabra en el texto empezando por la que esté en la 
		 * primera posición
		 */
		
		while((totalPalabras*2) >= bytesLeidos) { 
			/* (totalPalabras*2) sirve para saber cuánto ocupa el 2 BLOQUE, ya que si no 
			 * encontramos la palabra buscada se puede para la bçusqueda sin necesidad de
			 * seguir recorriendo el fichero
			 */
			int word = io.readInt(); //palabra del fichero
			int repeticiones = io.readInt(); //cantidad de veces que aparece la palabra en el texto
			bytesLeidos += Integer.BYTES*2; //dos enteros leídos, es decir, dos campos
			
			if(indicePalabra == word) { //si los índices de la palabra leída y la palabra buscada coinciden
				
				int puntero = (totalPalabras*2)+ Integer.BYTES; //Para colocar el puntero al principio del 3 BLOQUE
				puntero += cantidadRepTotales; //Se suma el total de las repeticiones de otras palabras no buscadas 
				io.seek(puntero); //Se avanza el puntero hasta el principio del dato a recoger
		
				for(int i=0; i < repeticiones; i++){ 
				//Se recogen las distintas apariciones de la palabra buscada en una lista
					apariciones.add(io.readInt());
				}
				
				io.close();
				return apariciones;
			}
			
			cantidadRepTotales = cantidadRepTotales + (Integer.BYTES * repeticiones);
			/* Se cuentan los bytes que habrá que leer hasta llegar a los índices de las 
			 * distintas posiciones del texto donde la palabra ha aparecido, es decir,
			 * indicaará hasta dónde deberá avanzar el puntero en el 3 BLOQUE
			 */
		}
		
		io.close();
		return null;
	}
	
	
	
	static public String FICHERO_DICCIONARIO = "castellano.dicc";
	
	
	/** Programa principal.
	 * 
	 * Permite probar los métodos uno a uno, sobre un fichero de texto concreto
	 * @throws IOException
	 */
	public static void main(String [] args) throws IOException {
		System.out.println("Leyendo diccionario...");
		HashMap<String, Integer> diccionario = leerDiccionario(FICHERO_DICCIONARIO);	
			
		System.out.println("Diccionario leido");
		System.out.println(" * Contiene " + diccionario.size() + " palabras distintas");	
		
		Scanner consola = new Scanner(System.in);
		System.out.print("Escribe el nombre de fichero a procesar (sin extensión): ");
		String base = consola.nextLine();
		
		Map<String, List<Integer>> indice = indexar(base + ".txt", diccionario.keySet());
		printIndice(indice);
		
		System.out.println("Escribiendo el indice en el fichero "+ base +".dat ...");
		long t = escribirIndice(base + ".dat", indice, diccionario); 
		System.out.println("Se han escrito " + t + " bytes");
		
		
		String siguiente = null;
		do {
			System.out.print("Escribe la palbra cuya información deseas recuperar  (\"null\" para terminar): ");
			siguiente = consola.nextLine().toLowerCase();
			if (siguiente.equals("null"))
				break;
			
			List<Integer> l = buscaPalabra (base + ".dat", siguiente, diccionario);
			if (l != null) 
				System.out.println(" -> " + siguiente + " (" + diccionario.get(siguiente) + "): " + l);
			else
				System.out.println(" XX " + siguiente + " no ha sido encontrada");
			
		} while (!siguiente.equals("null"));
		
		consola.close();
	}
	
	

}
