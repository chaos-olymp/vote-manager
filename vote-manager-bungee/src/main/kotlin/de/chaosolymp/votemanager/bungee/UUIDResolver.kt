package de.chaosolymp.votemanager.bungee

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class UUIDResolver(private val plugin: BungeePlugin) {

    private val gson: Gson = Gson()
    private val cache = mutableMapOf<String, UUID>()

    fun resolve(username: String): UUID? {
        val filteredPlayerList = plugin.proxy.players.filter {
            it.name.equals(username, true)
        }

        if(filteredPlayerList.any()) {
            return filteredPlayerList[0].uniqueId
        } else {
            val key = username.toLowerCase()
            if (cache.containsKey(key)) {
                return cache[key]
            } else {
                val res = this.getUniqueIdByMojang(username)
                if (res.isPresent) {
                    val uuid = res.get()
                    cache[key] = uuid
                    return uuid
                }
            }

            return null
        }
    }

    private fun getUniqueIdByMojang(username: String): Optional<UUID> {
        val optional = this.plugin.proxy.players.stream().filter { it.name.equals(username, ignoreCase = true) }.map {it.uniqueId}.findFirst()
        if(optional.isPresent) {
            return optional
        }

        val url = URL("https://api.mojang.com/users/profiles/minecraft/$username")

        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        return if(connection.responseCode == 200) Optional.of(parseUuid(this.gson.fromJson(InputStreamReader(connection.inputStream), JsonObject::class.java).get("id").asString)) else Optional.empty()
    }

    private fun parseUuid(string: String): UUID {
        val buf = StringBuffer(string)
        buf.insert(20, '-')
        buf.insert(16, '-')
        buf.insert(12, '-')
        buf.insert(8, '-')
        return UUID.fromString(buf.toString())
    }
}