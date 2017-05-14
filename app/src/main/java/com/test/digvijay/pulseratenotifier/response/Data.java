package com.test.digvijay.pulseratenotifier.response;

import java.util.List;

public class Data {
	
	private UserData userData;
	private PatientData patientData;
	private ErrorData errorData;
	private PulseRateData pulseRateData;
	private PulseRateDataMultiple pulseRateDataMultiple;
	private List<String> invalidEmails;
	private NormalRestingHeartRateData normalRestingHeartRateData;
	private DoctorData doctorData;
	
	public UserData getUserData() {
		return userData;
	}
	
	public void setUserData(UserData userData) {
		this.userData = userData;
	}

	public PatientData getPatientData() {
		return patientData;
	}

	public void setPatientData(PatientData patientData) {
		this.patientData = patientData;
	}

	public ErrorData getErrorData() {
		return errorData;
	}

	public void setErrorData(ErrorData errorData) {
		this.errorData = errorData;
	}

	public PulseRateData getPulseRateData() {
		return pulseRateData;
	}

	public void setPulseRateData(PulseRateData pulseRateData) {
		this.pulseRateData = pulseRateData;
	}

	public PulseRateDataMultiple getPulseRateDataMultiple() {
		return pulseRateDataMultiple;
	}

	public void setPulseRateDataMultiple(PulseRateDataMultiple pulseRateDataMultiple) {
		this.pulseRateDataMultiple = pulseRateDataMultiple;
	}

	public List<String> getInvalidEmails() {
		return invalidEmails;
	}
	
	public void setInvalidEmails(List<String> invalidEmails) {
		this.invalidEmails = invalidEmails;
	}

	public NormalRestingHeartRateData getNormalRestingHeartRateData() {
		return normalRestingHeartRateData;
	}

	public void setNormalRestingHeartRateData(NormalRestingHeartRateData normalRestingHeartRateData) {
		this.normalRestingHeartRateData = normalRestingHeartRateData;
	}

	public DoctorData getDoctorData() {
		return doctorData;
	}

	public void setDoctorData(DoctorData doctorData) {
		this.doctorData = doctorData;
	}
	
}
