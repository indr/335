/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.junit.Test;

import ch.indr.threethreefive.TtfRobolectricTestCase;
import rx.observers.TestSubscriber;

public class PageItemTests extends TtfRobolectricTestCase {

  @Test
  public void createWithTitle() {
    final TestPageItem pageItem = new TestPageItem("Title");

    assertPageItem(pageItem, "Title", null, "Title", null, 0);
  }

  @Test
  public void createWithTitleSubtitleAndDescription() {
    final TestPageItem pageItem = new TestPageItem("Title", "Subtitle", "Description");

    assertPageItem(pageItem, "Title", "Subtitle", "Description", null, 0);
  }

  @Test
  public void createWithTitleSubTitleDescriptionAndIconUri() {
    final TestPageItem pageItem = new TestPageItem("Title", "Subtitle", "Description", Uri.EMPTY, 99);

    assertPageItem(pageItem, "Title", "Subtitle", "Description", Uri.EMPTY, 99);
  }

  private void assertPageItem(TestPageItem pageItem, String pTitle, String pSubtitle, String pDescription,
                              Uri pIconUri, int pDefaultIconResId) {
    final TestSubscriber<String> title = new TestSubscriber<>();
    pageItem.title().subscribe(title);
    final TestSubscriber<String> subtitle = new TestSubscriber<>();
    pageItem.subtitle().subscribe(subtitle);
    final TestSubscriber<String> description = new TestSubscriber<>();
    pageItem.description().subscribe(description);
    final TestSubscriber<Uri> iconUri = new TestSubscriber<>();
    pageItem.iconUri().subscribe(iconUri);

    assertEquals(pTitle, pageItem.getTitle());
    assertEquals(pSubtitle, pageItem.getSubtitle());
    assertEquals(pDescription, pageItem.getDescription());
    assertEquals(pIconUri, pageItem.getIconUri());
    assertEquals(pDefaultIconResId, pageItem.getDefaultIconResId());

    title.assertValue(pTitle);
    subtitle.assertValue(pSubtitle);
    description.assertValue(pDescription);
    iconUri.assertValue(pIconUri);
  }
}

class TestPageItem extends PageItem {

  protected TestPageItem(final @NonNull String title) {
    super(title);
  }

  protected TestPageItem(final @NonNull String title, final @Nullable String subtitle,
                         final @NonNull String description) {
    super(title, subtitle, description);
  }

  public TestPageItem(final @NonNull String title, final @Nullable String subtitle,
                      final @NonNull String description, final @Nullable Uri iconUri,
                      final int defaultIconResId) {
    super(title, subtitle, description, iconUri, defaultIconResId);
  }
}
