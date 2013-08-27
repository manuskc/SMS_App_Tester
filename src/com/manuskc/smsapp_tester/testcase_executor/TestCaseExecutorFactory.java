package com.manuskc.smsapp_tester.testcase_executor;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.manuskc.smsapp_tester.Constants;

import android.util.Log;

public class TestCaseExecutorFactory {
	public static Map<String, Class<?>> observers;
	static {
		observers = new HashMap<String, Class<?>>();		
		observers.put("contains", TestForMessageContains.class); //$NON-NLS-1$
		observers.put("regex_matches", TestForRegexMatch.class); //$NON-NLS-1$
	}

	public static TestCaseExecutor getMeAnObserver(Map<String, String> test) {
		String type = test.get(Constants.TEST_TAGS.TEST_TYPE);
		if(observers.containsKey(type)) {
			try {
				return (TestCaseExecutor) observers.get(type).getConstructor(Map.class).newInstance(test);
			} catch (IllegalArgumentException e) {
				Log.e("TestSMSObserverFactory", e.getMessage()); //$NON-NLS-1$
			} catch (SecurityException e) {
				Log.e("TestSMSObserverFactory", e.getMessage()); //$NON-NLS-1$
			} catch (InstantiationException e) {
				Log.e("TestSMSObserverFactory", e.getMessage()); //$NON-NLS-1$
			} catch (IllegalAccessException e) {
				Log.e("TestSMSObserverFactory", e.getMessage()); //$NON-NLS-1$
			} catch (InvocationTargetException e) {
				Log.e("TestSMSObserverFactory", e.getMessage()); //$NON-NLS-1$
			} catch (NoSuchMethodException e) {
				Log.e("TestSMSObserverFactory", e.getMessage()); //$NON-NLS-1$
			}

		}
		return null;
	}
}
