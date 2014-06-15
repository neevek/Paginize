package net.neevek.android.lib.paginize;

/**
 * An InnerPage encapsulates a View(usually a layout with complex UIs),
 * which is to be put into a ViewGroup and finally be shown on screen.
 *
 * InnerPage is designed to be used in a UI layout that contains multiple views
 * that can be swapped at runtime.
 *
 * InnerPage is managed by InnerPageManager, we call InnerPageManager.setPage()
 * method to set an InnerPage as the current page.
 *
 * Date: 2/28/13
 * Time: 3:06 PM
 *
 * @author i@neevek.net
 * @version 1.0.0
 * @since 1.0.0
 */
public class InnerPage extends ViewWrapper {
    public InnerPage(PageActivity pageActivity) {
        super(pageActivity);
    }

    public void onSet(Object obj) { }
    public void onReplaced() { }
}