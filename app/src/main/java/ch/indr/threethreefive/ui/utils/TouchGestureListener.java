/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.ui.utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class TouchGestureListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
  private final GestureDetector gestureDetector;

  public TouchGestureListener(Context context) {
    gestureDetector = new GestureDetector(context, this);
  }

  @Override public boolean onTouch(View view, MotionEvent motionEvent) {
    return gestureDetector.onTouchEvent(motionEvent);
  }
}
