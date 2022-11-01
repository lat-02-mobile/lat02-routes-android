package com.jalasoft.routesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.amplitude.android.Amplitude
import com.google.firebase.auth.FirebaseAuth
import com.jalasoft.routesapp.databinding.ActivityMainBinding
import com.jalasoft.routesapp.util.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var amplitude: Amplitude

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        amplitude = PreferenceManager.getAmplitude(binding.root.context.applicationContext)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            goToAuthActivity()
        } else {
            amplitude.setUserId(currentUser.uid)
            com.amplitude.api.Amplitude.getInstance().trackSessionEvents(true)
            val phoneNumber = currentUser.phoneNumber
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                goToAuthActivity()
            }
        }

        supportActionBar?.hide()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
            val screensWithHiddenBottomNav = listOf(
                R.id.cityPickerFragment,
                R.id.splashScreenFragment,
                R.id.routeSelected,
                R.id.tourPointDetailFragment,
                R.id.routeEditorFragment,
                R.id.promoteUsersFragment
            )
            if (screensWithHiddenBottomNav.contains(nd.id)) {
                binding.bottomNavigation.visibility = View.GONE
            } else {
                binding.bottomNavigation.visibility = View.VISIBLE
            }
        }
    }
    private fun goToAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
}
