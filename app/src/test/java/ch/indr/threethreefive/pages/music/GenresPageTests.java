/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.pages.music;

import android.os.Bundle;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.TtfRobolectricTestCase;
import ch.indr.threethreefive.data.db.music.MusicStore;
import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.libs.PageUris;

import static org.mockito.Mockito.when;

public class GenresPageTests extends TtfRobolectricTestCase {

  private MusicStore musicStore;

  @Override public void setUp() throws Exception {
    super.setUp();

    this.musicStore = appModule().provideMusicStore(application());
  }

  @Test
  public void onCreate_whenNoGenresFound_addsNoGenresFound() {
    when(musicStore.queryGenres()).thenReturn(new ArrayList<>());

    final GenresPage page = createPage();

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(1, pageItems.size());
    assertEquals(getString(R.string.no_genres_found), pageItems.get(0).getTitle());
  }

  private GenresPage createPage() {
    final GenresPage page = new GenresPage(environment());
    page.onCreate(context(), PageUris.musicGenres(), new Bundle());
    return page;
  }
}