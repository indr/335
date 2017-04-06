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

import java.util.Arrays;
import java.util.List;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.data.network.radioBrowser.model.Language;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageItemsExpander;
import ch.indr.threethreefive.libs.PageUris;
import ch.indr.threethreefive.libs.pages.SpiceBasePage;
import ch.indr.threethreefive.libs.utils.CollectionUtils;
import timber.log.Timber;

public class LanguagesPage extends SpiceBasePage implements RequestListener<Language[]> {

  private static final int MIN_STATION_COUNT_FOR_TOP_LANGUAGES = 5;

  private PageItemsExpander<Language> expander = new PageItemsExpander<>();

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

  @Override public void onRequestSuccess(Language[] languages) {
    if (languages == null) {
      handle(getString(R.string.no_languages_found_error));
      return;
    }

    populateLists(languages);
    showNextItems();
  }

  private void showNextItems() {
    final PageItemsBuilder builder = pageItemsBuilder();
    expander.buildNext(builder, this::addPageItems, this::showNextItems);

    resetFirstVisibleItem();
    setPageItems(builder);
  }

  private void addPageItems(PageItemsBuilder builder, List<Language> languages) {
    if (languages.size() == 0) {
      builder.addText(getString(R.string.no_langauges_found));
      return;
    }

    for (Language language : languages) {
      final int stationCount = language.getStationCount();
      final String subtitle = getResources().getQuantityString(R.plurals.radio_stations, stationCount, stationCount);
      builder.addLink(PageUris.radioLanguage(language.getValue()),
          language.getName(),
          subtitle,
          language.getName() + ", " + subtitle);
    }
  }

  private void populateLists(Language[] languages) {
    Timber.d("populateLists languages %d, %s", languages.length, this.toString());

    List<Language> allLanguages = Arrays.asList(languages);
    List<Language> topLanguages = CollectionUtils.filter(allLanguages, language ->
        language.getStationCount() >= MIN_STATION_COUNT_FOR_TOP_LANGUAGES);

    expander.add(topLanguages, getString(R.string.show_top_languages));
    expander.add(allLanguages, getString(R.string.show_all_langauges));
  }
}

