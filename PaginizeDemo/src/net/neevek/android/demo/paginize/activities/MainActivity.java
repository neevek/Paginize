package net.neevek.android.demo.paginize.activities;

import android.os.Bundle;
import net.neevek.android.demo.paginize.pages.main.MainPage;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.anim.SlidePageAnimator;
import net.neevek.android.lib.paginize.annotation.InjectPageAnimator;

//@InjectPageAnimator(ZoomPageAnimator.class)
@InjectPageAnimator(SlidePageAnimator.class)
public class MainActivity extends PageActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    new MainPage(this).show(null, false);
//        showPage(MainPage.class, true);
  }
}
