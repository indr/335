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

import java.text.DateFormat;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.utils.StringUtils;
import ch.indr.threethreefive.navigation.SpiceBasePage;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.StationsRequest;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Station;

public class RecentsPage extends SpiceBasePage implements RequestListener<Station[]> {

  private DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

  public RecentsPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    setTitle("New Stations");
  }

  @Override public void onStart() {
    super.onStart();

    executeRequest(StationsRequest.recent(50), this);
  }

  @Override public void onRequestFailure(SpiceException spiceException) {
    handle(spiceException);
  }

  @Override public void onRequestSuccess(Station[] stations) {
    final PageItemsBuilder builder = pageItemsBuilder();
    builder.addToggleFavorite(getCurrentPageLink());

    for (Station station : stations) {
      builder.addLink("/radio/stations/" + station.getId(),
          station.getName(), makeSubtitle(station));
    }

    setPageItems(builder);
  }

  private String makeSubtitle(Station station) {

    final String updated = "Updated: " + dateFormat.format(station.getLastChangeTime());
    final String country = station.getCountry();
    final String language = station.getLanguage();
    if (StringUtils.isNotEmpty(country) && StringUtils.isNotEmpty(language)) {
      return updated + String.format(", Country: %s, Language: %s", country, language);
    }
    if (StringUtils.isNotEmpty(country)) {
      return updated + ", Country: " + country;
    }
    if (StringUtils.isNotEmpty(language)) {
      return updated + ", Language: " + language;
    }
    return updated;
  }
}
