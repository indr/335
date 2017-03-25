/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.pages.radio;

import android.net.Uri;
import android.os.Bundle;

import org.junit.Test;

import java.util.List;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.TtfRobolectricTestCase;
import ch.indr.threethreefive.libs.PageItem;

public class LanguagesPageTests extends TtfRobolectricTestCase {

  @Test
  public void onRequestSuccess_withNullResponse_errorNoLanguagesFound() throws Exception {
    final LanguagesPage page = createPage();

    page.onRequestSuccess(null);

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(getString(R.string.no_languages_found_error), pageItems.get(0).getTitle());
  }

  private LanguagesPage createPage() {
    final LanguagesPage page = new LanguagesPage(environment());
    page.onCreate(context(), Uri.parse("/radio/languages"), new Bundle());
    return page;
  }
}