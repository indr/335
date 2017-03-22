/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.radio;

import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;

import ch.indr.threethreefive.libs.MediaItem;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Station;

import static android.support.v4.media.MediaMetadataCompat.Builder;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_URI;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE;
import static ch.indr.threethreefive.libs.MetadataKeys.METADATA_KEY_RADIO_ID;
import static ch.indr.threethreefive.libs.PageUris.radioStation;

public class RadioMediaItemFactory {

  public static MediaItem make(final @NonNull Station station) {

    final String pageUri = radioStation(station.getId());
    final String mediaUri = station.getUrl();

    final MediaMetadataCompat metadata = new Builder()
        .putString(METADATA_KEY_DISPLAY_TITLE, station.getName())
        .putString(METADATA_KEY_MEDIA_ID, mediaUri)
        .putString(METADATA_KEY_MEDIA_URI, mediaUri)
        .putString(METADATA_KEY_RADIO_ID, station.getId())
        .putString(METADATA_KEY_TITLE, station.getName())
        .build();

    return new MediaItem(metadata, pageUri);
  }
}
