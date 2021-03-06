package de.hammacher.util;

import java.util.HashMap;
import java.util.Map;

public class IntegerMapMemory {

	public static void main(String[] args) {
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		//Map<Integer, Object> map = new IntegerMap<Object>();
		Object o = new Object();
		long startTime = System.currentTimeMillis();
		long lastTime = startTime;
		long lastMemory = 0;
		for (int i = 0; i < 20000000; ++i) {
			if (i % 1000000 == 0) {
				System.gc();
				long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				long time = System.currentTimeMillis();
				System.out.format("%10d, %4.1f sec: %7.2f MB (+%4.1f sec, +%5.2f MB)%n",
						i, 1e-3*(time - startTime), 1e-6*usedMemory, 1e-3*(time - lastTime), 1e-6*(usedMemory-lastMemory));
				lastMemory = usedMemory;
				lastTime = time;
			}
			map.put(i, o);
		}
	}

}
