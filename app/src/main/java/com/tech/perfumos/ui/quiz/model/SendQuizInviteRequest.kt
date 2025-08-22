package com.tech.perfumos.ui.quiz.model

data class SendQuizInviteRequest( val quizId: String,
                                  val userIds: List<String> // MUST be List<String>

                                   )
