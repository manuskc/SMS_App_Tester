package com.manuskc.smsapp_tester.transaction_handler;

import android.content.Context;

import com.manuskc.smsapp_tester.testcase_executor.TestCaseExecutor;

public abstract class TransactionHandler {
	public abstract void handleTransaction(Context baseContext , final TestCaseExecutor observer);
}
