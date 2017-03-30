/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;

import ch.indr.threethreefive.libs.utils.StringUtils;

public class MediaItem {

  private MediaMetadataCompat metadata;

  private final String title;
  private final String subtitle;
  private final String pageUri;
  private final String mediaUri;

  public MediaItem(final @NonNull MediaMetadataCompat metadata, final @NonNull String pageUri) {
    this.metadata = metadata;

    String title = metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE);
    if (StringUtils.isEmpty(title)) {
      title = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
    }
    this.title = title;
    this.subtitle = metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE);
    this.pageUri = pageUri;
    this.mediaUri = metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI);
  }

  @Nullable public String getTitle() {
    return title;
  }

  @Nullable public String getSubtitle() {
    return subtitle;
  }

  @NonNull public String getPageUri() {
    return pageUri;
  }

  @Nullable public String getMediaUri() {
    return mediaUri;
  }

  @NonNull public MediaMetadataCompat getMediaMetadata() {
    return metadata;
  }

  @NonNull public MediaDescriptionCompat getMediaDescription() {
    return getMediaMetadata().getDescription();
  }

  public void setAlbumArt(final @Nullable Bitmap bitmap) {
    metadata = new MediaMetadataCompat.Builder(metadata)
        .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
        .build();
  }

  public void setDisplayIcon(final @Nullable Bitmap bitmap) {
    metadata = new MediaMetadataCompat.Builder(metadata)
        .putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, bitmap)
        .build();
  }
}
