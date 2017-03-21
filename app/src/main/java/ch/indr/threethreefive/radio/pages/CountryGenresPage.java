/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.radio.pages;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import ch.indr.threethreefive.data.network.ApiClient;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.navigation.SpiceBasePage;

public class CountryGenresPage extends SpiceBasePage {

  private ApiClient apiClient;
  private String countryId;

  public CountryGenresPage(Environment environment) {
    super(environment);

  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);
    component().inject(this);

    this.countryId = getUriParam("countryId");
  }

  @Override public void onStart() {
    super.onStart();
  }
}