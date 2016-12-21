package org.aksw.beast.examples;

import java.util.Arrays;

import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;

public class HardwareMetadata {
	public static void main(String[] args) {

    	SystemInfo si = new SystemInfo();
    	System.out.println(si.getOperatingSystem());
    	System.out.println(si.getOperatingSystem().getVersion());
    	System.out.println(si.getOperatingSystem().getFamily());
    	System.out.println(si.getOperatingSystem().getManufacturer());
    	System.out.println(si.getHardware().getProcessor());
    	System.out.println(si.getHardware().getProcessor().getPhysicalProcessorCount());
    	System.out.println(si.getHardware().getProcessor().getLogicalProcessorCount());
    	System.out.println(si.getHardware().getMemory().getTotal());
    	for(HWDiskStore store : si.getHardware().getDiskStores()) {
    		System.out.println(Arrays.asList(store.getModel(), store.getName(), store.getSerial(), store.getWrites()));
    	}

	}
}
