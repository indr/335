/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.support.v4.media.MediaMetadataCompat.TextKey;

public final class MetadataKeys {

  private MetadataKeys() {
  }

  @TextKey public static final String METADATA_KEY_ALBUM_ID = "threethreefive.media.metadata.METADATA_KEY_ALBUM_ID";

  @TextKey public static final String METADATA_KEY_ARTIST_ID = "threethreefive.media.metadata.METADATA_KEY_ARTIST_ID";

  @TextKey public static final String METADATA_KEY_RADIO_ID = "threethreefive.media.metadata.METADATA_KEY_RADIO_ID";

  @TextKey public static final String METADATA_KEY_SONG_ID = "threethreefive.media.metadata.METADATA_KEY_SONG_ID";
}
