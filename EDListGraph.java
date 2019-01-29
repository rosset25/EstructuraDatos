package facebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.HashSet;
import java.util.Set;


/** Implementation of interface Graph using adjacency lists
 * @param <T> The base type of the nodes
 * @param <W> The base type of the weights of the edges
 */
public class EDListGraph<T,W> implements EDGraph<T,W> {
	@SuppressWarnings("hiding")
	private class Node<T> {
		T data;
		List< EDEdge<W> > lEdges;
		
		Node (T data) {
			this.data = data;
			this.lEdges = new LinkedList< EDEdge<W> >();
		}
		public boolean equals (Object other) {
			if (this == other) return true;
			if (!(other instanceof Node)) return false;
			//System.out.println("equals de node");
			Node<T> anotherNode = (Node<T>) other;
			return data.equals(anotherNode.data);
		}
	}
	
	// Private data
	private ArrayList<Node<T>> nodes;  //Vector of nodes
	private int size; //real number of nodes
	private boolean directed;
	
	/** Constructor
	 * @param direct <code>true</code> for directed edges; 
	 * <code>false</code> for non directed edges.
	 */

	public EDListGraph() {
		directed = false; //not directed
		nodes =  new ArrayList<Node<T>>();
		size =0;
	}
	
	public EDListGraph (boolean dir) {
		directed = dir;
		nodes =  new ArrayList<Node<T>>();
		size =0;
	}
	
	public int getSize() {
		return size;
	}

	public int nodesSize() {
		return nodes.size();
	}
	@Override
	public boolean isEmpty() {
		return size == 0;
	}
	
	@Override
	public int insertNode(T item) {
			
	    int i = 0;
	    while (i<nodes.size() && nodes.get(i).data != null) i++;
				
	    Node<T> newNode = new Node<T>(item);
	    if (i<nodes.size()) nodes.set(i,newNode);
	    else nodes.add(newNode);
	    size++;
	    //System.out.println("Insertado en posicion "+i);
	    return i;
	}
	
	@Override
	public int getNodeIndex(T item) {
		Node<T> aux = new Node<T>(item);
		return nodes.indexOf(aux);
	}

	@Override
	public T getNodeValue(int index) throws IndexOutOfBoundsException{
		
		return nodes.get(index).data;
		
	}
	
	@Override
	public boolean insertEdge(EDEdge<W> edge) {
		int sourceIndex = edge.getSource();
		int targetIndex = edge.getTarget();
		if (sourceIndex >=0 && sourceIndex<nodes.size() && targetIndex >=0 && targetIndex<nodes.size()) {
			Node<T> nodeSr = nodes.get(sourceIndex);
			Node<T> nodeTa = nodes.get(targetIndex);
			if (nodeSr.data!=null && nodeTa.data != null) {
			   if (!nodeSr.lEdges.contains(edge)) {
				   nodeSr.lEdges.add(edge);
				   nodes.set(sourceIndex,nodeSr); 
				   if (!directed) {//no dirigido
					  EDEdge<W> reverse = new EDEdge<W>(targetIndex,sourceIndex,edge.getWeight());
					  nodeTa.lEdges.add(reverse);
					  nodes.set(targetIndex, nodeTa);
				   }
				   return true;
			    }
			   else System.out.println("The graph has already this edge: "+edge.toString());
			}
		}
		return false;
	}
	
	@Override
	public EDEdge<W> getEdge (int source, int dest) {
		if (source <0 || source >= nodes.size()) return null;
		
			Node<T> node = nodes.get(source);
			if (node.data == null ) return null;
			for (EDEdge<W> edge: node.lEdges)
				if (edge.getTarget() == dest) return edge;
			
			return null;
	}
	
	
	
	@Override
	public EDEdge<W> removeEdge(int source, int target, W weight) {
		if (source <0 || source >= nodes.size() || target<0 || target >= nodes.size()) return null;
		if (nodes.get(source).data!=null && nodes.get(target).data!=null) {
			EDEdge<W> edge = new EDEdge<W>(source, target, weight);
			Node<T> node = nodes.get(source);
			int i = node.lEdges.indexOf(edge);
			if (i != -1) {
				edge = node.lEdges.remove(i);
				if (!directed) {
					EDEdge<W> reverse = new EDEdge<W>(target,source,weight);
					nodes.get(target).lEdges.remove(reverse);
				}
				return edge;
			}	
		}
		return null;	
	}

	@Override
	public T removeNode(int index) {
		if (index >=0 && index < nodes.size()){
			if (!directed) {
				Node<T> node = nodes.get(index);
				for (EDEdge<W> edge: node.lEdges ) {
					int target = edge.getTarget();
					W label = edge.getWeight();
					EDEdge<W> other = new EDEdge<W>(target,index,label);
					nodes.get(target).lEdges.remove(other);
				}
			}
			else { //directed
				for (int i=0; i<nodes.size(); i++) {
					if (i!=index && nodes.get(i).data !=null) {
						Node<T> node = nodes.get(i);
						for (EDEdge<W> edge: node.lEdges) {
							if (index == edge.getTarget()) //any weight/label
								node.lEdges.remove(edge);
						}
					}
				}
			}
			
			Node<T> node = nodes.get(index);
			node.lEdges.clear();
			T ret = node.data;
			node.data = null; //It is not remove, data is set to null
			nodes.set(index, node);
			size--;
			System.out.println("Borrada posicion: "+index);
			return ret;
		}
		return null;
	}
	
	/**  Set<Integer> getAdyacentNodes(index)
	 *  Devuelve el conjunto de nodos adyacentes al nodo de �ndice index
	 */
	@Override
	public Set<Integer> getAdyacentNodes(int index) {
		if (index < 0 || index >= nodes.size()) return new HashSet<Integer>();
		
		Set<Integer> ret = new HashSet<Integer>();
		for (EDEdge<W> ed: nodes.get(index).lEdges) {
			ret.add(ed.getTarget());
		}
		
		return ret;
	}
	
	
	/** int[] distanceToAll (T item)
	 * Devuelve la distancia (grados de separaci�n) del nodo con etiqueta item al resto de nodos.
	 * Devuelve el resultado en un vector con una posici�n por cada nodo.
	 * Si item no pertece al grafo, devuelve null
	 * Nota: Usar el recorrido en anchura
	 */
	@Override
	public int[] distanceToAll (T item) {
		
		int[] v = null;
		
		int index = getNodeIndex(item);
		
		if(index >= 0 && index < nodes.size() && nodes.get(index).data != null) {
			
			v = new int[nodes.size()];
			for(int i=0; i < v.length; i++) {
				v[i] = -1;
			}
			v[index] = 0;
			
			Queue<Integer> cola = new LinkedList<Integer>(); 
			cola.add(index);
			
			while(!cola.isEmpty()) {
			
				int n = cola.remove();
				int distancia = v[n]+1;
				
				for(EDEdge<W> e: nodes.get(n).lEdges) {
					int target = e.getTarget();
					T pers = getNodeValue(target);
					
					if( v[target] == -1) {
						v[target] = distancia;
						cola.add(target);						
					}
				}
			}
		}
		
		return v;
	}
	
	
	/** Set<T> common(T item1, T item2)
	 * Devuelve un conjunto con las etiquetas de los nodos comunes entre item1 e item2
	 * Si item1 o item2 no pertenecen al grafo, devuelve null
	 */
	public Set<T> common(T item1, T item2) {
		
		int persona1 = getNodeIndex(item1);
		int persona2 = getNodeIndex(item2);
		
		if( persona1 < 0 || persona2 < 0) {
			return null;
		}
		
		Set<T> comunes = new HashSet<T>();
		
		List<EDEdge<W>> amigos1 = nodes.get(persona1).lEdges;
		List<EDEdge<W>> amigos2 = nodes.get(persona2).lEdges;
		
		ListIterator<EDEdge<W>> iter = amigos1.listIterator();
		
		while(iter.hasNext()) {
			EDEdge<W> posibleColega = iter.next();
			int target = posibleColega.getTarget();
			
			for(EDEdge<W> e : amigos2){
				if(target == e.getTarget()){
					comunes.add(getNodeValue(target));
				}
			}
		}
		
		return comunes;
		
	}
	
	
	/** Set<T> suggest(T item)
	 * Devuelve un conjunto con las etiquetas de los nodos que est�n a distancia 2 del nodo
	 * con etiqueta item (es decir, sugiere amigos al nodo item, en funci�n de los amigos de este).
	 * Si item no est� en el grafo, devuelve null
	 */
	public Set<T> suggest(T item) {
		
		Set<T> sugeridos = null;
		
		if(getNodeIndex(item) >= 0){
			
			int[] posiblesAmigos = distanceToAll(item);
			sugeridos = new HashSet<T>();
			
			for(int i=0; i < posiblesAmigos.length; i++) {
				if(posiblesAmigos[i] == 2){
					sugeridos.add(getNodeValue(i));
				}
			}
		}
		
		return sugeridos;
		
	}
	
	/** T mostPopular()
	 * Devuelve la etiqueta del nodo con m�s arcos (m�s amigos). Si el grafo est� vac�o,
	 * devuelve null.
	 */
	public T mostPopular() {
		
		if(nodes.isEmpty()) {
			return null;
		}
		
		Iterator<Node<T>> iter = nodes.iterator();
		
		Node candidato = iter.next();
		Object persona = candidato.data;
		int cantidadAmigos = candidato.lEdges.size();
		//ListIterator<Node<T>> iter = listIterator<Node<T>>();
		
		while (iter.hasNext()){
			candidato = iter.next();
			int cantidadActual =  candidato.lEdges.size();
			
			if(cantidadAmigos < cantidadActual){
				persona = candidato.data;
				cantidadAmigos = cantidadActual;
			}
		}
		
		return (T) persona;
		
	}
	
	
	public void printGraphStructure() {
		//System.out.println("Vector size= " + nodes.length);
		System.out.println("Vector size " + nodes.size());
		System.out.println("Nodes: "+ this.getSize());
		for (int i=0; i<nodes.size(); i++) {
			System.out.print("pos "+i+": ");
	        Node<T> node = nodes.get(i);
			System.out.print(node.data+" -- ");
			Iterator<EDEdge<W>> it = node.lEdges.listIterator();
			while (it.hasNext()) {
					EDEdge<W> e = it.next();
					System.out.print("("+e.getSource()+","+e.getTarget()+", "+e.getWeight()+")->" );
			}
			System.out.println();
		}
	}
	
	
	@Override
	public void saveGraphStructure(RandomAccessFile f) {
		
		
			try {
				f.writeInt(this.size);
			 //numero de nodos
				//System.out.println("tama�o grafo "+this.size);
				//f.seek(0);
				//System.out.println("leido: "+f.readInt());
				for (int i=0; i<nodes.size();i++) {
				if (nodes.get(i)!=null) {
					f.writeUTF((String) nodes.get(i).data);
					f.writeInt(nodes.get(i).lEdges.size());
					for (EDEdge<W> edge: nodes.get(i).lEdges)
						f.writeInt(edge.getTarget());
				}
			}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	public Set<T> getNodes() {
		Set<T> s = new HashSet<T>();
		for (int i=0; i<nodes.size(); i++) {
			if (nodes.get(i).data!=null) 
				s.add(nodes.get(i).data);
		}
		return s;
	}


}
