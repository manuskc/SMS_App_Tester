package com.manuskc.smsapp_tester.transaction_handler;

import java.util.HashMap;
import java.util.Map;

public class TransactionFactory {
	
	public static Map<String, TransactionHandler> handlers;
	
	static {
		handlers = new HashMap<String, TransactionHandler>();
		handlers.put("sms", SMSTransactionHandler.getHandler()); //$NON-NLS-1$
		handlers.put("last_response", LastResponseTransactionHandler.getHandler()); //$NON-NLS-1$
	}
	
	public static TransactionHandler getMeATransactionHandler(String handlerName) {
		return handlers.get(handlerName);
	}
}
