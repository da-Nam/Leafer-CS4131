package com.example.androidproject.model.tools

import android.net.Uri
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*

class Souper {
    companion object {
        //launch with CoroutineScope(IO).launch {}
        fun getGoogImageUri(str: String): String? {
            var str = str
            var links: String? = null
            try {
                str = str.trim { it <= ' ' }
                if (str.contains(" ")) {
                    str = str.replace(" ", "+")
                }
                val url = ("https://www.google.com/search?tbm=isch&q=$str")
                //println(url)
                val doc = Jsoup.connect(url).get()
                val el = doc.getElementsByAttribute("data-src")
                if (!el.isEmpty()) {
                    links = el[Random().nextInt(((el.size)/9))].attr("data-src")
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            if (Objects.isNull(links)) {
                // MessageUtil.showErrorMessage("Ne postoji slika za takav naziv!");
                links =
                    "https://previews.123rf.com/images/esfirse/esfirse1812/esfirse181200156/115299132-cross-sign-red-x-icon-isolated-on-white-background-circle-symbol.jpg"
                return links
            }
            return links
        }
    }
}