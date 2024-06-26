package com.example.plugins

import com.example.data.local.db.DatabaseFactory
import com.example.domain.model.quiz.QuizSubCategory
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
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.h2.engine.Database

fun Application.configureRouting() {
    DatabaseFactory.init()
    val db = UsersRepository()
    val categoryDb = QuizRepository()
    val quizDb = QuizQuestionsRepository()
    val notesDb = NotesRepository()
    val boardsDb = BoardsRepository()
    val classesDb = ClassesRepository()
    val subject = SubjectsRepository()
    val paperRepository = PapersRepository()
    val subCategory = QuizSubRepository()
    val subquestion = QuizQuestionsRepositoryWithSubCategory()
    val promotions = PromotionsRepository()
    val resume = ResumeRepository()
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        users(db)
        category(categoryDb)
        quiz(quizDb, categoryDb)
        notes(notesDb)
        boards(boardsDb)
        classes(classesDb)
        subjects(subject)
        papersRoute(paperRepository)
        boardDetails(boardsDb, classesDb, subject,paperRepository)
        subcategory(subCategory)
        quizWithSubCategory(subquestion,subCategory)
        promotionsRoute(promotions)
        resumes(resume)
    }
}
