package net.neevek.android.lib.paginize.anim;

import net.neevek.android.lib.paginize.Page;

/**
 * Copyright (c) 2015 neevek <i@neevek.net>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * PageAnimator to animate transition when navigating pages
 */
public interface PageAnimator {
  enum AnimationDirection {
    FROM_LEFT,
    FROM_RIGHT
  }
  /**
   * called when a new Page is pushed onto the stack
   *
   * @param oldPage the page on top of the stack before the newPage is pushed, may be null
   * @param newPage the new page to be pushed onto the stack
   * @param animationDirection the direction from which the new page is pushed
   * @return true if implemented
   */
  boolean onPushPageAnimation(Page oldPage, Page newPage, AnimationDirection animationDirection);

  /**
   * called when a Page is popped out from stack
   *
   * @param oldPage the page on top of the stack before the oldPage is popped
   * @param newPage the page on top of the stack after oldPage is popped, may be null
   * @param animationDirection the direction from which the new page is pushed
   * @return true if implemented
   */
  boolean onPopPageAnimation(Page oldPage, Page newPage, AnimationDirection animationDirection);

  int getAnimationDuration();
}
