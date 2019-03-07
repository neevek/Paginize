package net.neevek.android.demo.paginize.pages;

import android.app.WallpaperManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.neevek.android.demo.paginize.R;
import net.neevek.android.demo.paginize.util.ToolbarHelper;
import net.neevek.android.lib.paginize.Page;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.PageLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by neevek on 3/16/14.
 */
@PageLayout(R.layout.page_list)
public class ListPage extends Page {
  @InjectView(R.id.tb_header_bar)
  private Toolbar mTbHeaderBar;
  @InjectView(R.id.iv_parallax_image)
  private ImageView mIvParallaxImage;
  @InjectView(R.id.rv_main_list)
  private RecyclerView mRvMainList;

  public ListPage(PageActivity pageActivity) {
    super(pageActivity);

    setupToolbar();
    setParallaxImage();
  }

  @Override
  public void onShown() {
    super.onShown();
    // loading data and rendering the RecyclerView may somewhat slow down the
    // UI thread, so do it in onShown(), which is called after the transition
    // animation is done
    setupRecyclerView();
  }

  private void setupToolbar() {
    mTbHeaderBar.setTitle("ListPage");
    ToolbarHelper.setNavigationIconEnabled(
        mTbHeaderBar, true, new View.OnClickListener () {
          @Override
          public void onClick(View v) {
            hide(true);
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
