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

import ch.indr.threethreefive.TtfRobolectricTestCase;

public class CountryGenrePageTests extends TtfRobolectricTestCase {

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

  @NonNull private CountryGenrePage createPage(Bundle bundle) {
    final CountryGenrePage page = new CountryGenrePage(environment());
    page.onCreate(context(), Uri.parse("/radio/countries/Country/genres/Genre"), bundle);
    return page;
  }
}
