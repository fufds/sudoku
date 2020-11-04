package zz.qihua.sdk;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;



public class SDK {

	public static ThreadPoolExecutor executor=new ThreadPoolExecutor(4, 4, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	public static void main(String[] args) {
		int[][] src=new int[9][];
		src[0]=new int[] {0,0,5,3,0,0,0,0,0};
		src[1]=new int[] {8,0,0,0,0,0,0,2,0};
		src[2]=new int[] {0,7,0,0,1,0,5,0,0};
		src[3]=new int[] {4,0,0,0,0,5,3,0,0};
		src[4]=new int[] {0,1,0,0,7,0,0,0,6};
		src[5]=new int[] {0,0,3,2,0,0,0,8,0};
		src[6]=new int[] {0,6,0,5,0,0,0,0,9};
		src[7]=new int[] {0,0,4,0,0,0,0,3,0};
		src[8]=new int[] {0,0,0,0,0,9,7,0,0};
		
		long start=System.currentTimeMillis();
		Matrix matrix=new Matrix(src);
		Phaser phaser=new Phaser();
		executor.execute(new SmartSolver(phaser,true,matrix));
		phaser.awaitAdvance(0);
		long end=System.currentTimeMillis();

		System.out.println("cost:"+(end-start)+"ms¡¢"+SmartSolver.count+"tasks, get "+SmartSolver.container.size()+" SDK matrixs");

	}

}
