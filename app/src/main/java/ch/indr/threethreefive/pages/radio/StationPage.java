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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.commands.AddToPlaylist;
import ch.indr.threethreefive.commands.OpenWebsite;
import ch.indr.threethreefive.commands.PlayMedia;
import ch.indr.threethreefive.data.MediaItemFactory;
import ch.indr.threethreefive.data.network.radioBrowser.model.Genre;
import ch.indr.threethreefive.data.network.radioBrowser.model.Station;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.MediaItem;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageUris;
import ch.indr.threethreefive.libs.pages.SpiceBasePage;
import ch.indr.threethreefive.libs.utils.StringUtils;
import ch.indr.threethreefive.services.UiModeManager;

public class StationPage extends SpiceBasePage implements RequestListener<Station> {

  private String stationId;

  protected @Inject UiModeManager uiModeManager;

  public StationPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);
    component().inject(this);

    this.stationId = bundle.getString("id");
  }

  @Override public void onStart() {
    super.onStart();

    apiClient.getStation(stationId, true, this);
  }

  @Override public void onRequestSuccess(Station station) {
    if (station == null) {
      getString(R.string.station_not_found_error, this.stationId);
      return;
    }

    setDescription(station.getName());
    setIconUri(station.getLogoUri());

    addPageItems(station);
  }

  private void addPageItems(final @NonNull Station station) {
    final PageItemsBuilder builder = pageItemsBuilder();

    MediaItem mediaItem = MediaItemFactory.make(station);
    if (station.isBroken()) {
      builder.addText(getString(R.string.item_station_is_broken_title),
          getString(R.string.item_station_is_broken_subtitle),
          getString(R.string.item_station_is_broken_description));
    } else {
      builder.add(new PlayMedia(getString(R.string.press_to_play), mediaItem));
      builder.add(new AddToPlaylist(getString(R.string.add_to_playlist), mediaItem));
    }
    builder.addToggleFavorite(getCurrentPageLink());

    // Country
    if (StringUtils.isNotEmpty(station.getCountry())) {
      builder.addLink(PageUris.radioCountry(station.getCountry()),
          getString(R.string.station_country, station.getCountry()));
    }

    // Language
    if (StringUtils.isNotEmpty(station.getLanguage())) {
      builder.addLink(PageUris.radioLanguage(station.getLanguage()),
          getString(R.string.station_language, station.getLanguage()));
    }

    // Genres
    Collection<Genre> genres = station.getGenres();
    if (genres != null && genres.size() > 0) {
      List<String> genresAsString = new ArrayList<>();
      for (Genre genre : station.getGenres()) {
        genresAsString.add(genre.getName());
      }
      builder.addLink(PageUris.radioStationGenres(station.getId()),
          getString(R.string.station_genres, StringUtils.join(genresAsString, ", ")));
    }

    // Votes
    builder.addText(getString(R.string.station_votes, station.getSummedVotes(),
        station.getPositiveVotes(), station.getNegativeVotes()));

    // Click count and trend
    builder.addText(getString(R.string.station_clicks, station.getClickCount(), station.getClickTrend()));

    // Updated
    if (station.getLastChangeTime() != null) {
      DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
      builder.addText(getString(R.string.station_updated, dateFormat.format(station.getLastChangeTime())));
      ;
    }

    // Link to Website
    if (!isButtonView() && station.getHomepage() != null) {
      builder.add(new OpenWebsite(getContext(), station.getHomepage()));
    }

    setPageItems(builder);
  }

  private boolean isButtonView() {
    return uiModeManager.getCurrentUiMode() == UiModeManager.UI_MODE_BUTTONS;
  }
}
