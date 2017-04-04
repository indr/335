/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.junit.Test;

import ch.indr.threethreefive.TtfRobolectricTestCase;
import rx.observers.TestSubscriber;

public class PageItemTests extends TtfRobolectricTestCase {

  @Test
  public void createWithTitle() {
    final TestPageItem pageItem = new TestPageItem("Title");

    final TestSubscriber<String> title = new TestSubscriber<>();
    pageItem.title().subscribe(title);
    final TestSubscriber<String> subtitle = new TestSubscriber<>();
    pageItem.subtitle().subscribe(subtitle);
    final TestSubscriber<String> description = new TestSubscriber<>();
    pageItem.description().subscribe(description);

    assertEquals("Title", pageItem.getTitle());
    assertEquals(null, pageItem.getSubtitle());
    assertEquals("Title", pageItem.getDescription());

    title.assertValue("Title");
    subtitle.assertValue(null);
    description.assertValue("Title");
  }

  @Test
  public void createWithTitleSubtitleAndDescription() {
    final TestPageItem pageItem = new TestPageItem("Title", "Subtitle", "Description");

    final TestSubscriber<String> title = new TestSubscriber<>();
    pageItem.title().subscribe(title);
    final TestSubscriber<String> subtitle = new TestSubscriber<>();
    pageItem.subtitle().subscribe(subtitle);
    final TestSubscriber<String> description = new TestSubscriber<>();
    pageItem.description().subscribe(description);

    assertEquals("Title", pageItem.getTitle());
    assertEquals("Subtitle", pageItem.getSubtitle());
    assertEquals("Description", pageItem.getDescription());

    title.assertValue("Title");
    subtitle.assertValue("Subtitle");
    description.assertValue("Description");
  }
}

class TestPageItem extends PageItem {

  protected TestPageItem(@NonNull String title) {
    super(title);
  }

  protected TestPageItem(final @NonNull String title, final @Nullable String subtitle, final @NonNull String description) {
    super(title, subtitle, description);
  }
}
