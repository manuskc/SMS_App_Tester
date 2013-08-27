package com.manuskc.smsapp_tester;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.manuskc.smsapp_tester.lib.CsvParser;
import com.manuskc.smsapp_tester.testcase_executor.TestCaseExecutor;
import com.manuskc.smsapp_tester.testcase_executor.TestCaseExecutorFactory;

public class TestExecutor extends AsyncTask<String, String, String>{

	private Context context;
	private View rootView;

	public TestExecutor(Context thecontext, View theRootView) {
		this.context = thecontext;
		this.rootView = theRootView;
	}

	@Override
	protected void onPreExecute() {
		this.rootView.findViewById(R.id.testFileURI).setEnabled(false);
		TextView resultView = (TextView) this.rootView.findViewById(R.id.result); 
		resultView.setText("Started..."); //$NON-NLS-1$
	}

	@Override
	protected String doInBackground(String... params) {
		System.setProperty("http.keepAlive", "false");  //$NON-NLS-1$//$NON-NLS-2$
		if(isCancelled()) return null;
		publishProgress(new String[] {"Downloading test file from " + params[0]+" ..."}); //$NON-NLS-1$ //$NON-NLS-2$
		if(isCancelled()) return null;

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(params[0]);
			HttpResponse response = httpClient.execute(request);

			while(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				Header[] headers = response.getHeaders("Location");	             //$NON-NLS-1$
				if (headers != null && headers.length != 0) {
					String newUrl = headers[headers.length - 1].getValue();
					request = new HttpGet(newUrl);
					response = httpClient.execute(request);
				} else {
					break;
				}
			}

			final HttpEntity entity = response.getEntity();
			StringBuffer testFileData = new StringBuffer();


			if (entity != null) {
				InputStream inputStream = null;
				InputStreamReader inputStreamReader = null;
				try {
					inputStream = entity.getContent();
					char[] data = new char[1024];
					int count;
					inputStreamReader = new InputStreamReader(inputStream);
					while ((count = inputStreamReader.read(data)) != -1) {
						testFileData.append(new String(data, 0, count));
					}
					inputStreamReader.close();
				} finally {
					if(inputStreamReader != null) {
						inputStreamReader.close();
					}
					if (inputStream != null) {
						inputStream.close();  
					}
					entity.consumeContent();
				}
			}

			publishProgress(new String[] {"Download complete.","Preparing test cases"}); //$NON-NLS-1$ //$NON-NLS-2$
			if(isCancelled()) return null;

			String lines[] = CsvParser.getLines(testFileData.toString());
			testFileData = null; //save memory
			int lineIndex = 0;
			for(String line : lines) {
				lineIndex += 1;
				String [] columns = CsvParser.getColumns(line);
				if(columns == null || columns.length == 0 || !"START".equalsIgnoreCase(columns[0])) { //$NON-NLS-1$
					continue;
				}
				break;
			}

			List<String> titles = new ArrayList<String>();
			//Read column title
			for(; lineIndex < lines.length; lineIndex +=1) {
				if(lines[lineIndex].length() <= 0) {
					continue; //ignore empty lines
				}
				String columns[] = CsvParser.getColumns(lines[lineIndex]);
				for(String column : columns) {
					titles.add(column.toLowerCase(Locale.US).trim());
				}
				lineIndex += 1;
				break;
			}
			publishProgress(new String[] {"Analyzing data"}); //$NON-NLS-1$
			if(isCancelled()) return null;

			List<Map<String, String>> testCases = new ArrayList<Map<String,String>>();
			//Read tests
			int testCaseIndex = 0;
			for(; lineIndex < lines.length; lineIndex +=1) {
				if(lines[lineIndex].length() <= 0) {
					continue; //ignore empty lines
				}
				Map<String,String> testCase = new HashMap<String, String>();
				String columns[] = CsvParser.getColumns(lines[lineIndex]);
				if(columns.length !=  titles.size()) {
					publishProgress(new String[] {"ERROR","Invalid number of columns in test case",lines[lineIndex],"Skipping to next test"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					continue;
				}
				testCaseIndex += 1;
				int i = 0;
				for(String column : columns) {
					testCase.put(titles.get(i),column);
					i+=1;
				}
				testCase.put(Constants.TEST_TAGS.ID, String.valueOf(testCaseIndex));
				testCases.add(testCase);
			}

			lines = null; // free memory
			publishProgress(new String[] {testCaseIndex + "Test(s) loaded"}); //$NON-NLS-1$
			if(isCancelled()) return null;

			//start executing test cases
			String lastResponse = "-none-"; //$NON-NLS-1$
			String lastResponseFrom = "-none-"; //$NON-NLS-1$
			for(Map<String, String> test : testCases) {
				if(isCancelled()) return null;
				//Need one more transaction handlers that executes test on previous result;
				//Need a dependency chain management logic
				try {
					TestCaseExecutor testCaseExecutor = TestCaseExecutorFactory.getMeAnObserver(test);
					if(testCaseExecutor != null) {
						testCaseExecutor.setLastResponse(lastResponse);
						testCaseExecutor.setLastResponseFrom(lastResponseFrom);
						testCaseExecutor.getTransactionHandler().handleTransaction(this.context, testCaseExecutor);
						publishProgress(testCaseExecutor.getResult());
						lastResponse = testCaseExecutor.getResponseMessage();
						lastResponseFrom = testCaseExecutor.getResponseFrom();
					} else {
						publishProgress(new String[] {"Invalid test type : " + test.get(Constants.TEST_TAGS.TEST_TYPE) , "Not executing test id : " + test.get(Constants.TEST_TAGS.ID)}); //$NON-NLS-1$ //$NON-NLS-2$
					}
				} catch (Exception e) {
					publishProgress(new String[] {e.getMessage()});
					Log.e("TestExecutor", e.getMessage() != null ? e.getMessage() : "null"); //$NON-NLS-1$ //$NON-NLS-2$
				}								
			}
		} catch (MalformedURLException e) {
			Log.e("TestExecutor", e.getMessage() != null ? e.getMessage() : "null"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (IOException e) {
			Log.e("TestExecutor", e.getMessage() != null ? e.getMessage() : "null"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return "COMPLETED"; //$NON-NLS-1$
	}

	@Override
	protected void onProgressUpdate(String... values) {
		TextView resultView = (TextView) this.rootView.findViewById(R.id.result);
		String resultProgress = ""; //$NON-NLS-1$
		for(String value : values) {
			resultProgress += "\n"+value; //$NON-NLS-1$
		}
		resultView.setText(resultView.getText()+resultProgress);
	}

	@Override
	protected void onPostExecute(String result) {
		TextView resultView = (TextView) this.rootView.findViewById(R.id.result);
		if(result != null) { //Normal flow completion
			resultView.setText(resultView.getText()+"\n----------\nCOMPLETED\n"); //$NON-NLS-1$
		} else {
			resultView.setText(resultView.getText()+"\n----------\nSTOPPED\n"); //$NON-NLS-1$
		}

		//TODO: Add summary of test results

		this.rootView.findViewById(R.id.testFileURI).setEnabled(true);
		Button button = (Button) this.rootView.findViewById(R.id.start);
		button.setText(R.string.start_button_title);
	}

	@Override
	protected void onCancelled() {
		TextView resultView = (TextView) this.rootView.findViewById(R.id.result); 
		resultView.setText(resultView.getText()+"\nABORTING TESTS..."); //$NON-NLS-1$
	}
}
