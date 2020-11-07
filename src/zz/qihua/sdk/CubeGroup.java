package zz.qihua.sdk;

import java.util.Arrays;
import java.util.IntSummaryStatistics;

public class CubeGroup {
	Cube[] cubes;
	
	public CubeGroup(int size) {
		cubes=new Cube[size];
	
	}

	public boolean fullUpdate(Integer val) {
		for(Cube cube:cubes) {
			if(cube.val==0) {
				cube.candidates.remove(val);
				if(cube.candidates.size()==0)
					return false;
			}
		}
		return true;
	}

	public boolean updateCandidates(int index,Integer val) {
		cubes[index].val=val;
		for(index++;index<cubes.length;index++) {
			if(cubes[index].val>0)
				continue;
			cubes[index].candidates.remove(val);
			if(cubes[index].candidates.isEmpty())
				return false;
		}
		return true;
	}
	
	public Cube getCube(int index) {
		return cubes[index];
	}
	
	public boolean SDKGroup(boolean ignoreZero) {
		if(ignoreZero) {
			int[] testArray=new int[cubes.length];
			for(int i=0;i<cubes.length;i++) {
				int index=cubes[i].val-1;
				testArray[index]++;
				if(testArray[index]>1)
					return false;
			}
		}else {
			IntSummaryStatistics statitics=Arrays.stream(cubes).mapToInt(c->c.val).distinct().summaryStatistics();
			if(statitics.getCount()!=cubes.length||statitics.getMax()>cubes.length||statitics.getMin()<1)
				return false;
		}
		
		return true;
	}
}
