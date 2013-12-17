package com.gmail.infomaniac50.droidbits;

import java.math.BigInteger;
import android.os.AsyncTask;
import randomX.randomHotBits;
import randomX.randomJava;
import randomX.randomLCG;
import randomX.randomLEcuyer;
import randomX.randomMCG;
import randomX.randomX;

public class RandomAsyncManager {
	public class AsyncResult {
		private byte[] bytes;
		private Exception ex;
		private boolean success;

		public AsyncResult(int length) {
			bytes = new byte[length];
		}

		public BigInteger getBigInteger() {
			return getBigInteger(false);
		}

		public BigInteger getBigInteger(boolean unsigned) {
			BigInteger number = new BigInteger(bytes);

			// Compensate for Java's averseness to unsigned number
			// crunching.
			if (unsigned) number = number.abs().shiftLeft(1);

			return number;
		}

		// public String getHexString() {
		//
		// }
		//
		// public String getDecimalString() {
		//
		// }
		//
		// public String getOctalString() {
		//
		// }

		public Exception getError() {
			return ex;
		}

		public boolean Success() {
			return success;
		}
	}

	public class AsyncSettings {
		private int length;
		private randomX randomizer;
		private String randomClassString = "";

		public void forgeRandomizer(String randomClassString) {
			// <item>MCG</item>
			// <item>LCG</item>
			// <item>LEcuyer</item>
			// <item>Java</item>
			// <item>HotBits</item>

			// "MCG"
			// "LCG"
			// "LEcuyer"
			// "Java"
			// "HotBits"
			
			if (randomClassString.compareTo(this.randomClassString) == 0)
				return;
			
			if (randomClassString.compareTo("MCG") == 0)
				settings.randomizer = new randomMCG();
			else if (randomClassString.compareTo("LCG") == 0)
				settings.randomizer = new randomLCG();
			else if (randomClassString.compareTo("LEcuyer") == 0)
				settings.randomizer = new randomLEcuyer();
			else if (randomClassString.compareTo("HotBits") == 0)
				settings.randomizer = new randomHotBits();
			else
				settings.randomizer = new randomJava();
		}

		public int getLength() {
			return length;
		}

		public void setLength(int length) {
			if (length > 0) this.length = length;
		}
	}

	private class RandomAsyncTask extends AsyncTask<AsyncSettings, Void, AsyncResult> {

		@Override
		protected AsyncResult doInBackground(AsyncSettings... params) {
			AsyncSettings settings = params[0];

			AsyncResult result = new AsyncResult(settings.length);
			randomX randomizer = settings.randomizer;

			try {
				for (int i = 0; i < settings.length; i++)
					result.bytes[i] = randomizer.nextByte();

				result.success = true;
			}
			catch (RuntimeException e) {
				result.success = false;
				result.ex = e;
			}

			return result;
		}

		@Override
		protected void onPostExecute(AsyncResult result) {
			if (callback != null) callback.Complete(result);
		}
	}

	private static RandomAsyncManager randomManager;

	static {
		randomManager = new RandomAsyncManager();
	}

	private AsyncCallback callback;

	private AsyncSettings settings;

	private RandomAsyncTask task;

	private RandomAsyncManager() {
		settings = new AsyncSettings();
	}

	public static RandomAsyncManager getInstance() {
		return randomManager;
	}

	public AsyncSettings getSettings() {
		return settings;
	}

	public void run() {
		task = new RandomAsyncTask();
		task.execute(settings);
	}

	public void setCallback(AsyncCallback callback) {
		this.callback = callback;
	}
}