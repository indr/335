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
import ch.indr.threethreefive.radio.radioBrowserInfo.api.LanguagesRequest;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Language;

public class LanguagesPage extends SpiceBasePage implements RequestListener<Language[]> {

  public LanguagesPage(Environment environment) {
    super(environment);

    setTitle("Languages");
  }

  @Override public void onCreate(@NonNull Context context, Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);
    component().inject(this);
  }

  @Override public void onStart() {
    super.onStart();

    executeRequest(new LanguagesRequest(), this);
  }

  @Override public void onRequestFailure(SpiceException spiceException) {
    handle(spiceException);
  }

  @Override public void onRequestSuccess(Language[] languages) {
    final PageItemsBuilder builder = pageItemsBuilder();

    for (Language language : languages) {
      final String subtitle = String.format(Locale.US, "%d radio stations", language.getStationCount());
      builder.addLink("/radio/languages/" + language.getValue(),
          language.getName(), subtitle, language.getName() + ", " + subtitle);
    }

    setPageItems(builder);
  }
}

