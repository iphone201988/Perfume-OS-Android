package com.tech.perfumos.ui.quiz.model

data class QuestionAnsModel(
    val id :String,
    val ques:String,
    val ansList:ArrayList<AnsListModel>,
)

data class AnsListModel(
    val ans: String,
    var correct :Boolean?,
    var ansSelected :Boolean = false
)
