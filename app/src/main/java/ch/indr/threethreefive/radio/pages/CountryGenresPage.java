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

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;
import java.util.Locale;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageItemsExpander;
import ch.indr.threethreefive.libs.PageUris;
import ch.indr.threethreefive.navigation.SpiceBasePage;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Tag;

public class CountryGenresPage extends SpiceBasePage implements RequestListener<List<Tag>> {

  private String countryId;

  private PageItemsExpander<Tag> expander = new PageItemsExpander<>();

  public CountryGenresPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);
    component().inject(this);

    this.countryId = getUriParam("countryId");
    setTitle(countryId);
  }

  @Override public void onStart() {
    super.onStart();

    apiClient.getGenresByCountry(countryId, this);
  }

  @Override public void onRequestFailure(SpiceException spiceException) {
    handle(spiceException);
  }

  @Override public void onRequestSuccess(List<Tag> response) {
    if (response == null) {
      handle(getString(R.string.no_genres_found_error));
      return;
    }

    populateLists(response);
    showNextItems();
  }

  private void showNextItems() {
    final PageItemsBuilder builder = pageItemsBuilder();
    builder.addToggleFavorite(getCurrentPageLink());
    expander.buildNext(builder, this::addGenreLinks, this::showNextItems);
    builder.addLink(PageUris.radioCountryStations(countryId), getString(R.string.show_all_stations));

    resetFirstVisibleItem();
    setPageItems(builder);
  }

  private void addGenreLinks(PageItemsBuilder builder, List<Tag> tags) {
    if (tags.size() == 0) {
      builder.addText(getString(R.string.no_genres_found));
      return;
    }

    for (Tag each : tags) {
      final String subtitle = String.format(Locale.US, "%d radio stations", each.getStationCount());
      builder.addLink(PageUris.radioCountryGenre(countryId, each.getValue()),
          each.getName(), subtitle, each.getName() + ", " + subtitle);
    }
  }

  private void populateLists(List<Tag> tags) {
    expander.add(tags, getString(R.string.show_top_genres));
  }
}
