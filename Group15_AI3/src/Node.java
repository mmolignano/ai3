import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Node {

	private int charcode;
	private int streampos;
	private int nxtchar;
	private int k;
	private ArrayList<DistanceVector> edges;
	
	public Node (int cc, int pos, int k){
		this.charcode = cc;
		this.streampos = pos;
		this.nxtchar = -1;
		this.k = k;
		this.edges = new ArrayList<DistanceVector>();
	}
	
	public void connect (List<Node> nodes){
		for (Node n : nodes){
			if (n != this){
				DistanceVector dv = new DistanceVector();
				dv.dest = n;
				dv.source = this;
				dv.length = (int) Math.ceil(Math.sqrt(Math.pow(Math.abs(this.charcode - n.getCharCode()),2) +
								  			Math.pow(Math.abs(this.streampos - n.getStreamPos()),2)));
				n.addEdge(dv);
				this.addEdge(dv);
			}
		}
	}
	
	public int getCharCode(){
		return charcode;
	}
	
	public int getStreamPos(){
		return streampos;
	}
	
	public int getNxtChar(){
		return nxtchar;
	}
	
	public void setNxtChar(int charcode){
		this.nxtchar = charcode;
	}
	
	public void addEdge(DistanceVector dv){
		if (edges.size() >= k){
			int maxdist = -1;
			int index = -1;
			for (DistanceVector dvit : this.edges){
				if (dvit.length > maxdist){
					maxdist = dvit.length;
					index = this.edges.indexOf(dvit);
				}
			}
			if (maxdist > -1 && dv.length < maxdist){
				this.edges.remove(index);
				this.edges.add(dv);
			}
		}else{
			this.edges.add(dv);
		}
	}
	
	public int predict(){
		HashMap<Integer,Integer> fmap = new HashMap<Integer,Integer>();
		for (DistanceVector dv : this.edges){
			Node iAfterE = null;
			if (dv.dest == this){
				iAfterE = dv.source;
			}else{
				iAfterE = dv.dest;
			}
			if (fmap.containsKey(iAfterE.getCharCode())){
				fmap.put(iAfterE.getCharCode(),fmap.get(iAfterE.getCharCode()) + 1);
			}else{
				fmap.put(iAfterE.getCharCode(),1);
			}
		}
		int m = -1;
		int occur = 0;
		for (Integer i : fmap.keySet()){
			if (fmap.get(i) > occur){
				m = i.intValue();
				occur = fmap.get(i).intValue();
			}
		}
		return m;
	}
	
}
