[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Paginize-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1545)

Paginize
========
Paginize is a light-weight application framework for Android, it provides an intuitive programming model for writing Android applications. Paginize models a screen as a `Page` or part of the screen as an `InnerPage`, which in essence are just view wrappers. Paginize breaks down complex user interfaces into smaller units, provides APIs for easily handling page navigations, and offers flexibility for Page inheritance and **layout inheritance**, which push code reuse in Android to another level.

Documentation
=============

1. [Getting started](#header1)
2. [The lifecycle callbacks](#header2)
3. [Paginize annotations](#header3)
4. [Argument passing between pages](#header4)
5. [Proguard rules](#header5)


####<a name="header1"></a> 1. Getting started

1. Create a layout file(res/layout/page_frame.xml) for FramePage:

```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="48dp"
    android:background="#ffe6e6e6">

    <TextView
        android:id="@+id/tv_back"
        android:layout_width="wrap_content"
        android:layout_height="fill_parent"
        android:layout_alignParentLeft="true"
        android:layout_gravity="center"
        android:gravity="center"
        android:paddingLeft="10dip"
        android:paddingRight="10dip"
        android:text="Back"
        />

    <TextView
        android:id="@+id/tv_title"
        style="@style/top_bar_title_center"
        android:layout_marginLeft="72dip"
        android:layout_marginRight="72dip"
        android:singleLine="true"
        android:text="Title" />

    <RelativeLayout
        android:id="@+id/container"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginTop="@dimen/header_bar_height"
        />

</RelativeLayout>
```

2. Create the FramePage, this page will be inherited by other pages that need a header(title bar) with a BACK button and a title: 

```java
@PageLayout(R.layout.page_frame)
public abstract class FramePage extends Page implements View.OnClickListener {
    @InjectView(value = R.id.tv_back, listenerTypes = {View.OnClickListener.class}) 
    private TextView mTvBack;

    @InjectView(R.id.tv_title) 
    private TextView mTvTitle;

    public FramePage(PageActivity pageActivity) {
        super(pageActivity);
    }

    protected void setBackButtonVisibility(int visibility) {
        mTvBack.setVisibility(visibility);
    }

    protected void enableBackButton(boolean enable) {
        mTvBack.setEnabled(enable);
    }

    protected void setBackButtonText(String text) {
        mTvBack.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        mTvBack.setText(text);
    }

    protected void setTitleText(String text) {
        mTvTitle.setText(text);
    }

    protected void onBackButtonClicked() {
        hide(true, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                onBackButtonClicked();
            break;
        }
    }
}
```

3. Create another layout(page_test.xml) for TestPage, this page contains only a TextView:

```xml
<TextView xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/tv_content"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent"
    android:gravity="center"
    android:padding="10dip"
    />
`````

4. Create the TestPage:

```java
// here we inherit the layout from FramePage, i.e. R.layout.page_frame, insert R.layout.page_test
// into the R.id.container element of the parent layout. Since we subclass FramePage, we also inherit
// the code for handling the BACK button press
@InsertPageLayout(value = R.layout.page_test, parent = R.id.container)
public class TestPage extends FramePage {
    @InjectView(R.id.tv_content)
    private TextView mTvContent;

    public TestPage(PageActivity pageActivity) {
        super(pageActivity);

        mTvContent.setText("Hello Paginize!");
    }
}
```

After the steps above, TestPage is a page that contains a titlebar with a BACK button on the 
top-left corner and a TextView as the content at the center. As you can see, we don't
need to repeat the boilterplate code for setting up the views of FramePage. **Page inherited, its layout is inherited as well.**

5. Create an Activity that extends PageActivity, and show the TestPage:

```java
@InjectPageAnimator(SlidePageAnimator.class)
public class MainActivity extends PageActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new TestPage(this).show(null, true);
    }
}
```

That's all for using **Paginize** to make a one screen application. Here you may find that it is sort of
more hassle than just use Activity, but **Paginize** is extremely useful when you use it to make more
complicated applications, you see the advantages of it when you use it to structure a real application.

####<a name="header2"></a> 2. The lifecycle methods

Page (including Page, InnerPage) subclasses `ViewWrapper`, lifecycle methods are delcared in this class:

* onShow()
    - The page is ready to be shown, but not yet attached to the view hierarchy
* onShown()
    - The page is shown, i.e. it is pushed onto the page stack, attached to the view hierarchy, and if an animation transition is involved, this method is called when the animation finishes.
* onCovered()
    - When a new page is about to be pushed onto the stack, this method is called for the previous page.
* onUncovered()
    - This method is called for the previous page right before `onShown` being called for the new page.
* onHide()
    - The page is *ready to be* hidden, i.e., it is about to be popped out from the page stack.
* onHidden()
    - The page is hidden, i.e., it is popped out from the page stack, detached from the view hierarchy. 


When a page is popped, it is ready be to garbage collected, because for most cases no one references the page at this point, but **you could**, that means if you keep a reference to the page, you can call show()/hide() pair multiple times to reuse the page. And that is why the method is named **onHidden()** instead of *onDestroy()*, because the framework will not know whether it is destroyed.

Besides the lifecycle methods introduced by the framework, Paginize mirrors most of the Activity lifecycle methods, for example, when `onResume()` is called for the current Activity, Paginize passes the method call to the top page(the currently showing page), same for methods like `onPause()`, `onActivityResult()`, etc.

####<a name="header3"></a> 3. Paginize annotations

Paginize takes advantage of Java Annotations to make use of the framework easier, and make features like **layout inheritance** possible. Some of the annotations are just syntax sugar to make the code more consistent when using Paginize.

* `@PageLayout`
    - Annotated on pages, specifies a layout resource id for the current page.
* `@InsertPageLayout`
    - Used in layout inheritance, annotated on pages, specifies a layout resource id to be inserted into the inherited layout, there is an optional `parent` field which can specify a parent element for the inserted layout. if the `parent` field is ommitted, the inserted layout will be added as the last element(s) of the inherited layout. Note, the layout specified for this annotation can be enclosed with the `<merge>` tag.
* `@InnerPageContainerLayoutResId`
    - Annotated on pages that subclass `ContainerPage`, which is normally used to implement tabbed UI(forget about TabHost :). This annotation specifies resource id(normally a ViewGroup or subclass of ViewGroup) in the layout specified with `@PageLayout`, regardless of whether it is specified directly or inherited. In the current container page, you can call `setInnerPage()` to insert an `InnerPage`, which will be added to the layout element specified by this annotation.
* `@InjectView`
    - Annotated on views declared in page, for most cases, the injection happens when the page is instantiated, but if you set the `lazy` field to `true`, injection may be triggered when the `lazyInitializeLayout()` method is called. This annotation also has `listenerTypes` and `listener` feilds to support setting listeners for the injected view.
* `@ListenerDefs` and `@SetListeners`
    - `@ListenerDefs` should be annotated on page constructors, it contains an array of `@SetListeners`, which is introduced to support setting up listeners for views in one place, make all Paginize-powered code consistent. It is normally used when you only want to setup listeners for the views, but do not need to keep references to the views, besides that, `@SetListeners` is same as `@InjectView`.
* `@InjectPageAnimator`
    - Annotated on Activity that subclasses `PageActivity`, offers page transition annimation for page push/pop. `PageAnimator` can be customized, simply subclass `PageAnimator` and override the required methods to create your own page transition annimation.
* `@ListenerMarker`
    - Annotated on listener class used for the `listener` field of `@InjectView` or `@SetListeners`, this annotation is introduced to prevent the listener class from being obfuscated by proguard. Note, add a rule in proguard-project.txt to make this annotation take effect.


####<a name="header4"></a> 4. Argument passing between pages

`onShow()` and `onShown()` take an object as arugment, which is passed from the `show()` method call. `onUncover()` and `onUncovered()` take an object argument as well, which is passed from the top page by calling `setReturnData()` before it is popped from the page stack. This is all that Pagenize offers for argument passing between pages. But, you are not limited to that, and I recommend against using `show()` to pass arguments(hmmm, the mechanism exists for historical reasons). I recommend simply put methods like `setArgumentXYZ` in the destination page, call these methods to pass arguments to the page, (you can even pass a callback to the destination page, which in some cases may be very useful), this is more intuitive. And if you stick to a certain pattern or naming convention, code will be more manageable with this approach.

####<a name="header5"></a> 5. Proguard rules

To prevent annotated classes and fields from being stripped away, the following rules must be put in `proguard-project.txt`.

```java
-keep public class net.neevek.android.lib.paginize.**
-keep @net.neevek.android.lib.paginize.annotation.ListenerMarker class ** { *; }
-keepclassmembers,allowobfuscation class ** {
  @net.neevek.android.lib.paginize.annotation.** <fields>;
}

```

Note
====
The project is still *NOT* stable, APIs may change(but not significantly).

For more, check out the demo project.

Contributing
============
Please fork this repository and contribute back using [pull requests](https://github.com/neevek/Paginize/pulls).


Under MIT license
=================

```
Copyright (c) 2014 - 2015 neevek <i@neevek.net>
See the file license.txt for copying permission.
