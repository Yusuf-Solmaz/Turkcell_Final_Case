package com.yusuf.yusuf_mucahit_solmaz_final.presentation.drawer.ui.favorites.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yusuf.yusuf_mucahit_solmaz_final.data.local.dao.FavoriteProductsDao
import com.yusuf.yusuf_mucahit_solmaz_final.data.local.model.FavoriteProducts
import com.yusuf.yusuf_mucahit_solmaz_final.presentation.drawer.ui.favorites.state.FavoriteState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteProductsDao: FavoriteProductsDao,

) : ViewModel() {

    private val _favoriteProducts = MutableLiveData<FavoriteState>()
    val favoriteProducts: LiveData<FavoriteState> = _favoriteProducts

    init {
        getFavoriteProducts()
    }

    fun getFavoriteProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            _favoriteProducts.postValue(
                FavoriteState(
                isLoading = true,
                error = null,
                favoriteProductsResponse = emptyList()
            )
            )
            try {
                val products = favoriteProductsDao.getAll()
                _favoriteProducts.postValue(
                        FavoriteState(
                            isLoading = false,
                            error = null,
                            favoriteProductsResponse = products
                        )
                    )

            }
            catch (e:Exception){
                _favoriteProducts.postValue(
                    FavoriteState(
                    isLoading = false,
                    error = e.message,
                    favoriteProductsResponse = emptyList()
                )
                )
            }
        }
    }


    fun removeFavorite(product: FavoriteProducts, onError: (String) -> Unit) {
       try {
           viewModelScope.launch {

               favoriteProductsDao.deleteProduct(product.productId)
               getFavoriteProducts()
           }
       }
       catch (e:Exception){
           e.localizedMessage?.let {
               onError(it)
           }
       }

    }
}
