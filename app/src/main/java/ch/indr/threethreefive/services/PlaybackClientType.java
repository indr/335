/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.services;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Pair;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public interface PlaybackClientType {

  @Nullable MediaControllerCompat mediaController();

  @NonNull Observable<MediaMetadataCompat> mediaMetadata();

  @NonNull BehaviorSubject<Integer> playbackState();

  @NonNull BehaviorSubject<PlaybackStateCompat> playbackStateCompat();

  @Nullable MediaControllerCompat.TransportControls transportControls();

  @NonNull Observable<Pair<String, Bundle>> customEvent();

  void playPause();
}
