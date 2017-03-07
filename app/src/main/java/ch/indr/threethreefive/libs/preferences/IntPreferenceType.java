/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.preferences;

public interface IntPreferenceType {
  /**
   * Get the current value of the preference.
   */
  int get();

  /**
   * Returns whether a value has been explicitly set for the preference.
   */
  boolean isSet();

  /**
   * Set the preference to a value.
   */
  void set(int value);

  /**
   * Delete the currently stored preference.
   */
  void delete();

  /**
   * Increments the preferences value and returns the new value.
   */
  int increment();
}
