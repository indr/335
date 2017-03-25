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

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

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
import ch.indr.threethreefive.libs.pages.SpiceBasePage;
import ch.indr.threethreefive.services.UiModeManager;

public class StationPage extends SpiceBasePage implements RequestListener<Station[]> {

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

    apiClient.getStation(stationId, this);
  }

  @Override public void onRequestFailure(SpiceException spiceException) {
    handle(spiceException);
  }

  @Override public void onRequestSuccess(Station[] stations) {
    if (stations == null || stations.length != 1) {
      getString(R.string.station_not_found_error, this.stationId);
      return;
    }

    Station station = stations[0];

    // setTitle(station.getName());

    final PageItemsBuilder builder = pageItemsBuilder();

    MediaItem mediaItem = MediaItemFactory.make(station);
    builder.add(new PlayMedia(mediaItem));
    builder.add(new AddToPlaylist(mediaItem));
    builder.addToggleFavorite(getCurrentPageLink());

    // Country
    if (station.getCountry() != null) {
      builder.addLink("/radio/countries/" + station.getCountry(), "Country: " + station.getCountry());
    }

    // Language
    if (station.getLanguage() != null) {
      builder.addLink("/radio/languages/" + station.getLanguage(), "Language: " + station.getLanguage());
    }

    // Genres
    Collection<Genre> genres = station.getGenres();
    if (genres != null && genres.size() > 0) {
      List<String> genresAsString = new ArrayList<>();
      for (Genre genre : station.getGenres()) {
        genresAsString.add(genre.getName());
      }
      builder.addLink(String.format("/radio/stations/%s/genres", station.getId()), "Genres: " + StringUtils.join(genresAsString, ", "));
    }

    // Votes
    builder.addText(String.format(Locale.US, "Votes: %d (+%d/-%d)",
        station.getSummedVotes(), station.getPositiveVotes(), station.getNegativeVotes()));

    // Click count and trend
    builder.addText(String.format(Locale.US, "Clicks: %d (%+d)", station.getClickCount(), station.getClickTrend()));

    // Updated
    if (station.getLastChangeTime() != null) {
      DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
      builder.addText("Updated: " + dateFormat.format(station.getLastChangeTime()));
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
