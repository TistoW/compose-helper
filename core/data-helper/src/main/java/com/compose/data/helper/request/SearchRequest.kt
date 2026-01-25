package com.compose.data.helper.request

import kotlin.collections.toMutableList
import kotlin.text.isNullOrEmpty


data class SearchRequest(
    var advanceQuery: List<Search>? = null,
    var page: Int = 1,
    var perpage: Int? = null,
    var simpleQuery: List<Search>? = null, // for single query,
    var searchName: String? = null, // easy way searchByName
    val orderBy: String? = null, // createdAt | name |etc | default createdAt
    val orderType: String? = null, // DESC | ASC |default DESC,
    val productType: String? = null //"normal" | "service" | "points" | "package"
) {
    init {
        if (!searchName.isNullOrEmpty()) {
            val list = advanceQuery?.toMutableList() ?: kotlin.collections.ArrayList()
            list.add(Search("name", value = searchName, condition = "like"))
            advanceQuery = list
        }
        if (orderBy != null) {
            val list = simpleQuery?.toMutableList() ?: kotlin.collections.ArrayList()
            list.add(Search("orderBy", orderBy))
            simpleQuery = list
        }
        if (orderType != null) {
            val list = simpleQuery?.toMutableList() ?: kotlin.collections.ArrayList()
            list.add(Search("orderType", orderType))
            simpleQuery = list
        }
    }
}

data class Search(
    val column: String,
    val value: String? = null,
    val condition: String = "=", // like | = | !=
    val type: String? = null, // OR | AND
)

fun SearchRequest?.defaultSearch(): SearchRequest {
    val tempSearch = this ?: SearchRequest()
    val list = tempSearch.advanceQuery ?: kotlin.collections.ArrayList()
    tempSearch.advanceQuery = list
    return tempSearch
}