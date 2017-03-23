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

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import ch.indr.threethreefive.libs.MetadataKeys;
import ch.indr.threethreefive.libs.net.RobospiceManagerImpl;
import ch.indr.threethreefive.libs.utils.StringUtils;
import ch.indr.threethreefive.data.network.radioBrowser.PlayableStationUrlRequest;
import ch.indr.threethreefive.data.network.radioBrowser.model.PlayableStationUrl;
import timber.log.Timber;

public class PlaybackAnswers {

  private final Context context;

  public PlaybackAnswers(final @NonNull Context context) {
    this.context = context;
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

    final MediaDescriptionCompat description = mediaMetadata.getDescription();
    Answers.getInstance().logCustom(new CustomEvent("Playback Connecting")
        .putCustomAttribute("radioId", radioId)
        .putCustomAttribute("title", StringUtils.getString(description.getTitle())));

    final RobospiceManagerImpl robospiceManager = new RobospiceManagerImpl();
    robospiceManager.start(context);
    robospiceManager.execute(new PlayableStationUrlRequest(radioId), new RequestListener<PlayableStationUrl>() {
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
}
