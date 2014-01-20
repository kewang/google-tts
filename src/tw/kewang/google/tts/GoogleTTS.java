package tw.kewang.google.tts;

import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;

public class GoogleTTS {
	private static final String TRANSLATION_URL = "http://translate.google.com/translate_tts?tl=%s&q=%s&ie=UTF-8";

	private MediaPlayer player;
	private Context context;
	private String language;

	/**
	 * @param context
	 */
	public GoogleTTS(Context context) {
		this(context, (String) null);
	}

	/**
	 * @param context
	 * @param language
	 */
	public GoogleTTS(Context context, String language) {
		player = new MediaPlayer();

		this.context = context;

		if (language == null) {
			setLanguage(context.getResources().getConfiguration().locale);
		}
	}

	/**
	 * @param context
	 * @param language
	 */
	public GoogleTTS(Context context, Locale locale) {
		player = new MediaPlayer();

		this.context = context;

		if (language == null) {
			setLanguage(context.getResources().getConfiguration().locale);
		}
	}

	/**
	 * @param language
	 */
	public GoogleTTS setLanguage(String language) {
		this.language = language;

		return this;
	}

	/**
	 * @param locale
	 */
	public GoogleTTS setLanguage(Locale locale) {
		this.language = locale.getLanguage() + "-" + locale.getCountry();

		return this;
	}

	/**
	 * @param sentence
	 */
	public GoogleTTS say(String sentence) {
		say(sentence, false, null);

		return this;
	}

	/**
	 * @param sentence
	 * @param showLoading
	 */
	public GoogleTTS say(String sentence, boolean showLoading) {
		say(sentence, showLoading, null);

		return this;
	}

	/**
	 * @param sentence
	 * @param showLoading
	 * @param callback
	 */
	public GoogleTTS say(String sentence, boolean showLoading,
			final OnFinishListener callback) {
		player.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				player.reset();

				if (callback != null) {
					callback.onFinish();
				}
			}
		});

		new SpeakTask(sentence, showLoading).execute();

		return this;
	}

	private class SpeakTask extends AsyncTask<Void, Void, Void> {
		private ProgressDialog dialog;
		private String sentence;
		private boolean showLoading;

		public SpeakTask(String sentence, boolean showLoading) {
			this.sentence = sentence;
			this.showLoading = showLoading;
		}

		@Override
		protected void onPreExecute() {
			if (showLoading) {
				dialog = new ProgressDialog(context);

				dialog.setCancelable(false);
				dialog.setMessage("轉換中");

				dialog.show();
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				String url = String.format(TRANSLATION_URL, language, sentence);

				player.setAudioStreamType(AudioManager.STREAM_MUSIC);
				player.setDataSource(url);
				player.prepare();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}

			player.start();
		}
	}

	public interface OnFinishListener {
		public void onFinish();
	}
}
