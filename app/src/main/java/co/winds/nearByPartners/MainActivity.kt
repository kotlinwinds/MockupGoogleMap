package co.winds.nearByPartners

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import co.winds.R
import co.winds.common.BaseActivity
import co.winds.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Utils.setToolbar(this, findViewById(R.id.toolbar),"Nearby Partners")



        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPager_map.adapter = viewPagerAdapter
        tabLayoutMap.setupWithViewPager(viewPager_map)

    }

    class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            var fragment: Fragment? = null
            if (position == 0) {
                fragment = FragmentA()
            } else if (position == 1) {
                fragment = FragmentB()
            }
            return fragment
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            var title: String? = null
            if (position == 0) {
                title = "Map View"
            } else if (position == 1) {
                title = "List View"
            }
            return title
        }
    }


}
