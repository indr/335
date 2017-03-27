/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.pages.radio;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.octo.android.robospice.request.listener.RequestListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.data.network.radioBrowser.model.Station;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageItemsExpander;
import ch.indr.threethreefive.libs.pages.SpiceBasePage;
import ch.indr.threethreefive.libs.utils.CollectionUtils;
import timber.log.Timber;

public abstract class StationListBasePage extends SpiceBasePage implements RequestListener<List<Station>> {

  protected PageItemsExpander<Station> expander = new PageItemsExpander<>();

  public StationListBasePage(Environment environment) {
    super(environment);
  }

  @Override public void onRequestSuccess(List<Station> response) {
    if (response == null) {
      handle(R.string.no_stations_found_error);
      return;
    }

    populateLists(response);
    showNextItems();
  }

  protected void populateLists(@NonNull List<Station> allStations) {
    Timber.d("populateLists stations %d, %s", allStations.size(), this.toString());

    final Comparator<Station> stationsComparator = getStationsComparator();
    if (stationsComparator != null) {
      Collections.sort(allStations, stationsComparator);
    }
    List<Station> topStations = CollectionUtils.slice(allStations, 0, 15);
    List<Station> moreStations = CollectionUtils.slice(allStations, 0, 50);

    Collections.sort(topStations, new Station.NameComparator());
    Collections.sort(moreStations, new Station.NameComparator());
    Collections.sort(allStations, new Station.NameComparator());

    expander.add(topStations, getString(R.string.show_top_stations));
    expander.add(moreStations, getString(R.string.show_more_stations));
    expander.add(allStations, getString(R.string.show_all_stations));
  }

  @Nullable protected Comparator<Station> getStationsComparator() {
    return Station.getBestStationsComparator();
  }

  protected void showNextItems() {
    final PageItemsBuilder builder = pageItemsBuilder();
    builder.addToggleFavorite(getCurrentPageLink());
    expander.buildNext(builder, this::addPageItems, this::showNextItems);

    resetFirstVisibleItem();
    setPageItems(builder);
  }

  protected abstract void addPageItems(PageItemsBuilder builder, List<Station> stations);
}
