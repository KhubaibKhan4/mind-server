package com.example.plugins

import com.example.data.local.db.DatabaseFactory
import com.example.domain.repository.user.UsersRepository
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.h2.engine.Database

fun Application.configureRouting() {
    install(AutoHeadResponse)
    DatabaseFactory.init()
    val db = UsersRepository()
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        users(db)
    }
}
