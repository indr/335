/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.pages.radio;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.octo.android.robospice.persistence.exception.SpiceException;

import org.junit.Test;

import java.util.List;

import ch.indr.threethreefive.Fake;
import ch.indr.threethreefive.R;
import ch.indr.threethreefive.TtfRobolectricTestCase;
import ch.indr.threethreefive.data.network.ApiClient;
import ch.indr.threethreefive.libs.PageItem;

import static org.mockito.Mockito.verify;

public class CountryGenrePageTests extends TtfRobolectricTestCase {

  private ApiClient apiClient;

  @Override public void setUp() throws Exception {
    super.setUp();

    this.apiClient = appModule().apiClient(context());
  }

  @Test(expected = IllegalArgumentException.class)
  public void onCreate_withoutCountryId_throws() {
    final Bundle bundle = new Bundle();
    bundle.putString("genreId", "jazz");
    try {
      createPage(bundle);
    } catch (Exception ex) {
      assertTrue(ex.getMessage().contains("key countryId"));
      throw ex;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void onCreate_withoutGenreId_throws() {
    final Bundle bundle = new Bundle();
    bundle.putString("countryId", "england");
    try {
      createPage(bundle);
    } catch (Exception ex) {
      assertTrue(ex.getMessage().contains("key genreId"));
      throw ex;
    }
  }

  @Test
  public void onStart_getsGenresByCountry() {
    final CountryGenrePage page = createPage();

    page.onStart();

    verify(apiClient).getStationsByCountryAndGenre("england", "jazz", page);
  }

  @Test
  public void onRequestFailure_handlesException() {
    final CountryGenrePage page = createPage();

    page.onRequestFailure(new SpiceException("Test spice exception message"));

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals("Test spice exception message", pageItems.get(0).getTitle());
  }

  @Test
  public void onRequestSuccess_withNullResponse_noStationsFoundError() {
    final CountryGenrePage page = createPage();

    page.onRequestSuccess(null);

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(getString(R.string.no_stations_found_error), pageItems.get(0).getTitle());
  }

  @Test
  public void onRequestSuccess_withEmptyResponse_noStationsFound() {
    final CountryGenrePage page = createPage();

    page.onRequestSuccess(Fake.stations(0));

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(getString(R.string.no_stations_found), pageItems.get(1).getTitle());
  }

  @Test
  public void onRequestSuccess_with15Stations_show15Stations() {
    final CountryGenrePage page = createPage();

    page.onRequestSuccess(Fake.stations(15));

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(1 + 15, pageItems.size());
  }

  @Test
  public void onRequestSuccess_with50Stations_shows15StationsAndShowAll() {
    final CountryGenrePage page = createPage();

    page.onRequestSuccess(Fake.stations(50));

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(1 + 15 + 1, pageItems.size());
    assertEquals(getString(R.string.show_all_stations), pageItems.get(pageItems.size() - 1).getTitle());
  }

  @Test
  public void onRequestSuccess_with51Stations_shows15StationsAndShowMore() {
    final CountryGenrePage page = createPage();

    page.onRequestSuccess(Fake.stations(51));

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(1 + 15 + 1, pageItems.size());
    assertEquals(getString(R.string.show_more_stations), pageItems.get(pageItems.size() - 1).getTitle());
  }

  @NonNull private CountryGenrePage createPage() {
    final Bundle bundle = new Bundle();
    bundle.putString("countryId", "england");
    bundle.putString("genreId", "jazz");
    return createPage(bundle);
  }

  @NonNull private CountryGenrePage createPage(Bundle bundle) {
    final CountryGenrePage page = new CountryGenrePage(environment());
    page.onCreate(context(), Uri.parse("/radio/countries/Country/genres/Genre"), bundle);
    return page;
  }
}
