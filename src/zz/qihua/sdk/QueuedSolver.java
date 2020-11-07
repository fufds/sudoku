package zz.qihua.sdk;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class QueuedSolver {
	
	
	public static List<Matrix> solve(Matrix matrix,boolean solveAll) {
		List<Matrix> sdkList=new ArrayList<Matrix>();
		Queue<Matrix> taskQueue=new LinkedList<Matrix>();
		while(matrix!=null) {
			Map<Integer,List<Cube>> groupMap=matrix.groupCube();
			while(!groupMap.isEmpty()) {
				boolean errorMatrix=false;
				for(int i:matrix.raw) {
					if(groupMap.get(i)!=null) {
						Cube cube=groupMap.get(i).get(0);
						Integer head=cube.candidates.removeFirst();
						for(Integer candidate:cube.candidates) {
							Matrix copy=matrix.clone();
							if(copy.fullUpdate(copy.rows[cube.rowIndex].cubes[cube.colIndex], candidate)) {
								taskQueue.add(copy);
							}
						}
						if(!matrix.fullUpdate(cube, head)) 
							errorMatrix=true;
						
						break;
					}
				}
				if(errorMatrix) 
					break;
				
				groupMap=matrix.groupCube();
			}
			if(matrix.SDKMatrix()) {
				sdkList.add(matrix);
				if(!solveAll)
					return sdkList;
			}
			matrix=taskQueue.poll();
		}
		
		return sdkList;
	}

}
