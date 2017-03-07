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

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;

import ch.indr.threethreefive.commands.AddToPlaylist;
import ch.indr.threethreefive.commands.OpenWebsite;
import ch.indr.threethreefive.commands.PlayMedia;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.MediaItem;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.navigation.SpiceBasePage;
import ch.indr.threethreefive.radio.RadioMediaItemFactory;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.StationRequest;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Station;

public class StationPage extends SpiceBasePage implements RequestListener<Station[]> {

  private String stationId;

  public StationPage(Environment environment) {
    super(environment);

    setTitle("Radiostation");
  }

  @Override public void onCreate(@NonNull Context context, Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    this.stationId = bundle.getString("id");
  }

  @Override public void onStart() {
    super.onStart();

    executeRequest(new StationRequest(stationId), this);
  }

  @Override public void onRequestFailure(SpiceException spiceException) {
    handle(spiceException);
  }

  @Override public void onRequestSuccess(Station[] stations) {
    if (stations.length != 1) {
      handle(String.format("Could not find radion station %s.", this.stationId));
      return;
    }

    Station station = stations[0];

    setTitle(station.getName());

    final PageItemsBuilder builder = pageItemsBuilder();

    MediaItem mediaItem = RadioMediaItemFactory.make(station);
    builder.addItem(new PlayMedia(mediaItem));
    builder.addItem(new AddToPlaylist(mediaItem));
    builder.addToggleFavorite(getCurrentPageLink());

    if (station.getCountry() != null) {
      builder.addLink("/radio/countries/" + station.getCountry(), "Country: " + station.getCountry());
    }
    if (station.getLanguage() != null) {
      builder.addLink("/radio/languages/" + station.getLanguage(), "Language: " + station.getLanguage());
    }
    if (station.getTags() != null) {
      builder.addLink(String.format("/radio/stations/%s/tags", station.getId()), "Tags: " + StringUtils.join(station.getTags(), ", "));
    }

    builder.addText("Votes: " + station.getSummedVotes());
    if (station.getLastChangeTime() != null) {

      DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
      builder.addText("Updated: " + dateFormat.format(station.getLastChangeTime()));
    }

    if (station.getHomepage() != null) {
      builder.addItem(new OpenWebsite(getContext(), station.getHomepage()));
    }

    setPageItems(builder);
  }
}
