/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.pages.radio;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.data.network.radioBrowser.model.Country;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageItemsExpander;
import ch.indr.threethreefive.libs.PageUris;
import ch.indr.threethreefive.libs.pages.SpiceBasePage;
import ch.indr.threethreefive.libs.utils.CollectionUtils;
import timber.log.Timber;

public class CountriesPage extends SpiceBasePage implements RequestListener<Country[]> {

  private List<String> featuredCountries = Arrays.asList("Australia", "Canada", "New Zealand", "United Kingdom", "United States of America");
  private static final int MIN_STATIONS_FOR_MORE_COUNTRIES = 5;

  private PageItemsExpander<Country> expander = new PageItemsExpander<>();

  public CountriesPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);
    component().inject(this);

    setDescription(getString(R.string.radio_countries_title));
  }

  @Override public void onStart() {
    super.onStart();

    apiClient.getCountries(this);
  }

  @Override public void onRequestSuccess(Country[] response) {
    if (response == null) {
      handle(getString(R.string.no_countries_found_error));
      return;
    }

    populateLists(response);
    showNextItems();
  }

  private void showNextItems() {
    final PageItemsBuilder builder = pageItemsBuilder();
    expander.buildNext(builder, this::addPageItems, this::showNextItems);

    resetFirstVisibleItem();
    setPageItems(builder);
  }

  private void addPageItems(PageItemsBuilder builder, List<Country> countries) {
    if (countries.size() == 0) {
      builder.addText(getString(R.string.no_countries_found));
      return;
    }

    for (Country country : countries) {
      final int stationCount = country.getStationCount();
      final String subtitle = getResources().getQuantityString(R.plurals.radio_stations, stationCount, stationCount);
      builder.addLink(PageUris.radioCountryGenres(country.getValue()),
          country.getName(),
          subtitle,
          country.getName() + ", " + subtitle);
    }
  }

  private void populateLists(Country[] response) {
    Timber.d("populateLists countries %d, %s", response.length, this.toString());

    List<Country> allCountries = Arrays.asList(response);
    List<Country> moreCountries = CollectionUtils.filter(allCountries, country ->
        country.getStationCount() >= MIN_STATIONS_FOR_MORE_COUNTRIES);
    List<Country> topCountries = new ArrayList<>();

    for (Country country : allCountries) {
      if (featuredCountries.contains(country.getValue())) {
        topCountries.add(country);
      }
    }

    expander.add(topCountries, getString(R.string.show_top_countries));
    expander.add(moreCountries, getString(R.string.show_more_countries));
    expander.add(allCountries, getString(R.string.show_all_countries));
  }
}
