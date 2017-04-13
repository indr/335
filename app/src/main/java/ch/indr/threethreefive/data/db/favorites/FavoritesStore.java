/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.db.favorites;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import ch.indr.threethreefive.data.db.favorites.model.Favorite;

public interface FavoritesStore {
  long add(final @NonNull Favorite favorite);

  @NonNull List<Favorite> findAll();

  void remove(final @NonNull Uri pageUri);

  boolean isFavorite(final @Nullable Uri pageUri);
}
