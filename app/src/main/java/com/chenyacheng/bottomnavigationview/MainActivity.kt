package com.chenyacheng.bottomnavigationview

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.chenyacheng.bottomnavigationview.databinding.ActivityMainBinding
import com.chenyacheng.bottomnavigationview.ui.dashboard.DashboardFragment
import com.chenyacheng.bottomnavigationview.ui.home.HomeFragment
import com.chenyacheng.bottomnavigationview.ui.notifications.NotificationsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var fragmentList: MutableList<Fragment> = ArrayList(3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fragmentList.add(HomeFragment())
        fragmentList.add(DashboardFragment())
        fragmentList.add(NotificationsFragment())

        binding.viewPager.adapter = MyFragmentPagerAdapter(
            supportFragmentManager,
            lifecycle
        )
        binding.viewPager.offscreenPageLimit = fragmentList.size

        // 禁止滑动
        binding.viewPager.isUserInputEnabled = false
        // 去掉两侧的光晕效果
        binding.viewPager.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER
        val navView: BottomNavigationView = binding.navView
        navView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_AUTO
        navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> binding.viewPager.setCurrentItem(0, false)
                R.id.navigation_dashboard -> binding.viewPager.setCurrentItem(1, false)
                R.id.navigation_notifications -> binding.viewPager.setCurrentItem(2, false)
            }
            true
        }
        clearBottomNavigationViewToast(navView)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(i: Int) {
                //将滑动到的页面对应的 menu 设置为选中状态
                navView.menu.getItem(i).isChecked = true
            }
        })
    }

    private fun clearBottomNavigationViewToast(bottomNavigationView: BottomNavigationView) {
        val ids = ArrayList<Int>()
        ids.add(R.id.navigation_home)
        ids.add(R.id.navigation_dashboard)
        ids.add(R.id.navigation_notifications)
        // 遍历子View，重写长按点击事件
        for (position in 0 until ids.size) {
            bottomNavigationView.getChildAt(0).findViewById<View>(ids[position])
                .setOnLongClickListener { true }
        }
    }

    private inner class MyFragmentPagerAdapter(
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle
    ) :
        FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getItemCount(): Int {
            return fragmentList.size
        }
    }
}