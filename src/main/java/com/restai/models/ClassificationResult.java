package com.restai.models;

public class ClassificationResult {
	private ClassificationContent result;

	public ClassificationResult(ClassificationContent result) {
		this.result = result;
	}

	public ClassificationContent getResult() {
		return result;
	}

	public void setResult(ClassificationContent result) {
		this.result = result;
	}
}
