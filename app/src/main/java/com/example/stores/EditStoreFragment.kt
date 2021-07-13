package com.example.stores

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.databinding.FragmentEditStoreBinding
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditStoreFragment : Fragment() {
    //hacemos uso de binding para inflar nuestro fragmento:
    private lateinit var mBinding: FragmentEditStoreBinding
    // para tomar todos los metodos y propiedades de AppCompatActivity
    private var mActivity: MainActivity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?     ): View? {
        mBinding = FragmentEditStoreBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //En este momento ya esta creado por completo el fragment...

        mActivity = activity as? MainActivity  //casteamos a la MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)  //activar la flecha de retroceso...
        mActivity?.supportActionBar?.title = getString(R.string.edith_store_title_add) //cambiar el titulo al fragmento...
        //le damos acceso al menu:
        setHasOptionsMenu(true)

        //vamos a cargar una foto con Glide:
        mBinding.etPhotoUrl.addTextChangedListener {
            Glide.with(this)
                .load(mBinding.etPhotoUrl.text.toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(mBinding.imgPhoto)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        //activar el menu para guardar....
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId)
        {
            android.R.id.home -> {
                //cuando presionamos retroceso:
                mActivity?.onBackPressed()
                true
            }
            R.id.action_save -> {
                //cuando presionamos salvar:
                val store = StoreEntity(name = mBinding.etName.text.toString().trim(),
                                        phone = mBinding.etPhone.text.toString().trim(),
                                        website = mBinding.etWebsite.text.toString().trim())

                doAsync {
                    //regresa el identificador de la nueva tienda....
                    store.id = StoreApplication.database.storeDao().addStore(store)

                    uiThread {
                        //notificar a la actividad que se agrego una nueva tienda:
                        mActivity?.addStore(store)

                        hideKeyboard() //oculta el teclado....

                        /*Snackbar.make(mBinding.root,
                            getString(R.string.edith_store_message_save_succesful),
                            Snackbar.LENGTH_SHORT)
                            .show()*/

                        Toast.makeText(mActivity, R.string.edith_store_message_save_succesful, Toast.LENGTH_SHORT).show()

                            //una vez que guarda la tienda se le da al boton regresar....
                            mActivity?.onBackPressed()//se regresa a la actividad anterior....
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
        //return super.onOptionsItemSelected(item)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun hideKeyboard(){
        //en caso de que el teclado este activo, que se oculte:
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(view != null)
        {
            imm.hideSoftInputFromWindow(view!!.windowToken, 0)
        }
    }

    override fun onDestroyView() {
        //antes de destruir el fragmento ocultar el teclado.....
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        //liberar recursos del fragmento:
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mActivity?.hideFab(true)

        setHasOptionsMenu(false) //ocultar opciones de menu
        super.onDestroy()
    }
}
