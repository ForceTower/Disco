/*
 * This file is part of the UNES Open Source Project.
 * UNES is licensed under the GNU GPLv3.
 *
 * Copyright (c) 2021. João Paulo Sena <joaopaulo761@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.forcetower.unes.club.util.string

object WordUtils {
    fun toTitleCase(str: String): String {
        val givenString = str.lowercase()

        val arr = givenString.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val sb = StringBuilder()

        for (i in arr.indices) {
            val anArr = arr[i]

            if (i == arr.size - 1 && anArr.length <= 2) {
                sb.append(anArr.uppercase())
                continue
            }

            if (isGreekOneToTen(anArr)) {
                sb.append(anArr.uppercase()).append(" ")
                continue
            }

            if (isPossessivePronoun(anArr)) {
                sb.append(anArr).append(" ")
                continue
            }

            // Special case only for "MI's". PBL!!!!
            if (anArr.equals("MI", ignoreCase = true)) {
                sb.append(anArr.uppercase()).append(" ")
                continue
            }

            // The word "para" is lower case :)
            if (anArr.equals("para", ignoreCase = true) && i != 0) {
                sb.append(anArr.lowercase()).append(" ")
                continue
            }

            if (anArr.length < 3 && !anArr.endsWith(".") || anArr.length == 3 && anArr.endsWith("s")) {
                sb.append(anArr).append(" ")
                continue
            }

            sb.append(anArr[0].uppercaseChar())
                .append(anArr.substring(1)).append(" ")
        }
        return sb.toString().trim()
    }

    private fun isPossessivePronoun(str: String): Boolean {
        return str.equals("meu", ignoreCase = true) ||
                str.equals("seu", ignoreCase = true) ||
                str.equals("teu", ignoreCase = true) ||
                str.equals("minha", ignoreCase = true) ||
                str.equals("sua", ignoreCase = true) ||
                str.equals("tua", ignoreCase = true) ||
                str.equals("minhas", ignoreCase = true) ||
                str.equals("suas", ignoreCase = true) ||
                str.equals("tuas", ignoreCase = true) ||
                str.equals("nossa", ignoreCase = true) ||
                str.equals("vossa", ignoreCase = true) ||
                str.equals("nossas", ignoreCase = true)
    }

    private fun isGreekOneToTen(str: String): Boolean {
        return str.equals("i", ignoreCase = true) ||
            str.equals("ii", ignoreCase = true) ||
            str.equals("iii", ignoreCase = true) ||
            str.equals("iv", ignoreCase = true) ||
            str.equals("v", ignoreCase = true) ||
            str.equals("vi", ignoreCase = true) ||
            str.equals("vii", ignoreCase = true) ||
            str.equals("viii", ignoreCase = true) ||
            str.equals("ix", ignoreCase = true) ||
            str.equals("x", ignoreCase = true)
    }

    fun capitalize(str: String?): String? {
        str ?: return null

        val givenString = str.lowercase()

        val arr = givenString.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val sb = StringBuilder()

        for (anArr in arr) {
            if (anArr.isEmpty()) continue
            if (anArr.length < 2) {
                sb.append(anArr).append(" ")
            } else {
                sb.append(anArr[0].uppercaseChar())
                    .append(anArr.substring(1)).append(" ")
            }
        }
        return sb.toString().trim { it <= ' ' }
    }

    fun validString(string: String?): Boolean {
        return !string.isNullOrBlank()
    }
}
