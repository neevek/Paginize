package net.neevek.android.lib.paginize;

/**
 * Author: neevek
 * Date: 12/2/15 4:48 PM
 */
public interface PageEventNotifier {
  void onPageShown(Page page);
  void onPageHidden(Page page);
}
