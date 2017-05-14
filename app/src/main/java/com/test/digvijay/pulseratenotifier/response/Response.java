package com.test.digvijay.pulseratenotifier.response;

import java.util.List;

public class Response {
	
	private String status;
	private Data data;
	private PulseRateDataMultiple pulseRateDataMultiple;
	private List<PulseRateData> pulseRateDataList;
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

    public PulseRateDataMultiple getPulseRateDataMultiple() {
        return pulseRateDataMultiple;
    }

    public void setPulseRateDataMultiple(PulseRateDataMultiple pulseRateDataMultiple) {
        this.pulseRateDataMultiple = pulseRateDataMultiple;
    }

	public List<PulseRateData> getPulseRateDataList() {
		return pulseRateDataList;
	}

	public void setPulseRateDataList(List<PulseRateData> pulseRateDataList) {
		this.pulseRateDataList = pulseRateDataList;
	}

    
}
