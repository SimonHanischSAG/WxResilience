package wx.resilience.impl;

public class ErrorInfo {

	private String error;
	private String errorType;
	private String service;
	private String flowStep;
	
	public ErrorInfo(String error, String errorType, String service, String flowStep) {
		super();
		this.error = error;
		this.errorType = errorType;
		this.service = service;
		this.flowStep = flowStep;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getErrorType() {
		return errorType;
	}
	
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getFlowStep() {
		return flowStep;
	}

	public void setFlowStep(String flowStep) {
		this.flowStep = flowStep;
	}	
}
