/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.content.res.Resources;
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

  public PageItemsBuilder addItem(String title, Action1<Environment> action1) {
    return addItem(new ActionCommand(title, action1));
  }

  public PageItemsBuilder addLink(final @NonNull String uri, final @NonNull String name) {
    items.add(new PageLink(uri, name));
    return this;
  }

  public PageItemsBuilder addLink(final @NonNull String uri, final int resourceId) {
    return this.addLink(uri, resources.getString(resourceId));
  }

  public PageItemsBuilder addText(final @NonNull String name) {
    return addText(name, null);
  }

  public PageItemsBuilder addText(final @NonNull String name, final @Nullable String description) {
    items.add(new TextItem(name, description));
    return this;
  }

  public List<PageItem> build() {
    return items;
  }

  public PageItemsBuilder addToggleFavorite(PageLink pageLink) {
    items.add(new ToggleFavorite(this.favoritesStores, pageLink));
    return this;
  }
}
