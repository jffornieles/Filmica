package io.keepcoding.filmica.view.util

import io.keepcoding.filmica.data.Film

interface OnClickLister {
    fun onClick(film: Film)
}