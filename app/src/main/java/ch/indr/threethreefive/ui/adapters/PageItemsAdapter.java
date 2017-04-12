/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.ui.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.BitmapCache;
import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.libs.Preferences;
import ch.indr.threethreefive.libs.utils.ObjectUtils;
import ch.indr.threethreefive.libs.utils.UriUtils;
import ch.indr.threethreefive.ui.utils.TouchGestureListener;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static ch.indr.threethreefive.libs.rx.transformers.Transfomers.observeForUI;

/**
 * https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView#defining-the-adapter
 */
public class PageItemsAdapter extends ArrayAdapter<PageItem> implements SharedPreferences.OnSharedPreferenceChangeListener {

  // ripple_material_light
  private static final int highlightColor = Color.argb(0x1f, 0, 0, 0);

  private final Preferences preferences;
  private final HashMap<View, List<Subscription>> subscriptions;
  private final BitmapCache bitmapCache;
  private float textSizeTitle;
  private float textSizeSubtitle;
  private final float textSizeSubTitleFactor = 14f / 18f;
  private HashSet<TextView> titles = new HashSet<>();
  private HashSet<TextView> subtitles = new HashSet<>();

  public PageItemsAdapter(Context context, List<PageItem> pageItems, @NonNull Preferences preferences) {
    super(context, 0, pageItems);

    this.preferences = preferences;
    this.textSizeTitle = preferences.textSize().get();
    this.textSizeSubtitle = this.textSizeTitle * textSizeSubTitleFactor;

    this.subscriptions = new HashMap<>();

    this.bitmapCache = BitmapCache.getInstance();
  }

  @Override @NonNull public View getView(int position, View convertView, @NonNull ViewGroup parent) {
    // Get the data item for this position
    PageItem pageItem = getItem(position);
    List<Subscription> subscriptions;

    // Check if an existing view is being reused, otherwise inflate the view
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_2_image, parent, false);
      this.subscriptions.put(convertView, new ArrayList<>());
    }

    // Lookup views for data population and touch listening
    ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
    TextView textViewTitle = (TextView) convertView.findViewById(R.id.textViewTitle);
    TextView textViewSubtitle = (TextView) convertView.findViewById(R.id.textViewSubtitle);
    HorizontalScrollView scrollView = (HorizontalScrollView) convertView.findViewById(R.id.scrollView);
    scrollView.scrollTo(0, 0);

    // Put TextViews in HashSet so we can update the appearance later
    titles.add(textViewTitle);
    subtitles.add(textViewSubtitle);

    // Set initial appearance
    textViewTitle.setTextSize(textSizeTitle);
    textViewSubtitle.setTextSize(textSizeSubtitle);

    // Unsubscribe in case this view is reused
    subscriptions = this.subscriptions.get(convertView);
    //noinspection Convert2streamapi
    for (Subscription each : subscriptions) {
      if (!each.isUnsubscribed()) {
        each.unsubscribe();
      }
    }
    subscriptions.clear();

    if (pageItem != null) {
      final Uri iconUri = pageItem.getIconUri();
      final int defaultIconResId = pageItem.getDefaultIconResId();
      if (defaultIconResId <= 0 && UriUtils.isEmpty(iconUri)) {
        imageView.setVisibility(View.GONE);
      } else {
        imageView.setImageResource(defaultIconResId);
        imageView.setVisibility(View.VISIBLE);

        if (UriUtils.isNotEmpty(iconUri)) {
          subscriptions.add(bitmapCache.getIconImage(iconUri)
              .take(1)
              .filter(ObjectUtils::isNotNull)
              .compose(observeForUI())
              .subscribe(imageView::setImageBitmap));
        }
      }
      textViewTitle.setText(pageItem.getTitle());
      textViewSubtitle.setVisibility(pageItem.getSubtitle() == null ? View.GONE : View.VISIBLE);
      textViewSubtitle.setText(pageItem.getSubtitle());
      convertView.setContentDescription(pageItem.getContentDescription());

      // Subscribe to page items observables
      subscriptions.add(pageItem.title()
          .skip(1)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(textViewTitle::setText));
      subscriptions.add(pageItem.subtitle()
          .skip(1)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(text -> {
            textViewSubtitle.setVisibility(text == null ? View.GONE : View.VISIBLE);
            textViewSubtitle.setText(text);
          }));
      subscriptions.add(pageItem.description()
          .skip(1)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(convertView::setContentDescription));
    }

    // Attach touch listeners to perform a list item click and visual indicator on focus, tap
    // If you want to puke, see: https://code.google.com/p/android/issues/detail?id=3414
    final TouchListener touchListener = new TouchListener(getContext(), parent, position, scrollView);
    textViewTitle.setOnTouchListener(touchListener);
    textViewSubtitle.setOnTouchListener(touchListener);
    scrollView.setOnTouchListener(touchListener);

    // Return the completed view to render on screen
    return convertView;
  }

  @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    this.textSizeTitle = preferences.textSize().get();
    this.textSizeSubtitle = this.textSizeTitle * textSizeSubTitleFactor;
    for (TextView textView : titles) {
      textView.setTextSize(textSizeTitle);
    }
    for (TextView textView : subtitles) {
      textView.setTextSize(textSizeSubtitle);
    }
  }

  private class TouchListener extends TouchGestureListener {
    private final ViewGroup parent;
    private final int position;
    private final View itemView;

    TouchListener(Context context, ViewGroup parent, int position, View itemView) {
      super(context);
      this.parent = parent;
      this.position = position;
      this.itemView = itemView;
    }

    @Override public boolean onTouch(View view, MotionEvent motionEvent) {
      final int action = motionEvent.getActionMasked();
      if (action == MotionEvent.ACTION_DOWN) {
        itemView.setBackgroundColor(highlightColor);
      } else if (action == MotionEvent.ACTION_UP) {
        itemView.setBackgroundResource(0);
      } else if (action == MotionEvent.ACTION_CANCEL) {
        itemView.setBackgroundResource(0);
      }
      return super.onTouch(view, motionEvent);
    }

    @Override public boolean onSingleTapUp(MotionEvent e) {
      if (!parent.getClass().equals(ListView.class)) {
        return super.onSingleTapUp(e);
      }
      // performItemClick() should be called with the rowId instead of the viewId.
      ListView listView = (ListView) parent;
      listView.performItemClick(itemView, position, itemView.getId());
      return true; // Event consumed
    }
  }
}
