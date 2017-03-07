/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.mocks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ch.indr.threethreefive.services.CommandSpeakerType;
import ch.indr.threethreefive.services.InstructionsSpeakerType;
import ch.indr.threethreefive.services.SpeakerType;
import rx.Observable;

public class MockSpeaker implements SpeakerType {
  public CharSequence lastSpeech;

  @Override public void start() {
  }

  @Override public void stop() {
  }

  @Override public void shutdown() {
  }

  @Override public Observable<Integer> status() {
    return null;
  }

  @Override public Observable<String> utteranceStart() {
    return null;
  }

  @Override public Observable<String> utteranceDone() {
    return null;
  }

  @Override public Observable<String> utteranceError() {
    return null;
  }

  @Override @NonNull public CommandSpeakerType command() {
    return null;
  }

  @Override @NonNull public InstructionsSpeakerType instructions() {
    return null;
  }

  @Override public String sayUrgent(CharSequence speech) {
    return say(speech, LEVEL_URGENT);
  }

  @Nullable @Override public String sayUrgent(int resourceId) {
    return sayUrgent("Resource id " + String.valueOf(resourceId));
  }

  @Override public String sayQueued(CharSequence speech) {
    return say(speech, LEVEL_QUEUED);
  }

  @Nullable @Override public String sayQueued(int resourceId) {
    return sayQueued("Resource id " + String.valueOf(resourceId));
  }

  @Override public String sayIdle(CharSequence speech) {
    return say(speech, LEVEL_IDLE);
  }

  @Override public String say(CharSequence speech, int level) {
    this.lastSpeech = speech;
    return speech.toString();
  }
}
