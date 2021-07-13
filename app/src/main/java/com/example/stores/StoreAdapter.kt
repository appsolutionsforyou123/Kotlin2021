package com.example.stores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stores.databinding.ItemStoreBinding

class StoreAdapter (private var stores: MutableList<StoreEntity>, private var listener: OnClickListener) :
        RecyclerView.Adapter<StoreAdapter.ViewHolder>(){
        private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreAdapter.ViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_store, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreAdapter.ViewHolder, position: Int) {
        val store = stores.get(position)

        with (holder){
            setListener(store)
            binding.tvName.text = store.name
            binding.cbFavorite.isChecked = store.isFavorite //actualiza el valor del checkbox...
        }
    }

    override fun getItemCount(): Int = stores.size

    fun setStores(stores: MutableList<StoreEntity>) {
        this.stores = stores
        notifyDataSetChanged() //notificamos del cambio al adaptador
    }

    fun add(storeEntity: StoreEntity) {
        //si no existe la tienda, agregala....
        if(!stores.contains(storeEntity)){
            stores.add(storeEntity)
            notifyItemInserted(stores.size-1) //notifica de forma particular, y que el nuevo elemento siempre se ira al final del arreglo.
            //notifyDataSetChanged()//notifica de forma general a la vista de los cambios realizados...
        }
    }

    fun update(storeEntity: StoreEntity) {
        val index = stores.indexOf(storeEntity)
        if (index != -1){  //si encontro el indice:
            stores.set(index, storeEntity)  //le paso el indice y el objeto
            notifyItemChanged(index)  //solo notifico el elemento que cambio
        }
    }

    fun delete(storeEntity: StoreEntity) {
        val index = stores.indexOf(storeEntity)
        if (index != -1){  //si encontro el indice:
            stores.removeAt(index)  //le paso el indice
            notifyItemRemoved(index)  //solo notifico el elemento se elimino...
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
    val binding = ItemStoreBinding.bind(view)
        //agregamos al listener para vincular el viewholder con el adaptador
        fun setListener (storeEntity: StoreEntity){

            with(binding.root){
                setOnClickListener{ listener.onClick(storeEntity)}
                setOnLongClickListener { //un click largo va a eliminar el elemneto....
                    listener.onDeleteStore(storeEntity)
                    true
                }
            }

            binding.cbFavorite.setOnClickListener{
                listener.onFavoriteStore(storeEntity)
            }
        }
    }
}