package com.test.digvijay.pulseratenotifier.response;

public class PulseRateData {

    private String deviceId;
	private Integer pulseRateCount;
	private Long pulseRateLogTime;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Integer getPulseRateCount() {
		return pulseRateCount;
	}
	
	public void setPulseRateCount(Integer pulseRateCount) {
		this.pulseRateCount = pulseRateCount;
	}

	public Long getPulseRateLogTime() {
		return pulseRateLogTime;
	}
	
	public void setPulseRateLogTime(Long pulseRateLogTime) {
		this.pulseRateLogTime = pulseRateLogTime;
	}
	
}
