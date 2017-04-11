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

import ch.indr.threethreefive.TtfRobolectricTestCase;
import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.libs.Preferences;
import ch.indr.threethreefive.services.UiModeManager;
import ch.indr.threethreefive.ui.IntentKey;
import rx.observers.TestSubscriber;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ButtonGuideViewModelTest extends TtfRobolectricTestCase {

  @Override public void setUp() throws Exception {
    super.setUp();

    UiModeManager uiModeManager = appModule().provideUiModeManager(context(), mock(Preferences.class));
    when(uiModeManager.getCurrentUiMode()).thenReturn(UiModeManager.UI_MODE_BUTTONS);
  }

  @Test
  public void activityTitle_afterCreation_equalsMusic() throws Exception {
    final ButtonGuideViewModel vm = createVm(makeIntent());
    final TestSubscriber<String> activityTitle = new TestSubscriber<>();
    vm.outputs.activityTitle().subscribe(activityTitle);

    activityTitle.assertValues("Music");
  }

  @Test
  public void canGoUp_afterCreation_isFalse() throws Exception {
    final ButtonGuideViewModel vm = createVm(makeIntent());
    final TestSubscriber<Boolean> canGoUp = new TestSubscriber<>();
    vm.outputs.canGoUp().subscribe(canGoUp);

    canGoUp.assertValue(false);
  }

  @Test
  public void pageItem_whenStepRight_returnsNextValue() throws Exception {
    final ButtonGuideViewModel vm = createVm(makeIntent());
    final TestSubscriber<PageItem> pageItem = new TestSubscriber<>();
    vm.outputs.pageItem().subscribe(pageItem);

    vm.inputs.stepRight();

    pageItem.assertValueCount(2);
  }


  @NonNull private Intent makeIntent() {
    final Intent intent = new Intent();
    intent.putExtra(IntentKey.PAGE_TITLE, "Intent");
    intent.putExtra(IntentKey.PAGE_URI, "/");
    return intent;
  }

  @NonNull private ButtonGuideViewModel createVm(final @Nullable Intent intent) {
    final ButtonGuideViewModel vm = new ButtonGuideViewModel(environment());
    vm.onCreate(context(), null);
    if (intent != null) {
      vm.intent(intent);
    }
    return vm;
  }
}
