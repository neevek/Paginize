package net.neevek.android.demo.paginize.pages.other;

import android.view.View;
import android.widget.Toast;
import net.neevek.android.demo.paginize.R;
import net.neevek.android.demo.paginize.pages.general.FramePage;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.DecoratePageConstructor;
import net.neevek.android.lib.paginize.annotation.InheritPageLayout;
import net.neevek.android.lib.paginize.annotation.SetListeners;

/**
 * Created by neevek on 3/16/14.
 */

@InheritPageLayout(R.layout.page_test)
public class TestPage extends FramePage implements View.OnClickListener {

  @DecoratePageConstructor(viewListeners = {
      @SetListeners(view = R.id.btn_back, listenerTypes = View.OnClickListener.class)
  })
  public TestPage(PageActivity pageActivity) {
    super(pageActivity);

    setTitle("Test Page!");
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_back:
        hideWithAnimation(true);
        break;
    }
  }


  @Override
  protected void onNextButtonClicked() {
    Toast.makeText(getContext(), "Next button clicked", Toast.LENGTH_SHORT).show();
  }
}
