/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.services;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;

public interface Speaker {
  int LEVEL_URGENT = 1;
  int LEVEL_QUEUED = 2;
  int LEVEL_IDLE = 3;

  float SPEECH_RATE_NORMAL = 0.8f;
  float SPEECH_RATE_SLOW = 0.65f;

  void start();

  void stop();

  /**
   * Call this when TTS is no longer needed.
   */
  void shutdown();

  /**
   * Observable that emits initialization status of the tts engine and language settings.
   */
  Observable<Integer> status();

  Observable<String> utteranceStart();

  Observable<String> utteranceDone();

  Observable<String> utteranceError();

  @NonNull CommandSpeaker command();

  @NonNull InstructionsSpeaker instructions();

  @Nullable String sayUrgent(CharSequence speech);

  @Nullable String sayUrgent(CharSequence speech, float speechRate);

  @Nullable String sayUrgent(int resourceId);

  @Nullable String sayQueued(CharSequence speech);

  @Nullable String sayQueued(CharSequence speech, float speechRate);

  @Nullable String sayQueued(int resourceId);

  @Nullable String sayIdle(CharSequence speech);

  @Nullable String say(CharSequence speech, int level);
}

