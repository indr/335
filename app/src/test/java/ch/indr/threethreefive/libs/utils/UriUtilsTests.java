/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.utils;

import android.net.Uri;

import org.junit.Test;

import ch.indr.threethreefive.TtfRobolectricTestCase;

public class UriUtilsTests extends TtfRobolectricTestCase {
  @Test
  public void tryParse_null_returnsNull() throws Exception {
    final Uri uri = UriUtils.tryParse(null);

    assertEquals(null, uri);
  }

  @Test
  public void tryParse_empty_returnsEmpty() throws Exception {
    final Uri uri = UriUtils.tryParse("");

    assertEquals(Uri.EMPTY, uri);
  }
}