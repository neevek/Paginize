package net.neevek.android.demo.paginize.activities;

import android.os.Bundle;
import net.neevek.android.demo.paginize.pages.main.MainPage;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.anim.SquashPageAnimator;
import net.neevek.android.lib.paginize.annotation.InjectPageAnimator;

@InjectPageAnimator(SquashPageAnimator.class)
//@InjectPageAnimator(ZoomPageAnimator.class)
//@InjectPageAnimator(SlidePageAnimator.class)
public class MainActivity extends PageActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

//    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//    getWindow().setStatusBarColor(0xffff8800);

//    getPageManager().setStatusBarBackgroundColor(0xff773311);
//    getPageManager().setStatusBarBackgroundColor(0x00ffffff);

    // if MainActivity is restored, the internal page stack will be restored
    // automatically, so there is no need to create and show MainPage manually here.
    // Note: this conditional is NOT needed if you are not going to support state recovery for
    //       device rotation or Activity recreation on low memory.
    if (savedInstanceState == null) {
      new MainPage(this).show(null, false);
    }
  }
}
