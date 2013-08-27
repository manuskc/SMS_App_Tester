package com.manuskc.smsapp_tester.transaction_handler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.manuskc.smsapp_tester.lib.DamerauLevenshteinAlgorithm;
import com.manuskc.smsapp_tester.testcase_executor.TestCaseExecutor;
import com.manuskc.smsapp_tester.transaction_handler.SMSTransactionHandler.SMSReceivedCallback;

public class Receiver extends BroadcastReceiver{

	private static AtomicBoolean isReceiverAvailable = new AtomicBoolean(true);
	private static Receiver receiver = new Receiver();
	private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED"; //$NON-NLS-1$
	private static AtomicInteger duplicateMessageCounter = new AtomicInteger(0);
	private static SMSReceivedCallback onMessageReceivecallback;
	private static List<String> cacheMessages = new ArrayList<String>();
	private static DamerauLevenshteinAlgorithm distanceCalculator = new DamerauLevenshteinAlgorithm(1, 1, 1000, 1, true);

	public Receiver() {
		//
	}

	public static Receiver requestReceiver(TestCaseExecutor observer, SMSReceivedCallback onMessageReceivedCallback) {
		if(isReceiverAvailable.compareAndSet(true, false)) {
			onMessageReceivecallback = onMessageReceivedCallback;
			return receiver;
		}
		return null;
	}	

	@Override
	public void onReceive(Context context, Intent intent) {

		Bundle extras = intent.getExtras();
		if (extras == null) {
			return;
		}

		String action = intent.getAction();

		if(ACTION_SMS_RECEIVED.equalsIgnoreCase(action)) {

			SmsMessage[] smsMessages = getMessagesFromIntent(intent);
			if (smsMessages != null) {
				String sender = "", message = ""; //$NON-NLS-1$ //$NON-NLS-2$

				for (int i = 0; i < smsMessages.length; i++) {
					sender = smsMessages[i].getOriginatingAddress();
					message += smsMessages[i].getMessageBody().toString();
					message += "\n"; //$NON-NLS-1$
				}

				if(sender.length() > 0) {
					if(duplicateMessageCounter.get() <= 0 
							|| !duplicateMessage(sender,message)) {
						cacheMessage(sender,message);

						if(onMessageReceivecallback != null) {
							try {
								if(onMessageReceivecallback.call(sender, message)) {
									isReceiverAvailable.set(true);
								}
							} catch (Exception e) {
								Log.e("Receiver",e.getMessage()); //$NON-NLS-1$
							}
						}
					}
				}
			}
		}


	}

	public static SmsMessage[] getMessagesFromIntent(Intent intent) {
		Object[] messages = (Object[]) intent.getSerializableExtra("pdus"); //$NON-NLS-1$
		byte[][] pduObjs = new byte[messages.length][];

		for (int i = 0; i < messages.length; i++) {
			pduObjs[i] = (byte[]) messages[i];
		}
		byte[][] pdus = new byte[pduObjs.length][];
		int pduCount = pdus.length;
		SmsMessage[] msgs = new SmsMessage[pduCount];
		for (int i = 0; i < pduCount; i++) {
			pdus[i] = pduObjs[i];
			msgs[i] = SmsMessage.createFromPdu(pdus[i]);
		}
		return msgs;
	}

	//Duplicate message handling
	public static void expectDuplicateMessage() {
		duplicateMessageCounter.incrementAndGet();
	}

	private static boolean duplicateMessage(String sender, String message) {
		for(String cacheMessage : cacheMessages) {
			if(distanceCalculator.computeDistance(cacheMessage, sender+message.substring(0, message.length() > 50 ? 50 : message.length())) < 5) {
				//Might be duplicate message!
				duplicateMessageCounter.decrementAndGet();
				return true;
			}
		}
		return false;
	}

	private static void cacheMessage(String sender, String message) {
		cacheMessages.add(sender+message.substring(0, message.length() > 50 ? 50 : message.length()));
	}

}
