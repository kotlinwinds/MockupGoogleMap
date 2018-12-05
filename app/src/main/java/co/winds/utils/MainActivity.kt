/*
package karun.com.recyclearviewselectall.select_

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.widget.LinearLayout
import com.google.gson.Gson
import karun.com.recyclearviewselectall.MainActivity1
import karun.com.recyclearviewselectall.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener{



    lateinit var myAdapter: MyAdapter
    lateinit var list: ArrayList<Model>
    var data: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.supportActionBar!!.hide()

        data = intent.getStringExtra("key")

        rec.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        list = ArrayList<Model>()

        list.add(Model(12374, "Karun Kumar", "http://1.bp.blogspot.com/-Mk5AyGkeYnw/Va1k6pum6eI/AAAAAAAAFlg/Tlorcet_EQ8/s1600/20150712_174650.jpg", false))
        list.add(Model(12133, "Neha Kumari", "http://images6.fanpop.com/image/photos/40000000/Karun-kumar-Shekhpurwa-pusauli-karunkumar2525-Neha-kumari-bihar-mohania-katrina-kaif-40040269-60-120.jpg", false))
        list.add(Model(11253, "Shaneha Beta", "http://www.tellychakkar.com/sites/tellychakkar.com/files/imagecache/Display_445x297/images/story/2013/03/04/neha.jpg", false))
        list.add(Model(14543, "Kunal  Kaouie", "http://static.sbuys.in/media/registration_users/DRC031.JPG", false))
        list.add(Model(18823, "Kolkata Sohad", "http://kollystills.com/wp-content/uploads/2013/12/Actress-Neha-Deshpande-Photos-at-Art-Exhibition-with-Dil-Deewana-Team-CelebsNext-0026.jpg", false))
        list.add(Model(17243, "Dhaere Tussd", "http://images.desimartini.com/media/uploads/2017-5/salman-khan-3.jpg", false))
        list.add(Model(84123, "Katrina Mehata", "https://i.pinimg.com/736x/a6/02/e6/a602e6c8c300bcae750bf88d2b28e41b--katrina-wallpaper-katrina-kaif-wallpapers.jpg", false))
        list.add(Model(16523, "Tinkui Kushiw", "http://stat3.bollywoodhungama.in/wp-content/uploads/2017/04/Akshay-Kumars-remark-on-taking-away-his-National-award-Serious-or-sarcasm.jpg", false))
        list.add(Model(96523, "Neha Dharma", "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d6/Neha_Sharma_at_the_Promo_launch_of_%27Jayanta_Bhai_Ki_Luv_Story%27_07.jpg/398px-Neha_Sharma_at_the_Promo_launch_of_%27Jayanta_Bhai_Ki_Luv_Story%27_07.jpg", false))

        myAdapter = MyAdapter(this@MainActivity, list)
        rec.adapter = myAdapter
        setupSearchView()

        if (data != null) {
            Log.d("TAGS", "Reponse Data With array $data")
            if (data != null && data != "") {
                val d = data!!.split("~")
                for (j in 0 until d.size) {
                    for (i in 0 until list.size) {
                        val m: Model = list[i]
                        if (m.ids == d[j].toInt()) {
                            m.isSelected = true
                            myAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        checkAll.setOnCheckedChangeListener({ _, isChecked ->
            for (i in 0 until list.size) {
                val model_addStudent:Model = list[i]
                model_addStudent.isSelected=isChecked
                myAdapter.notifyDataSetChanged()
            }
        })





        back.setOnClickListener({
            startActivity(Intent(this@MainActivity, MainActivity1::class.java))
            onBackPressed()


        })





        submit1.setOnClickListener({
            val mylist: ArrayList<HashMap<String, String>> = ArrayList()
            var map: HashMap<String, String>? = null
            for (i in 0 until list.size) {
                val m: Model = list[i]
                if (m.isSelected) {
                    map = HashMap()
                    map.put("id", m.ids.toString())
                    map.put("name", m.name)
                    map.put("avatar", m.avatar)
                    mylist.add(map)
                }

            }
            var data = Gson().toJson(mylist)
            Log.d("TAGS", data)


            startActivity(Intent(this@MainActivity, MainActivity1::class.java).putExtra("key", data))
            finish()
        })


    }


    private fun setupSearchView() {
        search_view.setIconifiedByDefault(false);
        search_view.setOnQueryTextListener(this);
        search_view.isSubmitButtonEnabled = true;
        search_view.queryHint = "Search Here";
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false;
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        myAdapter!!.filter(newText!!);
        return true;
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
*/
