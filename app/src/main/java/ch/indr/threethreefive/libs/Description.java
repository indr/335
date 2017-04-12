/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ch.indr.threethreefive.libs.utils.StringUtils;

public class Description {
  public static final Description EMPTY = new Description("");

  private final String title;
  private final String subtitle;
  private final String contentDescription;

  public Description(@NonNull final String title) {
    this.title = title;
    this.subtitle = null;
    this.contentDescription = null;
  }

  public Description(@NonNull final String title, @Nullable final String subtitle) {
    this.title = title;
    this.subtitle = subtitle;
    this.contentDescription = null;
  }

  public Description(@NonNull final String title, @Nullable final String subtitle,
                     @Nullable final String contentDescription) {
    this.title = title;
    this.subtitle = subtitle;
    this.contentDescription = contentDescription;
  }

  @NonNull public String getTitle() {
    return title;
  }

  @NonNull public Description setTitle(final @NonNull String title) {
    return new Description(title, subtitle, contentDescription);
  }

  @Nullable public String getSubtitle() {
    return subtitle;
  }

  @NonNull public Description setSubtitle(final @Nullable String subtitle) {
    return new Description(title, subtitle, contentDescription);
  }

  @NonNull public String getContentDescription() {
    if (contentDescription != null) return contentDescription;
    return StringUtils.isEmpty(subtitle) ? title : title + ", " + subtitle;
  }

  @NonNull public Description setContentDescription(final @Nullable String contentDescription) {
    return new Description(title, subtitle, contentDescription);
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Description that = (Description) o;

    if (!getTitle().equals(that.getTitle())) return false;
    if (getSubtitle() != null ? !getSubtitle().equals(that.getSubtitle()) : that.getSubtitle() != null)
      return false;
    return getContentDescription().equals(that.getContentDescription());

  }

  @Override public int hashCode() {
    int result = getTitle().hashCode();
    result = 31 * result + (getSubtitle() != null ? getSubtitle().hashCode() : 0);
    result = 31 * result + getContentDescription().hashCode();
    return result;
  }

  @Override public String toString() {
    return "Description{" +
        "title='" + title + '\'' +
        ", subtitle='" + subtitle + '\'' +
        ", contentDescription='" + contentDescription + '\'' +
        '}';
  }
}
