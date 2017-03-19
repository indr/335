/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ch.indr.threethreefive.commands.ActionCommand;
import ch.indr.threethreefive.commands.ToggleFavorite;
import ch.indr.threethreefive.favorites.FavoritesStore;
import rx.functions.Action1;

public class PageItemsBuilder {

  private final Resources resources;
  private final FavoritesStore favoritesStores;

  private final List<PageItem> items = new ArrayList<>();

  public PageItemsBuilder(@NonNull Resources resources, @NonNull FavoritesStore favoritesStore) {
    this.resources = resources;
    this.favoritesStores = favoritesStore;
  }

  public PageItemsBuilder addItem(final @NonNull PageItem item) {
    items.add(item);
    return this;
  }

  public PageItemsBuilder addItem(final @NonNull String title, Action1<Environment> action) {
    return addItem(new ActionCommand(title, action));
  }

  public PageItemsBuilder addLink(final @NonNull String uri, final int title) {
    return addLink(uri, resources.getString(title));
  }

  public PageItemsBuilder addLink(final @NonNull String uri, final @NonNull String title) {
    items.add(new PageLink(Uri.parse(uri), title));
    return this;
  }

  public PageItemsBuilder addLink(final @NonNull String uri, final @NonNull String title, final @Nullable String subtitle, final @NonNull String description) {
    items.add(new PageLink(Uri.parse(uri), title, subtitle, description));
    return this;
  }

  public PageItemsBuilder addText(final @NonNull String title) {
    items.add(new TextItem(title));
    return this;
  }

  public PageItemsBuilder addText(final @NonNull String title, final @Nullable String subtitle, final @NonNull String description) {
    items.add(new TextItem(title, subtitle, description));
    return this;
  }

  public PageItemsBuilder addToggleFavorite(PageLink pageLink) {
    items.add(new ToggleFavorite(this.favoritesStores, pageLink));
    return this;
  }

  public List<PageItem> build() {
    return items;
  }

}
