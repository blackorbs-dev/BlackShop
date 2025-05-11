package blackorbs.dev.blackshop.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import blackorbs.dev.blackshop.R
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.get

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupNavigation()
    }

    private fun setupNavigation(){
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.layout_container) as NavHostFragment

        val navController = navHostFragment.navController
        val graph = navController.navInflater.inflate(R.navigation.nav_graph)

        val isLoggedIn = get<FirebaseAuth>().currentUser != null
        graph.setStartDestination(
            if(isLoggedIn) R.id.listScreen
            else R.id.loginScreen
        )

        navController.graph = graph
    }
}