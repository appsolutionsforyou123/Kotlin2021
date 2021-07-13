package com.example.stores

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.databinding.ActivityMainBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), OnClickListener, MainAux {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: StoreAdapter
    private  lateinit var mGridLayout: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        /*mBinding.btnSave.setOnClickListener {
            val store = StoreEntity(name = mBinding.etName.text.toString().trim())

            Thread{
                StoreApplication.database.storeDao().addStore(store)  //que la tienda se agrega a la BD
            }.start()

            mAdapter.add(store)   //que la tienda se agregue al adaptador.....
        }*/

        mBinding.fab.setOnClickListener { launchEdithFragment() }

        setupRecyclerView()
    }

    private fun launchEdithFragment() {
        val fragment = EditStoreFragment() //instancia del fragment

        val fragmentManager = supportFragmentManager                    //
        val fragmentTransaction = fragmentManager.beginTransaction()  //como se ejecutara

        //configurar como debe salir el fragmento:
        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.addToBackStack(null) //desvincularlo el fragment de la vista principal.
        fragmentTransaction.commit() //para que se apliquen los cambios...

        //mBinding.fab.hide()
        hideFab(false)
    }

    private fun setupRecyclerView() {
        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this, 2) //creamos el grid
        getStores() //se hace la consulta a la BD.

        //se configura el adaptador:
        mBinding.recyclerView.apply {
            setHasFixedSize(true) //sera de tamaño fijo para optimizar el performance
            layoutManager = mGridLayout
            adapter = mAdapter
        }

    }

    private  fun getStores(){
        //utilizando Anko cargamos la consulta en un subproceso:
        doAsync {
            val stores = StoreApplication.database.storeDao().getAllStores() //consulta todas las tiendas de la BD

            //cuando termine el proceso anterior, lo cargamos al adaptador:
            uiThread {
                mAdapter.setStores(stores)  //actualiza el adaptador con las tiendas
            }
        }


    }

    override fun onClick(storeEntity: StoreEntity) {

    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        storeEntity.isFavorite = !storeEntity.isFavorite
        doAsync {
            StoreApplication.database.storeDao().updateStore(storeEntity)
            uiThread {
                mAdapter.update(storeEntity)
            }
        }
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        //utilizando la libreria Anko, hacemos asincrono el proceso:
        doAsync {
            StoreApplication.database.storeDao().deleteStore(storeEntity)
            uiThread {//una vez eliminado de la BD se actualiza el adaptador....
                mAdapter.delete(storeEntity)
            }
        }
    }

    /****
     * MainAux
     */
    override fun hideFab(isVisible: Boolean) {
       if (isVisible) mBinding.fab.show() else mBinding.fab.hide()
    }

    override fun addStore(storeEntity: StoreEntity) {
        mAdapter.add(storeEntity)
    }

    override fun updateStore(storeEntity: StoreEntity) {

    }
}