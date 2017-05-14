package com.test.digvijay.pulseratenotifier.response;

import java.util.List;

public class DoctorData {
	private List<String> patientsNameList;
	private List<String> patientsEmailList;
	
	public List<String> getPatientsNameList() {
		return patientsNameList;
	}
	
	public void setPatientsNameList(List<String> patientsNameList) {
		this.patientsNameList = patientsNameList;
	}
	
	public List<String> getPatientsEmailList() {
		return patientsEmailList;
	}
	
	public void setPatientsEmailList(List<String> patientsEmailList) {
		this.patientsEmailList = patientsEmailList;
	}
		
}
