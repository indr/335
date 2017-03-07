/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;

public class MediaItem {

  private final String name;
  private final String pageUri;
  private final String mediaUri;

  private MediaMetadataCompat metadata;

  public MediaItem(final @NonNull MediaMetadataCompat metadata, final @NonNull String pageUri) {
    this.name = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
    this.pageUri = pageUri;
    this.mediaUri = metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI);

    this.metadata = metadata;
  }

  @Deprecated
  public MediaItem(final @NonNull String name, final @NonNull String pageUri, final @NonNull String mediaUri) {
    this.name = name;
    this.pageUri = pageUri;
    this.mediaUri = mediaUri;

    this.metadata = new MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, this.mediaUri)
        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, this.name)
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, this.mediaUri)
        .build();
  }

  // TODO: Rename to title?
  public String getName() {
    return name;
  }

  public String getPageUri() {
    return pageUri;
  }

  public String getMediaUri() {
    return mediaUri;
  }

  @NonNull public MediaMetadataCompat getMediaMetadata() {
    return metadata;
  }

  @NonNull public MediaDescriptionCompat getMediaDescription() {
    return getMediaMetadata().getDescription();
  }

  public void setAlbumArt(Bitmap bitmap) {
    metadata = new MediaMetadataCompat.Builder(metadata)
        .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
        .build();
  }

  public void setDisplayIcon(Bitmap bitmap) {
    metadata = new MediaMetadataCompat.Builder(metadata)
        .putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, bitmap)
        .build();
  }
}
