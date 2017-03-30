/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.support.v4.media.MediaMetadataCompat;

import org.junit.Test;

import ch.indr.threethreefive.TtfRobolectricTestCase;

public class MediaItemTests extends TtfRobolectricTestCase {

  @Test
  public void getTitle_withMediaMetadataDisplayTitle() throws Exception {
    MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Title")
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, "Display title")
        .build();

    final MediaItem mediaItem = new MediaItem(metadata, "");

    assertEquals("Display title", mediaItem.getTitle());
  }

  @Test
  public void getTitle_withEmptyMediaMetadataDisplayTitle() throws Exception {
    MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Title")
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, "")
        .build();

    final MediaItem mediaItem = new MediaItem(metadata, "");

    assertEquals("Title", mediaItem.getTitle());
  }
}
