package com.gmail.infomaniac50.droidbits;

import randomX.*;
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
import com.gmail.infomaniac50.droidbits.R;

public class MainActivity extends Activity implements OnItemSelectedListener {
	private class RandomAsyncTask extends AsyncTask<randomX, Void, Byte> {
		@Override
		protected Byte doInBackground(randomX... params) {
			byte bytes = 0;
			randomX bits = params[0];
			try {
				bytes = bits.nextByte();
			}
			catch (RuntimeException e)
			{
				notifyToast(e.getMessage());
			}
			
			return bytes;
		}
		
		@Override
		protected void onPostExecute(Byte value) {
			randomString = MainActivity.toHexString(value);
			txtRandom.setText(randomString);
		}
	}
	
	public static String toHexString(byte bytes)
	{
		return toHexString(new byte[] {bytes});
	}
	
	public static String toHexString(byte[] bytes) {
		char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		
		char[] hexChars = new char[bytes.length * 2];
		
		int v;
		
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j*2] = hexArray[v/16];
			hexChars[j*2 + 1] = hexArray[v%16];
		}
		
		return new String(hexChars);
	}
	
	ClipboardManager clipper;
	TextView txtRandom;
	Spinner spnRandomSpinner;
	randomX randomizer;
	ArrayAdapter<CharSequence> adapter;
	String randomString;
	String randomClassString;
	
	public void notifyToast(CharSequence message)
	{
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	public void onBtnCopyRandom(View view) {
		clipper.setPrimaryClip(ClipData.newPlainText("Random Byte", randomString));
		notifyToast("Data copied to clipboard.");
	}

	public void onBtnFetchRandom(View view) {
		new RandomAsyncTask().execute(randomizer);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		txtRandom = (TextView)findViewById(R.id.txtRandom);
		spnRandomSpinner = (Spinner)findViewById(R.id.spnRandomChooser);
		
		adapter = ArrayAdapter.createFromResource(this, R.array.random_array, android.R.layout.simple_spinner_item);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnRandomSpinner.setAdapter(adapter);
		spnRandomSpinner.setOnItemSelectedListener(this);
		
		randomizer = new randomJava();
		
		clipper = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		randomClassString = "randomX.random" + spnRandomSpinner.getSelectedItem().toString();
		Class<?> randomType;

//      <item>MCG</item>
//      <item>LCG</item>
//      <item>LEcuyer</item>
//      <item>Java</item>
//      <item>HotBits</item>

		try {
		
			randomType = java.lang.Class.forName(randomClassString);
			randomizer = (randomX)randomType.newInstance();

		} catch (ClassNotFoundException e) {
			notifyToast("Class Not Found Error");
		} catch (InstantiationException e) {
			notifyToast("Class Instantiation Error");
		} catch (IllegalAccessException e) {
			notifyToast("Illegal Access Error");
		}
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		randomizer = new randomJava();
	}
}