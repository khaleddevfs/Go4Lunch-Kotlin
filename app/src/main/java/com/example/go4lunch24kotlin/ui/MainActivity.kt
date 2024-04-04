package com.example.go4lunch24kotlin.ui

import android.util.Log
import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.adapters.PredictionsAdapter
import com.example.go4lunch24kotlin.databinding.ActivityMainBinding
import com.example.go4lunch24kotlin.factory.Go4LunchFactory
import com.example.go4lunch24kotlin.models.PredictionViewState
import com.example.go4lunch24kotlin.util.PermissionsAction
import com.example.go4lunch24kotlin.viewModel.MainActivityViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var mainActivityViewModel: MainActivityViewModel? = null
    private var toolbar: Toolbar? = null
    private var drawerLayout: DrawerLayout? = null
    private var appBarConfiguration: AppBarConfiguration? = null
    private var restaurantId: String? = null
    private var currentUserRestaurantChoiceStatus = 0
    private var adapter: PredictionsAdapter? = null

    private val locationPermissionCode = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate: Starting.") // Log pour onCreate

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // CONFIGURE VIEWMODEL
        val viewModelFactory = Go4LunchFactory.instance
        mainActivityViewModel = ViewModelProvider(this, viewModelFactory!!)[MainActivityViewModel::class.java]

        Log.d("MainActivity", "onCreate: View model configured.") // Log pour la configuration du ViewModel


        drawerLayout = binding.mainDrawerLayout

        // CONFIGURE ALL VIEWS
        configureToolBar()
        configureDrawerLayout()
        configureNavigationView()
        configureDrawerMenu()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_frame_layout) as NavHostFragment?
        val navController = navHostFragment?.navController

        if (navController != null) {
            setupActionBarWithNavController(this, navController, appBarConfiguration!!)
            setupWithNavController(binding.mainBottomNavigationView, navController)
            Log.d("MainActivity", "NavController setup completed.") // Log pour confirmer la configuration du NavController.

        } else {
            Log.d("MainActivity", "Error: NavController could not be initialized.") // En cas d'erreur dans l'initialisation du NavController.

        }



        configureViewModel()
        updateUIWhenCreating()
        configureYourLunch()
        configureRecyclerView()
    }

    private fun configureToolBar() {
        Log.d("MainActivity", "Configuring toolbar.") // Log pour configureToolBar

        toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar)
    }

    private fun configureDrawerLayout() {
        Log.d("MainActivity", "Configuring DrawerLayout.") // Log pour configureDrawerLayout

        drawerLayout = findViewById(R.id.main_drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawerLayout,
            toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        with(drawerLayout) {
            this?.addDrawerListener(toggle)
        }
        toggle.syncState()
    }

    private fun configureNavigationView() {
        Log.d("MainActivity", "Configuring NavigationView.") // Log pour configureNavigationView

        val navigationView = findViewById<NavigationView>(R.id.main_navigation_view)
        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun configureDrawerMenu() {


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.bottom_navigation_menu_map_button, R.id.bottom_navigation_menu_list_button, R.id.bottom_navigation_menu_workMates_button)
            .setOpenableLayout(drawerLayout)
            .build()
        Log.d("MainActivity", "Configuring transition.")
    }

    private fun configureViewModel() {
        Log.d("MainActivity", "Configuring ViewModel.") // Log pour configureViewModel

        mainActivityViewModel!!.actionSingleLiveEvent.observe(this) { action ->
            when (action) {
                PermissionsAction.PERMISSION_ASKED -> {
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION),
                        locationPermissionCode)
                    Toast.makeText(this, getString(R.string.need_your_position), Toast.LENGTH_SHORT)
                        .show()
                }
                PermissionsAction.PERMISSION_DENIED -> {
                    val alertDialogBuilder = MaterialAlertDialogBuilder(this)
                    alertDialogBuilder.setTitle(getString(R.string.title_alert))
                    alertDialogBuilder.setMessage(getString(R.string.rational))
                    alertDialogBuilder.show()
                }
                else -> {}
            }
        }
    }

    private fun updateUIWhenCreating() {

        val header = binding.mainNavigationView.getHeaderView(0)
        val profilePicture = header.findViewById<ImageView>(R.id.user_navigation_header_image_view_picture)
        val profileUsername = header.findViewById<TextView>(R.id.user_navigation_header_name_text)
        val profileUserEmail = header.findViewById<TextView>(R.id.user_navigation_header_email_text)
        val currentUser = FirebaseAuth.getInstance().currentUser
        Log.d("MainActivity", "Updating UI when creating.") // Log pour updateUIWhenCreating

        currentUser?.let { user ->
            //Get picture URL from Firebase
            val photoUrl = user.photoUrl
            photoUrl?.let { photo ->
                Glide.with(this)
                    .load(photo)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePicture)
            }
        }

        //Get email & username from Firebase
        val email =
            if (TextUtils.isEmpty(currentUser!!.email)) getString(R.string.info_no_email_found) else currentUser.email!!
        val username =
            if (TextUtils.isEmpty(currentUser.displayName)) getString(R.string.info_no_username_found) else currentUser.displayName!!

        //Update views with data
        profileUsername.text = username
        profileUserEmail.text = email
    }

    private fun configureYourLunch() {
        mainActivityViewModel!!.getUserRestaurantChoice()
        mainActivityViewModel!!.mainActivityYourLunchViewStateMediatorLiveData.observe(this) { userLunch ->
            restaurantId = userLunch.restaurantId
            currentUserRestaurantChoiceStatus = userLunch.currentUserRestaurantChoiceStatus
        }
    }

    private fun configureRecyclerView() {
        adapter = PredictionsAdapter { predictionText ->
            mainActivityViewModel!!.userSearch(predictionText)
            initAutocomplete()
        }
        val recyclerView = findViewById<RecyclerView>(R.id.predictions_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        mainActivityViewModel!!.predictionsMediatorLiveData.observe(this) { predictions ->
            adapter!!.submitList(predictions)
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.drawer_menu_lunch_button -> checkUserRestaurantChoice()
            R.id.drawer_menu_settings_button -> {
                startActivity(Intent(this, SettingActivity::class.java))
            }
            R.id.drawer_menu_logout_button -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        drawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }

    private fun checkUserRestaurantChoice() {
        when (currentUserRestaurantChoiceStatus) {
            0 -> Toast.makeText(this,
                this.getString(R.string.no_restaurant_selected),
                Toast.LENGTH_SHORT).show()
            1 -> startActivity(RestaurantDetailsActivity.navigate(this, restaurantId!!))
        }
    }

    // CLEAR THE AUTOCOMPLETE ADAPTER WITH EMPTY LIST WHEN USER FINISHED HIS RESEARCH
    // TO CLEAN THE AUTOCOMPLETE RECYCLERVIEW
    private fun initAutocomplete() {
        val emptyList: List<PredictionViewState> = ArrayList()
        adapter!!.submitList(emptyList)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(this,
            R.id.main_frame_layout)
        return (navigateUp(navController, appBarConfiguration!!) || super.onSupportNavigateUp())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // INFLATE MENU
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_search_menu, menu)
        val item = menu!!.findItem(R.id.search_menu)

        // GET SEARCHVIEW
        val searchView = menu.findItem(R.id.search_menu).actionView as SearchView
        searchView.maxWidth = Int.MAX_VALUE
        searchView.setBackgroundColor(Color.TRANSPARENT)
        val editText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        editText.setTextColor(Color.WHITE)
        editText.setHintTextColor(Color.LTGRAY)

        searchView.setIconifiedByDefault(false)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                mainActivityViewModel!!.sendTextToAutocomplete(newText)
                return false
            }
        })

        // WHEN USER LEAVES THE SEARCHVIEW, RESET AND CLOSE THE SEARCHVIEW
        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                mainActivityViewModel!!.userSearch("")
                return true
            }
        })

        return true
    }

    // WHEN VIEW IS ON RESUME CHECK THE PERMISSION STATE IN VIEWMODEL (AND PASSED THE ACTIVITY
    // FOR THE ALERTDIALOG EVEN IF ITS NOT THE GOOD WAY TO USE A VIEWMODEL,
    // WE DON'T HAVE OTHER CHOICE)
    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume: Checking permissions.") // Log pour onResume

        mainActivityViewModel!!.checkPermission(this)
    }


}

/*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var restaurantId: String? = null
    private var currentUserRestaurantChoiceStatus = 0
    private lateinit var adapter: PredictionsAdapter

    private val locationPermissionCode = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        initializeViewModel()
        configureUI()
    }

    private fun initializeBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initializeViewModel() {
        val viewModelFactory = Go4LunchFactory.instance
        mainActivityViewModel = ViewModelProvider(this, viewModelFactory)[MainActivityViewModel::class.java]
    }

    private fun configureUI() {
        configureToolBar()
        configureDrawerLayout()
        configureNavigationView()
        configureDrawerMenu()
        configureNavController()
        configureViewModelObservers()
        updateUIWhenCreating()
        configureRecyclerView()
    }

    private fun configureToolBar() {
        toolbar = binding.mainToolbar
        setSupportActionBar(toolbar)
    }

    private fun configureDrawerLayout() {
        drawerLayout = binding.mainDrawerLayout
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    // Les autres fonctions de configuration suivent un modèle similaire à configureToolBar()
    // et configureDrawerLayout(), donc elles ne sont pas toutes réécrites ici pour éviter la répétition.

    // Exemple de fonction pour gérer les permissions (simplifiée pour l'exemple)
    private fun checkAndRequestPermissions() {
        // Logique de vérification et de demande des permissions
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Gestion de la navigation
    }

    private fun configureNavigationView() {
        Log.d("MainActivity", "Configuring NavigationView.") // Log pour configureNavigationView

        val navigationView = findViewById<NavigationView>(R.id.main_navigation_view)
        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun configureDrawerMenu() {


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.bottom_navigation_menu_map_button, R.id.bottom_navigation_menu_list_button, R.id.bottom_navigation_menu_workMates_button)
            .setOpenableLayout(drawerLayout)
            .build()
        Log.d("MainActivity", "Configuring transition.")
    }

    private fun updateUIWhenCreating() {

        val header = binding.mainNavigationView.getHeaderView(0)
        val profilePicture = header.findViewById<ImageView>(R.id.user_navigation_header_image_view_picture)
        val profileUsername = header.findViewById<TextView>(R.id.user_navigation_header_name_text)
        val profileUserEmail = header.findViewById<TextView>(R.id.user_navigation_header_email_text)
        val currentUser = FirebaseAuth.getInstance().currentUser
        Log.d("MainActivity", "Updating UI when creating.") // Log pour updateUIWhenCreating

        currentUser?.let { user ->
            //Get picture URL from Firebase
            val photoUrl = user.photoUrl
            photoUrl?.let { photo ->
                Glide.with(this)
                    .load(photo)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePicture)
            }
        }

        //Get email & username from Firebase
        val email =
            if (TextUtils.isEmpty(currentUser!!.email)) getString(R.string.info_no_email_found) else currentUser.email!!
        val username =
            if (TextUtils.isEmpty(currentUser.displayName)) getString(R.string.info_no_username_found) else currentUser.displayName!!

        //Update views with data
        profileUsername.text = username
        profileUserEmail.text = email
    }

    private fun configureYourLunch() {
        mainActivityViewModel!!.getUserRestaurantChoice()
        mainActivityViewModel!!.mainActivityYourLunchViewStateMediatorLiveData.observe(this) { userLunch ->
            restaurantId = userLunch.restaurantId
            currentUserRestaurantChoiceStatus = userLunch.currentUserRestaurantChoiceStatus
        }
    }

    private fun configureRecyclerView() {
        adapter = PredictionsAdapter { predictionText ->
            mainActivityViewModel!!.userSearch(predictionText)
            initAutocomplete()
        }
        val recyclerView = findViewById<RecyclerView>(R.id.predictions_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        mainActivityViewModel!!.predictionsMediatorLiveData.observe(this) { predictions ->
            adapter!!.submitList(predictions)
        }
    }
}

 */
