package com.gmail.infomaniac50.droidbits;

import java.math.BigInteger;
import java.util.Locale;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.infomaniac50.droidbits.RandomAsyncManager.AsyncResult;

public class MainActivity extends Activity {
	private class RandomAsyncCallback implements AsyncCallback {
		public void Complete(AsyncResult result) {
			if (result.Success()) {
				randomNumber = result.getBigInteger(true);
				setRandomText();
			}
			else {
				notifyToast(result.getError().getMessage());
			}
		}
	}

	private RandomAsyncCallback callback;
	private ClipboardManager clipper;
	private ClipData clipperData;
	private RandomAsyncManager manager;
	private static final int maxLength = 99;
	private static final Integer[] spnLengthArray = new Integer[maxLength];
	
	static {
		// Populate the byte count spinner
		for (Integer i = 1; i <= maxLength; i++) {
			spnLengthArray[i - 1] = i;
		}
	}
	
	private int[] numberBaseArray = new int[] {
			10, 8, 16
	};
	private BigInteger randomNumber;
	private String randomString = "";
	private Spinner spnDataMultiplier;

	private ArrayAdapter<CharSequence> spnDataMultiplierAdapter;
	private Spinner spnLength;

	private ArrayAdapter<Integer> spnLengthAdapter;
	private Spinner spnNumberBase;
	private ArrayAdapter<CharSequence> spnNumberBaseAdapter;
	private Spinner spnRandomizer;

	private ArrayAdapter<CharSequence> spnRandomizerAdapter;

	private TextView txtRandom;

	public void onBtnCopyRandom(View view) {
		if (randomString.isEmpty() || clipperData != null && clipperData.getItemAt(0).getText() == randomString)
			return;

		clipperData = ClipData.newPlainText("DroidBits", randomString);

		clipper.setPrimaryClip(clipperData);

		notifyToast("Data copied to clipboard.");
	}

	public void onBtnFetchRandom(View view) {
		manager.run();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void notifyToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	private void setRandomText() {
		if (randomNumber != null) {
			randomString = randomNumber.toString(numberBaseArray[spnNumberBase.getSelectedItemPosition()]).toUpperCase(Locale.US);
			txtRandom.setText(randomString);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		manager = RandomAsyncManager.getInstance();
		callback = new RandomAsyncCallback();
		manager.setCallback(callback);

		clipper = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

		// Reference the needed controls
		txtRandom = (TextView) findViewById(R.id.txtRandom);

		spnRandomizer = (Spinner) findViewById(R.id.spnRandomizer);
		spnLength = (Spinner) findViewById(R.id.spnLength);
		spnNumberBase = (Spinner) findViewById(R.id.spnNumberBase);
		spnDataMultiplier = (Spinner) findViewById(R.id.spnDataMulitplier);

		spnRandomizerAdapter = ArrayAdapter.createFromResource(this, R.array.spnRandomizer, android.R.layout.simple_spinner_item);
		spnRandomizerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnRandomizer.setAdapter(spnRandomizerAdapter);
		spnRandomizer.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				manager.getSettings().forgeRandomizer(spnRandomizer.getSelectedItem().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				manager.getSettings().forgeRandomizer("");
			}
		});

		// Make a new adapter for the number of bytes to fetch
		spnLengthAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, spnLengthArray);
		spnLengthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spnLength.setAdapter(spnLengthAdapter);
		spnLength.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				manager.getSettings().setLength((Integer)(spnLength.getSelectedItem()));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				manager.getSettings().setLength(1);
			}
		});

		spnDataMultiplierAdapter = ArrayAdapter.createFromResource(this, R.array.spnDataMultiplier, android.R.layout.simple_spinner_item);
		spnDataMultiplierAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spnDataMultiplier.setAdapter(spnDataMultiplierAdapter);

		// Setup the number base adapter
		spnNumberBaseAdapter = ArrayAdapter.createFromResource(this, R.array.spnNumberBase, android.R.layout.simple_spinner_item);
		spnNumberBaseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spnNumberBase.setAdapter(spnNumberBaseAdapter);
		spnNumberBase.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				setRandomText();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				setRandomText();
			}

		});
	}

}