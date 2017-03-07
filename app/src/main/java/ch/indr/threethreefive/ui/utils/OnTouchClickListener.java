/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.ui.utils;

import android.view.MotionEvent;
import android.view.View;

public class OnTouchClickListener implements View.OnTouchListener {
  /**
   * http://stackoverflow.com/questions/17831395/how-can-i-detect-a-click-in-an-ontouch-listener#17836095
   */

  private static final int CLICK_ACTION_THRESHHOLD = 5;

  private boolean actionDown = true;
  private float startX, startY;
  private float maxDeltaX, maxDeltaY;

  @Override public boolean onTouch(View view, MotionEvent motionEvent) {
    switch (motionEvent.getAction()) {
      case MotionEvent.ACTION_DOWN:
        actionDown = true;
        resetMaxDeltas(motionEvent.getX(), motionEvent.getY());
        break;
      case MotionEvent.ACTION_MOVE:
        calcMaxDeltas(motionEvent.getX(), motionEvent.getY());
        break;
      case MotionEvent.ACTION_UP:
        if (actionDown) {
          actionDown = false;
          if (isClick(motionEvent.getX(), motionEvent.getY())) {
            return onClick(view);
          }
        }
        break;
    }
    return false;
  }

  public boolean onClick(View view) {
    return false;
  }

  private void calcMaxDeltas(float x, float y) {
    maxDeltaX = Math.max(maxDeltaX, Math.abs(startX - x));
    maxDeltaY = Math.max(maxDeltaY, Math.abs(startY - y));
  }

  private void resetMaxDeltas(float x, float y) {
    startX = x;
    startY = y;
    maxDeltaX = 0;
    maxDeltaY = 0;
  }

  private boolean isClick(float x, float y) {
    calcMaxDeltas(x, y);
    if (maxDeltaX > CLICK_ACTION_THRESHHOLD || maxDeltaY > CLICK_ACTION_THRESHHOLD) {
      return false;
    }
    return true;
  }
}
