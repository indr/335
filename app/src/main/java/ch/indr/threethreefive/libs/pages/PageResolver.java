/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.pages;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ch.indr.threethreefive.libs.PageUris.AUTHORITY;

public abstract class PageResolver {

  protected Uri setDefaultAuthority(Uri uri) {
    return uri.buildUpon().authority(AUTHORITY).build();
  }

  protected PageMeta makeMeta(@NonNull Class pageClass, @NonNull Uri uri) {
    return new PageMeta(pageClass, uri, new Bundle());
  }

  protected PageMeta makeMeta(@NonNull Class pageClass, @NonNull Uri uri, @NonNull String id) {
    Bundle bundle = new Bundle();
    bundle.putString("id", id);
    return new PageMeta(pageClass, uri, bundle);
  }

  protected PageMeta makeMeta(@NonNull Class<?> pageClass, @NonNull Uri uri, @Nullable String[] keys, @NonNull MatchResult values) {
    Bundle bundle = new Bundle();
    if (keys != null) {
      for (int i = 0; i < keys.length; i++) {
        bundle.putString(keys[i], values.group(i + 1));
      }
    }
    return new PageMeta(pageClass, uri, bundle);
  }

  public abstract PageMeta resolve(Uri uri) throws PageNotFoundException;

  protected PageMeta resolvePatterns(List<UrlPattern> patterns, Uri uri) {
    final String path = uri.getPath();
    for (UrlPattern urlPattern : patterns) {
      final Matcher matcher = urlPattern.getPattern().matcher(path);
      if (matcher.matches()) {
        final MatchResult matchResult = matcher.toMatchResult();
        if (matchResult != null) {
          return makeMeta(urlPattern.getPageClass(), uri, urlPattern.getKeys(), matchResult);
        }
      }
    }
    throw new PageNotFoundException(uri);
  }

  protected static class UrlPattern {

    private final Pattern pattern;
    private final Class<? extends Page> pageClass;
    private final String[] keys;

    public UrlPattern(@NonNull String regex, @NonNull Class<? extends Page> pageClass, @Nullable String[] keys) {
      this.pattern = Pattern.compile(regex);
      this.pageClass = pageClass;
      this.keys = keys;
    }

    public @NonNull Pattern getPattern() {
      return pattern;
    }

    public @NonNull Class<? extends Page> getPageClass() {
      return pageClass;
    }

    public @Nullable String[] getKeys() {
      return keys;
    }
  }

  public class PageNotFoundException extends RuntimeException {

    public PageNotFoundException(@NonNull Uri uri) {
      super("Page not found: " + uri.toString());
    }
  }
}
