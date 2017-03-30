/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.db.favorites.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Favorite {

  private long id;
  private final String title;
  private final String subtitle;
  private final String description;
  private final String pageUri;
  private final String mediaUri;

  public Favorite(final long id, final @NonNull String title, final @Nullable String subtitle,
                  final @NonNull String description, final @NonNull String pageUri,
                  final @Nullable String mediaUri) {
    this.id = id;
    this.title = title;
    this.subtitle = subtitle;
    this.description = description;
    this.pageUri = pageUri;
    this.mediaUri = mediaUri;
  }

  public long getId() {
    return id;
  }

  @NonNull public String getTitle() {
    return title;
  }

  @Nullable public String getSubtitle() {
    return subtitle;
  }

  @NonNull public String getDescription() {
    return description;
  }

  @NonNull public String getPageUri() {
    return pageUri;
  }

  @Nullable public String getMediaUri() {
    return mediaUri;
  }
}
