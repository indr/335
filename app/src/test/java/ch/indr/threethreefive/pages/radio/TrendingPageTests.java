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

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import ch.indr.threethreefive.Fake;
import ch.indr.threethreefive.R;
import ch.indr.threethreefive.TtfRobolectricTestCase;
import ch.indr.threethreefive.data.network.ApiClient;
import ch.indr.threethreefive.libs.PageItem;

import static org.mockito.Mockito.verify;

public class TrendingPageTests extends TtfRobolectricTestCase {

  private ApiClient apiClient;

  @Before
  @Override public void setUp() throws Exception {
    super.setUp();

    this.apiClient = appModule().apiClient(context());
  }

  @Test
  public void onStart_getsTrendingStations() {
    final TrendingPage page = createPage();

    page.onStart();

    verify(apiClient).getTrendingStations(page);
  }

  @Test
  public void onRequestFailure_showsExceptionMessage() {
    final TrendingPage page = createPage();

    page.onRequestFailure(new SpiceException("Test spice exception message"));

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals("Test spice exception message", pageItems.get(0).getTitle());
  }

  @Test
  public void onRequestSuccess_withEmptyResponse_noStationsFound() {
    final TrendingPage page = createPage();

    page.onRequestSuccess(Fake.stations(0));

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(getString(R.string.no_stations_found), pageItems.get(1).getTitle());
  }

  @Test
  public void onRequestSuccess_with15Stations_show15Stations() {
    final TrendingPage page = createPage();

    page.onRequestSuccess(Fake.stations(15));

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(1 + 15, pageItems.size());
  }

  @Test
  public void onRequestSuccess_with50Stations_shows15StationsAndShowAll() {
    final TrendingPage page = createPage();

    page.onRequestSuccess(Fake.stations(50));

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(1 + 15 + 1, pageItems.size());
    assertEquals(getString(R.string.show_all_stations), pageItems.get(pageItems.size() - 1).getTitle());
  }

  @Test
  public void onRequestSuccess_with51Stations_shows15StationsAndShowMore() {
    final TrendingPage page = createPage();

    page.onRequestSuccess(Fake.stations(51));

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(1 + 15 + 1, pageItems.size());
    assertEquals(getString(R.string.show_more_stations), pageItems.get(pageItems.size() - 1).getTitle());
  }

  @NonNull private TrendingPage createPage() {
    final Bundle bundle = new Bundle();
    return createPage(bundle);
  }

  private TrendingPage createPage(Bundle bundle) {
    final TrendingPage page = new TrendingPage(environment());
    page.onCreate(context(), Uri.parse("/radio/trending"), bundle);
    return page;
  }
}