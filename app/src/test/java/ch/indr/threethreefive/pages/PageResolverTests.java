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
import ch.indr.threethreefive.navigation.AbstractPageResolver;
import ch.indr.threethreefive.navigation.PageMeta;
import ch.indr.threethreefive.navigation.PageResolver;
import ch.indr.threethreefive.radio.pages.CountriesPage;
import ch.indr.threethreefive.radio.pages.CountryPage;
import ch.indr.threethreefive.radio.pages.GenrePage;
import ch.indr.threethreefive.radio.pages.GenresPage;
import ch.indr.threethreefive.radio.pages.LanguagePage;
import ch.indr.threethreefive.radio.pages.LanguagesPage;
import ch.indr.threethreefive.radio.pages.StationGenresPage;
import ch.indr.threethreefive.radio.pages.StationPage;
import rx.functions.Action1;

public class PageResolverTests extends TtfRobolectricTestCase {

  @Test(expected = AbstractPageResolver.PageNotFoundException.class)
  public void resolve_withInvalidUri_throwsPageNotFound() {
    final PageResolver pageResolver = new PageResolver();

    try {
      pageResolver.resolve(Uri.parse("/a/b"));
    } catch (AbstractPageResolver.PageNotFoundException ex) {
      assertEquals("Page not found: //ch.indr.threethreefive/a/b", ex.getMessage());
      throw ex;
    }
  }

  @Test
  public void resolve_withValidUri_returnsPageMeta() {
    assertResolve("/", HomePage.class);
    assertResolve("/favorites", ch.indr.threethreefive.favorites.pages.IndexPage.class);
    assertResolve("/music", ch.indr.threethreefive.music.pages.IndexPage.class);
    assertResolve("/now-playing", NowPlayingPage.class);
    assertResolve("/playlist", ch.indr.threethreefive.playlist.pages.QueuePage.class);
    assertResolve("/preferences", PreferencesPage.class);
    assertResolve("/radio", ch.indr.threethreefive.radio.pages.IndexPage.class);

    assertResolve("/radio/countries", CountriesPage.class);
    assertResolve("/radio/countries/England", CountryPage.class, b -> b.putString("countryId", "England"));
    assertResolve("/radio/countries/England/genres", GenresPage.class, b -> b.putString("countryId", "England"));
    assertResolve("/radio/countries/England/genres/Rock", GenrePage.class, b -> {
      b.putString("countryId", "England");
      b.putString("genreId", "Rock");
    });
    assertResolve("/radio/languages", LanguagesPage.class);
    assertResolve("/radio/languages/German", LanguagePage.class, b -> b.putString("id", "German"));
    assertResolve("/radio/genres", GenresPage.class);
    assertResolve("/radio/genres/Blues", GenrePage.class, b -> b.putString("id", "Blues"));
    assertResolve("/radio/stations/1234", StationPage.class, b -> b.putString("id", "1234"));
    assertResolve("/radio/stations/1234/genres", StationGenresPage.class, b -> b.putString("id", "1234"));
  }

  private PageMeta assertResolve(String uri, Class<?> pageClass) {
    final PageResolver pageResolver = new PageResolver();
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
