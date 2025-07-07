package com.example.sw888p3

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SecondActivity : AppCompatActivity() {

    private lateinit var selectedProductsRecyclerView: RecyclerView
    private lateinit var emailButton: Button
    private lateinit var selectedProductAdapter: SelectedProductAdapter
    private var productsToDisplay = mutableListOf<Product>()

    private val emailActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            Toast.makeText(this, "Product information sent!", Toast.LENGTH_LONG).show()
            clearProductList()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        selectedProductsRecyclerView = findViewById(R.id.selectedProductsRecyclerView)
        emailButton = findViewById(R.id.emailButton)

        // Retrieve the passed products
        val receivedProducts: ArrayList<Product>? = intent.getParcelableArrayListExtra("selectedProducts")
        receivedProducts?.let {
            productsToDisplay.addAll(it)
        }

        setupRecyclerView()

        emailButton.setOnClickListener {
            if (productsToDisplay.isNotEmpty()) {
                showEmailinput()
            } else {
                Toast.makeText(this, "No products to email.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        selectedProductAdapter = SelectedProductAdapter(this, productsToDisplay)
        selectedProductsRecyclerView.layoutManager = LinearLayoutManager(this)
        selectedProductsRecyclerView.adapter = selectedProductAdapter
    }
    private fun showEmailinput(){
        val emailbox = AlertDialog.Builder(this)
        emailbox.setTitle("Enter Email")
        
        val input = EditText(this)
        input.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        input.hint = "Email"
        emailbox.setView(input)

        emailbox.setPositiveButton("Send") { dialog: DialogInterface, which: Int ->
            val emailAddress = input.text.toString().trim()
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                // Valid email, proceed to send
                sendEmailWithProducts(emailAddress)
            } else {
                Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
            }
        }
        emailbox.setNegativeButton("Cancel") { dialog: DialogInterface, which: Int -> dialog.cancel() }
        emailbox.show()
    }

    private fun sendEmailWithProducts(recipientEmail: String) { // Added parameter
        val emailSubject = "Selected Product Information"
        val emailBody = StringBuilder()
        emailBody.append("Here is information about the selected products:\n\n")

        productsToDisplay.forEach { product ->
            emailBody.append("Name: ${product.name}\n")
            emailBody.append("Description: ${product.description}\n")
            emailBody.append("Seller: ${product.seller}\n")
            emailBody.append("Price: $${String.format("%.2f", product.price)}\n")
            emailBody.append("-----------------------------\n")
        }

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // Only email apps should handle this
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail)) // Use the entered email
            putExtra(Intent.EXTRA_SUBJECT, emailSubject)
            putExtra(Intent.EXTRA_TEXT, emailBody.toString())
        }

        try {
            emailActivityResultLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No email client found.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearProductList() {
        selectedProductAdapter.clearProducts()
        emailButton.isEnabled = false
    }
}