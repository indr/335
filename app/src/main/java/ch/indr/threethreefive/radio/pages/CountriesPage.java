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
import java.util.List;
import java.util.Locale;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.navigation.SpiceBasePage;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.CountriesRequest;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Country;

public class CountriesPage extends SpiceBasePage implements RequestListener<Country[]> {

  private List<Country> featuredCountries = new ArrayList<>();
  private List<PageItem> collapsedItems;
  private List<PageItem> expandedItems;

  public CountriesPage(Environment environment) {
    super(environment);

    featuredCountries.add(new Country("Australia"));
    featuredCountries.add(new Country("Canada"));
    featuredCountries.add(new Country("New Zealand"));
    featuredCountries.add(new Country("United Kingdom"));
    featuredCountries.add(new Country("United States of America"));
  }

  @Override public void onCreate(@NonNull Context context, Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    setTitle("Countries");
  }

  @Override public void onStart() {
    super.onStart();

    final PageItemsBuilder builder = pageItemsBuilder();

    for (Country country : featuredCountries) {
      builder.addLink("/radio/countries/" + country.getValue(), country.getName());
    }
    builder.addItem("Show all Countries", this::expandList);

    setPageItems(builder);
  }

  @Override public void onRequestFailure(SpiceException spiceException) {
    handle(spiceException);
  }

  @Override public void onRequestSuccess(Country[] response) {
    final PageItemsBuilder builder = pageItemsBuilder();

    for (Country each : response) {
      builder.addLink("/radio/countries/" + each.getValue(),
          each.getName(),
          String.format(Locale.US, "%d radio stations", each.getStationCount()));
    }

    setPageItems(builder);
  }

  private void expandList(Environment environment) {
    setLoading();
    executeRequest(new CountriesRequest(), this);
  }
}
