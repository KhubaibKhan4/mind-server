package com.example.plugins

import com.example.domain.model.chat.ChatMessage
import com.example.domain.model.login.LoginResponse
import com.example.domain.model.papers.combine.BoardDetails
import com.example.domain.model.papers.combine.ClassDetails
import com.example.domain.model.papers.combine.SubjectDetails
import com.example.domain.repository.classes.ClassesRepository
import com.example.domain.repository.notes.NotesRepository
import com.example.domain.repository.papers.BoardsRepository
import com.example.domain.repository.papers.PapersRepository
import com.example.domain.repository.papers.SubjectsRepository
import com.example.domain.repository.promotion.PromotionsRepository
import com.example.domain.repository.quiz.QuizQuestionsRepository
import com.example.domain.repository.quiz.QuizQuestionsRepositoryWithSubCategory
import com.example.domain.repository.quiz.QuizRepository
import com.example.domain.repository.quiz.QuizSubRepository
import com.example.domain.repository.resume.ResumeRepository
import com.example.domain.repository.user.UsersRepository
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap

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
    get("v1/users/emails") {
        val emailsParam = call.request.queryParameters["emails"]
        try {
            if (emailsParam.isNullOrBlank()) {
                return@get call.respondText(
                    "Emails parameter is missing",
                    status = HttpStatusCode.BadRequest
                )
            }

            val emails = emailsParam.split(",")
            val users = emails.mapNotNull { email ->
                db.getUserByEmail(email)
            }

            if (users.isEmpty()) {
                return@get call.respondText(
                    "No users found",
                    status = HttpStatusCode.NotFound
                )
            } else {
                return@get call.respond(
                    HttpStatusCode.OK,
                    users
                )
            }
        } catch (e: Exception) {
            call.respond(
                status = HttpStatusCode.InternalServerError,
                "Error while fetching data from server: ${e.message}"
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

fun Route.notes(
    db: NotesRepository
) {
    post("v1/notes") {
        val multipart = call.receiveMultipart()
        var title = ""
        var description = ""
        var pdfFileName = ""
        var pdfFilePath: String? = null
        val uploadDir = File("upload/products/notes")
        if (!uploadDir.exists()) {
            uploadDir.mkdirs()
        }

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "title" -> title = part.value
                        "description" -> description = part.value
                    }
                }

                is PartData.FileItem -> {
                    if (part.name == "pdf") {
                        pdfFileName = part.originalFileName?.replace(" ", "_")
                            ?: "pdf_${System.currentTimeMillis()}.pdf"
                        val file = File("upload/products/notes", pdfFileName)
                        part.streamProvider().use { input ->
                            file.outputStream().buffered().use { output ->
                                input.copyTo(output)
                            }
                        }
                        pdfFilePath = "/upload/products/notes/$pdfFileName"
                    }
                }

                else -> {}
            }
            part.dispose()
        }

        try {
            if (pdfFilePath == null) {
                call.respond(HttpStatusCode.BadRequest, "PDF file is required")
                return@post
            }

            val note = db.insert(title, description, pdfFilePath!!)
            if (note != null) {
                call.respond(HttpStatusCode.Created, note)
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Unable to create note")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Unauthorized, "Error While Uploading Data to Server ${e.message}")
        }
    }
    get("v1/notes") {
        try {
            val notes = db.getAllNotes()
            if (notes != null) {
                call.respond(notes)
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Unable to fetch notes")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Unauthorized, "Error While Fetching Data From Server ${e.message}")
        }
    }
    get("v1/notes/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        try {
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid note ID")
                return@get
            }

            val note = db.getNoteById(id)
            if (note != null) {
                call.respond(note)
            } else {
                call.respond(HttpStatusCode.NotFound, "Note not found")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Unauthorized, "Error While Fetching Data From Server ${e.message}")
        }
    }
    put("v1/notes/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid note ID")
            return@put
        }

        val multipart = call.receiveMultipart()
        var title = ""
        var description = ""
        var pdfFileName = ""
        var pdfFilePath: String? = null

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "title" -> title = part.value
                        "description" -> description = part.value
                    }
                }

                is PartData.FileItem -> {
                    if (part.name == "pdf") {
                        pdfFileName = part.originalFileName?.replace(" ", "_")
                            ?: "pdf_${System.currentTimeMillis()}.pdf"
                        val file = File("upload/notes", pdfFileName)
                        part.streamProvider().use { input ->
                            file.outputStream().buffered().use { output ->
                                input.copyTo(output)
                            }
                        }
                        pdfFilePath = "/upload/notes/$pdfFileName"
                    }
                }

                else -> {}
            }
            part.dispose()
        }

        try {
            val currentNote = db.getNoteById(id)
            if (currentNote == null) {
                call.respond(HttpStatusCode.NotFound, "Note not found")
                return@put
            }

            val finalPdfFilePath = pdfFilePath ?: currentNote.pdfUrl

            val updateCount = db.updateNote(
                id = id,
                title = title,
                description = description,
                pdfUrl = finalPdfFilePath
            )

            if (updateCount > 0) {
                call.respond(HttpStatusCode.OK, "Note updated successfully")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Unable to update note")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Unauthorized, "Error While Updating Data to Server ${e.message}")
        }
    }
    delete("v1/notes/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid note ID")
            return@delete
        }

        try {
            val deleteCount = db.deleteNote(id)
            if (deleteCount > 0) {
                call.respond(HttpStatusCode.OK, "Note deleted successfully")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Unable to delete note")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Unauthorized, "Error While Deleting Data to Server ${e.message}")
        }
    }

}

fun Route.boards(db: BoardsRepository) {
    post("v1/boards") {
        val multipart = call.receiveMultipart()
        var title = ""
        var description = ""
        var imageFileName = ""
        var imageFilePath: String? = null
        val uploadDir = File("upload/products/boards")
        if (!uploadDir.exists()) {
            uploadDir.mkdirs()
        }

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "title" -> title = part.value
                        "description" -> description = part.value
                    }
                }

                is PartData.FileItem -> {
                    if (part.name == "image") {
                        imageFileName = part.originalFileName?.replace(" ", "_")
                            ?: "image_${System.currentTimeMillis()}.jpg"
                        val file = File(uploadDir, imageFileName)
                        part.streamProvider().use { input ->
                            file.outputStream().buffered().use { output ->
                                input.copyTo(output)
                            }
                        }
                        imageFilePath = "/upload/products/boards/$imageFileName"
                    }
                }

                else -> {}
            }
            part.dispose()
        }

        try {
            if (imageFilePath == null) {
                call.respond(HttpStatusCode.BadRequest, "Image file is required")
                return@post
            }

            val board = db.insert(title, description, imageFilePath!!)
            if (board != null) {
                call.respond(HttpStatusCode.Created, board)
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Unable to create board")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while uploading data to server: ${e.message}")
        }
    }

    get("v1/boards") {
        try {
            val boards = db.getAllBoards()
            if (boards != null) {
                call.respond(boards)
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Unable to fetch boards")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while fetching boards: ${e.message}")
        }
    }

    get("v1/boards/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        try {
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid board ID")
                return@get
            }

            val board = db.getBoardById(id)
            if (board != null) {
                call.respond(board)
            } else {
                call.respond(HttpStatusCode.NotFound, "Board not found")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while fetching board: ${e.message}")
        }
    }

    put("v1/boards/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid board ID")
            return@put
        }

        val multipart = call.receiveMultipart()
        var title = ""
        var description = ""
        var imageFileName = ""
        var imageFilePath: String? = null

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "title" -> title = part.value
                        "description" -> description = part.value
                    }
                }

                is PartData.FileItem -> {
                    if (part.name == "image") {
                        imageFileName = part.originalFileName?.replace(" ", "_")
                            ?: "image_${System.currentTimeMillis()}.jpg"
                        val file = File("upload/products/boards", imageFileName)
                        part.streamProvider().use { input ->
                            file.outputStream().buffered().use { output ->
                                input.copyTo(output)
                            }
                        }
                        imageFilePath = "/upload/products/boards/$imageFileName"
                    }
                }

                else -> {}
            }
            part.dispose()
        }

        try {
            val currentBoard = db.getBoardById(id)
            if (currentBoard == null) {
                call.respond(HttpStatusCode.NotFound, "Board not found")
                return@put
            }

            val finalImageFilePath = imageFilePath ?: currentBoard.imageUrl

            val updateCount = db.updateBoard(
                id = id,
                title = title,
                description = description,
                imageUrl = finalImageFilePath
            )

            if (updateCount > 0) {
                call.respond(HttpStatusCode.OK, "Board updated successfully")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Unable to update board")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while updating board: ${e.message}")
        }
    }

    delete("v1/boards/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid board ID")
            return@delete
        }

        try {
            val deleteCount = db.deleteBoardById(id)
            if (deleteCount > 0) {
                call.respond(HttpStatusCode.OK, "Board deleted successfully")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Unable to delete board")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while deleting board: ${e.message}")
        }
    }
}

fun Route.classes(
    db: ClassesRepository
) {
    post("v1/classes") {
        val parameters = call.receiveParameters()
        val boardId = parameters["boardId"]?.toLongOrNull()
        val title = parameters["title"]
        val description = parameters["description"]

        if (boardId == null || title.isNullOrBlank() || description.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, "Invalid parameters")
            return@post
        }

        try {
            val newClass = db.insert(boardId, title, description)
            if (newClass != null) {
                call.respond(HttpStatusCode.Created, newClass)
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Unable to create class")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while processing request: ${e.message}")
        }
    }

    get("v1/classes") {
        try {
            val classes = db.getAllClasses()
            call.respond(HttpStatusCode.OK, classes ?: emptyList())
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while processing request: ${e.message}")
        }
    }

    get("v1/classes/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid class ID")
            return@get
        }

        try {
            val classDetails = db.getClassesById(id)
            if (classDetails != null) {
                call.respond(HttpStatusCode.OK, classDetails)
            } else {
                call.respond(HttpStatusCode.NotFound, "Class not found")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while processing request: ${e.message}")
        }
    }

    get("v1/classes/board/{id}") {
        val boardId = call.parameters["id"]?.toLongOrNull()
        if (boardId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid board ID")
            return@get
        }

        try {
            val classesForBoard = db.getClassesByBoardId(boardId)
            if (classesForBoard != null) {
                call.respond(HttpStatusCode.OK, classesForBoard)
            } else {
                call.respond(HttpStatusCode.NotFound, "No classes found for this board")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while processing request: ${e.message}")
        }
    }

    delete("v1/classes/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid class ID")
            return@delete
        }

        try {
            val deleteCount = db.deleteBoardById(id)
            if (deleteCount > 0) {
                call.respond(HttpStatusCode.OK, "Class deleted successfully")
            } else {
                call.respond(HttpStatusCode.NotFound, "Class not found")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while processing request: ${e.message}")
        }
    }

    put("v1/classes/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid class ID")
            return@put
        }

        val parameters = call.receiveParameters()
        val boardId = parameters["boardId"]?.toLongOrNull()
        val title = parameters["title"]
        val description = parameters["description"]

        if (boardId == null || title.isNullOrBlank() || description.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, "Invalid parameters")
            return@put
        }

        try {
            val updateCount = db.updateBoard(id, boardId, title, description)
            if (updateCount > 0) {
                call.respond(HttpStatusCode.OK, "Class updated successfully")
            } else {
                call.respond(HttpStatusCode.NotFound, "Class not found")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while processing request: ${e.message}")
        }
    }
}

fun Route.subjects(
    db: SubjectsRepository
) {
    post("/v1/subjects") {
        val parameters = call.receiveParameters()
        val boardId = parameters["boardId"]?.toLongOrNull()
        val classId = parameters["classId"]?.toLongOrNull()
        val title = parameters["title"]
        val description = parameters["description"]

        if (boardId == null || classId == null || title.isNullOrBlank() || description.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, "Invalid parameters")
            return@post
        }

        try {
            val newSubject = db.insert(boardId, classId, title, description)
            if (newSubject != null) {
                call.respond(HttpStatusCode.Created, newSubject)
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Unable to create subject")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while processing request: ${e.message}")
        }
    }

    get("/v1/subjects") {
        try {
            val subjects = db.getAllSubjects()
            if (subjects.isNullOrEmpty()) {
                call.respond(HttpStatusCode.OK, "No Subjects Found")
            } else {
                call.respond(HttpStatusCode.OK, subjects)
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while processing request: ${e.message}")
        }
    }

    get("/v1/subjects/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid subject ID")
            return@get
        }

        try {
            val subject = db.getSubjectById(id)
            if (subject != null) {
                call.respond(HttpStatusCode.OK, subject)
            } else {
                call.respond(HttpStatusCode.NotFound, "Subject not found")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while processing request: ${e.message}")
        }
    }

    get("/v1/subjects/board/{boardId}") {
        val boardId = call.parameters["boardId"]?.toLongOrNull()
        if (boardId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid board ID")
            return@get
        }

        try {
            val subjects = db.getSubjectsByBoardId(boardId)
            call.respond(HttpStatusCode.OK, subjects ?: emptyList())
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while processing request: ${e.message}")
        }
    }

    get("/v1/subjects/class/{classId}") {
        val classId = call.parameters["classId"]?.toLongOrNull()
        if (classId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid class ID")
            return@get
        }

        try {
            val subjects = db.getSubjectsByClassId(classId)
            call.respond(HttpStatusCode.OK, subjects ?: emptyList())
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while processing request: ${e.message}")
        }
    }

    delete("/v1/subjects/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid subject ID")
            return@delete
        }

        try {
            val deleteCount = db.deleteSubjectById(id)
            if (deleteCount > 0) {
                call.respond(HttpStatusCode.OK, "Subject deleted successfully")
            } else {
                call.respond(HttpStatusCode.NotFound, "Subject not found")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while processing request: ${e.message}")
        }
    }

    put("/v1/subjects/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid subject ID")
            return@put
        }

        val parameters = call.receiveParameters()
        val boardId = parameters["boardId"]?.toLongOrNull()
        val classId = parameters["classId"]?.toLongOrNull()
        val title = parameters["title"]
        val description = parameters["description"]

        if (boardId == null || classId == null || title.isNullOrBlank() || description.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, "Invalid parameters")
            return@put
        }

        try {
            val updateCount = db.updateSubject(id, boardId, classId, title, description)
            if (updateCount > 0) {
                call.respond(HttpStatusCode.OK, "Subject updated successfully")
            } else {
                call.respond(HttpStatusCode.NotFound, "Subject not found")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while processing request: ${e.message}")
        }
    }
}

fun Route.boardDetails(
    boardDb: BoardsRepository,
    classDb: ClassesRepository,
    subjectDb: SubjectsRepository,
    papersDb: PapersRepository
) {
    get("/v1/board-details/{boardId}") {
        val boardId = call.parameters["boardId"]?.toLongOrNull()
        if (boardId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid board ID")
            return@get
        }

        try {
            val board = boardDb.getBoardById(boardId)?.let { board ->
                val classes = classDb.getClassesByBoardId(boardId) ?: emptyList()
                val classDetails = classes.map { classItem ->
                    val subjects = subjectDb.getSubjectsByClassId(classItem.id)?.map { subject ->
                        val papers = papersDb.getPapersBySubjectId(subject.id) ?: emptyList()
                        val papersWithUrls = papers.map { paper ->
                            // Construct the PDF URL by combining the base URL with the paper's URL
                            val pdfUrl = "${paper.pdfUrl}"
                            paper.copy(pdfUrl = pdfUrl)
                        }
                        SubjectDetails(
                            id = subject.id,
                            classId = subject.classId,
                            title = subject.title,
                            description = subject.description,
                            papers = papersWithUrls
                        )
                    } ?: emptyList<SubjectDetails>()
                    ClassDetails(
                        id = classItem.id,
                        boardId = classItem.boardId,
                        title = classItem.title,
                        description = classItem.description,
                        subjects = subjects
                    )
                }

                BoardDetails(
                    id = board.id,
                    title = board.title,
                    description = board.description,
                    imageUrl = board.imageUrl,
                    classes = classDetails
                )
            }

            if (board == null) {
                call.respond(HttpStatusCode.NotFound, "Board not found")
            } else {
                call.respond(HttpStatusCode.OK, board)
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while processing request: ${e.message}")
        }
    }
}


fun Route.papersRoute(db: PapersRepository) {
    post("/v1/papers") {
        val multipart = call.receiveMultipart()
        var boardId: Long? = null
        var classId: Long? = null
        var subjectId: Long? = null
        var pdfFilePath: String? = null
        val uploadDir = File("upload/products/pdfs/papers")
        if (!uploadDir.exists()) {
            uploadDir.mkdirs()
        }

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "boardId" -> boardId = part.value.toLongOrNull()
                        "classId" -> classId = part.value.toLongOrNull()
                        "subjectId" -> subjectId = part.value.toLongOrNull()
                    }
                }

                is PartData.FileItem -> {
                    if (part.name == "pdf") {
                        val pdfFileName = "${System.currentTimeMillis()}_${part.originalFileName?.replace(" ", "_")}"
                        val file = File(uploadDir, pdfFileName)
                        part.streamProvider().use { input ->
                            file.outputStream().buffered().use { output ->
                                input.copyTo(output)
                            }
                        }
                        pdfFilePath = file.absolutePath
                    }
                }

                else -> {}
            }
            part.dispose()
        }

        if (boardId == null || classId == null || subjectId == null || pdfFilePath == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid parameters")
            return@post
        }

        try {
            val newPaper = db.insert(boardId!!, classId!!, subjectId!!, pdfFilePath!!)
            if (newPaper != null) {
                call.respond(HttpStatusCode.Created, newPaper)
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Unable to create paper")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while processing request: ${e.message}")
        }
    }

    put("/v1/papers/{paperId}") {
        val paperId = call.parameters["paperId"]?.toLongOrNull()
        if (paperId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid paper ID")
            return@put
        }

        val multipart = call.receiveMultipart()
        var boardId: Long? = null
        var classId: Long? = null
        var subjectId: Long? = null
        var pdfFileName: String? = null
        var pdfFilePath: String? = null
        val uploadDir = File("upload/products/pdfs/papers")
        if (!uploadDir.exists()) {
            uploadDir.mkdirs()
        }

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "boardId" -> boardId = part.value.toLongOrNull()
                        "classId" -> classId = part.value.toLongOrNull()
                        "subjectId" -> subjectId = part.value.toLongOrNull()
                    }
                }

                is PartData.FileItem -> {
                    if (part.name == "pdf") {
                        pdfFileName = part.originalFileName?.replace(" ", "_")?.let {
                            if (!it.endsWith(".pdf")) "$it.pdf" else it
                        }
                        val file = File(uploadDir, pdfFileName)
                        part.streamProvider().use { input ->
                            file.outputStream().buffered().use { output ->
                                input.copyTo(output)
                            }
                        }
                        pdfFilePath = "/upload/products/pdfs/papers/$pdfFileName"
                    }
                }

                else -> {}
            }
            part.dispose()
        }

        if (boardId == null || classId == null || subjectId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid parameters")
            return@put
        }

        try {
            val updatedPaper = db.update(paperId, boardId, classId, subjectId, pdfFilePath)
            if (updatedPaper != null) {
                call.respond(HttpStatusCode.OK, updatedPaper)
            } else {
                call.respond(HttpStatusCode.NotFound, "Paper not found")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while processing request: ${e.message}")
        }
    }

    get("/v1/papers/board/{boardId}") {
        val boardId = call.parameters["boardId"]?.toLongOrNull()
        if (boardId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid board ID")
            return@get
        }

        try {
            val papers = db.getPapersByBoardId(boardId)
            if (papers != null) {
                call.respond(HttpStatusCode.OK, papers)
            } else {
                call.respond(HttpStatusCode.NotFound, "No papers found for this board")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while fetching papers: ${e.message}")
        }
    }

    get("/v1/papers/class/{classId}") {
        val classId = call.parameters["classId"]?.toLongOrNull()
        if (classId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid class ID")
            return@get
        }

        try {
            val papers = db.getPapersByClassId(classId)
            if (papers != null) {
                call.respond(HttpStatusCode.OK, papers)
            } else {
                call.respond(HttpStatusCode.NotFound, "No papers found for this class")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while fetching papers: ${e.message}")
        }
    }

    get("/v1/papers/subject/{subjectId}") {
        val subjectId = call.parameters["subjectId"]?.toLongOrNull()
        if (subjectId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid subject ID")
            return@get
        }

        try {
            val papers = db.getPapersBySubjectId(subjectId)
            if (papers != null) {
                call.respond(HttpStatusCode.OK, papers)
            } else {
                call.respond(HttpStatusCode.NotFound, "No papers found for this subject")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while fetching papers: ${e.message}")
        }
    }
}

fun Route.subcategory(
    db: QuizSubRepository
) {
    post("v1/subcategory") {
        val multipart = call.receiveMultipart()

        var name: String? = null
        var description: String? = null
        var categoryName: String? = null
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
                        "categoryName" -> categoryName = part.value
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
        categoryName ?: return@post call.respondText(
            text = "Category Name Missing",
            status = HttpStatusCode.BadRequest
        )
        imageUrl ?: return@post call.respondText(
            text = "Image Missing",
            status = HttpStatusCode.BadRequest
        )

        try {
            val quizSubCategory = db.insert(
                categoryName!!, name!!, description!!, imageUrl!!
            )
            quizSubCategory?.id?.let {
                call.respond(
                    status = HttpStatusCode.OK,
                    "Subcategory uploaded to server successfully: $quizSubCategory"
                )
            }
        } catch (e: Exception) {
            call.respond(
                status = HttpStatusCode.InternalServerError,
                "Error while uploading data to server: ${e.message}"
            )
        }
    }

    put("v1/subcategory/{id}") {
        val id = call.parameters["id"] ?: return@put call.respondText(
            text = "Id Not Found",
            status = HttpStatusCode.NotFound
        )
        val multipart = call.receiveMultipart()

        var name: String? = null
        var description: String? = null
        var imageUrl: String? = null
        var categoryName: String? = null

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "name" -> name = part.value
                        "description" -> description = part.value
                        "categoryName" -> categoryName = part.value
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
            text = "Description Missing",
            status = HttpStatusCode.BadRequest
        )
        categoryName ?: return@put call.respondText(
            text = "Category Name Missing",
            status = HttpStatusCode.BadRequest
        )
        imageUrl ?: return@put call.respondText(
            text = "Image Missing",
            status = HttpStatusCode.BadRequest
        )

        try {
            val result = id.toLong().let { categoryId ->
                db.updateSubCategory(
                    categoryId,
                    categoryName!!,
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
    get("v1/subcategory") {
        try {
            val quizSubCategories = db.getAllSubCategories()
            if (quizSubCategories?.isNotEmpty() == true) {
                call.respond(HttpStatusCode.OK, quizSubCategories)
            } else {
                call.respondText(text = "No Subcategory Found", status = HttpStatusCode.NotFound)
            }
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.Unauthorized,
                "Error While Fetching Data From Server: ${e.message}"
            )
        }
    }
    get("v1/subcategory/{id}") {
        val parameters = call.parameters["id"]
        try {
            val subcategoryId = parameters?.toLong()
            if (subcategoryId == null) {
                return@get call.respondText(
                    "Invalid ID",
                    status = HttpStatusCode.BadRequest
                )
            }
            val quizSubCategory = db.getSubCategoryById(subcategoryId)
            if (quizSubCategory == null) {
                return@get call.respondText(
                    text = "Subcategory Not Found",
                    status = HttpStatusCode.NotFound
                )
            } else {
                return@get call.respond(
                    HttpStatusCode.OK,
                    quizSubCategory
                )
            }

        } catch (e: Exception) {
            call.respond(
                status = HttpStatusCode.Unauthorized,
                "Error While Fetching Data from Server : ${e.message}"
            )
        }
    }
    delete("v1/subcategory/{id}") {
        val parameters = call.parameters["id"]
        try {
            val quizSubCategory = parameters?.toLongOrNull()?.let { subcategoryId ->
                db.deleteSubCategoryById(subcategoryId)
            } ?: return@delete call.respondText(
                text = "No Id Found",
                status = HttpStatusCode.BadRequest
            )
            if (quizSubCategory == 1) {
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
                "Error While Deleting Subcategory From Server ${e.message}"
            )
        }
    }
}
fun Route.quizWithSubCategory(
    db: QuizQuestionsRepositoryWithSubCategory,
    categoryDb: QuizSubRepository
) {
    post("v1/quiz-questions-sub") {
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

        val categoryTitle = categoryDb.getSubCategoryById(categoryId)?.name
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

    put("v1/quiz-questions-sub/{id}") {
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

        val categoryTitle = categoryDb.getSubCategoryById(categoryId)?.name
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

    get("v1/quiz-questions-sub") {
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

    get("v1/quiz-questions-sub/{id}") {
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

    get("v1/quiz-questions-sub/category/{categoryId}") {
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

    delete("v1/quiz-questions-sub/{id}") {
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
fun Route.promotionsRoute(db: PromotionsRepository) {
    post("/v1/promotions") {
        val multipart = call.receiveMultipart()
        var imageUrl: String? = null
        val uploadDir = File("upload/products/promotions/images")
        if (!uploadDir.exists()) {
            uploadDir.mkdirs()
        }

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    if (part.name == "image") {
                        val fileName = part.originalFileName?.replace(" ", "_")
                            ?: "image${System.currentTimeMillis()}"
                        val file = File("upload/products/promotions/images", fileName)
                        part.streamProvider().use { input ->
                            file.outputStream().buffered().use { output ->
                                input.copyTo(output)
                            }
                        }
                        imageUrl = "/upload/products/promotions/images/${fileName}"
                    }
                }
                else -> {}
            }
            part.dispose()
        }

        if (imageUrl == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing image URL")
            return@post
        }

        try {
            val newPromotion = db.insert(imageUrl!!)
            if (newPromotion != null) {
                call.respond(HttpStatusCode.Created, newPromotion)
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Unable to create promotion")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while processing request: ${e.message}")
        }
    }

    put("/v1/promotions/{promotionId}") {
        val promotionId = call.parameters["promotionId"]?.toLongOrNull()
        if (promotionId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid promotion ID")
            return@put
        }

        val multipart = call.receiveMultipart()
        var imageUrl: String? = null
        val uploadDir = File("upload/products/promotions/images")
        if (!uploadDir.exists()) {
            uploadDir.mkdirs()
        }

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    if (part.name == "image") {
                        val fileName = part.originalFileName?.replace(" ", "_")
                            ?: "image${System.currentTimeMillis()}"
                        val file = File("upload/products/users", fileName)
                        part.streamProvider().use { input ->
                            file.outputStream().buffered().use { output ->
                                input.copyTo(output)
                            }
                        }
                        imageUrl = "/upload/products/promotions/images/${fileName}"
                    }
                }
                else -> {}
            }
            part.dispose()
        }

        if (imageUrl == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing image URL")
            return@put
        }

        try {
            val updatedRows = db.updatePromotion(promotionId, imageUrl!!)
            if (updatedRows > 0) {
                call.respond(HttpStatusCode.OK, "Promotion updated successfully")
            } else {
                call.respond(HttpStatusCode.NotFound, "Promotion not found")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while processing request: ${e.message}")
        }
    }

    get("/v1/promotions/{promotionId}") {
        val promotionId = call.parameters["promotionId"]?.toLongOrNull()
        if (promotionId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid promotion ID")
            return@get
        }

        try {
            val promotion = db.getPromotionById(promotionId)
            if (promotion != null) {
                call.respond(HttpStatusCode.OK, promotion)
            } else {
                call.respond(HttpStatusCode.NotFound, "Promotion not found")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while processing request: ${e.message}")
        }
    }

    get("/v1/promotions") {
        try {
            val promotions = db.getAllPromotions()
            if (promotions != null) {
                call.respond(HttpStatusCode.OK, promotions)
            } else {
                call.respond(HttpStatusCode.NotFound, "No promotions found")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while fetching promotions: ${e.message}")
        }
    }

    delete("/v1/promotions/{promotionId}") {
        val promotionId = call.parameters["promotionId"]?.toLongOrNull()
        if (promotionId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid promotion ID")
            return@delete
        }

        try {
            val deletedRows = db.deletePromotionById(promotionId)
            if (deletedRows > 0) {
                call.respond(HttpStatusCode.OK, "Promotion deleted successfully")
            } else {
                call.respond(HttpStatusCode.NotFound, "Promotion not found")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error while deleting promotion: ${e.message}")
        }
    }
}
suspend fun generatePdf(htmlContent: String): File {
    val tempHtmlFile = File.createTempFile("resume", ".html")
    val tempPdfFile = File.createTempFile("resume", ".pdf")

    tempHtmlFile.writeText(htmlContent)

    withContext(Dispatchers.IO) {
        val process = ProcessBuilder("C:\\\\Program Files\\\\wkhtmltopdf\\\\bin\\\\wkhtmltopdf.exe", tempHtmlFile.absolutePath, tempPdfFile.absolutePath)
            .start()
        process.waitFor()
    }

    tempHtmlFile.delete()
    return tempPdfFile
}
fun Route.resumes(db: ResumeRepository) {
    post("/generate-pdf") {
        val htmlContent = call.receiveText()
        val pdfFile = generatePdf(htmlContent)
        call.respondFile(pdfFile)
    }
    post("v1/resumes") {
        val multipart = call.receiveMultipart()
        var categoryName = ""
        var imageFileName = ""
        var imageFilePath: String? = null
        val uploadDir = File("upload/products/resumes/images")
        if (!uploadDir.exists()) {
            uploadDir.mkdirs()
        }

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    if (part.name == "categoryName") {
                        categoryName = part.value
                    }
                }
                is PartData.FileItem -> {
                    if (part.name == "image") {
                        val fileExtension = part.originalFileName?.substringAfterLast(".", "jpg")
                        imageFileName = "${UUID.randomUUID()}.$fileExtension"
                        val file = File(uploadDir, imageFileName)
                        part.streamProvider().use { input ->
                            file.outputStream().buffered().use { output ->
                                input.copyTo(output)
                            }
                        }
                        imageFilePath = "/upload/products/resumes/images/$imageFileName"
                    }
                }
                else -> {}
            }
            part.dispose()
        }

        try {
            if (imageFilePath == null) {
                call.respond(HttpStatusCode.BadRequest, "Image file is required")
                return@post
            }

            val resume = db.insert(categoryName, imageFilePath!!)
            if (resume != null) {
                call.respond(HttpStatusCode.Created, resume)
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Unable to create resume")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Unauthorized, "Error while uploading data to server: ${e.message}")
        }
    }

    get("v1/resumes") {
        try {
            val resumes = db.getAllResume()
            if (resumes != null) {
                call.respond(resumes)
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Unable to fetch resumes")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Unauthorized, "Error while fetching data from server: ${e.message}")
        }
    }

    get("v1/resumes/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        try {
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid resume ID")
                return@get
            }

            val resume = db.getResumeById(id)
            if (resume != null) {
                call.respond(resume)
            } else {
                call.respond(HttpStatusCode.NotFound, "Resume not found")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Unauthorized, "Error while fetching data from server: ${e.message}")
        }
    }

    put("v1/resumes/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid resume ID")
            return@put
        }

        val multipart = call.receiveMultipart()
        var categoryName = ""
        var imageFileName = ""
        var imageFilePath: String? = null

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    if (part.name == "categoryName") {
                        categoryName = part.value
                    }
                }
                is PartData.FileItem -> {
                    if (part.name == "image") {
                        val fileExtension = part.originalFileName?.substringAfterLast(".", "jpg")
                        imageFileName = "${UUID.randomUUID()}.$fileExtension"
                        val file = File("upload/products/resumes/images", imageFileName)
                        part.streamProvider().use { input ->
                            file.outputStream().buffered().use { output ->
                                input.copyTo(output)
                            }
                        }
                        imageFilePath = "/upload//products/resumes/images/$imageFileName"
                    }
                }
                else -> {}
            }
            part.dispose()
        }

        try {
            val currentResume = db.getResumeById(id)
            if (currentResume == null) {
                call.respond(HttpStatusCode.NotFound, "Resume not found")
                return@put
            }

            val finalImageFilePath = imageFilePath ?: currentResume.imageUrl

            val updateCount = db.updateResume(
                id = id,
                categoryName = categoryName,
                imageUrl = finalImageFilePath
            )

            if (updateCount > 0) {
                call.respond(HttpStatusCode.OK, "Resume updated successfully")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Unable to update resume")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Unauthorized, "Error while updating data to server: ${e.message}")
        }
    }

    delete("v1/resumes/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid resume ID")
            return@delete
        }

        try {
            val deleteCount = db.deleteById(id)
            if (deleteCount > 0) {
                call.respond(HttpStatusCode.OK, "Resume deleted successfully")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Unable to delete resume")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Unauthorized, "Error while deleting data from server: ${e.message}")
        }
    }
}
