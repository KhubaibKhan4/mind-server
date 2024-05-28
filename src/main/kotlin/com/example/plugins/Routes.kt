package com.example.plugins

import com.example.domain.model.login.LoginResponse
import com.example.domain.repository.quiz.QuizQuestionsRepository
import com.example.domain.repository.quiz.QuizRepository
import com.example.domain.repository.user.UsersRepository
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

fun Route.users(
    db: UsersRepository
) {
    post("v1/users") {
        val multipart = call.receiveMultipart()

        var userName: String? = null
        var email: String? = null
        var password: String? = null
        var fullName: String? = null
        var address: String? = null
        var city: String? = null
        var country: String? = null
        var postalCode: Long? = null
        var phoneNumber: String? = null
        var userRole: String? = null
        var imageUrl: String? = null
        val uploadDir = File("upload/products/users")
        if (!uploadDir.exists()) {
            uploadDir.mkdirs()
        }


        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "username" -> userName = part.value
                        "email" -> email = part.value
                        "password" -> password = part.value
                        "fullName" -> fullName = part.value
                        "address" -> address = part.value
                        "city" -> city = part.value
                        "country" -> country = part.value
                        "postalCode" -> postalCode = part.value.toLongOrNull()
                        "phoneNumber" -> phoneNumber = part.value
                        "userRole" -> userRole = part.value
                    }
                }

                is PartData.FileItem -> {
                    val fileName = part.originalFileName?.replace(" ", "_") ?: "image${System.currentTimeMillis()}"
                    val file = File("upload/products/users", fileName)
                    part.streamProvider().use { input ->
                        file.outputStream().buffered().use { output ->
                            input.copyTo(output)
                        }

                    }
                    imageUrl = "/upload/products/users/${fileName}"
                }

                is PartData.BinaryChannelItem -> {}
                is PartData.BinaryItem -> {}
            }
        }
        userName ?: return@post call.respondText(
            text = "Username Missing",
            status = HttpStatusCode.BadRequest
        )
        email ?: return@post call.respondText(
            text = "Email Missing",
            status = HttpStatusCode.BadRequest
        )
        password ?: return@post call.respondText(
            text = "Password Missing",
            status = HttpStatusCode.BadRequest
        )
        fullName ?: return@post call.respondText(
            text = "fullName Missing",
            status = HttpStatusCode.BadRequest
        )
        address ?: return@post call.respondText(
            text = "address Missing",
            status = HttpStatusCode.BadRequest
        )
        city ?: return@post call.respondText(
            text = "city Missing",
            status = HttpStatusCode.BadRequest
        )
        country ?: return@post call.respondText(
            text = "country Missing",
            status = HttpStatusCode.BadRequest
        )
        postalCode ?: return@post call.respondText(
            text = "postalCode Missing",
            status = HttpStatusCode.BadRequest
        )
        phoneNumber ?: return@post call.respondText(
            text = "phoneNumber Missing",
            status = HttpStatusCode.BadRequest
        )
        userRole ?: return@post call.respondText(
            text = "userRole Missing",
            status = HttpStatusCode.BadRequest
        )
        imageUrl ?: return@post call.respondText(
            text = "Image Missing",
            status = HttpStatusCode.BadRequest
        )

        try {
            val user = db.insert(
                userName!!,
                email!!,
                password!!,
                fullName!!,
                address!!,
                city!!,
                country!!,
                postalCode!!,
                phoneNumber!!,
                userRole!!,
                imageUrl!!
            )
            user?.id?.let {
                call.respond(
                    status = HttpStatusCode.OK,
                    "User uploaded to server successfully: $user"
                )
            }
        } catch (e: Exception) {
            call.respond(
                status = HttpStatusCode.InternalServerError,
                "Error while uploading data to server: ${e.message}"
            )
        }
    }
    post("v1/signup") {
        val parameters = call.receive<Parameters>()
        val email = parameters["email"] ?: return@post call.respondText(
            text = "Email Missing",
            status = HttpStatusCode.Unauthorized
        )
        val password = parameters["password"] ?: return@post call.respondText(
            text = "Password Missing",
            status = HttpStatusCode.Unauthorized
        )
        val userName = parameters["userName"] ?: return@post call.respondText(
            text = "Username Missing",
            status = HttpStatusCode.BadRequest
        )
        val fullName = parameters["fullName"] ?: return@post call.respondText(
            text = "fullName Missing",
            status = HttpStatusCode.BadRequest
        )
        val address = parameters["address"] ?: return@post call.respondText(
            text = "address Missing",
            status = HttpStatusCode.BadRequest
        )
        val city = parameters["city"] ?: return@post call.respondText(
            text = "city Missing",
            status = HttpStatusCode.BadRequest
        )
        val country = parameters["country"] ?: return@post call.respondText(
            text = "country Missing",
            status = HttpStatusCode.BadRequest
        )
        val postalCode = parameters["postalCode"]?.toLongOrNull() ?: return@post call.respondText(
            text = "postalCode Missing",
            status = HttpStatusCode.BadRequest
        )
        val phoneNumber = parameters["phoneNumber"] ?: return@post call.respondText(
            text = "phoneNumber Missing",
            status = HttpStatusCode.BadRequest
        )
        val userRole = parameters["userRole"] ?: return@post call.respondText(
            text = "userRole Missing",
            status = HttpStatusCode.BadRequest
        )
        val imageUrl = parameters["imageUrl"] ?: return@post call.respondText(
            text = "imageUrl Missing",
            status = HttpStatusCode.BadRequest
        )

        try {
            val user = db.insert(
                userName,
                email,
                password,
                fullName,
                address,
                city,
                country,
                postalCode,
                phoneNumber,
                userRole,
                "null"
            )
            user?.id?.let {
                call.respond(
                    status = HttpStatusCode.OK,
                    "User uploaded to server successfully: $user"
                )
            }
        } catch (e: Exception) {
            call.respond(
                status = HttpStatusCode.InternalServerError,
                "Error while uploading data to server: ${e.message}"
            )
        }
    }

    post("v1/login") {
        val parameters = call.receive<Parameters>()
        val email = parameters["email"] ?: return@post call.respondText(
            text = "Email Missing",
            status = HttpStatusCode.Unauthorized
        )
        val password = parameters["password"] ?: return@post call.respondText(
            text = "Password Missing",
            status = HttpStatusCode.Unauthorized
        )

        try {
            val user = db.login(email, password)
            if (user != null) {
                val loginResponse = LoginResponse("Login Successful", user)
                val responseJson = Json { prettyPrint = true }.encodeToString(loginResponse)
                call.respondText(responseJson, ContentType.Application.Json)
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid Email or Password")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error during login: ${e.message}")
        }
    }
    get("v1/users") {
        try {
            val users = db.getAllUsers()
            if (users?.isNotEmpty() == true) {
                call.respond(HttpStatusCode.OK, users)
            } else {
                call.respondText(text = "No User Found", status = HttpStatusCode.NotFound)
            }
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.Unauthorized,
                "Error While Fetching Data From Server: ${e.message}"
            )
        }
    }
    get("v1/users/email/{email}") {
        val email = call.parameters["email"]
        try {
            if (email.isNullOrBlank()) {
                return@get call.respondText(
                    "Email parameter is missing",
                    status = HttpStatusCode.BadRequest
                )
            }

            val user = db.getUserByEmail(email)
            if (user == null) {
                return@get call.respondText(
                    "User Not Found",
                    status = HttpStatusCode.NotFound
                )
            } else {
                return@get call.respond(
                    HttpStatusCode.OK,
                    user
                )
            }
        } catch (e: Exception) {
            call.respond(
                status = HttpStatusCode.InternalServerError,
                "Error While Fetching Data from Server : ${e.message}"
            )
        }
    }

    get("v1/users/{id}") {
        val parameters = call.parameters["id"]
        try {
            val usersId = parameters?.toLong()
            if (usersId == null) {
                return@get call.respondText(
                    "Invalid ID",
                    status = HttpStatusCode.BadRequest
                )
            }
            val user = db.getUserById(usersId)
            if (user == null) {
                return@get call.respondText(
                    text = "User Not Found",
                    status = HttpStatusCode.NotFound
                )
            } else {
                return@get call.respond(
                    HttpStatusCode.OK,
                    user
                )
            }

        } catch (e: Exception) {
            call.respond(
                status = HttpStatusCode.Unauthorized,
                "Error While Fetching Data from Server : ${e.message}"
            )
        }

    }
    delete("v1/users/{id}") {
        val parameters = call.parameters["id"]
        try {
            val users = parameters?.toLongOrNull()?.let { usersId ->
                db.deleteUserById(usersId)
            } ?: return@delete call.respondText(
                text = "No Id Found",
                status = HttpStatusCode.BadRequest
            )
            if (users == 1) {
                call.respondText(
                    text = "Deleted Successfully",
                    status = HttpStatusCode.OK
                )
            } else {
                call.respondText(
                    text = "Id Not Found",
                    status = HttpStatusCode.BadRequest
                )
            }
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.Unauthorized,
                "Error While Deleting User From Server ${e.message}"
            )
        }
    }
    put("v1/users/{id}") {
        val id = call.parameters["id"] ?: return@put call.respondText(
            text = "Id Not Found",
            status = HttpStatusCode.NotFound
        )
        val multipart = call.receiveMultipart()

        var username: String? = null
        var email: String? = null
        var password: String? = null
        var fullName: String? = null
        var address: String? = null
        var city: String? = null
        var country: String? = null
        var postalCode: Long? = null
        var phoneNumber: String? = null
        var imageUrl: String? = null

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "username" -> username = part.value
                        "email" -> email = part.value
                        "password" -> password = part.value
                        "fullName" -> fullName = part.value
                        "address" -> address = part.value
                        "city" -> city = part.value
                        "country" -> country = part.value
                        "postalCode" -> postalCode = part.value.toLongOrNull()
                        "phoneNumber" -> phoneNumber = part.value
                    }
                }

                is PartData.FileItem -> {
                    val fileName = part.originalFileName?.replace(" ", "_")
                        ?: "image${System.currentTimeMillis()}"
                    val file = File("upload/products/users", fileName)
                    part.streamProvider().use { input ->
                        file.outputStream().buffered().use { output ->
                            input.copyTo(output)
                        }
                    }
                    imageUrl = "/upload/products/users/${fileName}"
                }

                is PartData.BinaryChannelItem -> {}
                is PartData.BinaryItem -> {}
            }
        }

        username ?: return@put call.respondText(
            text = "Username Missing",
            status = HttpStatusCode.BadRequest
        )
        email ?: return@put call.respondText(
            text = "Email Missing",
            status = HttpStatusCode.BadRequest
        )
        password ?: return@put call.respondText(
            text = "Password Missing",
            status = HttpStatusCode.BadRequest
        )
        fullName ?: return@put call.respondText(
            text = "Full Name Missing",
            status = HttpStatusCode.BadRequest
        )
        address ?: return@put call.respondText(
            text = "Address Missing",
            status = HttpStatusCode.BadRequest
        )
        city ?: return@put call.respondText(
            text = "City Missing",
            status = HttpStatusCode.BadRequest
        )
        country ?: return@put call.respondText(
            text = "Country Missing",
            status = HttpStatusCode.BadRequest
        )
        postalCode ?: return@put call.respondText(
            text = "Postal Code Missing",
            status = HttpStatusCode.BadRequest
        )
        phoneNumber ?: return@put call.respondText(
            text = "Phone Number Missing",
            status = HttpStatusCode.BadRequest
        )

        try {
            val result = id.toLong().let { userId ->
                db.updateUsers(
                    userId,
                    username!!,
                    email!!,
                    password!!,
                    fullName!!,
                    address!!,
                    city!!,
                    postalCode!!,
                    country!!,
                    phoneNumber!!,
                    imageUrl!!
                )
            }
            if (result == 1) {
                call.respondText(
                    text = "Update Successfully",
                    status = HttpStatusCode.OK
                )
            } else {
                call.respondText(
                    text = "Something Went Wrong",
                    status = HttpStatusCode.BadRequest
                )
            }
        } catch (e: Exception) {
            call.respondText(
                text = e.message.toString(),
                status = HttpStatusCode.BadRequest
            )
        }
    }
    put("v1/users/address/{id}") {
        val id = call.parameters["id"] ?: return@put call.respondText(
            text = "Id Not Found",
            status = HttpStatusCode.NotFound
        )
        val parameters = call.receive<Parameters>()

        val address = parameters["address"] ?: return@put call.respondText(
            text = "address Missing",
            status = HttpStatusCode.BadRequest
        )
        val city = parameters["city"] ?: return@put call.respondText(
            text = "city Missing",
            status = HttpStatusCode.BadRequest
        )
        val country = parameters["country"] ?: return@put call.respondText(
            text = "country Missing",
            status = HttpStatusCode.BadRequest
        )
        val postalCode = parameters["postalCode"]?.toLongOrNull() ?: return@put call.respondText(
            text = "postalCode Missing",
            status = HttpStatusCode.Unauthorized
        )

        try {
            val result = id.toLong().let { userId ->
                db.updateAddress(userId, address, city, country, postalCode)
            }
            if (result == 1) {
                call.respondText(
                    text = "Update Successfully...",
                    status = HttpStatusCode.OK
                )
            } else {
                call.respondText(
                    "Something Went Wrong...",
                    status = HttpStatusCode.BadRequest
                )
            }

        } catch (e: Exception) {
            call.respondText(
                text = e.message.toString(),
                status = HttpStatusCode.BadRequest
            )
        }
    }
    put("v1/users/userDetail/{id}") {
        val id = call.parameters["id"] ?: return@put call.respondText(
            text = "Id Not Found",
            status = HttpStatusCode.NotFound
        )
        val parameters = call.receive<Parameters>()

        val username = parameters["username"] ?: return@put call.respondText(
            text = "username Missing",
            status = HttpStatusCode.BadRequest
        )
        val fullName = parameters["fullName"] ?: return@put call.respondText(
            text = "fullName Missing",
            status = HttpStatusCode.BadRequest
        )
        val email = parameters["email"] ?: return@put call.respondText(
            text = "email Missing",
            status = HttpStatusCode.BadRequest
        )
        val address = parameters["address"] ?: return@put call.respondText(
            text = "address Missing",
            status = HttpStatusCode.BadRequest
        )
        val city = parameters["city"] ?: return@put call.respondText(
            text = "city Missing",
            status = HttpStatusCode.BadRequest
        )
        val country = parameters["country"] ?: return@put call.respondText(
            text = "country Missing",
            status = HttpStatusCode.BadRequest
        )
        val postalCode = parameters["postalCode"]?.toLongOrNull() ?: return@put call.respondText(
            text = "postalCode Missing",
            status = HttpStatusCode.Unauthorized
        )
        val phoneNumber = parameters["phoneNumber"] ?: return@put call.respondText(
            text = "postalCode Missing",
            status = HttpStatusCode.Unauthorized
        )

        try {
            val result = id.toLong().let { userId ->
                db.updateUsersDetail(
                    id = userId,
                    username = username,
                    email = email,
                    fullName = fullName,
                    address = address,
                    city = city,
                    postalCode = postalCode,
                    country = country,
                    phoneNumber = phoneNumber
                )
            }
            if (result == 1) {
                call.respondText(
                    text = "Update Successfully...",
                    status = HttpStatusCode.OK
                )
            } else {
                call.respondText(
                    "Something Went Wrong...",
                    status = HttpStatusCode.BadRequest
                )
            }

        } catch (e: Exception) {
            call.respondText(
                text = e.message.toString(),
                status = HttpStatusCode.BadRequest
            )
        }
    }

    put("v1/users/country/{id}") {
        val id = call.parameters["id"] ?: return@put call.respondText(
            text = "Id Not Found",
            status = HttpStatusCode.NotFound
        )
        val parameters = call.receive<Parameters>()

        val countryName = parameters["countryName"] ?: return@put call.respondText(
            text = "countryName Missing",
            status = HttpStatusCode.BadRequest
        )


        try {
            val result = id.toLong().let { userId ->
                db.updateCountry(userId, countryName)
            }
            if (result == 1) {
                call.respondText(
                    text = "Update Successfully...",
                    status = HttpStatusCode.OK
                )
            } else {
                call.respondText(
                    "Something Went Wrong...",
                    status = HttpStatusCode.BadRequest
                )
            }

        } catch (e: Exception) {
            call.respondText(
                text = e.message.toString(),
                status = HttpStatusCode.BadRequest
            )
        }
    }
    put("v1/users/profileImage/{id}") {
        val id = call.parameters["id"]?.toLongOrNull() ?: return@put call.respondText(
            text = "Invalid user ID",
            status = HttpStatusCode.BadRequest
        )

        val multipart = call.receiveMultipart()

        var profileImage: String? = null

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    val fileName = part.originalFileName?.replace(" ", "_")
                        ?: "image${System.currentTimeMillis()}"
                    val file = File("upload/products/users", fileName)
                    part.streamProvider().use { input ->
                        file.outputStream().buffered().use { output ->
                            input.copyTo(output)
                        }
                    }
                    profileImage = "/upload/products/users/${fileName}"
                }

                is PartData.FormItem -> {}
                is PartData.BinaryChannelItem -> {}
                is PartData.BinaryItem -> {}
            }
        }

        profileImage ?: return@put call.respondText(
            text = "Profile image is missing",
            status = HttpStatusCode.BadRequest
        )

        try {
            val result = db.updateProfile(id, profileImage!!)
            if (result == 1) {
                call.respondText(
                    text = "Profile image updated successfully",
                    status = HttpStatusCode.OK
                )
            } else {
                call.respondText(
                    text = "Failed to update profile image",
                    status = HttpStatusCode.InternalServerError
                )
            }
        } catch (e: Exception) {
            call.respondText(
                text = "Error updating profile image: ${e.message}",
                status = HttpStatusCode.InternalServerError
            )
        }
    }


}

fun Route.category(
    db: QuizRepository
) {
    post("v1/category") {
        val multipart = call.receiveMultipart()

        var name: String? = null
        var description: String? = null
        var imageUrl: String? = null
        val uploadDir = File("upload/products/users")
        if (!uploadDir.exists()) {
            uploadDir.mkdirs()
        }


        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "name" -> name = part.value
                        "description" -> description = part.value
                    }
                }

                is PartData.FileItem -> {
                    val fileName = part.originalFileName?.replace(" ", "_") ?: "image${System.currentTimeMillis()}"
                    val file = File("upload/products/users", fileName)
                    part.streamProvider().use { input ->
                        file.outputStream().buffered().use { output ->
                            input.copyTo(output)
                        }

                    }
                    imageUrl = "/upload/products/users/${fileName}"
                }

                is PartData.BinaryChannelItem -> {}
                is PartData.BinaryItem -> {}
            }
        }
        name ?: return@post call.respondText(
            text = "Name Missing",
            status = HttpStatusCode.BadRequest
        )
        description ?: return@post call.respondText(
            text = "Description Missing",
            status = HttpStatusCode.BadRequest
        )
        imageUrl ?: return@post call.respondText(
            text = "Image Missing",
            status = HttpStatusCode.BadRequest
        )

        try {
            val quizCategory = db.insert(
                name!!, description!!, imageUrl!!
            )
            quizCategory?.id?.let {
                call.respond(
                    status = HttpStatusCode.OK,
                    "Category uploaded to server successfully: $quizCategory"
                )
            }
        } catch (e: Exception) {
            call.respond(
                status = HttpStatusCode.InternalServerError,
                "Error while uploading data to server: ${e.message}"
            )
        }
    }
    put("v1/category/{id}") {
        val id = call.parameters["id"] ?: return@put call.respondText(
            text = "Id Not Found",
            status = HttpStatusCode.NotFound
        )
        val multipart = call.receiveMultipart()

        var name: String? = null
        var description: String? = null
        var imageUrl: String? = null

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "name" -> name = part.value
                        "description" -> description = part.value
                    }
                }

                is PartData.FileItem -> {
                    val fileName = part.originalFileName?.replace(" ", "_")
                        ?: "image${System.currentTimeMillis()}"
                    val file = File("upload/products/users", fileName)
                    part.streamProvider().use { input ->
                        file.outputStream().buffered().use { output ->
                            input.copyTo(output)
                        }
                    }
                    imageUrl = "/upload/products/users/${fileName}"
                }

                is PartData.BinaryChannelItem -> {}
                is PartData.BinaryItem -> {}
            }
        }

        name ?: return@put call.respondText(
            text = "Name Missing",
            status = HttpStatusCode.BadRequest
        )
        description ?: return@put call.respondText(
            text = "description Missing",
            status = HttpStatusCode.BadRequest
        )

        try {
            val result = id.toLong().let { userId ->
                db.updateCategory(
                    userId,
                    name!!,
                    description!!,
                    imageUrl!!
                )
            }
            if (result == 1) {
                call.respondText(
                    text = "Update Successfully",
                    status = HttpStatusCode.OK
                )
            } else {
                call.respondText(
                    text = "Something Went Wrong",
                    status = HttpStatusCode.BadRequest
                )
            }
        } catch (e: Exception) {
            call.respondText(
                text = e.message.toString(),
                status = HttpStatusCode.BadRequest
            )
        }
    }
    get("v1/category") {
        try {
            val quizCategories = db.getAllCategories()
            if (quizCategories?.isNotEmpty() == true) {
                call.respond(HttpStatusCode.OK, quizCategories)
            } else {
                call.respondText(text = "No Category Found", status = HttpStatusCode.NotFound)
            }
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.Unauthorized,
                "Error While Fetching Data From Server: ${e.message}"
            )
        }
    }
    get("v1/category/{id}") {
        val parameters = call.parameters["id"]
        try {
            val categoryId = parameters?.toLong()
            if (categoryId == null) {
                return@get call.respondText(
                    "Invalid ID",
                    status = HttpStatusCode.BadRequest
                )
            }
            val quizCategory = db.getCategoryById(categoryId)
            if (quizCategory == null) {
                return@get call.respondText(
                    text = "Category Not Found",
                    status = HttpStatusCode.NotFound
                )
            } else {
                return@get call.respond(
                    HttpStatusCode.OK,
                    quizCategory
                )
            }

        } catch (e: Exception) {
            call.respond(
                status = HttpStatusCode.Unauthorized,
                "Error While Fetching Data from Server : ${e.message}"
            )
        }

    }
    delete("v1/category/{id}") {
        val parameters = call.parameters["id"]
        try {
            val quizCategory = parameters?.toLongOrNull()?.let { usersId ->
                db.deleteCategoryById(usersId)
            } ?: return@delete call.respondText(
                text = "No Id Found",
                status = HttpStatusCode.BadRequest
            )
            if (quizCategory == 1) {
                call.respondText(
                    text = "Deleted Successfully",
                    status = HttpStatusCode.OK
                )
            } else {
                call.respondText(
                    text = "Id Not Found",
                    status = HttpStatusCode.BadRequest
                )
            }
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.Unauthorized,
                "Error While Deleting User From Server ${e.message}"
            )
        }
    }
}

fun Route.quiz(db: QuizQuestionsRepository, categoryDb: QuizRepository) {
    post("v1/quiz-questions") {
        val parameters = call.receive<Parameters>()
        val categoryId = parameters["categoryId"]?.toLongOrNull() ?: return@post call.respondText(
            text = "Invalid or missing categoryId",
            status = HttpStatusCode.BadRequest
        )
        val title = parameters["title"] ?: return@post call.respondText(
            text = "title Not Found",
            status = HttpStatusCode.BadRequest
        )
        val answer1 = parameters["answer1"] ?: return@post call.respondText(
            text = "answer1 Not Found",
            status = HttpStatusCode.BadRequest
        )
        val answer2 = parameters["answer2"] ?: return@post call.respondText(
            text = "answer2 Not Found",
            status = HttpStatusCode.BadRequest
        )
        val answer3 = parameters["answer3"] ?: return@post call.respondText(
            text = "answer3 Not Found",
            status = HttpStatusCode.BadRequest
        )
        val answer4 = parameters["answer4"] ?: return@post call.respondText(
            text = "answer4 Not Found",
            status = HttpStatusCode.BadRequest
        )
        val correctAnswer = parameters["correctAnswer"] ?: return@post call.respondText(
            text = "correctAnswer Not Found",
            status = HttpStatusCode.BadRequest
        )

        val categoryTitle = categoryDb.getCategoryById(categoryId)?.name
            ?: return@post call.respondText(
                text = "Category not found",
                status = HttpStatusCode.NotFound
            )

        try {
            val quizQuestion = db.insert(
                categoryId, categoryTitle, title, answer1, answer2, answer3, answer4, correctAnswer
            )
            if (quizQuestion != null) {
                call.respond(HttpStatusCode.Created, quizQuestion)
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Error creating quiz question")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
        }
    }

    put("v1/quiz-questions/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
            ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid or missing quiz ID")

        val parameters = call.receive<Parameters>()
        val categoryId = parameters["categoryId"]?.toLongOrNull() ?: return@put call.respondText(
            text = "Invalid or missing categoryId",
            status = HttpStatusCode.BadRequest
        )
        val title = parameters["title"] ?: return@put call.respondText(
            text = "title Not Found",
            status = HttpStatusCode.BadRequest
        )
        val answer1 = parameters["answer1"] ?: return@put call.respondText(
            text = "answer1 Not Found",
            status = HttpStatusCode.BadRequest
        )
        val answer2 = parameters["answer2"] ?: return@put call.respondText(
            text = "answer2 Not Found",
            status = HttpStatusCode.BadRequest
        )
        val answer3 = parameters["answer3"] ?: return@put call.respondText(
            text = "answer3 Not Found",
            status = HttpStatusCode.BadRequest
        )
        val answer4 = parameters["answer4"] ?: return@put call.respondText(
            text = "answer4 Not Found",
            status = HttpStatusCode.BadRequest
        )
        val correctAnswer = parameters["correctAnswer"] ?: return@put call.respondText(
            text = "correctAnswer Not Found",
            status = HttpStatusCode.BadRequest
        )

        val categoryTitle = categoryDb.getCategoryById(categoryId)?.name
            ?: return@put call.respondText(
                text = "Category not found",
                status = HttpStatusCode.NotFound
            )

        try {
            val updatedRows = db.updateQuiz(
                id, categoryId, categoryTitle, title, answer1, answer2, answer3, answer4, correctAnswer
            )
            if (updatedRows > 0) {
                call.respond(HttpStatusCode.OK, "Quiz question updated successfully")
            } else {
                call.respond(HttpStatusCode.NotFound, "Quiz question not found")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
        }
    }

    get("v1/quiz-questions") {
        try {
            val quizQuestions = db.getAllQuestions()
            if (quizQuestions != null && quizQuestions.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, quizQuestions)
            } else {
                call.respond(HttpStatusCode.NotFound, "No quiz questions found")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
        }
    }

    get("v1/quiz-questions/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid or missing quiz ID")
        try {
            val quizQuestion = db.getQuizById(id)
            if (quizQuestion != null) {
                call.respond(HttpStatusCode.OK, quizQuestion)
            } else {
                call.respond(HttpStatusCode.NotFound, "Quiz question not found")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
        }
    }

    get("v1/quiz-questions/category/{categoryId}") {
        val categoryId = call.parameters["categoryId"]?.toLongOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid or missing category ID")
        try {
            val quizQuestions = db.getQuizByCategoryId(categoryId)
            if (quizQuestions != null && quizQuestions.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, quizQuestions)
            } else {
                call.respond(HttpStatusCode.NotFound, "No quiz questions found for the given category")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
        }
    }

    delete("v1/quiz-questions/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
            ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid or missing quiz ID")
        try {
            val deletedRows = db.deleteQuizById(id)
            if (deletedRows > 0) {
                call.respond(HttpStatusCode.OK, "Quiz question deleted successfully")
            } else {
                call.respond(HttpStatusCode.NotFound, "Quiz question not found")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
        }
    }
}