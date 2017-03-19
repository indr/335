/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.favorites;

import android.support.annotation.NonNull;

import java.util.List;

import ch.indr.threethreefive.favorites.model.Favorite;
import ch.indr.threethreefive.libs.PageLink;

public interface FavoritesStore {

  boolean isFavorite(@NonNull PageLink pageLink);

  boolean isFavorite(@NonNull String pageUri);

  long add(@NonNull Favorite favorite);

  void remove(@NonNull String pageUri);

  @NonNull List<Favorite> findAll();
}
