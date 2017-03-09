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

import java.util.Locale;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.navigation.SpiceBasePage;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.TagsRequest;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Tag;

public class TagsPage extends SpiceBasePage implements RequestListener<Tag[]> {

  public TagsPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    setTitle("Tags");
  }

  @Override public void onStart() {
    super.onStart();

    executeRequest(new TagsRequest("hidebroken=true&order=value"), this);
  }

  @Override public void onRequestFailure(SpiceException spiceException) {
    handle(spiceException);
  }

  @Override public void onRequestSuccess(Tag[] tags) {
    final PageItemsBuilder builder = pageItemsBuilder();

    for (Tag each : tags) {
      if (each.getStationCount() > 1) {
        builder.addLink("/radio/tags/" + each.getValue(),
            each.getName(),
            String.format(Locale.US, "%d radio stations", each.getStationCount()));
      }
    }

    setPageItems(builder);
  }
}
