package zz.qihua.sdk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;


public class SmartSolver implements Runnable{
	private Matrix matrix;
	private Phaser phaser;
	private boolean solverAll;
	public static AtomicInteger count=new AtomicInteger(0);
	public static List<Matrix> container=Collections.synchronizedList(new ArrayList<Matrix>());
	
	public SmartSolver(Phaser phaser,Matrix sdk) {
		this(phaser,false, sdk);
	}
	public SmartSolver(Phaser phaser,boolean solverAll,Matrix sdk) {
		matrix=sdk;
		this.solverAll=solverAll;
		this.phaser=phaser;
		phaser.register();
		int current=count.incrementAndGet();
	
	}
	
	@Override
	public void run() {
		Map<Integer,List<Cube>> groupMap=matrix.groupCube();
		while(!groupMap.isEmpty()) {
			for(int i:matrix.raw) {
				if(groupMap.get(i)!=null) {
					Cube cube=groupMap.get(i).get(0);
					Integer head=cube.candidates.removeFirst();
					for(Integer candidate:cube.candidates) {
						Matrix copy=matrix.clone();
						if(copy.fullUpdate(copy.rows[cube.rowIndex].cubes[cube.colIndex], candidate)) {
							SDK.executor.execute(new SmartSolver(phaser,solverAll, copy));
						}
					}
					if(!matrix.fullUpdate(cube, head)) {
						phaser.arrive();
						return;
					}
					break;
				}
			}
			groupMap=matrix.groupCube();
		}
		
		container.add(matrix);
//		if(SDK.executor.getQueue().size()==0) {
//			System.out.println("预估合法数独量="+container.size());
//		}
		phaser.arrive();
		if(!solverAll) {
			SDK.executor.setRejectedExecutionHandler((t,e)->{phaser.arrive();});
			SDK.executor.shutdown();
		}
	}
}
