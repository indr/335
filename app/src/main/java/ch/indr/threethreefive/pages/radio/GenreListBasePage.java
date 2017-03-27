/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.pages.radio;

import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.data.network.radioBrowser.model.Genre;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageItemsExpander;
import ch.indr.threethreefive.libs.pages.SpiceBasePage;

abstract class GenreListBasePage extends SpiceBasePage implements RequestListener<List<Genre>> {

  protected static final int MAX_NUMBER_OF_TOP_GENRES = 15;
  protected static final int MAX_NUMBER_OF_MORE_GENRES = 50;

  protected PageItemsExpander<Genre> expander = new PageItemsExpander<>();

  public GenreListBasePage(Environment environment) {
    super(environment);
  }

  @Override public void onRequestSuccess(List<Genre> response) {
    if (response == null) {
      handle(getString(R.string.no_genres_found_error));
      return;
    }

    populateLists(response);
    showNextItems();
  }

  private void showNextItems() {
    final PageItemsBuilder builder = pageItemsBuilder();
    expander.buildNext(builder, this::addPageItems, this::showNextItems);

    resetFirstVisibleItem();
    setPageItems(builder);
  }

  protected abstract void addPageItems(PageItemsBuilder builder, List<Genre> genres);

  protected abstract void populateLists(List<Genre> genres);
}
