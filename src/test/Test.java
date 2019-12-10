package test;

import java.util.HashMap;
import java.util.Map;

public class Test {

	public static void main(String[] args) {
		Map<Integer,Integer> map = new HashMap<>();
		for(int i=0;i<200000;i++) {
			map.put(i, i);
		}
		
		for(int i:map.keySet()) {
			System.out.print(i+",");
		}
	}
}
