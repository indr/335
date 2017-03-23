/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.pages;

import android.net.Uri;
import android.os.Bundle;

import org.junit.Test;

import ch.indr.threethreefive.TtfRobolectricTestCase;
import ch.indr.threethreefive.libs.pages.PageResolver;
import ch.indr.threethreefive.libs.pages.PageMeta;
import ch.indr.threethreefive.pages.playlist.QueuePage;
import ch.indr.threethreefive.pages.radio.IndexPage;
import ch.indr.threethreefive.pages.radio.CountriesPage;
import ch.indr.threethreefive.pages.radio.CountryGenrePage;
import ch.indr.threethreefive.pages.radio.CountryGenresPage;
import ch.indr.threethreefive.pages.radio.CountryStationsPage;
import ch.indr.threethreefive.pages.radio.GenrePage;
import ch.indr.threethreefive.pages.radio.GenresPage;
import ch.indr.threethreefive.pages.radio.LanguagePage;
import ch.indr.threethreefive.pages.radio.LanguagesPage;
import ch.indr.threethreefive.pages.radio.StationGenresPage;
import ch.indr.threethreefive.pages.radio.StationPage;
import rx.functions.Action1;

public class PageResolverTests extends TtfRobolectricTestCase {

  @Test(expected = PageResolver.PageNotFoundException.class)
  public void resolve_withInvalidUri_throwsPageNotFound() {
    final RootPageResolver pageResolver = new RootPageResolver();

    try {
      pageResolver.resolve(Uri.parse("/a/b"));
    } catch (PageResolver.PageNotFoundException ex) {
      assertEquals("Page not found: //ch.indr.threethreefive/a/b", ex.getMessage());
      throw ex;
    }
  }

  @Test
  public void resolve_withValidUri_returnsPageMeta() {
    assertResolve("/", HomePage.class);
    assertResolve("/favorites", ch.indr.threethreefive.pages.favorites.IndexPage.class);
    assertResolve("/music", ch.indr.threethreefive.pages.music.IndexPage.class);
    assertResolve("/now-playing", NowPlayingPage.class);
    assertResolve("/playlist", QueuePage.class);
    assertResolve("/preferences", PreferencesPage.class);
    assertResolve("/radio", IndexPage.class);

    assertResolve("/radio/countries", CountriesPage.class);
    assertResolve("/radio/countries/New%20Zealand/stations", CountryStationsPage.class, b -> b.putString("countryId", "New Zealand"));
    assertResolve("/radio/countries/New%20Zealand/genres", CountryGenresPage.class, b -> b.putString("countryId", "New Zealand"));
    assertResolve("/radio/countries/New%20Zealand/genres/Top%2040", CountryGenrePage.class, b -> {
      b.putString("countryId", "New Zealand");
      b.putString("genreId", "Top 40");
    });
    assertResolve("/radio/languages", LanguagesPage.class);
    assertResolve("/radio/languages/Bahasa%20Melayu", LanguagePage.class, b -> b.putString("id", "Bahasa Melayu"));
    assertResolve("/radio/genres", GenresPage.class);
    assertResolve("/radio/genres/Top%2040", GenrePage.class, b -> b.putString("id", "Top 40"));
    assertResolve("/radio/stations/1234", StationPage.class, b -> b.putString("id", "1234"));
    assertResolve("/radio/stations/1234/genres", StationGenresPage.class, b -> b.putString("id", "1234"));
  }

  private PageMeta assertResolve(String uri, Class<?> pageClass) {
    final RootPageResolver pageResolver = new RootPageResolver();
    final PageMeta pageMeta = pageResolver.resolve(Uri.parse(uri));

    assertEquals(Uri.parse("//ch.indr.threethreefive" + uri), pageMeta.getUri());
    assertEquals(pageClass, pageMeta.getClazz());

    return pageMeta;
  }

  private PageMeta assertResolve(String uri, Class<?> pageClass, Action1<Bundle> bundleAction) {
    final PageMeta pageMeta = assertResolve(uri, pageClass);

    final Bundle expected = new Bundle();
    final Bundle actual = pageMeta.getBundle();
    bundleAction.call(expected);
    for (String key : expected.keySet()) {
      assertTrue("Bundle does not contain key " + key, actual.containsKey(key));
      assertEquals(expected.get(key), actual.get(key));
    }

    return pageMeta;
  }
}
