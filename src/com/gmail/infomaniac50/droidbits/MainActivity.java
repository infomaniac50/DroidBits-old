package com.gmail.infomaniac50.droidbits;

import java.math.BigInteger;
import java.util.Locale;

import randomX.randomJava;
import randomX.randomX;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private class RandomAsyncSettings {
		public randomX randomizer;
		public int length;
	}

	final int maxLength = 99;

	ArrayAdapter<CharSequence> spnRandomizerAdapter;
	ArrayAdapter<CharSequence> spnLengthAdapter;
	ArrayAdapter<CharSequence> spnNumberBaseAdapter;
	ArrayAdapter<CharSequence> spnDataMultiplierAdapter;

	ClipboardManager clipper;
	ClipData clipperData;

	RandomAsyncSettings settings;

	String randomString = "";
	BigInteger bigValue;

	// <string-array name="spnNumberBaseFormat">
	// <item>%d</item>
	// <item>%o</item>
	// <item>%02X</item>
	// </string-array>
	int[] numberBaseArray = new int[] { 10, 8, 16 };

	Spinner spnRandomizer;
	Spinner spnLength;
	Spinner spnNumberBase;
	Spinner spnDataMultiplier;

	TextView txtRandom;

	private void setRandomText() {
		// StringBuilder randomBuilder = new StringBuilder();

		// String numberBase =
		// numberBaseArray[spnNumberBase.getSelectedItemPosition()];

		// randomBuilder.append(String.format(numberBase, value));

		// randomString = randomBuilder.toString();

		randomString = bigValue.toString(
				numberBaseArray[spnNumberBase.getSelectedItemPosition()])
				.toUpperCase(Locale.US);
		txtRandom.setText(randomString);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		settings = new RandomAsyncSettings();
		clipper = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

		// Reference the needed controls
		txtRandom = (TextView) findViewById(R.id.txtRandom);

		spnRandomizer = (Spinner) findViewById(R.id.spnRandomizer);
		spnLength = (Spinner) findViewById(R.id.spnLength);
		spnNumberBase = (Spinner) findViewById(R.id.spnNumberBase);
		spnDataMultiplier = (Spinner) findViewById(R.id.spnDataMulitplier);

		spnRandomizerAdapter = ArrayAdapter.createFromResource(this,
				R.array.spnRandomizer, android.R.layout.simple_spinner_item);
		spnRandomizerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnRandomizer.setAdapter(spnRandomizerAdapter);
		spnRandomizer.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				setRandomizer("randomX.random"
						+ spnRandomizer.getSelectedItem().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				setRandomizer(randomJava.class.toString());
			}
		});

		// Make a new adapter for the number of bytes to fetch
		spnLengthAdapter = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_item);
		spnLengthAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// Populate the byte count spinner
		for (int i = 1; i <= maxLength; i++) {
			spnLengthAdapter.add(Integer.toString(i));
		}

		spnLength.setAdapter(spnLengthAdapter);
		spnLength.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				settings.length = Integer.parseInt(spnLength.getSelectedItem()
						.toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				settings.length = 1;
			}
		});

		spnDataMultiplierAdapter = ArrayAdapter
				.createFromResource(this, R.array.spnDataMultiplier,
						android.R.layout.simple_spinner_item);
		spnDataMultiplierAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spnDataMultiplier.setAdapter(spnDataMultiplierAdapter);

		// Setup the number base adapter
		spnNumberBaseAdapter = ArrayAdapter.createFromResource(this,
				R.array.spnNumberBase, android.R.layout.simple_spinner_item);
		spnNumberBaseAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spnNumberBase.setAdapter(spnNumberBaseAdapter);
		spnNumberBase.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				if (MainActivity.this.bigValue == null)
					return;
				setRandomText();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				if (MainActivity.this.bigValue == null)
					return;
				setRandomText();
			}

		});
	}

	public void onBtnCopyRandom(View view) {
		if (randomString.isEmpty() || clipperData != null
				&& clipperData.getItemAt(0).getText() == randomString)
			return;

		clipperData = ClipData.newPlainText("DroidBits", randomString);

		clipper.setPrimaryClip(clipperData);

		notifyToast("Data copied to clipboard.");
	}

	public void onBtnFetchRandom(View view) {

		new AsyncTask<RandomAsyncSettings, Void, BigInteger>() {
			@Override
			protected BigInteger doInBackground(RandomAsyncSettings... params) {
				RandomAsyncSettings settings = params[0];
				randomX randomizer = settings.randomizer;
				byte[] bytes = new byte[settings.length];

				try {
					for (int i = 0; i < settings.length; i++)
						bytes[i] = randomizer.nextByte();
				} catch (RuntimeException e) {
					notifyToast(e.getMessage());
				}

				return new BigInteger(bytes);
			}

			@Override
			protected void onPostExecute(BigInteger value) {
				MainActivity.this.bigValue = value;

				// Compensate for Java's averseness to unsigned number
				// crunching.
				bigValue = bigValue.abs().shiftLeft(1);
				setRandomText();
			}
		}.execute(settings);
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

	private void setRandomizer(String randomClassString) {
		Class<?> randomType;

		// <item>MCG</item>
		// <item>LCG</item>
		// <item>LEcuyer</item>
		// <item>Java</item>
		// <item>HotBits</item>

		try {
			randomType = java.lang.Class.forName(randomClassString);
			settings.randomizer = (randomX) randomType.newInstance();
		} catch (ClassNotFoundException e) {
			notifyToast(e.getMessage());
		} catch (InstantiationException e) {
			notifyToast(e.getMessage());
		} catch (IllegalAccessException e) {
			notifyToast(e.getMessage());
		}
	}

}