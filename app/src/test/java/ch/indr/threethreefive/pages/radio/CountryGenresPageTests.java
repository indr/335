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
import ch.indr.threethreefive.data.network.ApiClient;

import static org.mockito.Mockito.verify;

public class CountryGenresPageTests extends TtfRobolectricTestCase {

  private ApiClient apiClient;

  @Override public void setUp() throws Exception {
    super.setUp();

    this.apiClient = appModule().apiClient();
  }

  @Test(expected = IllegalArgumentException.class)
  public void onCreate_withoutCountryId_throws() {
    final Bundle bundle = new Bundle();
    try {
      createPage(bundle);
    } catch (Exception ex) {
      assertTrue(ex.getMessage().contains("key countryId"));
      throw ex;
    }
  }

  @Test
  public void onStart_getsGenresByCountry() {
    final CountryGenresPage page = createPage();

    page.onStart();

    verify(apiClient).getGenresByCountry("England", page);
  }

  @NonNull private CountryGenresPage createPage() {
    final Bundle bundle = new Bundle();
    bundle.putString("countryId", "England");
    return createPage(bundle);
  }

  @NonNull private CountryGenresPage createPage(Bundle bundle) {
    final CountryGenresPage page = new CountryGenresPage(environment());
    page.onCreate(context(), Uri.parse("/radio/countries/Country/genres"), bundle);
    return page;
  }
}
