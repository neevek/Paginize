package net.neevek.paginize.demo;

import android.os.Bundle;
import net.neevek.lib.android.paginize.PageActivity;
import net.neevek.lib.android.paginize.anim.SlidePageAnimationManager;
import net.neevek.lib.android.paginize.annotation.InjectPageAnimationManager;

//@InjectPageAnimationManager(ZoomPageAnimationManager.class)
@InjectPageAnimationManager(SlidePageAnimationManager.class)
public class MainActivity extends PageActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new MainPage(this).show(null, false);
//        showPage(MainPage.class, true);
    }
}
