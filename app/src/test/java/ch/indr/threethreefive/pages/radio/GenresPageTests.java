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
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;

import ch.indr.threethreefive.TtfRobolectricTestCase;
import ch.indr.threethreefive.libs.net.RobospiceManager;
import ch.indr.threethreefive.radio.pages.GenresPage;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.TagsRequest;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class GenresPageTests extends TtfRobolectricTestCase {

  private RobospiceManager robospiceManager;

  @Override public void setUp() throws Exception {
    super.setUp();

    this.robospiceManager = appModule().provideRobospiceManager();
  }

  @Test
  public void onStart_executesTagsRequest() {
    final GenresPage page = createPage();

    page.onStart();

    verify(robospiceManager).getFromCacheAndLoadFromNetworkIfExpired(
        ArgumentMatchers.argThat((ArgumentMatcher<TagsRequest>) argument ->
            argument.getQuery().equals("hidebroken=true&order=stationcount")),
        anyString(), anyLong(), eq(page));
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
