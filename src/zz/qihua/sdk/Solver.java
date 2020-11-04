package zz.qihua.sdk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;


public class Solver implements Runnable{
	private Matrix matrix;
	private int initIndex;
	private Phaser phaser;
	private boolean solverAll;
	public static AtomicInteger count=new AtomicInteger(0);
	public static AtomicInteger divide=new AtomicInteger(10);
	public static List<Matrix> container=Collections.synchronizedList(new ArrayList<Matrix>());
	
	public Solver(Phaser phaser,Matrix sdk,int initIndex) {
		this(phaser,false, sdk,initIndex);
	}
	public Solver(Phaser phaser,boolean solverAll,Matrix sdk,int initIndex) {
		matrix=sdk;
		this.initIndex=initIndex;
		this.solverAll=solverAll;
		this.phaser=phaser;
		phaser.register();
		int current=count.incrementAndGet();
		if(current%divide.get()==0) {
			divide.set(divide.get()*10);
			System.out.println(current+"\t"+System.currentTimeMillis());
		}
	}
	
	@Override
	public void run() {
		boolean illegal=false;
		Cube[] cubes=matrix.cubes;
		for(int i=initIndex;i<cubes.length;i++) {
			int val=cubes[i].val;
			if(val>0)
				continue;
			LinkedList<Integer> candidates=cubes[i].candidates;
			if(candidates.size()>1) {
				for(int vIndex=1;vIndex<candidates.size();vIndex++) {
					Matrix copy=matrix.clone();
					//如合法填充，开新线程进行并行校验
					boolean legal=copy.fill(cubes[i],candidates.get(vIndex));
					if(legal) {
						SDK.executor.execute(new Solver(phaser,solverAll,copy,i+1));
					}
				}
			}
			illegal=!matrix.fill(cubes[i],candidates.getFirst());
			if(illegal) {
//				System.out.println(matrix);
				phaser.arrive();
				return;	
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
