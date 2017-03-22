/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.radio.pages;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageItemsExpander;
import ch.indr.threethreefive.libs.PageUris;
import ch.indr.threethreefive.navigation.SpiceBasePage;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.CountriesRequest;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Country;
import timber.log.Timber;

public class CountriesPage extends SpiceBasePage implements RequestListener<Country[]> {

  private List<String> featuredCountries = Arrays.asList("Australia", "Canada", "New Zealand", "United Kingdom", "United States of America");

  private PageItemsExpander<Country> expander = new PageItemsExpander<>();

  public CountriesPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);
    component().inject(this);

    setTitle(getString(R.string.countries));
  }

  @Override public void onStart() {
    super.onStart();

    executeRequest(new CountriesRequest(), this);
  }

  @Override public void onRequestFailure(SpiceException spiceException) {
    handle(spiceException);
  }

  @Override public void onRequestSuccess(Country[] response) {
    if (response == null) {
      handle(R.string.no_countries_found_error);
      return;
    }

    populateLists(response);
    showNextItems(null);
  }

  private void showNextItems(Environment environment) {
    final PageItemsBuilder builder = pageItemsBuilder();
    expander.buildNext(builder, this::addCountryLinks, this::showNextItems);

    resetFirstVisibleItem();
    setPageItems(builder);
  }

  private void addCountryLinks(PageItemsBuilder builder, List<Country> countries) {
    if (countries.size() == 0) {
      builder.addText(getString(R.string.no_countries_found));
      return;
    }

    for (Country country : countries) {
      final String subtitle = String.format(Locale.US, "%d radio stations", country.getStationCount());
      builder.addLink(PageUris.radioCountryGenres(country.getValue()),
          country.getName(),
          subtitle,
          country.getName() + ", " + subtitle);
    }
  }

  private void populateLists(Country[] response) {
    Timber.d("populateLists countries %d, %s", response.length, this.toString());

    List<Country> allCountries = Arrays.asList(response);
    List<Country> topCountries = new ArrayList<>();

    for (Country country : allCountries) {
      if (featuredCountries.contains(country.getValue())) {
        topCountries.add(country);
      }
    }

    expander.add(topCountries, getString(R.string.show_top_countries));
    expander.add(allCountries, getString(R.string.show_all_countries));
  }
}
