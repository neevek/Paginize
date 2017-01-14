package net.neevek.android.demo.paginize.pages;

import android.content.DialogInterface;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.neevek.android.demo.paginize.R;
import net.neevek.android.demo.paginize.paginizecontrib.PaginizeContribMainPage;
import net.neevek.android.demo.paginize.util.ToolbarHelper;
import net.neevek.android.lib.paginize.Page;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.ListenerDefs;
import net.neevek.android.lib.paginize.annotation.ListenerMarker;
import net.neevek.android.lib.paginize.annotation.PageLayout;
import net.neevek.android.lib.paginize.annotation.SetListeners;

/**
 * Created by neevek on 3/16/14.
 */
@PageLayout(R.layout.page_main)
public class MainPage extends Page implements View.OnClickListener {
  private static final String TAG = MainPage.class.getName();

  @InjectView(R.id.drawer_layout)
  private DrawerLayout mDrawerLayout;
  @InjectView(R.id.tb_header_bar)
  private Toolbar mTbHeaderBar;
  @InjectView(R.id.navigation_view)
  private NavigationView mNavigationView;
  @InjectView(R.id.tv_text)
  private TextView mTvText;

  // Demonstration only, this SHOULD be defined in @ListenerDefs instead
  // because the reference is not used.
  // Without setting the "listener" attribute, instance of current page is
  // assumed to have implemented View.OnClickListener.
  @InjectView(value = R.id.btn_swipeable_tab_page,
      listenerTypes = View.OnClickListener.class)
  private Button mBtnSwipeableTabPage;

  // Demonstration only, this SHOULD be defined in @ListenerDefs instead
  // because the reference is not used.
  @InjectView(value = R.id.btn_simple_tab_page,
      listenerTypes = View.OnClickListener.class,
      listener = SimpleClickListener.class)
  private Button mBtnSimpleTabPage;

  // Instead of using @InjectView for injecting the view and setting listeners,
  // here @ListenerDefs and @SetListeners are used for the reason that we do not
  // need references to the buttons, also findViewById() is not used because
  // consistency is important, views and listeners are better be injected.
  @ListenerDefs({
      @SetListeners(view = R.id.btn_list_page,
          listenerTypes = View.OnClickListener.class,
          listener = SimpleClickListener.class),
      @SetListeners(view = R.id.btn_cta_page,
          listenerTypes = View.OnClickListener.class),
      @SetListeners(view = R.id.btn_custom_page,
          listenerTypes = View.OnClickListener.class),
      @SetListeners(view = R.id.btn_contrib_page,
          listenerTypes = View.OnClickListener.class),
  })
  @SuppressWarnings("deprecation")
  public MainPage(PageActivity pageActivity) {
    super(pageActivity);

    setupToolbar();
    setupNavigationView();
    mTvText.setText(Html.fromHtml(
        "<b>Paginize</b> is a light-weight application framework for Android. " +
        "It was designed to <b>accelerate development cycles</b> and <b>make " +
        "maintenance easier</b> of complex projects. " +
        "With <b>Paginize</b>, you gain without pain the following: <br><br>" +
        "• Code organization of <i>big and complex</i> projects made easy<br>" +
        "• Code reuse pushed to another level with <b>Layout inheritance</b><br>" +
        "• Global page transition animation and custom transition animation<br>" +
        "• Some syntax sugar to make code succinct and consistent in style<br>" +
        "• No tricks for weird bugs needed like using Fragment<br>" +
        "• And many more to be discovered :-)"
    ));
  }

  private void setupToolbar() {
    mTbHeaderBar.setTitle("Paginize · MainPage");
    setupDrawerToggle();
    ToolbarHelper.setupMenu(mTbHeaderBar, R.menu.menu_main,
        new Toolbar.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        new AlertDialog.Builder(getContext())
            .setTitle("Paginize")
            .setMessage("Paginize is a light-weight application framework " +
                "for Android.")
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), "OK clicked",
                    Toast.LENGTH_SHORT).show();
              }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), "Cancel clicked",
                    Toast.LENGTH_SHORT).show();
              }
            })
            .show();
        return true;
      }
    });
  }

  private void setupNavigationView() {
    mNavigationView.setNavigationItemSelectedListener(
        new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(false);
        mDrawerLayout.closeDrawers();

        switch (menuItem.getItemId()) {
          case R.id.mi_main_tab_page: {
            SwipeableTabPage page = new SwipeableTabPage(getContext());
            // pass args with getBundle()
            page.getBundle().putString("title", "SwipeableTabPage:-)");
            page.show(true);
            break;
          }
          case R.id.mi_simple_tab_page:
            new SimpleTabPage(getContext()).show(true);
            break;
          case R.id.mi_list_page:
            new ListPage(getContext()).show(true);
            break;
          case R.id.mi_cta_page:
            new CustomTransitionAnimationPage(getContext()).show(true);
            break;
          case R.id.mi_custom_page:
            new CustomPage(getContext()).show(true);
            break;
          case R.id.mi_contrib_page:
            new PaginizeContribMainPage(getContext()).show(true);
            break;
        }
        return true;
      }
    });
  }

  private void setupDrawerToggle() {
    ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
        getContext(),
        mDrawerLayout,
        mTbHeaderBar,
        R.string.app_name,
        R.string.app_name
    );

    mDrawerToggle.setDrawerIndicatorEnabled(true);
    mDrawerLayout.addDrawerListener(mDrawerToggle);
    mDrawerToggle.syncState();
  }

  @Override
  public boolean onMenuPressed() {
    if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
      mDrawerLayout.openDrawer(GravityCompat.START);
    } else {
      mDrawerLayout.closeDrawer(GravityCompat.START);
    }
    return true;
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.btn_swipeable_tab_page: {
        SwipeableTabPage page = new SwipeableTabPage(getContext());
        // pass args with getBundle()
        page.getBundle().putString("title", "SwipeableTabPage:-)");
        page.show(true);
        break;
      }
      case R.id.btn_cta_page:
        new CustomTransitionAnimationPage(getContext()).show(true);
        break;
      case R.id.btn_custom_page:
        new CustomPage(getContext()).show(true);
        break;
      case R.id.btn_contrib_page:
        new PaginizeContribMainPage(getContext()).show(true);
        break;
    }
  }

  // This annotation is required to prevent this class from being striped
  // when progurad is used to obfuscate the code at building time
  @ListenerMarker
  class SimpleClickListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.btn_simple_tab_page:
          new SimpleTabPage(getContext()).show(true);
          break;
        case R.id.btn_list_page:
          new ListPage(getContext()).show(true);
          break;
      }
    }
  }


  @Override
  public void onShow() {
    super.onShow();
    Log.i(TAG, "MainPage onShow()");
  }

  @Override
  public void onShown() {
    super.onShown();
    Log.i(TAG, "MainPage onShown()");
  }

  @Override
  public void onCover() {
    super.onCover();
    Log.i(TAG, "MainPage onCover()");
  }

  @Override
  public void onCovered() {
    super.onCovered();
    Log.i(TAG, "MainPage onCovered()");
  }
}
