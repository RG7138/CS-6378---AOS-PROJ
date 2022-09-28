package HelperPackage;
import java.util.LinkedList;
import java.util.Queue;

public class SpanningTreeHelperClass {
	
	static int[] parent;
	
	public static int getParent(int id) {
		return parent[id];
	}
	
	public static void constructNodeTree(int[][] Mt){
		boolean[] visited = new boolean[Mt.length];
		parent = new int[Mt.length];
		Queue<Integer> queue = new LinkedList<Integer>();
		queue.add(0);
		parent[0] = 0;
		//If its already visited then no need to visit again since its done in bfs tree , nodes 
		//visited at first level will have direct parents and so on
		visited[0] = true;
		while(!queue.isEmpty()){
			int node = queue.remove();
			for(int i=0;i<Mt[node].length;i++){
				if(Mt[node][i] == 1 && visited[i] == false){
					queue.add(i);
					SpanningTreeHelperClass.parent[i] = node;
					visited[i] = true;
				}
			}
		}
	}
}
