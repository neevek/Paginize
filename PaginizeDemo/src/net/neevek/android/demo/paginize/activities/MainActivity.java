package net.neevek.android.demo.paginize.activities;

import android.os.Bundle;
import net.neevek.android.demo.paginize.pages.main.MainPage;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.anim.ZoomPageAnimator;
import net.neevek.android.lib.paginize.annotation.InjectPageAnimator;

@InjectPageAnimator(ZoomPageAnimator.class)
//@InjectPageAnimator(SlidePageAnimator.class)
public class MainActivity extends PageActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // if MainActivity is restored, the internal page stack will be restored
    // automatically, so there is no need to create and show MainPage manually here.
    if (savedInstanceState == null) {
      new MainPage(this).show(null, false);
    }
  }
}
