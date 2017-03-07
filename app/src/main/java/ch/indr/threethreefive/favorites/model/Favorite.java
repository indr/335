/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.favorites.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Favorite {
  private long id;
  private @NonNull String title;
  private @NonNull String pageUri;
  private @Nullable String mediaUri;

  public Favorite(long id, @NonNull String title, @NonNull String pageUri, @Nullable String mediaUri) {
    this.id = id;
    this.title = title;
    this.pageUri = pageUri;
    this.mediaUri = mediaUri;
  }

  public long getId() {
    return id;
  }

  @NonNull public String getTitle() {
    return title;
  }

  @NonNull public String getPageUri() {
    return pageUri;
  }

  @Nullable public String getMediaUri() {
    return mediaUri;
  }
}
