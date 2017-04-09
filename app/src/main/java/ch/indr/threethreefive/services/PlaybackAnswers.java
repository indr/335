/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.services;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Pair;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import ch.indr.threethreefive.BuildConfig;
import ch.indr.threethreefive.data.network.ApiClient;
import ch.indr.threethreefive.data.network.radioBrowser.model.PlayableStationUrl;
import ch.indr.threethreefive.libs.MetadataKeys;
import ch.indr.threethreefive.libs.net.RobospiceManagerImpl;
import ch.indr.threethreefive.libs.utils.StringUtils;
import timber.log.Timber;

public class PlaybackAnswers {

  private final Context context;

  public PlaybackAnswers(final @NonNull Context context) {
    this.context = context;
  }

  public void reportAccessibilityNotice(String reason) {
    if (StringUtils.isEmpty(reason)) {
      return;
    }

    if (BuildConfig.ANSWERS) {
      Answers.getInstance().logCustom(new CustomEvent("Accessibility Notice")
          .putCustomAttribute("reason", reason));
    }
  }

  public void reportConnecting(MediaMetadataCompat mediaMetadata) {
    Timber.d("reportConnecting %s", this.toString());
    if (mediaMetadata == null) {
      return;
    }

    final String radioId = mediaMetadata.getString(MetadataKeys.METADATA_KEY_RADIO_ID);
    if (StringUtils.isEmpty(radioId)) {
      return;
    }

    final MediaDescriptionCompat mediaDescription = mediaMetadata.getDescription();

    if (BuildConfig.ANSWERS) {
      Answers.getInstance().logCustom(new CustomEvent("Playback Connecting")
          .putCustomAttribute("radioId", radioId)
          .putCustomAttribute("title", StringUtils.getString(mediaDescription.getTitle())));
    }

    final RobospiceManagerImpl robospiceManager = new RobospiceManagerImpl();
    robospiceManager.start(context);
    new ApiClient(robospiceManager).countStationClick(radioId, new RequestListener<PlayableStationUrl>() {
      @Override public void onRequestFailure(SpiceException spiceException) {
        Timber.e(spiceException, "onRequestFailure %s", this.toString());
        robospiceManager.shouldStop();
      }

      @Override public void onRequestSuccess(PlayableStationUrl playableStationUrl) {
        Timber.d("onRequestSuccess %s, %s", playableStationUrl.toString(), this.toString());
        robospiceManager.shouldStop();
      }
    });
  }

  public void reportError(Pair<MediaMetadataCompat, PlaybackStateCompat> pair) {
    Timber.d("reportError %s", this.toString());
    if (pair == null || pair.first == null || pair.second == null) {
      return;
    }

    final String radioId = pair.first.getString(MetadataKeys.METADATA_KEY_RADIO_ID);
    if (StringUtils.isEmpty(radioId)) {
      return;
    }

    final MediaDescriptionCompat mediaDescription = pair.first.getDescription();
    final PlaybackStateCompat playbackState = pair.second;
    if (BuildConfig.ANSWERS) {
      Answers.getInstance().logCustom(new CustomEvent("Playback Error")
          .putCustomAttribute("radioId", radioId)
          .putCustomAttribute("title", StringUtils.getString(mediaDescription.getTitle()))
          .putCustomAttribute("errorCode", playbackState.getErrorCode())
          .putCustomAttribute("errorMessage", StringUtils.getString(playbackState.getErrorMessage())));
    }
  }
}
