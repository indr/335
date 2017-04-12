/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.pages.radio;

import android.os.Bundle;
import android.support.annotation.NonNull;

import org.junit.Test;

import java.util.List;

import ch.indr.threethreefive.Fake;
import ch.indr.threethreefive.R;
import ch.indr.threethreefive.TtfRobolectricTestCase;
import ch.indr.threethreefive.data.network.ApiClient;
import ch.indr.threethreefive.data.network.radioBrowser.model.GenresBuilder;
import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.libs.PageUris;

import static org.mockito.Mockito.verify;

public class GenrePageTests extends TtfRobolectricTestCase {

  private final String GENRE_ID = "classic rock";

  private ApiClient apiClient;

  @Override public void setUp() throws Exception {
    super.setUp();

    this.apiClient = appModule().apiClient(context());
  }

  @Test(expected = IllegalArgumentException.class)
  public void onCreate_withoutIdInBundle_throws() {
    try {
      createPage(new Bundle());
    } catch (Exception ex) {
      assertTrue(ex.getMessage().contains("key id"));
      throw ex;
    }
  }

  @Test
  public void onCreate_setsTitle() {
    final GenrePage page = createPage();

    assertEquals("Classic Rock", page.getTitle());
  }

  @Test
  public void onStart_getsStationsByGenre() {
    final GenrePage page = createPage();

    page.onStart();

    verify(apiClient).getStationsByGenre(GenresBuilder.getGenre(GENRE_ID), page);
  }

  @Test
  public void onRequestSuccess_withNullResponse_noStationsFound() {
    final GenrePage page = createPage();

    page.onRequestSuccess(null);

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(getString(R.string.no_stations_found_error), pageItems.get(0).getTitle());
  }

  @Test
  public void onRequestSuccess_withEmptyResponse_noStationsFound() {
    final GenrePage page = createPage();

    page.onRequestSuccess(Fake.stations(0));

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(getString(R.string.no_stations_found), pageItems.get(0).getTitle());
  }

  @Test
  public void onRequestSuccess_with15Stations_show15Stations() {
    final GenrePage page = createPage();

    page.onRequestSuccess(Fake.stations(15));

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(1 + 15, pageItems.size());
  }

  @Test
  public void onRequestSuccess_with50Stations_shows15StationsAndShowAll() {
    final GenrePage page = createPage();

    page.onRequestSuccess(Fake.stations(50));

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(1 + 15 + 1, pageItems.size());
    assertEquals(getString(R.string.show_all_stations), pageItems.get(pageItems.size() - 1).getTitle());
  }

  @Test
  public void onRequestSuccess_with51Stations_shows15StationsAndShowMore() {
    final GenrePage page = createPage();

    page.onRequestSuccess(Fake.stations(51));

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(1 + 15 + 1, pageItems.size());
    assertEquals(getString(R.string.show_more_stations), pageItems.get(pageItems.size() - 1).getTitle());
  }

  @NonNull private GenrePage createPage() {
    final Bundle bundle = new Bundle();
    bundle.putString("id", GENRE_ID);
    return createPage(bundle);
  }

  @NonNull private GenrePage createPage(@NonNull final Bundle bundle) {
    final GenrePage page = new GenrePage(environment());
    page.onCreate(context(), PageUris.radioGenre(GENRE_ID), bundle);
    return page;
  }
}
