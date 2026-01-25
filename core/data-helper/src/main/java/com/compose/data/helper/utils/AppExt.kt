package com.compose.data.helper.utils

import com.compose.data.helper.request.SearchRequest
import com.compose.data.helper.utils.ext.logs
import com.compose.data.helper.utils.ext.toJson
import com.compose.data.helper.utils.ext.translateJson
import java.util.HashMap

fun SearchRequest?.convertToQuery(): HashMap<String, String> {
    val tempSearch = this ?: SearchRequest(page = 1)
    tempSearch.advanceQuery = tempSearch.advanceQuery ?: ArrayList()
    tempSearch.simpleQuery = tempSearch.simpleQuery ?: ArrayList()
    logs("Search:${tempSearch.toJson().translateJson()}")
    val options: HashMap<String, String> = HashMap()
    tempSearch.let { sr ->
        sr.simpleQuery?.let { s ->
            s.forEach {
                if (it.column.isNotEmpty() && !it.value.isNullOrEmpty()) {
                    options[it.column] = it.value
                }
            }
        }
        var index = 0
        options["page"] = sr.page.toString()
        if (sr.perpage != null) {
            options["perpage"] = sr.perpage.toString()
        }

        sr.advanceQuery?.let { s ->
            s.forEach {
                if (it.column.isNotEmpty()) options["search[$index][column]"] = it.column
                if (it.condition.isNotEmpty()) options["search[$index][condition]"] = it.condition
                if (!it.value.isNullOrEmpty()) options["search[$index][value]"] = it.value
                if (!it.type.isNullOrEmpty()) options["search[$index][type]"] = it.type
                index += 1
            }
        }
    }
    return options
}

fun <T> List<T>.insertAt(value: T, index: Int = 1): List<T> {
    return when {
        index <= 0 -> listOf(value) + this
        index >= size -> this + value
        else -> take(index) + value + drop(index)
    }
}