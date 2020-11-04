package zz.qihua.sdk;

import java.util.LinkedList;

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
}
