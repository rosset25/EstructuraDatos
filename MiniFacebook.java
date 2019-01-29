package facebook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.Set;

//import ejer1.EDListGraph.Node;

public class MiniFacebook {

	
	/** EDGraph<String, Object> leerGrafo (String nomfich)
	 * 
	 * @param nomfich Nombre del fichero de texto
	 * @return grafo implementado con listas de adyacencia a partir de la informaci�n del fichero
	 * 
	 * Si no puede abrir el fichero, devolver� null. Si el fichero est� vac�o, devolver� un grafo vac�o.
	 * 
	 */
	public static EDGraph<String, Object> leerGrafo (String nomfich) {
		
		Scanner input = null;
		try {
			input = new Scanner(new FileInputStream(nomfich));
		}
		catch (FileNotFoundException e) {
			return null;
		}
		
		EDGraph<String, Object> face = new EDListGraph<String, Object>();
		
		while(input.hasNext()){
			
			String amigo1 = input.next();
			String amigo2 = input.next();
			
			if(face.getNodeIndex(amigo1) < 0) {
				face.insertNode(amigo1);
			}
			if(face.getNodeIndex(amigo2) < 0){
				face.insertNode(amigo2);
			}			
			
			EDEdge<Object> edge = new EDEdge<Object>(face.getNodeIndex(amigo1),face.getNodeIndex(amigo2));
			
			face.insertEdge(edge);
		}
		
		return face;
		
	}
	

}
