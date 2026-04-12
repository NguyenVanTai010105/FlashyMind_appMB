package com.example.flashcard.domain

import com.example.flashcardapp.modal.FolderModal
import com.example.flashcardapp.modal.WordModel

fun calculateSM2(card: WordModel, quality: Int): WordModel {
    var rep = card.repetition
    var interval = card.interval
    var ease = card.easeFactor // Đã tự động nhận kiểu Double

    // Cập nhật số lần lặp và khoảng thời gian
    if (quality >= 3) {
        interval = when (rep) {
            0 -> 1
            1 -> 6
            else -> (interval * ease).toInt()
        }
        rep++
    } else {
        rep = 0
        interval = 1
    }

    // Cập nhật hệ số dễ (Ease Factor) - Đã bỏ chữ 'f'
    ease += (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02))
    if (ease < 1.3) ease = 1.3 // Giới hạn dưới của ease factor là 1.3 (Double)

    // Tính toán mốc thời gian ôn tập tiếp theo
    val nextReviewTime = System.currentTimeMillis() + (interval * 24L * 60L * 60L * 1000L)

    return card.copy(
        repetition = rep,
        interval = interval,
        easeFactor = ease,
        nextReviewDate = nextReviewTime
    )
}