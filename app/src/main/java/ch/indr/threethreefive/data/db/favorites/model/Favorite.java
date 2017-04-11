/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.db.favorites.model;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ch.indr.threethreefive.libs.utils.StringUtils;

public class Favorite {
  private long id;
  private final String title;
  private final String subtitle;
  private final String description;
  private final Uri pageUri;
  private final Uri iconUri;

  public Favorite(final long id, final @NonNull String title, final @Nullable String subtitle,
                  final @Nullable String description, final @NonNull Uri pageUri,
                  final @Nullable Uri iconUri) {
    this.id = id;
    this.title = title;
    this.subtitle = subtitle;
    this.description = StringUtils.isEmpty(description) ? title : description;
    this.pageUri = pageUri;
    this.iconUri = iconUri;
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

  @NonNull public Uri getPageUri() {
    return pageUri;
  }

  @Nullable public Uri getIconUri() {
    return iconUri;
  }
}
