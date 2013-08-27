package com.manuskc.smsapp_tester.transaction_handler;

import android.content.Context;

import com.manuskc.smsapp_tester.testcase_executor.TestCaseExecutor;
import com.manuskc.smsapp_tester.testcase_executor.TestCaseExecutor.STATUS;

public class LastResponseTransactionHandler extends TransactionHandler{
	private static LastResponseTransactionHandler lastResponseTransactionHandler = new LastResponseTransactionHandler();

	private LastResponseTransactionHandler() {
		//hidden constructor
	}

	public static LastResponseTransactionHandler getHandler() {
		return lastResponseTransactionHandler;
	}
	
	@Override
	public void handleTransaction(Context baseContext, TestCaseExecutor observer) {
		if(!observer.executeTestIfRequestedMessage(observer.getLastResponseFrom(),observer.getLastResponse())) {
			observer.updateTestResult(STATUS.NOT_EXECUTED, observer.getLastResponseFrom(),observer.getLastResponse());
		}
	}

}
