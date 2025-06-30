package com.insfinal.bookdforall.utils

object BookAssetPaths {

    private val assetMap = mapOf(
        101 to Pair("atomic_habits.pdf", "atomic_habits.webp"),
        102 to Pair("C++ tutorial.pdf", "c++_tutorial.png"), // Perhatikan spasi jika ada di nama file asli
        103 to Pair("sapiens.pdf", "sapiens.png"),
        104 to Pair("the_alchemist.pdf", "the_alchemist.png"),
        105 to Pair("the_lord_of_the_rings.pdf", "the_lord_of_the_rings.png"),
        106 to Pair("the_midnight_library.pdf", "the_midnight_library.webp")
    )

    fun getPdfFileName(bookId: Int): String? {
        return assetMap[bookId]?.first
    }

    fun getCoverImageFileName(bookId: Int): String? {
        return assetMap[bookId]?.second
    }

    const val ASSETS_ROOT = "file:///android_asset/"
    const val ASSETS_IMG_PATH = "assetsimg/"
}