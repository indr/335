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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.navigation.SpiceBasePage;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.TagsRequest;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.NameComparator;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.StationCountComparator;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Tag;

public class GenresPage extends SpiceBasePage implements RequestListener<Tag[]> {

  public GenresPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    setTitle("Genres");
  }

  @Override public void onStart() {
    super.onStart();

    executeRequest(new TagsRequest("hidebroken=true&order=stationcount"), this);
  }

  @Override public void onRequestFailure(SpiceException spiceException) {
    handle(spiceException);
  }

  @Override public void onRequestSuccess(Tag[] response) {
    final PageItemsBuilder builder = pageItemsBuilder();

    List<Tag> tags = new ArrayList<>();
    for (Tag tag : response) {
      if (tag.getStationCount() > 10) {
        tags.add(tag);
      }
    }

    Collections.sort(tags, new StationCountComparator());
    if (tags.size() > 0) {
      tags = tags.subList(0, Math.min(30, tags.size()));
    }
    Collections.sort(tags, new NameComparator());

    for (Tag each : tags) {
      builder.addLink("/radio/genres/" + each.getValue(), String.format(Locale.US, "%s (%d)", each.getName(), each.getStationCount()));
    }

    setPageItems(builder);
  }
}
