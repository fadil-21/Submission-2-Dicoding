package com.example.submission2

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submission2.databinding.ActivityMainBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject
import kotlin.Exception

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: UserAdapter
    private val list : ArrayList<User> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = UserAdapter(list)
        adapter.notifyDataSetChanged()
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        rvConfig()
        getListUsers()

    }

    private fun rvConfig() {
        binding.rvUsers.layoutManager = LinearLayoutManager(binding.rvUsers.context)
        binding.rvUsers.setHasFixedSize(true)
        binding.rvUsers.addItemDecoration(
            DividerItemDecoration(
                binding.rvUsers.context,
                DividerItemDecoration.VERTICAL
            )
        )
    }


    private fun getListUsers() {
        showLoading(true)
        val client = AsyncHttpClient()
        client.addHeader("User-agent", "request")
        client.addHeader("Authorization", "token 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")
        val url = "https://api.github.com/users"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>?,
                responseBody: ByteArray
            ) {
                showLoading(false)
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val responObj = JSONArray(result)
                    for (i in 0 until responObj.length()) {
                        val listObj = responObj.getJSONObject(i)
                        val username = listObj.getString("login")
                        getUserDetails(username)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity,  e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                showLoading(false)
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
            }

        })

    }

    private fun getUserSearch(query: String?) {
        showLoading(true)
        val client = AsyncHttpClient()
        client.addHeader("User-agent", "request")
        client.addHeader("Authorization", "token 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")
        val url = "https://api.github.com/search/users?q=$query"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>?,
                responseBody: ByteArray
            ) {
                showLoading(false)
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val responObj = JSONObject(result)
                    val listObj = responObj.getJSONArray("items")
                    Log.d(TAG, "length: " + listObj.length().toString())
                    for (i in 0 until listObj.length()) {
                        val user = listObj.getJSONObject(i)
                        val username = user.getString("login")
                        getUserDetails(username)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                showLoading(false)
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun getUserDetails(loginx: String) {
        Log.d(TAG, loginx)
        showLoading(true)
        val client = AsyncHttpClient()
        client.addHeader("User-agent", "request")
        client.addHeader("Authorization", "token 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")
        val url = "https://api.github.com/users/$loginx"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                showLoading(false)
                val result = String(responseBody)
                Log.d(TAG, result)

                try {
                    val responObj = JSONObject(result)

                    val username= responObj.getString("login")
                    val name= responObj.getString("name")
                    val avatar= responObj.getString("avatar_url")
                    val company = responObj.getString("company")
                    val location= responObj.getString("location")
                    val repository= responObj.getString("public_repos")
                    val followers= responObj.getString("followers")
                    val following = responObj.getString("following")
                    list.add(
                        User(
                            username,
                            name,
                            avatar,
                            company,
                            location,
                            repository,
                            followers,
                            following
                        )
                    )
                    showRecyclerList()
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity,  e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                showLoading(false)
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : GetUserDetail Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                Toast.makeText(this@MainActivity,  errorMessage, Toast.LENGTH_LONG).show()
            }

        })

    }

    private fun showRecyclerList() {
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.adapter = adapter
        val listAdapter = UserAdapter(list)

        listAdapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback{
            override fun onItemClicked(data: User) {
                showSelectedUser(data)
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_USER, data)
                this@MainActivity.startActivity(intent)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.search)?.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                list.clear()
                getUserSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_change_settings) {
            val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(mIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSelectedUser(user: User) {
        Toast.makeText(this, user.username, Toast.LENGTH_SHORT ).show()
     }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}