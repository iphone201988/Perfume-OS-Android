package com.tech.perfumos.ui.quiz.model
//
//data class StartQuesModel(
//    val nameValuePairs: NameValuePairs
//)
//
//data class NameValuePairs(
//    val getQuiz: GetQuiz
//)
//
//data class GetQuiz(
//    val nameValuePairs: NameValuePairsX
//)
//
//data class NameValuePairsX(
//    val __v: Int,
//    val _id: String,
//    val createdAt: String,
//    val hostId: String,
//    val mode: String,
//    val playType: String,
//    val players: Players,
//    val questions: Questions,
//    val quizCategory: QuizCategory24,
//    val quizType: String,
//    val roomId: Int,
//    val status: String,
//    val totalQuestions: Int,
//    val updatedAt: String
//)
//
//data class Players(
//    val values: List<Value>
//)
//
//data class Questions(
////    val values: List<QuizQuestion>
//    val values: List<ValueX>
//)
//
//data class QuizCategory24(
//    val nameValuePairs: NameValuePairsXXXXX
//)
//
//data class Value(
//    val nameValuePairs: NameValuePairsXX
//)
//
//data class NameValuePairsXX(
//    val _id: String,
//    val answers: Answers,
//    val correctAnswers: Int,
//    val isActive: Boolean,
//    val joinedAt: String,
//    val pointsEarned: Int,
//    val score: Int,
//    val userId: UserId24
//)
//
//data class Answers(
//    val values: List<Any>
//)
//
//data class UserId24(
//    val nameValuePairs: NameValuePairsXXX
//)
//
//data class NameValuePairsXXX(
//    val _id: String,
//    val fullname: String,
//    val profileImage: String
//)
//
//data class ValueX(
//    val nameValuePairs: NameValuePairsXXXX
//)
//
//data class NameValuePairsXXXX(
//    val __v: Int,
//    val _id: String,
//    val correctAnswer: String,
//    val createdAt: String,
//    val isDeleted: Boolean,
//    val options: Options,
//    val questionText: String,
//    val type: String,
//    val updatedAt: String
//)
//
//data class Options(
//    val values: List<String>
//)
//
//data class NameValuePairsXXXXX(
//    val __v: Int,
//    val _id: String,
//    val description: String,
//    val image: String,
//    val isDeleted: Boolean,
//    val subTitle: String,
//    val title: String,
//    val totalQuestions: Int,
//    val type: String
//)
data class StartQuesModel(
    val getQuiz: GetQuiz
)

data class GetQuiz(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val hostId: String,
    val mode: String,
    val playType: String,
    val players: List<PlayerStartQuesModel>,
    val questions: List<QuestionStartQuesModel>,
    val quizCategory: QuizCategoryStartQuesModel,
    val quizType: String,
    val roomId: Int,
    val status: String,
    val totalQuestions: Int,
    val updatedAt: String
)

data class PlayerStartQuesModel(
    val _id: String,
    val answers: List<Any>,
    val correctAnswers: Int,
    val isActive: Boolean,
    val joinedAt: String,
    val pointsEarned: Int,
    val score: Int,
    val userId: UserIdStartQuesModel
)

data class QuestionStartQuesModel(
    val __v: Int,
    val _id: String,
    val correctAnswer: String,
    val createdAt: String,
    val explanation: String,
    val image: String,
    val isDeleted: Boolean,
    val options: List<String>,
    val questionText: String,
    val type: String,
    val updatedAt: String
)

data class QuizCategoryStartQuesModel(
    val __v: Int,
    val _id: String,
    val description: String,
    val image: String,
    val isDeleted: Boolean,
    val subTitle: String,
    val title: String,
    val totalQuestions: Int,
    val type: String
)

data class UserIdStartQuesModel(
    val _id: String,
    val fullname: String,
    val profileImage: String
)