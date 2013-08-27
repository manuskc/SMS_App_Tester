package com.manuskc.smsapp_tester.testcase_executor;

import java.util.Locale;
import java.util.Map;

import com.manuskc.smsapp_tester.Constants;

public class TestForMessageContains extends TestCaseExecutor{

	private String findString;
	private String fromAddressContains;
	private String to;
	private String sendMessage;

	public TestForMessageContains(Map<String, String> testCase) throws MissingFieldException {
		super(testCase);
		//String to, String sendMessage, String findString, String fromAddressRegex
		this.findString = testCase.get(Constants.TEST_TAGS.CONTAIN_STRING);
		this.fromAddressContains = testCase.get(Constants.TEST_TAGS.FROM_CONTAINS);
		this.to = testCase.get(Constants.TEST_TAGS.TO_ADDRESS);
		this.sendMessage = testCase.get(Constants.TEST_TAGS.SEND_MESSAGE);
		if(this.findString == null 
				|| this.fromAddressContains == null
				|| this.to == null
				|| this.sendMessage == null) {
			String message = "Missing fields for TestForMessageContains ( id : " + getIdString() +")\n"; //$NON-NLS-1$ //$NON-NLS-2$
			if(this.findString == null) message += Constants.TEST_TAGS.CONTAIN_STRING + "\n"; //$NON-NLS-1$
			if(this.fromAddressContains == null) message += Constants.TEST_TAGS.FROM_CONTAINS + "\n"; //$NON-NLS-1$
			if(this.to == null) message += Constants.TEST_TAGS.TO_ADDRESS + "\n"; //$NON-NLS-1$
			if(this.sendMessage == null) message += Constants.TEST_TAGS.SEND_MESSAGE + "\n"; //$NON-NLS-1$
			throw new MissingFieldException(message);
		}
	}

	@Override
	public boolean isRequestedMessage(String from, String message) {
		if(from.toLowerCase(Locale.US).contains(this.fromAddressContains.toLowerCase(Locale.US))) {
			return true;
		}
		return false;
	}

	@Override
	boolean executeTest(String from, String message) {
		if(message.contains(this.findString)) {
			return true;
		}
		return false;
	}

	@Override
	public String getSendMessge() {
		return this.sendMessage;
	}

	@Override
	public String getToNumber() {
		return this.to;
	}

	@Override
	public String getResult() {
		return getTestCase().get(Constants.TEST_TAGS.ID) + ":" + getTestCase().get(Constants.TEST_TAGS.TEST_TYPE) + "\n" + getTestCase().get(Constants.TEST_TAGS.STATUS) + "\n-----\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
