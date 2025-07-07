package com.example.sw888p3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var nextButton: Button
    private lateinit var productAdapter: ProductAda
    private lateinit var dbHelper: ProductDB
    private var allProducts = mutableListOf<Product>() // To hold products from DB
    private val selectedProducts = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = ProductDB(this)

        productsRecyclerView = findViewById(R.id.productsRecyclerView)
        nextButton = findViewById(R.id.nextButton)

        setupRecyclerView()
        loadProducts()

        nextButton.setOnClickListener {
            if (selectedProducts.size >= 3) {
                val intent = Intent(this, SecondActivity::class.java).apply {
                    putParcelableArrayListExtra("selectedProducts", ArrayList(selectedProducts))
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please select at least 3 products.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setupRecyclerView() {
        // Initialize adapter with an empty list first, it will be updated
        productAdapter = ProductAda(this, mutableListOf()) { product, isSelected ->
            if (isSelected) {
                if (!selectedProducts.contains(product)) {
                    selectedProducts.add(product)
                }
            } else {
                selectedProducts.remove(product)
            }
            // Update the product's own isSelected state in the main list
            allProducts.find { it.id == product.id }?.isSelected = isSelected
        }
        productsRecyclerView.layoutManager = LinearLayoutManager(this)
        productsRecyclerView.adapter = productAdapter
    }

    private fun loadProducts() {
        lifecycleScope.launch {
            val productsFromDb = dbHelper.getAllProducts()
            allProducts.clear()
            allProducts.addAll(productsFromDb)
            // Update the adapter's list on the main thread
            runOnUiThread {
                // Create a new list for the adapter to ensure diffing works correctly if you use DiffUtil later
                productAdapter.productList.clear()
                productAdapter.productList.addAll(allProducts.map { it.copy() }) // Create copies
                productAdapter.notifyDataSetChanged() // Or use more specific notify methods
            }
            // Clear previous selections if any
            selectedProducts.clear()
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload products and clear selections when returning to MainActivity
        // This ensures checkboxes are reset if the user navigates back
        loadProducts()
        // Also, ensure the selectedProducts list reflects the (now deselected) state
        allProducts.forEach { it.isSelected = false }
        selectedProducts.clear()
        if (::productAdapter.isInitialized) {
            productAdapter.notifyDataSetChanged()
        }
    }
}