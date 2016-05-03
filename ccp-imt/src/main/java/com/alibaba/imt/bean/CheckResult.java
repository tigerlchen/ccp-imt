package com.alibaba.imt.bean;

public class CheckResult<T> {

	private boolean passed;
	
	private T result;

	public boolean isPassed() {
		return passed;
	}

	public void setPassed(boolean passed) {
		this.passed = passed;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}
	
	
}
