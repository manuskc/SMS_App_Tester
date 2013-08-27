package com.manuskc.smsapp_tester;

public class Constants {
	public static class TEST_TAGS {
		public static final String TO_ADDRESS = "_to_"; //$NON-NLS-1$
		public static final String STATUS = "_status_"; //$NON-NLS-1$
		public static final String RESPONSE = "_response_message_"; //$NON-NLS-1$
		public static final String FROM = "_from_id_"; //$NON-NLS-1$
		public static final String LAST_RESPONSE = "_last_response_message_"; //$NON-NLS-1$
		public static final String SEND_MESSAGE = "_send_message_"; //$NON-NLS-1$
		public static final String MESSAGE_SENT_STATUS = "_message_sent_status_"; //$NON-NLS-1$
		public static final String MESSSAGE_DELIVERED_STATUS = "_message_delivered_status_"; //$NON-NLS-1$
		public static final String ID = "_id_"; //$NON-NLS-1$
		public static final String INFO = "_message_info_"; //$NON-NLS-1$
		public static final String CONTAIN_STRING = "_contains_string_"; //$NON-NLS-1$
		public static final String FROM_CONTAINS = "_from_contains_"; //$NON-NLS-1$
		public static final String TEST_TYPE = "_type_"; //$NON-NLS-1$
		public static final String TRANSACTION_TYPE = "_transaction_"; //$NON-NLS-1$
		public static final String RESPONSE_REGEX = "_response_regex_"; //$NON-NLS-1$
		public static final String LAST_RESPONSE_FROM = "_last_response_from_"; //$NON-NLS-1$
	}
	
	public static class SEND_MESSAGE {
		public static final int MAX_RETRIES = 3;
		public static final int RETRY_TIMEOUT_SECONDS = 60;
	}
	
	public static class SMS_RECEIVER_INTENT {
		public static final String SMS_SENT = "com.manuskc.smsapp_tester.sms.sent"; //$NON-NLS-1$
		public static final String SMS_DELIVERED = "com.manuskc.smsapp_tester.sms.delivered"; //$NON-NLS-1$
	}
}
