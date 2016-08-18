package net.neevek.android.demo.paginize.pages.main;

import android.app.WallpaperManager;
import android.graphics.drawable.Drawable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.neevek.android.demo.paginize.R;
import net.neevek.android.demo.paginize.util.ToolbarHelper;
import net.neevek.android.lib.paginize.Page;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.PageLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neevek on 3/16/14.
 */
@PageLayout(R.layout.page_main)
public class MainPage extends Page {
  @InjectView(R.id.drawer_layout)
  private DrawerLayout mDrawerLayout;
  @InjectView(R.id.tb_header_bar)
  private Toolbar mTbHeaderBar;
  @InjectView(R.id.navigation_view)
  private NavigationView mNaviView;
  @InjectView(R.id.iv_parallax_image)
  private ImageView mIvParallaxImage;
  @InjectView(R.id.rv_main_list)
  private RecyclerView mRvMainList;

  public MainPage(PageActivity pageActivity) {
    super(pageActivity);

    setupToolbar();
    setupNavigationView();
    setupRecyclerView();
    setParallaxImage();
  }

  private void setupToolbar() {
    setupDrawerToggle();
    mTbHeaderBar.setTitle("Paginize · Demo");
    ToolbarHelper.setupMenu(mTbHeaderBar, R.menu.menu_main, new Toolbar.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        Toast.makeText(getContext(), "Hello Settings.", Toast.LENGTH_SHORT).show();
        return false;
      }
    });
  }

  private void setupNavigationView() {
    mNaviView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(false);
        mDrawerLayout.closeDrawers();

        switch (menuItem.getItemId()) {
          case R.id.mi_main_tab_page:
            new MainTabPage(getContext()).show(true);
        }
        return true;
      }
    });
  }

  private void setupRecyclerView() {
    mRvMainList.setLayoutManager(new LinearLayoutManager(getContext()));
    mRvMainList.setAdapter(new MainListItemAdapter(new ArrayList<String>(){{
      for (int i = 0; i < 25; ++i) {
        add("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
      }
    }}));
  }

  private void setParallaxImage() {
    Drawable wallpager = WallpaperManager.getInstance(getContext()).getDrawable();
    if (wallpager != null) {
      mIvParallaxImage.setImageDrawable(wallpager);
    }
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

  private class MainListItemAdapter extends RecyclerView.Adapter<MainListItemAdapter.ViewHolder> {
    private List<String> mTextList;

    public MainListItemAdapter(List<String> textList) {
      mTextList = textList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.recycler_view_item, parent, false);
      return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      holder.tvText.setText(mTextList.get(position));
    }

    @Override
    public int getItemCount() {
      return mTextList != null ? mTextList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
      CardView cvContainer;
      TextView tvText;

      public ViewHolder(View itemView){
        super(itemView);
        cvContainer = (CardView) itemView;
        tvText = (TextView) itemView.findViewById(R.id.tv_text);
      }

    }
  }
}
