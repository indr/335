/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.db.favorites;

import android.support.annotation.NonNull;

import org.junit.Test;

import java.util.List;

import ch.indr.threethreefive.TtfRobolectricTestCase;
import ch.indr.threethreefive.data.db.favorites.model.Favorite;
import ch.indr.threethreefive.libs.PageUris;

public class FavoritesStoreImplTest extends TtfRobolectricTestCase {
  @Test
  public void add_returnsId() throws Exception {
    final FavoritesStoreImpl favoritesStore = getFavoritesStore();

    final long id = favoritesStore.add(makeFavorite("Favorite"));

    assertTrue("id should be greater than 0", id > 0);
  }

  @Test
  public void findAll_returnsAddedFavorite() {
    final FavoritesStoreImpl favoritesStore = getFavoritesStore();
    final Favorite expected = makeFavorite("Favorite");
    final long id = favoritesStore.add(expected);

    final Favorite actual = favoritesStore.findAll().get(0);

    assertEquals(id, actual.getId());
    assertEquals(expected.getTitle(), actual.getTitle());
    assertEquals(expected.getContentDescription(), actual.getContentDescription());
    assertEquals(expected.getPageUri(), actual.getPageUri());
    assertEquals(expected.getSubtitle(), actual.getSubtitle());
  }

  @Test
  public void findAll_returnsFavorites() throws Exception {
    final FavoritesStoreImpl favoritesStore = getFavoritesStore();
    favoritesStore.add(makeFavorite("1"));
    favoritesStore.add(makeFavorite("2"));
    favoritesStore.add(makeFavorite("3"));

    final List<Favorite> all = favoritesStore.findAll();

    assertEquals(3, all.size());
  }

  @NonNull private FavoritesStoreImpl getFavoritesStore() {
    return new FavoritesStoreImpl(context());
  }

  @NonNull private Favorite makeFavorite(final @NonNull String title) {
    return new Favorite(0, title, "Subtitle", "Description", PageUris.favorites(), null);
  }
}
