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

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageUris;
import ch.indr.threethreefive.navigation.SpiceBasePage;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Tag;

public class CountryGenresPage extends SpiceBasePage implements RequestListener<List<Tag>> {

  private String countryId;

  public CountryGenresPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);
    component().inject(this);

    this.countryId = getUriParam("countryId");
  }

  @Override public void onStart() {
    super.onStart();

    apiClient.getGenresByCountry(countryId, this);
  }

  @Override public void onRequestFailure(SpiceException spiceException) {
    handle(spiceException);
  }

  @Override public void onRequestSuccess(List<Tag> tags) {
    if (tags == null) {

    }
    final PageItemsBuilder builder = pageItemsBuilder();
    builder.addToggleFavorite(getCurrentPageLink());

    for (Tag tag : tags) {
      builder.addLink(PageUris.makeCountryGenre(countryId, tag.getValue()),
          tag.getName());
    }

    builder.addText("Show all Genres");
    builder.addText("Show all Stations");

    setPageItems(builder);
  }
}
