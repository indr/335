/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Stack;

import ch.indr.threethreefive.ThreeThreeFiveApp;
import ch.indr.threethreefive.libs.pages.Page;
import ch.indr.threethreefive.libs.pages.PageResolver;
import ch.indr.threethreefive.libs.pages.PageMeta;
import ch.indr.threethreefive.libs.pages.PageRequest;
import ch.indr.threethreefive.pages.RootPageResolver;
import ch.indr.threethreefive.pages.errors.NotFound;
import timber.log.Timber;

public class PageManager {

  public static Page fetch(final @NonNull Context context, final @NonNull PageRequest pageRequest) {
    return fetch(context, pageRequest.getUri(), pageRequest.getTitle());
  }

  public static Page fetch(final @NonNull Context context, final @NonNull Uri pageUri) {
    return fetch(context, pageUri, null);
  }

  private static Page fetch(final @NonNull Context context, final @NonNull Uri pageUri, final @Nullable String pageTitle) {
    Timber.i("Fetching page for %s", pageUri.toString());

    final Page page;
    final Environment environment = ((ThreeThreeFiveApp) context.getApplicationContext()).component().environment();

    try {
      PageMeta pageMeta = null;
      try {
        pageMeta = new RootPageResolver().resolve(pageUri);
      } catch (PageResolver.PageNotFoundException ex) {
        pageMeta = new PageMeta(NotFound.class, pageUri, new Bundle());
      }
      final Constructor constructor = pageMeta.getClazz().getConstructor(Environment.class);
      page = (Page) constructor.newInstance(environment);
      Timber.i("Setting title %s", pageTitle);
      page.setTitle(pageTitle);
      page.onCreate(context, pageMeta.getUri(), pageMeta.getBundle());
      page.onStart();

    } catch (IllegalAccessException exception) {
      throw new RuntimeException(exception);
    } catch (InvocationTargetException exception) {
      throw new RuntimeException(exception);
    } catch (InstantiationException exception) {
      throw new RuntimeException(exception);
    } catch (NoSuchMethodException exception) {
      throw new RuntimeException(exception);
    }

    return page;
  }

  public static void destroy(final @NonNull Page page) {
    try {
      page.onStop();
      page.onDestroy();
    } catch (Exception ex) {
      Timber.w(ex, "Error stopping and destroying page %s", page.toString());
    }
  }

  public static void start(final @NonNull Page page) {
    if (page.getState() != Page.State.Started) {
      page.onStart();
    }
  }

  public static void stop(final @NonNull Page page) {
    if (page.getState() != Page.State.Stopped) {
      page.onStop();
    }
  }

  public static void resume(final @NonNull Page page) {
    if (page.getState() != Page.State.Resumed) {
      page.onResume();
    }
  }

  public static void pause(final @NonNull Page page) {
    if (page.getState() != Page.State.Paused) {
      page.onPause();
    }
  }

  public static void destroy(Stack<Page> pageStack) {
    while (pageStack.size() > 0) {
      Page page = pageStack.pop();
      PageManager.pause(page);
      PageManager.destroy(page);
    }

    pageStack.clear();
  }
}
