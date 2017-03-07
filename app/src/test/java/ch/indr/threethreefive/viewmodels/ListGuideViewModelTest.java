/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.viewmodels;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.junit.Test;

import java.util.List;

import ch.indr.threethreefive.ThreeThreeFiveRobolectricTestCase;
import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.libs.PageLink;
import ch.indr.threethreefive.ui.IntentKey;
import rx.observers.TestSubscriber;

public class ListGuideViewModelTest extends ThreeThreeFiveRobolectricTestCase {

  @Test
  public void testCanGoUp() throws Exception {
    final ListGuideViewModel vm = createVm(makeIntent());
    final TestSubscriber<Boolean> canGoUp = new TestSubscriber<>();

    vm.outputs.canGoUp().subscribe(canGoUp);

    canGoUp.assertValue(false);
  }

  @Test
  public void testPageItemClick() throws Exception {
    final ListGuideViewModel vm = createVm(makeIntent());
    final TestSubscriber<PageLink> showPage = new TestSubscriber<>();

    vm.outputs.showPage().subscribe(showPage);
    vm.pageItemClick(new PageLink("/page/link", null));

    showPage.assertValueCount(1);
  }

  @Test
  public void testPageTitle() throws Exception {
    final ListGuideViewModel vm = createVm(makeIntent());
    final TestSubscriber<String> pageTitle = new TestSubscriber<>();

    vm.outputs.pageTitle().subscribe(pageTitle);

    pageTitle.assertValues("Home");
  }

  @Test
  public void testPageItems() throws Exception {
    final ListGuideViewModel vm = createVm(makeIntent());
    final TestSubscriber<List<PageItem>> pageItems = new TestSubscriber<List<PageItem>>();

    vm.outputs.pageItems().subscribe(pageItems);

    pageItems.assertValueCount(1);
  }

  @NonNull private Intent makeIntent() {
    Intent intent = new Intent();
    intent.putExtra(IntentKey.PAGE_TITLE, "Intent");
    intent.putExtra(IntentKey.PAGE_URI, "/");
    return intent;
  }

  @NonNull private ListGuideViewModel createVm(final @Nullable Intent intent) {
    ListGuideViewModel vm = new ListGuideViewModel(environment());
    vm.onCreate(context(), null);
    if (intent != null) {
      vm.intent(intent);
    }
    return vm;
  }
}
