package com.chenyacheng.bottomnavigationview

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.chenyacheng.bottomnavigationview.databinding.ActivityMainBinding
import com.chenyacheng.bottomnavigationview.ui.dashboard.DashboardFragment
import com.chenyacheng.bottomnavigationview.ui.home.HomeFragment
import com.chenyacheng.bottomnavigationview.ui.notifications.NotificationsFragment
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var fragmentList: MutableList<Fragment> = ArrayList(3)
    private val lottieJsonList = arrayListOf(
        "lottie/tabbar_animate_home.json",
        "lottie/tabbar_animate_dynamic.json",
        "lottie/tabbar_animate_discover.json",
        "lottie/tabbar_animate_mine.json"
    )
    private var mPreClickPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fragmentList.add(HomeFragment())
        fragmentList.add(DashboardFragment())
        fragmentList.add(DashboardFragment())
        fragmentList.add(NotificationsFragment())

        val navView = binding.navView
        // 添加 menu 菜单文件的两种方式
        // 方式1：动态添加 menu 布局文件
        navView.inflateMenu(R.menu.bottom_nav_menu)
        navView.itemIconTintList = null
        // 方式2：添加 lottie 动画图标
//        navView.menu.apply {
//            add(Menu.NONE, R.id.navigation_home, 0, resources.getString(R.string.title_home))
//            add(Menu.NONE, R.id.navigation_life, 1, resources.getString(R.string.title_life))
//            add(Menu.NONE, R.id.navigation_message, 2, resources.getString(R.string.title_message))
//            add(Menu.NONE, R.id.navigation_mine, 3, resources.getString(R.string.title_mine))
//            for (i in lottieJsonList.indices) {
//                setLottieDrawable(lottieJsonList, navView, i)
//            }
//        }
        // 显示角标
        val badge = navView.getOrCreateBadge(R.id.navigation_message)
        badge.number = 30
        badge.maxCharacterCount = 2
        badge.backgroundColor = Color.BLUE
        badge.badgeTextColor = Color.RED
        badge.badgeGravity = BadgeDrawable.BOTTOM_START
        badge.clearNumber()
        navView.removeBadge(R.id.navigation_message)
        //navView.menu.findItem(R.id.navigation_me).isVisible = false
        navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> binding.viewPager.setCurrentItem(0, false)
                R.id.navigation_life -> binding.viewPager.setCurrentItem(1, false)
                R.id.navigation_message -> binding.viewPager.setCurrentItem(2, false)
                R.id.navigation_mine -> binding.viewPager.setCurrentItem(3, false)
            }
            true
        }
        clearBottomNavigationViewToast(navView)
        navView.selectedItemId = R.id.navigation_home

        binding.viewPager.adapter = MyFragmentStateAdapter(supportFragmentManager, lifecycle)
        binding.viewPager.offscreenPageLimit = fragmentList.size
        // 去掉两侧的光晕效果
        binding.viewPager.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER
        // 禁止滑动
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(i: Int) {
                // 将滑动到的页面对应的 menu 设置为选中状态
                navView.menu.getItem(i).isChecked = true
                handleNavigationItem(navView, navView.menu.getItem(i))
            }
        })
    }

    private fun Menu.setLottieDrawable(
        lottieAnimationList: ArrayList<String>, navView: BottomNavigationView, position: Int
    ) {
        getItem(position).icon = getLottieDrawable(lottieAnimationList[position], navView)
    }

    /**
     * 获取 Lottie Drawable
     */
    private fun getLottieDrawable(
        fileName: String, bottomNavigationView: BottomNavigationView
    ): LottieDrawable {
        return LottieDrawable().apply {
            val result = LottieCompositionFactory.fromAssetSync(
                bottomNavigationView.context.applicationContext, fileName
            )
            callback = bottomNavigationView
            composition = result.value
        }
    }

    private fun handleNavigationItem(bottomNavigationView: BottomNavigationView, item: MenuItem) {
        handlePlayLottieAnimation(bottomNavigationView, item)
        mPreClickPosition = item.order
    }

    private fun handlePlayLottieAnimation(
        bottomNavigationView: BottomNavigationView, item: MenuItem
    ) {
        val currentIcon = item.icon as? LottieDrawable
        currentIcon?.apply {
            playAnimation()
        }
        // 这里判断如果当前点击的和上一次点击索引不同，则将上一次点击索引位置的 MenuItem Icon 替换
        if (item.order != mPreClickPosition) {
            // 获取到上一个 MenuItem 并修改对应的 icon drawable
            bottomNavigationView.menu.getItem(mPreClickPosition).icon = getLottieDrawable(
                lottieJsonList[mPreClickPosition], bottomNavigationView
            )
        }
    }

    private fun clearBottomNavigationViewToast(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.menu.forEach {
            val menuItemView = bottomNavigationView.getChildAt(0)
                .findViewById(it.itemId) as BottomNavigationItemView
            menuItemView.setOnLongClickListener {
                // 去掉震动效果
                menuItemView.isHapticFeedbackEnabled = false
                true
            }
        }
    }

    private inner class MyFragmentStateAdapter(
        fragmentManager: FragmentManager, lifecycle: Lifecycle
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getItemCount(): Int {
            return fragmentList.size
        }
    }
}