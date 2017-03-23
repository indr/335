/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.pages.radio;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import org.junit.Test;

import ch.indr.threethreefive.TtfRobolectricTestCase;
import ch.indr.threethreefive.data.network.ApiClient;

import static org.mockito.Mockito.verify;

public class GenresPageTests extends TtfRobolectricTestCase {

  private ApiClient apiClient;

  @Override public void setUp() throws Exception {
    super.setUp();

    this.apiClient = appModule().apiClient();
  }

  @Test
  public void onStart_executesTagsRequest() {
    final GenresPage page = createPage();

    page.onStart();

    verify(apiClient).getTags(page);
  }

  @NonNull private GenresPage createPage() {
    final Bundle bundle = new Bundle();
    return createPage(bundle);
  }

  @NonNull private GenresPage createPage(@NonNull final Bundle bundle) {
    final GenresPage page = new GenresPage(environment());
    page.onCreate(context(), Uri.parse("/radio/genres/Jazz"), bundle);
    return page;
  }
}
