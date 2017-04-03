/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.services;

import android.view.accessibility.AccessibilityManager;

import org.junit.Test;

import ch.indr.threethreefive.TtfRobolectricTestCase;

import static android.accessibilityservice.AccessibilityServiceInfo.FEEDBACK_SPOKEN;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccessibilityServicesTests extends TtfRobolectricTestCase {

  private AccessibilityManager accessibilityManager;

  @Override public void setUp() throws Exception {
    super.setUp();

    accessibilityManager = mock(AccessibilityManager.class);
  }

  @Test
  public void isSpokenFeedbackEnabled_whenAccessibilityManagerReturnsNull_returnsFalse() throws Exception {
    when(accessibilityManager.getEnabledAccessibilityServiceList(FEEDBACK_SPOKEN)).thenReturn(null);
    final AccessibilityServices accessibilityServices = new AccessibilityServices(accessibilityManager);

    final boolean spokenFeedbackEnabled = accessibilityServices.isSpokenFeedbackEnabled();

    assertFalse(spokenFeedbackEnabled);
  }
}