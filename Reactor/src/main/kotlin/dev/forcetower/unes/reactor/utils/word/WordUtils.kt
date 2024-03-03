package dev.forcetower.unes.reactor.utils.word

object WordUtils {
    @JvmStatic
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

            sb.append(Character.toUpperCase(anArr[0]))
                .append(anArr.substring(1)).append(" ")
        }
        return sb.toString().trim()
    }

    @JvmStatic
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

    @JvmStatic
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
                sb.append(Character.toUpperCase(anArr[0]))
                    .append(anArr.substring(1)).append(" ")
            }
        }
        return sb.toString().trim { it <= ' ' }
    }

    @JvmStatic
    fun validString(string: String?): Boolean {
        return !string.isNullOrBlank()
    }
}
