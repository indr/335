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

import org.junit.Test;

import java.util.List;

import ch.indr.threethreefive.Fake;
import ch.indr.threethreefive.TtfRobolectricTestCase;
import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.radio.pages.CountryPage;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Station;
import rx.observers.TestSubscriber;

public class CountryPageTests extends TtfRobolectricTestCase {

  @Test
  public void onCreate_withoutIdInBundle_throwsRuntimeException() {
    try {
      createPage(new Bundle());

      assertFalse(true);
    } catch (RuntimeException ex) {
      assertEquals("Bundle does not contain an id or id is null or empty", ex.getMessage());
    }
  }

  @Test
  public void onCreate_withIdInBundle_setsPageTitle() {
    final Bundle bundle = new Bundle();
    bundle.putString("id", "Dreamland");
    final CountryPage page = createPage(bundle);
    final TestSubscriber<String> title = new TestSubscriber<>();
    page.pageTitle().subscribe(title);

    title.assertValue("Dreamland");
  }

  @Test
  public void onRequestSuccess_withNullResponse_noStationsFound() {
    final CountryPage page = createPage();

    page.onRequestSuccess(null);

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals("Error: No stations found", pageItems.get(0).getTitle());
  }

  @Test
  public void onRequestSuccess_withEmptyResponse_noStationsFound() {
    final CountryPage page = createPage();

    page.onRequestSuccess(new Station[0]);

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals("No stations found", pageItems.get(1).getTitle());
  }

  @Test
  public void onRequestSuccess_with15Stations_show15Stations() {
    final CountryPage page = createPage();

    page.onRequestSuccess(Fake.stations(15));

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(1 + 15, pageItems.size());
  }

  @Test
  public void onRequestSuccess_with50Stations_shows15StationsAndShowAll() {
    final CountryPage page = createPage();

    page.onRequestSuccess(Fake.stations(50));

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(1 + 15 + 1, pageItems.size());
    assertEquals("Show all Stations", pageItems.get(pageItems.size() -1 ).getTitle());
  }

  @Test
  public void onRequestSuccess_with51Stations_shows15StationsAndShowMore() {
    final CountryPage page = createPage();

    page.onRequestSuccess(Fake.stations(51));

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(1 + 15 + 1, pageItems.size());
    assertEquals("Show more Stations", pageItems.get(pageItems.size() -1 ).getTitle());
  }

  @NonNull private CountryPage createPage() {
    final Bundle bundle = new Bundle();
    bundle.putString("id", "Dreamland");
    return createPage(bundle);
  }

  @NonNull private CountryPage createPage(@NonNull final Bundle bundle) {
    final CountryPage page = new CountryPage(environment());
    page.onCreate(context(), Uri.parse("/radio/countries/Fantasia"), bundle);
    return page;
  }

}
