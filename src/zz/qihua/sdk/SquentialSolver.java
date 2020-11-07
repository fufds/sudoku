package zz.qihua.sdk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;


public class SquentialSolver implements Runnable{
	private Matrix matrix;
	private Phaser phaser;
	private boolean solverAll;
	public static AtomicInteger count=new AtomicInteger(0);
	public static List<Matrix> container=Collections.synchronizedList(new ArrayList<Matrix>());
	
	public SquentialSolver(Phaser phaser,Matrix sdk) {
		this(phaser,false, sdk);
	}
	public SquentialSolver(Phaser phaser,boolean solverAll,Matrix sdk) {
		matrix=sdk;
		this.solverAll=solverAll;
		this.phaser=phaser;
		phaser.register();
		count.incrementAndGet();
	}
	
	@Override
	public void run() {
		for(Cube cube:matrix.cubes) {
			if(cube.val==0) {
				Integer head=cube.candidates.removeFirst();
				for(Integer candidate:cube.candidates) {
					Matrix copy=matrix.clone();
					if(copy.fullUpdate(copy.rows[cube.rowIndex].cubes[cube.colIndex], candidate)) {
						SDK.executor.execute(new SquentialSolver(phaser,solverAll, copy));
					}
				}
				if(!matrix.fullUpdate(cube, head)) {
					phaser.arrive();
					return;
				}
			}
			
		}
		
		container.add(matrix);
		phaser.arrive();
		if(!solverAll) {
			SDK.executor.setRejectedExecutionHandler((t,e)->{phaser.arrive();});
			SDK.executor.shutdown();
		}
	}
}
