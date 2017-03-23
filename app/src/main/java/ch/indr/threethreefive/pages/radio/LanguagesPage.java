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

import java.util.Locale;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageUris;
import ch.indr.threethreefive.libs.pages.SpiceBasePage;
import ch.indr.threethreefive.data.network.radioBrowser.model.Language;

public class LanguagesPage extends SpiceBasePage implements RequestListener<Language[]> {

  public LanguagesPage(Environment environment) {
    super(environment);

  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);
    component().inject(this);

    setTitle(getString(R.string.languages));
  }

  @Override public void onStart() {
    super.onStart();

    apiClient.getLanguages(this);
  }

  @Override public void onRequestFailure(SpiceException spiceException) {
    handle(spiceException);
  }

  @Override public void onRequestSuccess(Language[] languages) {
    if (languages == null) {
      handle(getString(R.string.no_languages_found_error));
      return;
    }

    final PageItemsBuilder builder = pageItemsBuilder();
    addLanguageLinks(builder, languages);
    setPageItems(builder);
  }

  private void addLanguageLinks(PageItemsBuilder builder, Language[] languages) {
    if (languages.length == 0) {
      builder.addText(getString(R.string.no_langauges_found));
      return;
    }

    for (Language language : languages) {
      final String subtitle = String.format(Locale.US, "%d radio stations", language.getStationCount());
      builder.addLink(PageUris.radioLanguage(language.getValue()),
          language.getName(),
          subtitle,
          language.getName() + ", " + subtitle);
    }
  }
}

