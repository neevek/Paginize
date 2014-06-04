Paginize
========

Introduction
------------
**Paginize** is a **light-weight** application framework for Android, which eases development of Android applications with complex UI structures.
It is more than easy to develop an Android application with Paginize to gain unified user interfaces and modular design for free, Paginize abstracts each user interface or screen as a `Page`, which is simply a wrapper of a `View`. Also it is easy to reuse the layout with the `@InheritPageLayout` annotation, it is like the `frameset` tag in HTML.

Let's take a look at an example:

1. Create a layout file(res/layout/page_main.xml) for the Page:

````
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
````

2. Create a `Page`:

````
@PageLayout(R.layout.page_main)
public class MainPage extends Page {
    @InjectView(R.id.tv_text)
    private TextView mTvText;

    public MainPage(PageActivity pageActivity) {
        super(pageActivity);

        mTvText.setText("Hello Paginize!");
    }
}
````

3. Create an Activity that extends `PageActivity`, and show the Page we just created above:

````
@InjectPageAnimationManager(SlidePageAnimationManager.class)
public class MainActivity extends PageActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new MainPage(this).show();
    }
}
````

That's all for using **Paginize** to make a one screen application. Here you may find that it is sort of more hassle than just use Activity, but **Paginize** is only useful when you use it to make more complicated applications, you see the advantages of it when you use it to structure a real application.

Highlights & Limitations
------------------------

With **Paginize**, you tend to break *huge* Activity into `Page`s, just like breaking *big* problems into smaller pieces so you can focus on one smaller problem at a time, to some extent you isolate each `Page` and its related business logics, which makes your application more maintainable. You can also use a `Page` and multiple `InnerPage`s to create a tabbed user interface, see the example for more details.

**Paginize** is flexible, you can reuse a Page even if it is popped out from the page stack, you can pass a callback into a Page, you can easily customize animations for page transition, etc. there's quite a few in terms of flexibility. But all this comes at some cost, **Paginize** *does not* support device rotation, to be more precise, Pages will not be recreated automatically when the Activity that contains the Pages gets killed and recreated(which also happens on low memory). I could have made it support Page recreation, but that would make `Page` simply an "Activity" that can be directly started without need for declaration in `AndroidManifest.xml`, because in that case I will need to use reflection to restore a `Page`, reusing a `Page` is NOT possible then, passing a callback into a `Page` is NOT possible too, all the flexibility is lost, and that was absolutely NOT what I wanted.

Note, though, that in spite of the limitations I mentioned above, if you create the main `Page` in `Activity.onCreate()`(which you would normally do), it will still be *recreated* as usual when `Activity.onCreate()` is called again, it is just NOT *restored*. Anyway, you can still override `Activity.onSaveInstanceState()` to save some states of your `Pages` and `Activity.onRestoreInstanceState()` to restore the states, it is just NOT handled by the library.

For most apps, it might not be a big deal that no direct support for keeping states for the `Page`s for device rotation.

You may want to compare **Paginize** with Fragment, well I didn't mean to write the library to compete with Fragment, it is just a simpler, more flexible, easier to use, but less feature-rich library.

中文说明文档
------------

概述
----

Paginize是一个轻量级的Android应用框架，可用于快速构建包含复杂结构、具备统一风格、模块化的Android应用程序。
Paginize的实现思路类似Android Fragment，由一个ViewGroup作为View容器，由PageManager(类似FragmentManager)管理，每个界面被抽象为一个Page，每个Page是对一个View的封装（View可以是一个复杂的用户界面），PageManager内部实现一个PageStack用于维护Page，界面的切换实际上是PageStack的压栈（push）与出栈（pop）。
Paginize的模块化主要体现在把一个Activity按照界面业务逻辑拆分成多个Page或者InnerPage，有效地简化复杂界面结构的组织，提高代码的可维护与扩展性。

<h3>Paginize's design philosophy: Simple things should be kept simple.</h3>


开始使用Paginize
----------------

使用Paginize构建模块化的功能/界面是非常简单的：

1. 定义一个Activity（如：MainActivity），继承PageActivity。
2. 定义一个Page（如：MainPage），继承Page，使用@PageLayout注解布局资源文件。
3. 在MainActivity.onCreate()方法中加上new MainPage(this).show();

以上三个步骤即可利用Paginize组织构建一个单个界面的简单Android应用。
> 注：和普通的Android应用一样，MainActivity需要在AndroidManifest.xml中定义、Page的布局文件需要在res/layout/目录下定义。
> 更详细的使用请看项目中的代码

核心类与接口说明
----------------

* PageActivity - 继承自Activity，主要职责是在运行时根据Activity中声明的注解注入相应实例，PageActivity中支持的注解有：@InjectPage、@InjectView，同时PageActivity提供了getPageManager(), showPage(), hideTopPage()等实用方法。
* PageManager - 实现page栈（PageStack），管理Page切换。主要提供pushPage()与popPage()方法。
* Page - 用户界面（UI）的抽象，包装了一个View（a wrapper of View），主要提供getView()方法用于获取被包装的View，show()方法用于显示当前Page（即把当前Page通过PageManager压到PageStack中，同时把getView()所返回的View加到View容器中），hide()或者hideWithAnimation()方法用于隐藏/退出当前Page（把当前Page通过PageManager从PageStack中弹出来）。Page支持@InjectPage、@InjectView、@PageLayout注解。Page还包含了Activity中已有的一些生命周期方法和新增了4个Page特有的生命周期方法：
  * onBackPressed() - 当Activity.onBackPressed()被调用的时候，当前PageStack上最顶的Page.onBackPressed()会被调用。
  * onActivityResult() - 当Activity.onActivityResult()被调用的时候，当前PageStack上最顶的Page.onActivityResult()会被调用。
  * onResume() - 当Activity.onResume()被调用的时候，当前PageStack上最顶的Page.onResume()会被调用。
  * onPause() - 当Activity.onPause()被调用的时候，当前PageStack上最顶的Page.onPause()会被调用。
  * onShown() - 当被压到PageStack栈顶的时候（即调用Page.show()），Page.onShown()会被调用。
  * onHidden() - 当被从PageStack弹出的时候（即调用Page.hide()或者Page.hideWithAnimation()），Page.onHidden()会被调用。
  * onCovered() - 当有新Page被压到栈顶的时候，新Page的前一个Page的onCovered()会被调用。
  * onUncovered() - 当栈顶的Page被弹出的时候，被弹出的Page的前一个Page的onUncovered()会被调用。
* InnerPagerContainer - 继承自Page，用于支持包含多个InnerPage功能（通常用于构建包含tab按钮的界面）。
* InnerPage - 类似Page，只能在InnerPageContainer中使用。
* onSet() - 当InnerPage被set到InnerPageManager中，此方法被调用。
* onReplaced() - 当新的InnerPage被set到InnerPageManager中，旧InnerPage的onReplaced方法被调用。
  > 同时InnerPage也mirror了Page中的其它生命周期方法，InnerPage中的这些生命周期方法仅当InnerPage是最上层界面的时候才被调用。

对比Android Fragment
--------------------

对比Fragment，最大的一点区别就是：Fragment内置支持状态保存，即可以很好支持旋屏，在onLowMemory发生时可以有更优雅的处理。Paginize则放弃支持状态保存，从而获取更大的灵活度。
Paginize的API比Fragment更加简洁，更加straightforward，从代码实现上，Paginize的运行效率比Fragment高（Fragment的实现每次切换Fragment都需要一个FragmentTransaction、每次调用FragmentManager.add/replace其内部实现都需要执行findViewById），Paginize并不具备Fragment的所有功能，这也不是它的设计初衷，Paginize以最简单的方式提供了模块化、View注入、统一切换动画效果等功能。

* Q1: 为什么放弃支持状态保存可以获取更大灵活度？
* A1: 支持Page状态保存，那就要支持Page状态恢复，势必要用反射恢复Page类，这个时候数据可以恢复，但是行为是没办法恢复的。Page间是支持回调的，可以把一个行为在任意多的Page间进行传递，优雅地实现各种复杂的界面逻辑。
* Q2: onLowMemory发生时，使用Paginize会有什么问题？
* A2: PageActivity可能会被销毁，导致所有Page被销毁，应用仍然可以使用Activity.onSaveInstanceState()保存需要保存的应用数据，等到PageActivity被重建的时候使用Activity.onRestoreInstanceState()恢复相应的数据，但是Page不会被重建，所以这时候会出现“回到首页的情况”。
* Q3: Paginize不支持状态保存，实际应用会有什么影响？
* A3: 就目前的硬件水平，发生onLowMemory的情况已经非常少。另外就算发生了，出现“回到首页的情况”，对于大部分应用来讲，也无关紧要，并不会很严重影响用户体验(It is not the end of the world)。实际上很多直接使用Activity或者Fragment的应用都不处理onSaveInstanceState()。基于这些考虑，才有了Paginize的取舍。

Under MIT license
-----------------

````
Copyright (c) 2014 neevek <i at neevek.net>
See the file license.txt for copying permission.
````
