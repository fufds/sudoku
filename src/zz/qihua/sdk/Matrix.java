package zz.qihua.sdk;

import java.util.Arrays;
import java.util.Collections;
import java.util.IntSummaryStatistics;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;

public class Matrix {
	int d;
	Cube[] cubes;
	CubeGroup[] rows;
	CubeGroup[] columns;
	CubeGroup[] blocks;
	int len;
	int[] raw;
	
	public Matrix(int[][] initRows) {
		len=initRows.length;
		rows=new CubeGroup[len];
		columns=new CubeGroup[len];
		blocks=new CubeGroup[len];
		for(int i=0;i<len;i++) {
			rows[i]=new CubeGroup(len);
			columns[i]=new CubeGroup(len);
			blocks[i]=new CubeGroup(len);
		}
		raw=new int[len] ;
		for(int i=0;i<len;i++) {
			raw[i]=i+1;
		}
		d=(int)Math.sqrt(len);
		cubes=new Cube[len*len];
		for(int i=0;i<len;i++) {
			for(int j=0;j<len;j++) {
				int index=i*len+j;
				cubes[index]=new Cube(i, j, i/d*d+j/d, initRows[i][j],raw);
				rows[i].cubes[j]=cubes[index];
				columns[j].cubes[i]=cubes[index];
				blocks[i/d*d+j/d].cubes[i%d*d+j%d]=cubes[index];
			}
		}
		for(Cube cube:cubes) {
			if(cube.val>0)
				continue;
			LinkedList<Integer> list=cube.candidates;
			
			for(Cube c:rows[cube.rowIndex].cubes) 
				if(c.val>0)
					list.remove(new Integer(c.val));
			for(Cube c:columns[cube.colIndex].cubes) 
				if(c.val>0)
					list.remove(new Integer(c.val));
			for(Cube c:blocks[cube.blockIndex].cubes)
				if(c.val>0)
					list.remove(new Integer(c.val));			
		}
//		System.out.println("end");
	}
	
	public Matrix clone() {
		int[][] initRows=new int[len][len];
		for(int i=0;i<len;i++) {
			for(int j=0;j<len;j++) {
				initRows[i][j]=cubes[i*len+j].val;
			}
		}
		return new Matrix(initRows);
	}
	
	/**
	 * true：有效填充，false：无效填充
	 * @param cube
	 * @param val
	 * @return
	 */
	public boolean fill(Cube cube,Integer val) {
		int i=cube.rowIndex;
		int j=cube.colIndex;
		int k=cube.blockIndex;
		
		if(!rows[i].updateCandidates(j, val))
			return false;
		if(!columns[j].updateCandidates(i, val))
			return false;
		if(!blocks[k].updateCandidates(i%d*d+j%d, val))
			return false;
		return true;
	}
	
	public Map<Integer,List<Cube>> groupCube(){
		return Arrays.stream(cubes).filter(cube->cube.val==0)
				.collect(Collectors.groupingBy(cube->cube.candidates.size()));
	}
	
	public boolean fullUpdate(Cube cube,Integer val) {
		int i=cube.rowIndex;
		int j=cube.colIndex;
		int k=cube.blockIndex;
		
		cube.val=val;
		if(!rows[i].fullUpdate(val))
			return false;
		if(!columns[j].fullUpdate(val))
			return false;
		if(!blocks[k].fullUpdate(val))
			return false;
		return true;
	}
	
	public Cube[] getCubes() {
		return cubes;
	}
	public CubeGroup[] getBlocks() {
		return blocks;
	}
	public CubeGroup[] getColumns() {
		return columns;
	}
	public CubeGroup[] getRows() {
		return rows;
	}
	
	public boolean SDKMatrix() {
		IntSummaryStatistics expect=Arrays.stream(raw).summaryStatistics();
		for(CubeGroup group:rows) {
			if(!validation(group, expect))
				return false;
		}
		for(CubeGroup group:columns) {
			if(!validation(group, expect))
				return false;
		}
		for(CubeGroup group:blocks) {
			if(!validation(group, expect))
				return false;
		}
		return true;
	}
	
	private boolean validation(CubeGroup group,IntSummaryStatistics expect) {
		IntSummaryStatistics statistics=Arrays.stream(group.cubes).mapToInt(cube->cube.val).summaryStatistics();
		return statistics.toString().equals(expect.toString());
	}
	
	@Override
	public String toString() {
		StringBuilder buffer=new StringBuilder();
		for(CubeGroup group:rows) {
//			buffer.append(Arrays.toString(group.cubes)).append("\n");
			buffer.append("[");
			for(Cube c:group.cubes) {
				buffer.append(c.val).append(",");
			}
			buffer.deleteCharAt(buffer.length()-1).append("]").append("\n");
		}
		return buffer.toString();
	}
}
