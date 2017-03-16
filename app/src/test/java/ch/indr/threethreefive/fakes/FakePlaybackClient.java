/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.fakes;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Pair;

import ch.indr.threethreefive.services.PlaybackClientType;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class FakePlaybackClient implements PlaybackClientType {
  public BehaviorSubject<MediaMetadataCompat> mediaMetadata = BehaviorSubject.create();
  public BehaviorSubject<Integer> playbackState = BehaviorSubject.create();
  private Observable<Pair<String,Bundle>> customEvent = BehaviorSubject.create();

  @Nullable @Override public MediaControllerCompat mediaController() {
    return null;
  }

  @NonNull @Override public Observable<MediaMetadataCompat> mediaMetadata() {
    return mediaMetadata;
  }

  @Nullable @Override public MediaControllerCompat.TransportControls transportControls() {
    return null;
  }

  @NonNull @Override public Observable<Pair<String, Bundle>> customEvent() {
    return customEvent;
  }

  @NonNull @Override public BehaviorSubject<Integer> playbackState() {
    return playbackState;
  }

  @NonNull @Override public BehaviorSubject<PlaybackStateCompat> playbackStateCompat() {
    return null;
  }

  @Override public void playPause() {

  }
}
