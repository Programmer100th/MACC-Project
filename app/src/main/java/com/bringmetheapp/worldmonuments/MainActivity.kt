package com.bringmetheapp.worldmonuments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var listener: NavController.OnDestinationChangedListener

    lateinit var toggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        drawerLayout = findViewById(R.id.drawer_layout)





        //Put here top level destinations of tab bar.
        // User Fragment is needed if we want to visualize hamburger menu instead of back arrow
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.searchFragment,
                R.id.favoritesFragment,
                R.id.gameFragment,
                R.id.userFragment
            ),
            drawerLayout
        )

        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        //Bottom tab bar
        bottom_nav.setupWithNavController(navController)

        //Navigation drawer
        nav_view.setupWithNavController(navController)

        //It is important that the listener is set after set up with nav controller
        nav_view.setNavigationItemSelectedListener(this)


    }



    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d("Prova", "ciao")
        when(item.itemId)
        {


            R.id.userFragment -> {

                if(!item.isChecked) {

                    //bottom_nav.menu.setGroupCheckable(0, false, true)
                    bottom_nav.uncheckAllItems()
                    navController.navigate(R.id.userFragment)
                    drawerLayout.closeDrawer(nav_view)
                }
                else
                {
                    drawerLayout.closeDrawer(nav_view)

                }


                /*

                val intent = Intent(applicationContext, UserActivity::class.java)
                startActivity(intent)
                drawerLayout.closeDrawer(nav_view)

                 */

            }
        }
        return true
    }

    //Needed to uncheck items of tab view when an item of drawer is clicked
    fun BottomNavigationView.uncheckAllItems() {
        menu.setGroupCheckable(0, true, false)
        for (i in 0 until menu.size()) {
            menu.getItem(i).isChecked = false
        }
        menu.setGroupCheckable(0, true, true)
    }



}