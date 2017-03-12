/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.radio.pages;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.navigation.SpiceBasePage;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.CountriesRequest;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Country;
import timber.log.Timber;

public class CountriesPage extends SpiceBasePage implements RequestListener<Country[]> {

  private List<String> featuredCountries = Arrays.asList("Australia", "Canada", "New Zealand", "United Kingdom", "United States of America");

  private List<Country> allCountries;
  private List<Country> topCountries;

  public CountriesPage(Environment environment) {
    super(environment);

    setTitle("Countries");
  }

  @Override public void onStart() {
    super.onStart();

    executeRequest(new CountriesRequest(), this);
  }

  @Override public void onRequestFailure(SpiceException spiceException) {
    handle(spiceException);
  }

  @Override public void onRequestSuccess(Country[] response) {
    populateLists(response);
    showTopCountries();
  }

  private void showTopCountries() {
    final PageItemsBuilder builder = pageItemsBuilder();
    addCountryLinks(builder, topCountries);
    builder.addItem("Show all Countries", this::showAllCountries);
    setPageItems(builder);
  }

  private void showAllCountries(Environment environment) {
    resetFirstVisibleItem();
    final PageItemsBuilder builder = pageItemsBuilder();
    addCountryLinks(builder, allCountries);
    setPageItems(builder);
  }

  private void addCountryLinks(PageItemsBuilder builder, List<Country> countries) {
    for (Country country : countries) {
      final String subtitle = String.format(Locale.US, "%d radio stations", country.getStationCount());
      builder.addLink("/radio/countries/" + country.getValue(),
          country.getName(), subtitle, country.getName() + ", " + subtitle);
    }
  }

  private void populateLists(Country[] response) {
    Timber.d("populateLists countries %d, %s", response.length, this.toString());

    this.allCountries = Arrays.asList(response);
    this.topCountries = new ArrayList<>();

    for (Country country : allCountries) {
      if (featuredCountries.contains(country.getValue())) {
        topCountries.add(country);
      }
    }
  }
}
