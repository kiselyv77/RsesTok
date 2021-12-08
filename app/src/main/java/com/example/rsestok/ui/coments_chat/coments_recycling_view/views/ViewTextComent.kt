package com.example.rsestok.ui.coments_chat.coments_recycling_view.views


data class ViewTextComent(
    override val id: String,
    override val text: String,
    override val type: String,
    override val from: String,
    override val timeStamp: String,
    override val videoId: String,
) :ComentView {

    override fun getTypeView(): Int {
        return ComentView.COMENT_TEXT
    }

    override fun equals(other: Any?): Boolean {
        return (other as ComentView).id == id
    }
}