Paginize
========

The embryo of this project existed before I knew anything about Fragment, at that time, I was planning refactor of an old project, which was started by myself, that project consisted of only a few Activities, but lots of features were planned to be added to it, I felt the need for a more extensible & easier solution for managing the user interfaces & business logics, each user interface with an Activity is overkill to me and it is definitely not the choice. 

I created a basic concept that each user interface be modeld as a **Page**, and Pages will be pushed onto or popped out from a stack, states of the Page will persist as long as it is kept in the stack, simple and straightforward. 

After developing & using **Paginize** for some time, I learned that there was an offical framework called Fragment from the Support Library(I didn't know much about offical support from Google at that time, including this Support Library, sorry for my being so naive...), I thought, damnit I must have reinvented a useless wheel. I spent some time learning Fragment, and trying to use it in a demo project, but at the end I just didn't buy the idea behind it: 

 1. Fragments have to be explicitly saved to the backstack. 
 2. Each Fragment supports creating its own option menus but no easy way to remove the menus. 
 3. No intuitive way to handle BACK press on a per-Fragment basis. 
 4. No global setting for Fragment transition animation
 5. Too many life cycle methods, Yes, I counted, there are 29 `onXXX` methods. Do we really need ALL of them?
 6. Hard-to-understand APIs, the `FragmentTransaction` object has methods like `add`, `addToBackstack`, `replace`, `show`, `hide`, `attach` and `detach`, the methods alone are good and I can easily guess what they do, but why methods like `show` and `hide` exist in a `Transaction` object? it is weird, the feeling is that it gives me too many options, and I find it hard to make a choice.

You may argue that it is not difficult to solve all these problems and learn how to use it, but I hate it when I have to keep looking for all kinds of workaround, that's not neat and the code would be ugly.
 
Fragment is good and it is versatile, but for me it is too heavy, not flexible at all and there are quite a few restrictions. **Paginize** turned out to be a useful wheel for myself, and maybe hopefully for you. Since this is the README for **Paginize**, I will not talk much about inflexibilties & restrictions of Fragment, but if you have ever used Fragment, I believe you already have a collection of them.

**Paginize** is a light-weight framework for Android, which eases development of Android applications with complex UI structures. **Paginize** makes use of Java annotations to inject layouts and views, layouts and Pages can be inherited, the layout inheritance feature is just like the `frameset` tag in HTML, and this makes code reuse much easier.

1. Create a layout file(res/layout/page_header.xml) for HeaderPage:

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

</RelativeLayout>
```

2. Create the HeaderPage, this page will be inherited by other pages that need a header(title bar)
with a BACK button and a title: 

```java
@PageLayout(R.layout.page_header)
public abstract class HeaderPage extends Page {
    @InjectView(R.id.tv_back) private TextView mTvBack;
    @InjectView(R.id.tv_title) private TextView mTvTitle;

    public HeaderPage(PageActivity pageActivity) {
        super(pageActivity);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_back:
                        onBackButtonClicked();
                        break;
                }
            }
        };

        mTvBack.setOnClickListener(onClickListener);
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
        hideWithAnimation(true);
    }
}
```

3. Create another layout(page_xxx_detail.xml) for XXXDetailPage, let's say this detail page contains only one TextView:

```xml
<TextView xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/tv_content"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent"
    android:gravity="center"
    android:padding="10dip"
    />
````

4. Create the XXXDetailPage:

```java
// here we inherit the layout from HeaderPage, and of course 
// the logics for handling the BACK button press
@InheritPageLayout(R.layout.page_xxx_detail)
public class XXXDetailPage extends HeaderPage {
    @InjectView(R.id.tv_content)
    private TextView mTvContent;

    public XXXDetailPage(PageActivity pageActivity) {
        super(pageActivity);

        mTvContent.setText("Hello Paginize!");
    }
}
```

After the steps above, XXXDetailPage is a page that contains a titlebar with a BACK button on the 
top-left corner and a TextView as the content at the center.

5. Create an Activity that extends PageActivity, and show the XXXDetailPage:

```java
@InjectPageAnimationManager(SlidePageAnimationManager.class)
public class MainActivity extends PageActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new XXXDetailPage(this).show(null, true);
    }
}
```

That's all for using **Paginize** to make a one screen application. Here you may find that it is sort of
more hassle than just use Activity, but **Paginize** is only useful when you use it to make more
complicated applications, you see the advantages of it when you use it to structure a real
application.

You have already seen the usages of `@PageLayout`, `@InheritPageLayout` and a few other annotations, and 
some framework code, you may want to explore and find more from the demo project.

Under MIT license
-----------------

```
Copyright (c) 2014 neevek <i at neevek.net>
See the file license.txt for copying permission.
```
