/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.pages.favorites;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.List;

import ch.indr.threethreefive.data.db.favorites.FavoritesStore;
import ch.indr.threethreefive.data.db.favorites.model.Favorite;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.pages.Page;

public class IndexPage extends Page {

  private final FavoritesStore favoritesStore;

  public IndexPage(Environment environment) {
    super(environment);

    this.favoritesStore = environment.favoritesStore();
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    setTitle("Favorites");
  }

  @Override public void onResume() {
    super.onResume();

    final PageItemsBuilder builder = pageItemsBuilder();

    List<Favorite> favorites = favoritesStore.findAll();
    if (favorites.size() == 0) {
      builder.addText("No favorites");
    } else for (Favorite favorite : favorites) {
      builder.addLink(favorite.getPageUri(), favorite.getTitle());
    }

    setPageItems(builder);
  }
}