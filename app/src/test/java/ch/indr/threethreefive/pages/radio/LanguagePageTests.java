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
import ch.indr.threethreefive.R;
import ch.indr.threethreefive.TtfRobolectricTestCase;
import ch.indr.threethreefive.libs.PageItem;
import rx.observers.TestSubscriber;

public class LanguagePageTests extends TtfRobolectricTestCase {

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
  public void onCreate_withIdInBundle_setsPageTitle() {
    final Bundle bundle = new Bundle();
    bundle.putString("id", "Fantasian");
    final LanguagePage page = createPage(bundle);
    final TestSubscriber<String> title = new TestSubscriber<>();
    page.pageTitle().subscribe(title);

    title.assertValue("Fantasian");
  }

  @Test
  public void onRequestSuccess_withNullResponse_noStationsFound() {
    final LanguagePage page = createPage();

    page.onRequestSuccess(null);

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(getString(R.string.no_stations_found_error), pageItems.get(0).getTitle());
  }

  @Test
  public void onRequestSuccess_withEmptyResponse_noStationsFound() {
    final LanguagePage page = createPage();

    page.onRequestSuccess(Fake.stations(0));

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(getString(R.string.no_stations_found), pageItems.get(1).getTitle());
  }

  @Test
  public void onRequestSuccess_with15Stations_show15Stations() {
    final LanguagePage page = createPage();

    page.onRequestSuccess(Fake.stations(15));

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(1 + 15, pageItems.size());
  }

  @Test
  public void onRequestSuccess_with50Stations_shows15StationsAndShowAll() {
    final LanguagePage page = createPage();

    page.onRequestSuccess(Fake.stations(50));

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(1 + 15 + 1, pageItems.size());
    assertEquals(getString(R.string.show_all_stations), pageItems.get(pageItems.size() - 1).getTitle());
  }

  @Test
  public void onRequestSuccess_with51Stations_shows15StationsAndShowMore() {
    final LanguagePage page = createPage();

    page.onRequestSuccess(Fake.stations(51));

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(1 + 15 + 1, pageItems.size());
    assertEquals(getString(R.string.show_more_stations), pageItems.get(pageItems.size() - 1).getTitle());
  }

  @NonNull private LanguagePage createPage() {
    final Bundle bundle = new Bundle();
    bundle.putString("id", "Dreamland");
    return createPage(bundle);
  }

  @NonNull private LanguagePage createPage(@NonNull final Bundle bundle) {
    final LanguagePage page = new LanguagePage(environment());
    page.onCreate(context(), Uri.parse("/radio/language/Fantasian"), bundle);
    return page;
  }

}
