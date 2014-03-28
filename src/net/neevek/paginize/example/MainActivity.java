package net.neevek.paginize.example;

import android.os.Bundle;
import net.neevek.paginize.lib.PageActivity;
import net.neevek.paginize.lib.anim.ZoomPageAnimationManager;
import net.neevek.paginize.lib.annotation.InjectPageAnimationManager;

@InjectPageAnimationManager(ZoomPageAnimationManager.class)
//@InjectPageAnimationManager(SlidePageAnimationManager.class)
public class MainActivity extends PageActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new MainPage(this).show(null, false);
//        showPage(MainPage.class, true);
    }
}
