package net.neevek.android.demo.paginize.activities;

import android.os.Bundle;

import net.neevek.android.demo.paginize.pages.MainPage;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.anim.ZoomPageAnimator;
import net.neevek.android.lib.paginize.annotation.InjectPageAnimator;

@InjectPageAnimator(ZoomPageAnimator.class)
public class MainActivity extends PageActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getPageManager().setDebug(true);
    getPageManager().enableSwipeToHide(true);
    // this method call overrides the PageAnimator specified with @InjectPageAnimator
    getPageManager().useSwipePageTransitionEffect();

    // if MainActivity is restored, the internal page stack will be restored
    // automatically, so there is no need to create and show MainPage manually
    // here. Note that this conditional is NOT needed if you are not going to
    // support state recovery for device rotation or Activity recreation
    // on low memory.
    if (savedInstanceState == null) {
      new MainPage(this).show(false);
    }
  }
}
