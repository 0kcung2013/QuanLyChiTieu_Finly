//package com.example.quanlychitieu_finly
//
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.drawable.BitmapDrawable
//import android.os.Bundle
//import android.widget.*
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import com.cloudinary.Cloudinary
//import com.google.firebase.auth.FirebaseAuth
//import java.io.ByteArrayInputStream
//import java.io.ByteArrayOutputStream
//import kotlin.concurrent.thread
//
//class RegisterActivity : AppCompatActivity() {
//
//    private lateinit var auth: FirebaseAuth
//    private lateinit var cloudinary: Cloudinary
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_register)
//
//        auth = FirebaseAuth.getInstance()
//        cloudinary = CloudinaryConfig.cloudinaryInstance  // 🔹 Lấy config Cloudinary
//
//        val edtUsername = findViewById<EditText>(R.id.edtUsername)
//        val edtEmail = findViewById<EditText>(R.id.edtEmail)
//        val edtPassword = findViewById<EditText>(R.id.edtPassword)
//        val edtConfirmPassword = findViewById<EditText>(R.id.edtConfirmPassword)
//        val btnRegister = findViewById<Button>(R.id.btnRegister)
//        val txtGoLogin = findViewById<TextView>(R.id.txtGoLogin)
//
//        btnRegister.setOnClickListener {
//            val username = edtUsername.text.toString().trim()
//            val email = edtEmail.text.toString().trim()
//            val password = edtPassword.text.toString().trim()
//            val confirmPassword = edtConfirmPassword.text.toString().trim()
//
//            if (username.isEmpty()) {
//                edtUsername.error = "Vui lòng nhập tên người dùng"
//                return@setOnClickListener
//            }
//            if (email.isEmpty()) {
//                edtEmail.error = "Vui lòng nhập email"
//                return@setOnClickListener
//            }
//            if (password.isEmpty() || password.length < 6) {
//                edtPassword.error = "Mật khẩu phải ≥ 6 ký tự"
//                return@setOnClickListener
//            }
//            if (password != confirmPassword) {
//                edtConfirmPassword.error = "Mật khẩu nhập lại không khớp"
//                return@setOnClickListener
//            }
//
//            // 🔹 Đăng ký Firebase
//            auth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
//
//                        val userId = auth.currentUser?.uid ?: email
//                        createDefaultCloudinaryFolders(userId)
//
//                        // 🔹 Chuyển qua Login
//                        val intent = Intent(this, LoginActivity::class.java)
//                        startActivity(intent)
//                        finish()
//                    } else {
//                        Toast.makeText(
//                            this,
//                            "Lỗi: ${task.exception?.message}",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                }
//        }
//
//        txtGoLogin.setOnClickListener {
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
//        }
//    }
//
//    /**
//     * 🔹 Tạo danh mục chi tiêu & thu nhập mặc định cho user trên Cloudinary
//     */
//    private fun createDefaultCloudinaryFolders(userId: String) {
//        // Danh mục Chi tiêu
//        val spendingCategories = listOf(
//            Pair(R.drawable.ic_category_food, "Ăn uống"),
//            Pair(R.drawable.ic_car, "Di chuyển"),
//            Pair(R.drawable.ic_category_shop, "Mua sắm"),
//            Pair(R.drawable.ic_category_billic, "Hóa đơn"),
//            Pair(R.drawable.ic_category_sk, "Y tế"),
//            Pair(R.drawable.ic_cinema, "Giải trí"),
//            Pair(R.drawable.ic_sports, "Thể thao"),
//            Pair(R.drawable.ic_adds, "Khác")
//        )
//
//        // Danh mục Thu nhập
//        val incomeCategories = listOf(
//            Pair(R.drawable.ic_category_wage, "Lương"),
//            Pair(R.drawable.ic_category_wages, "Thưởng"),
//            Pair(R.drawable.ic_adds, "Quà tặng"),
//            Pair(R.drawable.ic_adds, "Khác")
//        )
//
//        thread {
//            try {
//                val allCategories = mapOf(
//                    "spending" to spendingCategories,
//                    "income" to incomeCategories
//                )
//
//                for ((type, categories) in allCategories) {
//                    for ((iconRes, name) in categories) {
//                        val drawable = resources.getDrawable(iconRes, null)
//                        val bitmap = (drawable as BitmapDrawable).bitmap
//
//                        val baos = ByteArrayOutputStream()
//                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
//                        val inputStream = ByteArrayInputStream(baos.toByteArray())
//
//                        // 🔸 Đường dẫn Cloudinary: /users/{userId}/{type}/{name}/icon.png
//                        val uploadParams = mapOf(
//                            "folder" to "users/$userId/$type/$name",
//                            "public_id" to "icon",
//                            "upload_preset" to CloudinaryConfig.UPLOAD_PRESET
//                        )
//
//                        cloudinary.uploader().upload(inputStream, uploadParams)
//                        inputStream.close()
//                    }
//                }
//
//                runOnUiThread {
//                    Toast.makeText(this, "✅ Tạo danh mục mẫu thành công!", Toast.LENGTH_SHORT)
//                        .show()
//                }
//
//            } catch (e: Exception) {
//                runOnUiThread {
//                    Toast.makeText(this, "❌ Lỗi khi tạo danh mục: ${e.message}", Toast.LENGTH_LONG)
//                        .show()
//                }
//            }
//        }
//    }
//}

package com.example.quanlychitieu_finly

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var edtUsername: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var txtGoLogin: TextView

    private fun isPasswordValid(password: String): Boolean {
        val passwordRegex =
            Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{6,}$")
        return passwordRegex.matches(password)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        // Ánh xạ view
        edtUsername = findViewById(R.id.edtUsername)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        txtGoLogin = findViewById(R.id.txtGoLogin)

        btnRegister.setOnClickListener {
            val username = edtUsername.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            val confirmPassword = edtConfirmPassword.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ Dùng MaterialAlertDialogBuilder cho giao diện đẹp hơn
            if (!isPasswordValid(password)) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("🔒 Mật khẩu chưa hợp lệ")
                    .setMessage(
                        """
                        Vui lòng đảm bảo mật khẩu của bạn có:
                        
                        • Ít nhất 6 ký tự  
                        • Một chữ thường  
                        • Một chữ in hoa  
                        • Một chữ số  
                        • Một ký tự đặc biệt (@, #, $, %...)
                        """.trimIndent()
                    )
                    .setPositiveButton("OK") { dialog, _ ->
                        edtPassword.text.clear()
                        edtConfirmPassword.text.clear()
                        edtPassword.requestFocus()
                        dialog.dismiss()
                    }
                    .setCancelable(false)
                    .show()
                return@setOnClickListener
            }


            // Đăng ký tài khoản Firebase
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Đăng ký thất bại: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@addOnCompleteListener
                    }

                    val user = mAuth.currentUser
                    if (user == null) {
                        Toast.makeText(
                            this,
                            "Không lấy được thông tin người dùng",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@addOnCompleteListener
                    }

                    val profileUpdates = userProfileChangeRequest {
                        displayName = username
                    }

                    user.updateProfile(profileUpdates)
                        .addOnSuccessListener {
                            // Lưu thông tin vào Firestore
                            val db = FirebaseFirestore.getInstance()
                            val userMap = hashMapOf(
                                "uid" to user.uid,
                                "username" to username,
                                "email" to email,
                                "createdAt" to Timestamp.now()
                            )
                            db.collection("users").document(user.uid).set(userMap)

                            user.reload().addOnCompleteListener {
                                Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Lỗi cập nhật tên: ${e.message}", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                }
        }

        txtGoLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}

