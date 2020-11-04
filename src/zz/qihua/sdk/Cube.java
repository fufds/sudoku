package zz.qihua.sdk;

import java.util.LinkedList;

public class Cube {
	int rowIndex;
	int colIndex;
	int blockIndex;
	int val;
	
	LinkedList<Integer> candidates=new LinkedList<>();
	
	public Cube() {
		// TODO Auto-generated constructor stub
	}
	public Cube(int r,int c,int b) {
		this(r,c,b,0,null);
	}
	public Cube(int r,int c,int b,int v,int[] candidates) {
		rowIndex=r;
		colIndex=c;
		blockIndex=b;
		val=v;
		if(candidates!=null) {
			for(int i:candidates){
				this.candidates.add(i);
			}
		}
	}
	
	@Override
	public String toString() {

		return new StringBuilder("(").append(rowIndex).append(",").append(colIndex).append(",").append(blockIndex).append(")")
				.append("=").append(val).append(",")
				.append(candidates)
				.toString();
	}
}
