Paginize
========

Introduction
------------
**Paginize** is a library that eases development of Android applications with unified user interfaces. It abstracts each user interface or screen as a `Page`, which is simply a wrapper of a `View`.

Let's take a look at an example:

1. Create a layout file(res/layout/page_main.xml) for the Page:

    <?xml version="1.0" encoding="utf-8"?>
    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
                  android:layout_width="fill_parent"
                  android:layout_height="fill_parent"
                  android:orientation="vertical" >

        <TextView
                android:id="@+id/tv_text"
                android:layout_width="fill_parent"
                android:layout_height="wrap_content"
                android:text="This TextView will be set in Java code"
                />

    </LinearLayout>

2. Create a `Page`:

    @PageLayout(R.layout.page_main)
    public class MainPage extends Page {
        @InjectView(R.id.tv_text)
        private TextView mTvText;

        public MainPage(PageActivity pageActivity) {
            super(pageActivity);

            mTvText.setText("Hello Paginize!");
        }
    }

3. Create an Activity that extends `PageActivity`, and show the Page we just created above:

    @InjectPageAnimationManager(SlidePageAnimationManager.class)
    public class MainActivity extends PageActivity {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            new MainPage(this).show();
        }
    }

That's all for using **Paginize** to make a one screen application. Here you may find that it is sort of more hassle than just use Activity, but **Paginize** is only useful when you use it to make more complicated applications, you see the advantages of it when you use it to structure a real application.

Highlights & Limitations
------------------------

With **Paginize**, you tend to break *huge* Activity into `Page`s, just like breaking *big* problems into smaller pieces so you can focus on one smaller problem at a time, to some extent you isolate each `Page` and its related business logics, which makes your application more maintainable. You can also use a `Page` and multiple `InnerPage`s to create a tabbed user interface, see the example for more details.

**Paginize** is flexible, you can reuse a Page even if it is popped out from the page stack, you can pass a callback into a Page, you can easily customize animations for page transition, etc. there's quite a few in terms of flexibility. But all this comes at some cost, **Paginize** *does not* support device rotation, to be more precise, Pages will not be recreated automatically when the Activity that contains the Pages gets killed and recreated(which also happens on low memory). I could have made it support Page recreation, but that would make `Page` simply an "Activity" that can be directly started without need for declaration in `AndroidManifest.xml`, because in that case I will need to use reflection to restore a `Page`, reusing a `Page` is NOT possible then, passing a callback into a `Page` is NOT possible too, all the flexibility is lost, and that was absolutely NOT what I wanted.

Note, though, that in spite of the limitations I mentioned above, if you create the main `Page` in `Activity.onCreate()`(which you would normally do), it will still be *recreated* as usual when `Activity.onCreate()` is called again, it is just NOT *restored*. Anyway, you can still override `Activity.onSaveInstanceState()` to save some states of your `Pages` and `Activity.onRestoreInstanceState()` to restore the states, it is just NOT handled by the library.

For most apps, it might not be a big deal that no direct support for keeping states for the `Page`s for device rotation.

You may want to compare **Paginize** with Fragment, well I didn't mean to write the library to compete with Fragment, it is just a simpler, more flexible, easier to use, but less feature-rich library.

Under MIT license
-----------------

    Copyright (c) 2014 neevek <i at neevek.net>

    See the file license.txt for copying permission.
