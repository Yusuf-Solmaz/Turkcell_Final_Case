package com.yusuf.yusuf_mucahit_solmaz_final.presentation.drawer.ui.detail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.yusuf.yusuf_mucahit_solmaz_final.R
import com.yusuf.yusuf_mucahit_solmaz_final.core.utils.ViewUtils.setupGlide
import com.yusuf.yusuf_mucahit_solmaz_final.core.utils.ViewUtils.setVisibility
import com.yusuf.yusuf_mucahit_solmaz_final.data.datastore.repo.UserSessionRepository
import com.yusuf.yusuf_mucahit_solmaz_final.data.mapper.toAddCartRequest
import com.yusuf.yusuf_mucahit_solmaz_final.data.mapper.toFavoriteProduct
import com.yusuf.yusuf_mucahit_solmaz_final.data.remote.responses.product.Product
import com.yusuf.yusuf_mucahit_solmaz_final.data.remoteconfig.RemoteConfigManager.loadBackgroundColor
import com.yusuf.yusuf_mucahit_solmaz_final.databinding.FragmentDetailBinding
import com.yusuf.yusuf_mucahit_solmaz_final.presentation.drawer.ui.detail.adapter.CommentsAdapter
import com.yusuf.yusuf_mucahit_solmaz_final.presentation.drawer.ui.detail.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : Fragment() {

    @Inject
    lateinit var session: UserSessionRepository

    private  var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailViewModel by viewModels()
    private val args: DetailFragmentArgs by navArgs()
    private lateinit var commentAdapter: CommentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForColorStateLists")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupUI()

        val id = args.id
        viewModel.getProductById(id){
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        setupObservers()

    }

    private fun setupUI(){
        loadBackgroundColor(requireContext()){
            color->
            view?.setBackgroundColor(Color.parseColor(color))
        }

        commentAdapter = CommentsAdapter(arrayListOf())
        binding.rvComments.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = commentAdapter
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupObservers(){
        viewModel.productDetail.observe(viewLifecycleOwner) { state ->

            setVisibility(
                isLoading = state.isLoading,
                isError = state.error != null,
                isSuccess = state.productResponse != null,
                loadingView = binding.profileLoadingErrorComponent.loadingLayout,
                errorView = binding.profileLoadingErrorComponent.errorLayout,
                successView = binding.detailLayout
            )

            if (state.productResponse != null){

                binding.apply {
                    title.text = state.productResponse.title
                    productPrice.text = ("${state.productResponse.price}$")
                    discount.text = "(-%${state.productResponse.discountPercentage})"
                    rating.text = state.productResponse.rating.toString()


                    setupGlide(requireContext(),state.productResponse.images[0],productImage,loadingAnimationView)

                    description.text = state.productResponse.description
                    stock.text = "${requireContext().getString(R.string.in_stock)}: ${state.productResponse.stock}"
                    stock.setTextColor(
                        if (state.productResponse.availabilityStatus == "Low Stock") {
                            AppCompatResources.getColorStateList(requireContext(), R.color.statusRed)
                        } else {
                            AppCompatResources.getColorStateList(requireContext(), R.color.statusGreen)
                        }
                    )

                    commentAdapter.updateComments(state.productResponse.reviews)

                    textView2.paintFlags = textView2.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                    val oldPrice =
                        state.productResponse.price / (1 - state.productResponse.discountPercentage / 100)
                    val decimalFormat = DecimalFormat("#.##")
                    val formattedPrice = decimalFormat.format(oldPrice)
                    textView2.text = ("$formattedPrice$")


                    viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->

                        binding.checkBox.setOnCheckedChangeListener(null)
                        binding.checkBox.isChecked = isFavorite

                        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                            val favoriteProduct = state.productResponse.toFavoriteProduct()
                            if (isChecked) {
                                viewModel.addOrRemoveFavorite(favoriteProduct){
                                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                viewModel.addOrRemoveFavorite(favoriteProduct){
                                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                    viewModel.addToCartState.observe(viewLifecycleOwner) { state ->

                        setVisibility(
                            isLoading = state.isLoading,
                            isError = state.error != null,
                            isSuccess = state.success != null,
                            loadingView = binding.profileLoadingErrorComponent.loadingLayout,
                            errorView = binding.profileLoadingErrorComponent.errorLayout,
                            successView = binding.detailLayout
                        )

                        if(state.success != null) {
                            Toast.makeText(requireContext(), requireContext().getString(R.string.added_to_cart), Toast.LENGTH_SHORT)
                                .show()
                        }


                    }
                }
                binding.addToCart.setOnClickListener {
                    showAddToCartDialog(product = state.productResponse)
                }
            }

        }
    }

    private fun showAddToCartDialog(product: Product) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_to_cart, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val etQuantity = dialogView.findViewById<EditText>(R.id.et_quantity)
        val btnAddToCart = dialogView.findViewById<Button>(R.id.btn_add_to_cart)

        btnAddToCart.setOnClickListener {
            val quantity = etQuantity.text.toString().toIntOrNull()

            if (quantity != null ) {
                if (quantity.toInt() > product.stock){
                    Toast.makeText(requireContext(), requireContext().getString(R.string.out_of_stock), Toast.LENGTH_SHORT).show()

                }
                else if (quantity > 0) {
                    val cartProduct = product.toAddCartRequest(session.getUserId(), quantity.toString())

                    viewModel.addToCart(cartProduct)
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), requireContext().getString(R.string.invalid_quantity), Toast.LENGTH_SHORT).show()
                }
            }


        }
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}


