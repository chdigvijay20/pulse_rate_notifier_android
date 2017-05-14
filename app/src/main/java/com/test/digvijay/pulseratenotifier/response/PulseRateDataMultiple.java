package com.test.digvijay.pulseratenotifier.response;

import java.util.ArrayList;
import java.util.List;

public class PulseRateDataMultiple {

	List<PulseRateData> pulseRateDataList = new ArrayList<PulseRateData>();
	
//	public PulseRateDataMultiple() {
//		System.out.println("inside set constr");
//		if(pulseRateDataList == null) {
//			System.out.println("inside init");
//			pulseRateDataList = new ArrayList<PulseRateData>();
//		}
//	}
	
//	public void add(PulseRateData pulseRateData) {
//		pulseRateDataList.add(pulseRateData);
//	}
	
	public void setPulseRateDataList(List<PulseRateData> pulseRateDataList) {
		System.out.println("inside set " + pulseRateDataList);
		this.pulseRateDataList = pulseRateDataList;
	}

	public List<PulseRateData> getPulseRateDataList() {
		return pulseRateDataList;
	}
}
