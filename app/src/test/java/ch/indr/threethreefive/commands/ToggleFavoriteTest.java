/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.commands;

import org.junit.Test;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.TtfRobolectricTestCase;
import ch.indr.threethreefive.data.db.favorites.FavoritesStore;
import ch.indr.threethreefive.libs.PageLink;

import static org.mockito.Mockito.when;

public class ToggleFavoriteTest extends TtfRobolectricTestCase {

  private FavoritesStore favoritesStore;

  @Override public void setUp() throws Exception {
    super.setUp();

    this.favoritesStore = appModule().provideFavoritesStore(context());
  }

  @Test
  public void create_isFavorite_hasTitleAndDescription() {
    final PageLink homePage = PageLink.HomePage;
    when(favoritesStore.isFavorite(homePage.getUri())).thenReturn(true);

    final ToggleFavorite toggleFavorite = new ToggleFavorite(context().getResources(), environment().favoritesStore(), homePage);

    assertEquals(getString(R.string.remove_from_favorites), toggleFavorite.getTitle());
    assertNull(toggleFavorite.getSubtitle());
    assertEquals(getString(R.string.remove_from_favorites), toggleFavorite.getDescription());
  }

  @Test
  public void create_isNotFavorite_hasTitleAndDescription() {
    final PageLink homePage = PageLink.HomePage;
    when(favoritesStore.isFavorite(homePage.getUri())).thenReturn(false);

    final ToggleFavorite toggleFavorite = new ToggleFavorite(context().getResources(), environment().favoritesStore(), homePage);

    assertEquals(getString(R.string.add_to_favorites), toggleFavorite.getTitle());
    assertNull(toggleFavorite.getSubtitle());
    assertEquals(getString(R.string.add_to_favorites), toggleFavorite.getDescription());
  }
}