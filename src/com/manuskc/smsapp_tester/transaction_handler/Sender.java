package com.manuskc.smsapp_tester.transaction_handler;

import com.manuskc.smsapp_tester.Constants;
import com.manuskc.smsapp_tester.testcase_executor.TestCaseExecutor;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

public class Sender extends BroadcastReceiver {
	private static Sender sender = new Sender();
	private static TestCaseExecutor observer;

	public Sender() {
		//default constructor
	}

	public static Sender getSender() {
		return sender;
	}

	@SuppressWarnings("static-method")
	public void sendSMS(String to, String message, Context baseContext, TestCaseExecutor theObserver) {
		SmsManager smsManager = SmsManager.getDefault();
		PendingIntent pendingMessageSentIntent = PendingIntent.getBroadcast(baseContext, 0, new Intent(Constants.SMS_RECEIVER_INTENT.SMS_SENT), 0);
		PendingIntent pendingMessageDeliveredIntent = PendingIntent.getBroadcast(baseContext, 0, new Intent(Constants.SMS_RECEIVER_INTENT.SMS_DELIVERED), 0);
		Sender.observer = theObserver;
		smsManager.sendTextMessage(to, null, message, pendingMessageSentIntent, pendingMessageDeliveredIntent);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if(Sender.observer == null) {
			Log.w("Sender", "No observer to receive pending intents."); //$NON-NLS-1$ //$NON-NLS-2$
			return; //No one the handle
		}
		String actionName = intent.getAction();
		if(Constants.SMS_RECEIVER_INTENT.SMS_SENT.equalsIgnoreCase(actionName)) {
			Log.w("Sender", "SMS sent, result = " + getResultCode()); //$NON-NLS-1$ //$NON-NLS-2$
			if(getResultCode() == Activity.RESULT_OK) {
				Sender.observer.setMessageSentStatus(true);
			} else {
				Sender.observer.setMessageSentStatus(false);
			}
		} else if ( Constants.SMS_RECEIVER_INTENT.SMS_DELIVERED.equalsIgnoreCase(actionName)) {
			Log.w("Sender", "SMS delivered, result = " + getResultCode()); //$NON-NLS-1$ //$NON-NLS-2$
			if(getResultCode() == Activity.RESULT_OK) {
				Sender.observer.setMessageDeliveredStatus(true);
			} else {
				Sender.observer.setMessageDeliveredStatus(false);
			}
		}
	}
}
