package zz.qihua.sdk;

import java.util.Arrays;
import java.util.Collections;
import java.util.IntSummaryStatistics;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.PrimitiveIterator.OfInt;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Matrix {
	int d;
	Cube[] cubes;
	CubeGroup[] rows;
	CubeGroup[] columns;
	CubeGroup[] blocks;
	int len;
	int[] raw;
	
	public Matrix(int[][] initRows) {
		int l=initRows.length;
		d=(int)Math.sqrt(l);
		raw=new int[l] ;
		for(int i=0;i<l;i++) {
			raw[i]=i+1;
		}
		Cube[] initCubes=new Cube[l*l];
		for(int i=0;i<l;i++) {
			for(int j=0;j<l;j++) {
				int index=i*l+j;
				initCubes[index]=new Cube(i, j, i/d*d+j/d, initRows[i][j],raw);
			}
		}
		init(initCubes);

	}
	private Matrix() {
		// TODO Auto-generated constructor stub
	}
	private void init(Cube[] initCubes) {
		cubes=initCubes;
		len=(int)Math.sqrt(initCubes.length);
		rows=new CubeGroup[len];
		columns=new CubeGroup[len];
		blocks=new CubeGroup[len];
		for(int i=0;i<len;i++) {
			rows[i]=new CubeGroup(len);
			columns[i]=new CubeGroup(len);
			blocks[i]=new CubeGroup(len);
		}
		
		d=(int)Math.sqrt(len);
		for(int i=0;i<len;i++) {
			for(int j=0;j<len;j++) {
				int index=i*len+j;
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
	}
	public static Matrix genMatrix(int dimension,boolean initblock) {
		int length=dimension*dimension;
		int[][] initRows=new int[length][length];
		if(initblock) {
			for(int block=0;block<length;block++) {
				OfInt intIterator=IntStream.rangeClosed(1, length).unordered().iterator();
				for(int index=0;index<length;index++) {
					int i=index/dimension+block%dimension*dimension;
					int j=index%dimension+block%dimension*dimension;
					initRows[i][j]=intIterator.nextInt();
				}
			}
		}
		
		return new Matrix(initRows);
	}
	
	public Matrix clone() {
		Matrix copy=new Matrix();
		Cube[] initCubes=new Cube[cubes.length];
		for(int i=0;i<cubes.length;i++) {
			if(cubes[i].val>0) {
				initCubes[i]=cubes[i];
			}else {
				initCubes[i]=new Cube(cubes[i].rowIndex,cubes[i].colIndex,cubes[i].blockIndex,cubes[i].val,raw);
			}
		}
		copy.raw=raw;
		copy.init(initCubes);
		return copy;
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
	
	/**
	 * 按方格节点的拥有的分支节点数量进行分组
	 * @return
	 */
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
		return Stream.concat(Stream.concat(Arrays.stream(rows), Arrays.stream(columns)),Arrays.stream(blocks)).
				allMatch(g->g.SDKGroup(false));
	}
	
	
	@Override
	public String toString() {
		StringBuilder buffer=new StringBuilder();
		for(CubeGroup group:rows) {
			buffer.append("[");
			for(Cube c:group.cubes) {
				if(c.val<10)
					buffer.append(0);
				buffer.append(c.val).append(",");
			}
			buffer.deleteCharAt(buffer.length()-1).append("]").append("\n");
		}
		return buffer.toString();
	}
}
