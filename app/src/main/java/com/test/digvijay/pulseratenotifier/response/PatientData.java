package com.test.digvijay.pulseratenotifier.response;

import java.util.ArrayList;
import java.util.List;

/**
 * @author digvijay
 *
 */
/**
 * @author digvijay
 *
 */
public class PatientData {
	
	private String deviceId;
	private Long dateOfBirth;
	private String gender;
	private List<String> emergencyContacts;
	private List<String> emergencyFirebaseTokens;
	
	List<PulseRateData> pulseRateDataList = new ArrayList<PulseRateData>();
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public Long getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Long dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}

	public List<String> getEmergencyContacts() {
		return emergencyContacts;
	}

	public void setEmergencyContacts(List<String> emergencyContacts) {
		this.emergencyContacts = emergencyContacts;
	}

	public List<String> getEmergencyFirebaseTokens() {
		return emergencyFirebaseTokens;
	}
	
	public void setEmergencyFirebaseTokens(List<String> emergencyFirebaseTokens) {
		this.emergencyFirebaseTokens = emergencyFirebaseTokens;
	}

	public List<PulseRateData> getPulseRateDataList() {
		return pulseRateDataList;
	}

	public void setPulseRateDataList(List<PulseRateData> pulseRateDataList) {
		this.pulseRateDataList = pulseRateDataList;
	}
}
