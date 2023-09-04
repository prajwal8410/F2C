package com.F2C.jwt.mongodb.models;

import lombok.Data;

@Data
public class CCToQCReq {
	String requestId;
	String cropId;
	String farmerId;
	String assignedCCId;
	String assignedQCId;
	
	String handledCC;
	String handledQC;
	boolean isHandledByCC;
	boolean isHandledByQC;
	
	public boolean getIsHandledByCC() {
		return isHandledByCC;
	}
	public boolean getIsHandledByQC() {
		return isHandledByQC;
	}
	public void setIsHandledByCC(boolean isHandledByCC) {
		this.isHandledByCC=isHandledByCC;
	}
	public void setIsHandledByQC(boolean isHandledByQC) {
		this.isHandledByQC=isHandledByQC;
	}
	
}
