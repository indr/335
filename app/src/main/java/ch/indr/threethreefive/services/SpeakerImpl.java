/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.services;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static android.speech.tts.TextToSpeech.Engine.KEY_PARAM_STREAM;
import static android.speech.tts.TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID;
import static android.speech.tts.TextToSpeech.Engine.KEY_PARAM_VOLUME;

public class SpeakerImpl implements Speaker {

  private static final int ADD = TextToSpeech.QUEUE_ADD;
  private static final int FLUSH = TextToSpeech.QUEUE_FLUSH;

  private final Resources resources;
  private final TextToSpeech textToSpeech;

  private boolean isInitialized = false;
  private boolean started = true;
  private ArrayList<OnInitListener> onInitListeners = new ArrayList<>();
  private BehaviorSubject<Integer> status = BehaviorSubject.create();

  private PublishSubject<String> utteranceStart = PublishSubject.create();
  private PublishSubject<String> utteranceDone = PublishSubject.create();
  private PublishSubject<String> utteranceError = PublishSubject.create();

  private Speech queue = null;

  private float baseSpeechRate = 1.0f;

  public SpeakerImpl(final @NonNull Context context) {
    Timber.d("SpeakerImpl() %s", this.toString());

    this.resources = context.getResources();
    this.textToSpeech = new TextToSpeech(context, new TtsInitListener());


    final float defaultSpeechRate = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.TTS_DEFAULT_RATE, 100) / 100f;
    Timber.d("Default tts speech rate %f, %s", defaultSpeechRate, this.toString());
    // Set base speech rate to a minimum of 0.6f
    this.baseSpeechRate = Math.max(0.6f, defaultSpeechRate < 0.2f ? baseSpeechRate : defaultSpeechRate);

    Timber.d("Default tts engine %s, %s", textToSpeech.getDefaultEngine(), this.toString());
    // Make Google TTS 10% slower
    if ("com.google.android.tts".equals(textToSpeech.getDefaultEngine())) {
      this.baseSpeechRate *= 0.9f;
    }

//    if (BuildConfig.ANSWERS) {
//      final CustomEvent customEvent = new CustomEvent("Text-to-Speech")
//          .putCustomAttribute("Default engine", textToSpeech.getDefaultEngine())
//          .putCustomAttribute("Default speech rate", defaultSpeechRate)
//          .putCustomAttribute("Base speech rate", baseSpeechRate);
//      Answers.getInstance().logCustom(customEvent);
//    }

    textToSpeech.setOnUtteranceProgressListener(new TtsUtteranceListener());
  }

  @Override public void start() {
    Timber.d("start %s", this.toString());

    this.started = true;
  }

  @Override public void stop() {
    Timber.d("stop %s", this.toString());

    this.started = false;
    textToSpeech.stop();
  }

  public void shutdown() {
    Timber.d("shutdown %s", this.toString());

    textToSpeech.stop();
    textToSpeech.shutdown();
  }

  /**
   * Adds/registers a listener to be called when TTS is status.
   * Listeners onInit() method is called immediately if TTS is already status.
   */
  public void addOnInitListener(OnInitListener listener) {

    if (isInitialized) {
      listener.onInit();
      return;
    }
    onInitListeners.add(listener);
  }

  public void removeOnInitListener(OnInitListener listener) {
    onInitListeners.remove(listener);
  }

  @Override @NonNull public CommandSpeaker command() {
    return new CommandSpeakerImpl(this);
  }

  @Override @NonNull public InstructionsSpeaker instructions() {
    return new InstructionsSpeakerImpl(this, this.resources);
  }

  @Override public Observable<Integer> status() {
    return status;
  }

  @Override public Observable<String> utteranceStart() {
    return utteranceStart;
  }

  @Override public Observable<String> utteranceDone() {
    return utteranceDone;
  }

  @Override public Observable<String> utteranceError() {
    return utteranceError;
  }

  @Nullable @Override public String sayUrgent(CharSequence speech) {
    return say(speech, LEVEL_URGENT);
  }

  @Nullable @Override public String sayUrgent(CharSequence speech, float rate) {
    return say(speech, LEVEL_URGENT, rate);
  }

  @Nullable @Override public String sayUrgent(int resourceId) {
    return say(resourceId, LEVEL_URGENT);
  }

  @Nullable @Override public String sayQueued(CharSequence speech) {
    return say(speech, LEVEL_QUEUED);
  }

  @Nullable @Override public String sayQueued(CharSequence speech, float rate) {
    return say(speech, LEVEL_QUEUED, rate);
  }

  @Nullable @Override public String sayQueued(int resourceId) {
    return say(resourceId, LEVEL_QUEUED);
  }

  @Nullable @Override public String sayIdle(CharSequence speech) {
    return say(speech, LEVEL_IDLE);
  }

  @Nullable @Override public String say(CharSequence text, int level) {
    return say(text, level, SPEECH_RATE_NORMAL);
  }

  @Nullable private String say(CharSequence text, int level, float rate) {
    Timber.d("say level %d, text %s, %s", level, text, this.toString());

    if (!started) {
      Timber.d("Speaker is stopped");
      return null;
    }

    switch (level) {
      case LEVEL_URGENT:
        return speak(text, FLUSH, rate);
      case LEVEL_QUEUED:
        return speak(text, ADD, rate);
//        if (isSpeaking(LEVEL_URGENT)) {
//          enqueue(text, level);
//        } else {
//          speak(text, FLUSH);
//        }
      case LEVEL_IDLE:
        if (isIdle()) {
          return speak(text, ADD, rate);
        } else {
          Timber.d("Dropping level %d, text %s, %s", level, text, this.toString());
          return null;
        }
      default:
        return speak(text, ADD, rate);
    }
  }

  private String say(int resourceId, int level) {
    return say(resources.getString(resourceId), level);
  }

  /**
   * Returns true if a text is being spoken with less or equal the specified level.
   */
  private boolean isSpeaking(int level) {
    Timber.d("isSpeaking level=%d, %s", level, this.toString());
    Speech queue = this.queue;

    Timber.d(queue == null ? "queue == null" : "queue != null, queued level=" + queue.getLevel());
    return queue != null && queue.getLevel() <= level;
  }

  /**
   * Adds text and level to the queue, ready to spoken after the current utterance.
   */
  private void enqueue(CharSequence text, int level) {
    Timber.d("enqueue level %d, text %s, %s", level, text, this.toString());

    this.queue = new Speech(text, level, SPEECH_RATE_NORMAL);
  }

  /**
   * Removes and speaks the queued item.
   */
  private void dequeue() {
    Speech queue = this.queue;
    if (queue == null) {
      return;
    }
    this.queue = null;

    Timber.d("dequeue level %d, text %s, %s", queue.getLevel(), queue.getText(), this.toString());

    speak(queue.getText(), ADD, queue.getRate());
  }

  /**
   * Returns true if nothing is being spoken.
   */
  private boolean isIdle() {
    return !textToSpeech.isSpeaking();
  }

  /**
   * Speaks the specified text. Queue mode is deprecated and should always be FLUSH.
   */
  private String speak(CharSequence text, int queueMode, float rate) {
    if (text == null || text.length() == 0) return null;
    return speak(text.toString(), queueMode, rate);
  }

  /**
   * Speaks the specified text. Queue mode is deprecated and should always be FLUSH.
   */
  private String speak(String text, int queueMode, float rate) {
    if (text == null || text.isEmpty()) return null;

    if (!isInitialized) {
      Timber.w("speak called before initialization, dropping %s, %s", text, this.toString());
      return null;
    }

    final float speechRate = baseSpeechRate * rate;
    Timber.d("speak %s, %s, %f, %s", queueMode == FLUSH ? "flushed" : "queued", text, speechRate, this.toString());

    final String utteranceId = getNextUtteranceId();
    final HashMap<String, String> params = getSpeakParams(utteranceId);
    textToSpeech.setSpeechRate(speechRate);
    textToSpeech.speak(text, queueMode, params);

    return utteranceId;
  }

  @NonNull private HashMap<String, String> getSpeakParams(final String utteranceId) {
    final HashMap<String, String> params = new HashMap<>();
    params.put(KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
    params.put(KEY_PARAM_UTTERANCE_ID, utteranceId);
    params.put(KEY_PARAM_VOLUME, String.valueOf(1.0f));
    return params;
  }

  private long lastUtteranceId = 0;

  private String getNextUtteranceId() {
    return String.valueOf(++lastUtteranceId);
  }

  interface OnInitListener {
    void onInit();
  }

  private class TtsInitListener implements TextToSpeech.OnInitListener {

    @Override public void onInit(int res) {
      Timber.d("TextToSpeech onInit %d, %s", res, this.toString());

      if (res != TextToSpeech.SUCCESS) {
        status.onNext(res);
        Timber.d("TextToSpeech initialization failed, status=%d, %s", res, this.toString());
        return;
      }

      Locale locale = Locale.ENGLISH;
      res = textToSpeech.setLanguage(locale);
      if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
        Timber.d("Locale " + locale.toString() + " is not supported, status=%d, %s", res, this.toString());
        status.onNext(res);
        return;
      }

      isInitialized = true;
      for (OnInitListener listener : onInitListeners) {
        listener.onInit();
      }
      onInitListeners.clear();

      status.onNext(TextToSpeech.SUCCESS);
    }
  }

  private class TtsUtteranceListener extends UtteranceProgressListener {
    @Override public void onStart(String s) {
      Timber.d("Utterance onStart %s, %s", s, this.toString());
      utteranceStart.onNext(s);
    }

    @Override public void onDone(String s) {
      Timber.d("Utterance onDone %s, %s", s, this.toString());

      dequeue();
      utteranceDone.onNext(s);
    }

    @Override public void onError(String s) {
      Timber.w("Utterance onError %s, %s", s, this.toString());

      dequeue();
      utteranceError.onNext(s);
    }
  }

  private class Speech {
    private final CharSequence text;
    private int level;
    private float rate;

    public Speech(CharSequence text, int level, float rate) {
      this.text = text;
      this.level = level;
      this.rate = rate;
    }

    public CharSequence getText() {
      return text;
    }

    public int getLevel() {
      return level;
    }

    public void setLevel(int level) {
      this.level = level;
    }

    public float getRate() {
      return rate;
    }

    public void setRate(float rate) {
      this.rate = rate;
    }
  }
}
