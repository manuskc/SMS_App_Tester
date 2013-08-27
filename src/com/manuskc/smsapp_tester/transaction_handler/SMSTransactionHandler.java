package com.manuskc.smsapp_tester.transaction_handler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.util.Log;

import com.manuskc.smsapp_tester.Constants;
import com.manuskc.smsapp_tester.testcase_executor.TestCaseExecutor;
import com.manuskc.smsapp_tester.testcase_executor.TestCaseExecutor.STATUS;

public class SMSTransactionHandler extends TransactionHandler{
	private static SMSTransactionHandler smsTransactionHandler = new SMSTransactionHandler();

	private SMSTransactionHandler() {
		//hidden constructor
	}

	public static SMSTransactionHandler getHandler() {
		return smsTransactionHandler;
	}

	public static abstract class SMSReceivedCallback {
		abstract boolean call(String from, String message);
	}

	@Override
	public void handleTransaction(Context baseContext , final TestCaseExecutor observer) {
		final AtomicBoolean responseReceived = new AtomicBoolean(false);
		final AtomicBoolean transactionCompleted = new AtomicBoolean(false);
		final CountDownLatch latch = new CountDownLatch(1);

		SMSReceivedCallback onMessageReceivedCallback = new SMSReceivedCallback() {
			@Override
			public boolean call(String from, String message) {
				if(transactionCompleted.compareAndSet(false, false)) { //only if we have not cleaned up
					if(observer.executeTestIfRequestedMessage(from, message)) {
						if(responseReceived.compareAndSet(false, true)) {
							latch.countDown();
							return true;
						}
						return true;
					}
				}
				return false;
			}
		};

		while(Receiver.requestReceiver(observer, onMessageReceivedCallback)== null) { //Use a queue?
			try {
				Thread.sleep(1000);
				Log.w("RequestResponseHandler","Waiting for SMS Receiver."); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (InterruptedException e) {
				Log.e("RequestResponseHandler",e.getMessage()); //$NON-NLS-1$
			}
		}

		//Once we have registered a handle.
		int currentTry = 1;
		while(currentTry <= Constants.SEND_MESSAGE.MAX_RETRIES) {
			observer.setInfo("Sending message (" + currentTry + "/" + Constants.SEND_MESSAGE.MAX_RETRIES + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			currentTry += 1;
			Sender.getSender().sendSMS(observer.getToNumber(), observer.getSendMessge(), baseContext, observer);
			try {
				latch.await(Constants.SEND_MESSAGE.RETRY_TIMEOUT_SECONDS, TimeUnit.SECONDS);
				if(responseReceived.compareAndSet(false, false)) {
					//Try again;
					Receiver.expectDuplicateMessage();
					continue;
				}
				break;
			} catch (InterruptedException e) {
				Log.e("RequestResponseHandler",e.getMessage()); //$NON-NLS-1$
			}
		}

		//cleanup - when we do not receive any message
		if(responseReceived.compareAndSet(false, false) 
				&& transactionCompleted.compareAndSet(false, true)) {
			
			Receiver.expectDuplicateMessage(); // Known issue: Currently, No way to discard messages if none of them arrive in time
											   // Consider increasing timeout and retries.
											   // This happens only in case of no response received at all for a all the tries without timeout;
											   // Best way to avoid breaking tests is to have a "message pattern" defined in every test case that response message must conform to. 
			observer.updateTestResult(STATUS.NOT_EXECUTED, "-none-", "-none-"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
