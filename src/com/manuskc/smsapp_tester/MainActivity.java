package com.manuskc.smsapp_tester;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Button startButton;
	TestExecutor executor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		((TextView)findViewById(R.id.result)).setMovementMethod(new ScrollingMovementMethod());
		
		this.startButton = (Button) findViewById(R.id.start);		
		this.startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String buttonState = ((Button)findViewById(R.id.start)).getText().toString();
				if(getText(R.string.start_button_title).equals(buttonState)) {
					((Button)findViewById(R.id.start)).setText(R.string.abort_button_title);
					MainActivity.this.executor = new TestExecutor(getApplicationContext(), getWindow().getDecorView().findViewById(android.R.id.content));
					String url = ((TextView)findViewById(R.id.testFileURI)).getText().toString();
					MainActivity.this.executor.execute(url);
				} else {
					if(MainActivity.this.executor != null) {
						MainActivity.this.executor.cancel(true);
					}
					((Button)findViewById(R.id.start)).setText(R.string.on_cancel_button_title);
				}
				//else ignore : Button is in ON_CANCEL state
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
