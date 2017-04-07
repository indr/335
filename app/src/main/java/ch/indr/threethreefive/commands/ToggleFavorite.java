/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.commands;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.data.db.favorites.FavoritesStore;
import ch.indr.threethreefive.data.db.favorites.model.Favorite;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageCommand;
import ch.indr.threethreefive.libs.PageLink;

public class ToggleFavorite extends PageCommand {

  private final Resources resources;

  private Favorite favorite;

  public ToggleFavorite(final @NonNull Resources resources, final @NonNull FavoritesStore favoritesStore, final @NonNull PageLink pageLink) {
    this.resources = resources;
    this.favorite = new Favorite(0, pageLink.getTitle(), pageLink.getSubtitle(),
        pageLink.getDescription(), pageLink.getUri(), pageLink.getIconUri());

    updateTitle(favoritesStore.isFavorite(pageLink.getUri()));
  }

  @Override public void execute(@NonNull Environment environment) {
    FavoritesStore favoritesStore = environment.favoritesStore();

    if (!favoritesStore.isFavorite(favorite.getPageUri())) {
      addFavorite(environment, favoritesStore);
    } else {
      removeFavorite(environment, favoritesStore);
    }
  }

  private void addFavorite(@NonNull Environment environment, FavoritesStore favoritesStore) {
    favoritesStore.add(this.favorite);
    updateTitle(true);

    environment.toastManager().toast(resources.getString(R.string.favorite_added));
    environment.speaker().command().favoriteAdded();
  }

  private void removeFavorite(@NonNull Environment environment, FavoritesStore favoritesStore) {
    favoritesStore.remove(favorite.getPageUri());
    updateTitle(false);

    environment.toastManager().toast(resources.getString(R.string.favorite_removed));
    environment.speaker().command().favoriteRemoved();
  }

  private void updateTitle(final boolean isFavorite) {
    setTitle(isFavorite
        ? resources.getString(R.string.remove_from_favorites)
        : resources.getString(R.string.add_to_favorites));
  }
}
