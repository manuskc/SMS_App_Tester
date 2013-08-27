package com.manuskc.smsapp_tester.testcase_executor;

import java.util.Map;
import java.util.Map.Entry;

import com.manuskc.smsapp_tester.Constants;
import com.manuskc.smsapp_tester.transaction_handler.TransactionFactory;
import com.manuskc.smsapp_tester.transaction_handler.TransactionHandler;

public abstract class TestCaseExecutor {
	private Map<String,String> testCase;
	
	public enum STATUS {
		PASSED("Passed"),FAILED("FAILED"),NOT_EXECUTED("NOT EXECUTED"),; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		private String statusMessage;
		
		private STATUS(String theStatusMessage) {
			this.statusMessage = theStatusMessage;
		}
		
		public String getStatusMessage() {
			return this.statusMessage;
		}
	}

	public static class MissingFieldException extends Exception {
		private static final long serialVersionUID = -6413836915801497591L;

		public MissingFieldException(String message) {
			super(message);
		}
	}

	public TestCaseExecutor(Map<String,String>theTestCase) throws MissingFieldException {
		this.setTestCase(theTestCase);
		//Must have ID
		boolean isError = false;
		String errorMessage = "Invalid test case - Missing mandatory field(s): "; //$NON-NLS-1$
		if(theTestCase.get(Constants.TEST_TAGS.ID) == null) {
			errorMessage += "ID"; //$NON-NLS-1$
			isError = true;
		}
		if(!TestCaseExecutorFactory.observers.containsKey(theTestCase.get(Constants.TEST_TAGS.TEST_TYPE))) {
			errorMessage += ", "+Constants.TEST_TAGS.TEST_TYPE; //$NON-NLS-1$
			isError = true;
		}
		if(!TransactionFactory.handlers.containsKey(theTestCase.get(Constants.TEST_TAGS.TRANSACTION_TYPE))) {
			errorMessage += ", "+Constants.TEST_TAGS.TRANSACTION_TYPE; //$NON-NLS-1$
			isError = true;
		}
		if(isError) {
			throw new MissingFieldException(errorMessage);
		}
	}

	public final boolean executeTestIfRequestedMessage(String from, String message) {
		if(isRequestedMessage(from, message)) {
			boolean status = executeTest(from,message);
			updateTestResult(status ? STATUS.PASSED : STATUS.FAILED, from, message);
			return true;
		}
		return false;
	}
	
	public final TransactionHandler getTransactionHandler() {
		return TransactionFactory.getMeATransactionHandler(this.testCase.get(Constants.TEST_TAGS.TRANSACTION_TYPE));
	}

	abstract boolean executeTest(String from, String message);
	abstract boolean isRequestedMessage(String from, String message);
	abstract public String getSendMessge();
	abstract public String getToNumber();

	public void updateTestResult(STATUS status, String from, String message) {
		this.getTestCase().put(Constants.TEST_TAGS.STATUS, status.getStatusMessage());
		this.getTestCase().put(Constants.TEST_TAGS.RESPONSE, message);
		this.getTestCase().put(Constants.TEST_TAGS.FROM, from);
	}

	public void setMessageSentStatus(boolean status) {
		this.getTestCase().put(Constants.TEST_TAGS.MESSAGE_SENT_STATUS, 
				this.getTestCase().get(Constants.TEST_TAGS.MESSAGE_SENT_STATUS) != null 
				? this.getTestCase().get(Constants.TEST_TAGS.MESSAGE_SENT_STATUS) + "|" + String.valueOf(status)  //$NON-NLS-1$
						: String.valueOf(status)); 
	}

	public void setMessageDeliveredStatus(boolean status) {
		this.getTestCase().put(Constants.TEST_TAGS.MESSSAGE_DELIVERED_STATUS, 
				this.getTestCase().get(Constants.TEST_TAGS.MESSSAGE_DELIVERED_STATUS) != null 
				? this.getTestCase().get(Constants.TEST_TAGS.MESSSAGE_DELIVERED_STATUS) + "|" + String.valueOf(status) //$NON-NLS-1$
						: String.valueOf(status)); 
	}

	public String getIdString() {
		return this.getTestCase().get(Constants.TEST_TAGS.ID); 
	}

	public void setInfo(String info) {
		String currentInfo = this.getTestCase().get(Constants.TEST_TAGS.INFO); 
		if(currentInfo == null || currentInfo.length() <= 0) {
			this.getTestCase().put(Constants.TEST_TAGS.INFO, info); 
		} else {
			this.getTestCase().put(Constants.TEST_TAGS.INFO, currentInfo+"\n"+info); //$NON-NLS-1$ 
		}
	}

	public Map<String,String> getTestCase() {
		return this.testCase;
	}

	private void setTestCase(Map<String,String> theTestCase) {
		this.testCase = theTestCase;
	}
	
	public String getResult() {
		String result = "----------\nResult for : " + getTestCase().get(Constants.TEST_TAGS.ID) + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
		for(Entry<String, String> entry : getTestCase().entrySet()) {
			result += entry.getKey().trim() + " : " + entry.getValue().trim() + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return result;
	}
	
	public String getResponseMessage() {
		return this.testCase.get(Constants.TEST_TAGS.RESPONSE);
	}
	
	public String getResponseFrom() {
		return this.testCase.get(Constants.TEST_TAGS.FROM);
	}
	
	public String setLastResponse(String response) {
		return this.testCase.put(Constants.TEST_TAGS.LAST_RESPONSE,response);
	}
	
	public String getLastResponse() {
		return this.testCase.get(Constants.TEST_TAGS.LAST_RESPONSE);
	}
	
	public String setLastResponseFrom(String responseFrom) {
		return this.testCase.put(Constants.TEST_TAGS.LAST_RESPONSE_FROM,responseFrom);
	}
	
	public String getLastResponseFrom() {
		return this.testCase.get(Constants.TEST_TAGS.LAST_RESPONSE_FROM);
	}
}
