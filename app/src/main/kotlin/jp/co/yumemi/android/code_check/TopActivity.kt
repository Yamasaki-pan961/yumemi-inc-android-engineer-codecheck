/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import androidx.appcompat.app.AppCompatActivity
import java.util.*

/**
 * アプリのトップアクティビティ
 * */
class TopActivity : AppCompatActivity(R.layout.activity_top) {

    companion object {
        var lastSearchDate: Date? = null
    }
}
