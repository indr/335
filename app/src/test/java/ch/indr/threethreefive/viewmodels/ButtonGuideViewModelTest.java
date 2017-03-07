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

import ch.indr.threethreefive.ThreeThreeFiveRobolectricTestCase;
import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.ui.IntentKey;
import rx.observers.TestSubscriber;

public class ButtonGuideViewModelTest extends ThreeThreeFiveRobolectricTestCase {

  @Test
  public void testActivityTitle() throws Exception {
    final ButtonGuideViewModel vm = createVm(makeIntent());
    final TestSubscriber<String> activityTitle = new TestSubscriber<>();

    vm.activityTitle().subscribe(activityTitle);

    activityTitle.assertValues("Home");
  }

  @Test
  public void testCanGoUp() throws Exception {
    final ButtonGuideViewModel vm = createVm(makeIntent());
    final TestSubscriber<Boolean> canGoUp = new TestSubscriber<>();

    vm.outputs.canGoUp().subscribe(canGoUp);

    canGoUp.assertValue(false);
  }

  @Test
  public void testPageItem() throws Exception {
    final ButtonGuideViewModel vm = createVm(makeIntent());
    final TestSubscriber<PageItem> pageItem = new TestSubscriber<>();

    vm.outputs.pageItem().subscribe(pageItem);
    vm.stepRight();

    pageItem.assertValueCount(2);
  }

  @NonNull private Intent makeIntent() {
    Intent intent = new Intent();
    intent.putExtra(IntentKey.PAGE_TITLE, "Intent");
    intent.putExtra(IntentKey.PAGE_URI, "/");
    return intent;
  }

  @NonNull private ButtonGuideViewModel createVm(final @Nullable Intent intent) {
    ButtonGuideViewModel vm = new ButtonGuideViewModel(environment());
    vm.onCreate(context(), null);
    if (intent != null) {
      vm.intent(intent);
    }
    return vm;
  }
}
