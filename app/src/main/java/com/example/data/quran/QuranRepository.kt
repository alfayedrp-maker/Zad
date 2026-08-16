package com.example.data.quran

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object QuranRepository {

    val surahs: List<Surah> = listOf(
        Surah(1, "الفَاتِحَة", "Al-Fatiha", "The Opening", 7, RevelationType.MECCAN, 1),
        Surah(2, "البَقَرَة", "Al-Baqarah", "The Cow", 286, RevelationType.MEDINAN, 1),
        Surah(3, "آل عِمْرَان", "Ali 'Imran", "Family of Imran", 200, RevelationType.MEDINAN, 3),
        Surah(4, "النِّسَاء", "An-Nisa", "The Women", 176, RevelationType.MEDINAN, 4),
        Surah(5, "المَائِدَة", "Al-Ma'idah", "The Table Spread", 120, RevelationType.MEDINAN, 6),
        Surah(6, "الأَنْعَام", "Al-An'am", "The Cattle", 165, RevelationType.MECCAN, 7),
        Surah(7, "الأَعْرَاف", "Al-A'raf", "The Heights", 206, RevelationType.MECCAN, 8),
        Surah(8, "الأَنْفَال", "Al-Anfal", "The Spoils of War", 75, RevelationType.MEDINAN, 9),
        Surah(9, "التَّوْبَة", "At-Tawbah", "The Repentance", 129, RevelationType.MEDINAN, 10),
        Surah(10, "يُونُس", "Yunus", "Jonah", 109, RevelationType.MECCAN, 11),
        Surah(11, "هُود", "Hud", "Hud", 123, RevelationType.MECCAN, 11),
        Surah(12, "يُوسُف", "Yusuf", "Joseph", 111, RevelationType.MECCAN, 12),
        Surah(13, "الرَّعْد", "Ar-Ra'd", "The Thunder", 43, RevelationType.MEDINAN, 13),
        Surah(14, "إِبْرَاهِيم", "Ibrahim", "Abraham", 52, RevelationType.MECCAN, 13),
        Surah(15, "الحِجْر", "Al-Hijr", "The Rocky Tract", 99, RevelationType.MECCAN, 14),
        Surah(16, "النَّحْل", "An-Nahl", "The Bee", 128, RevelationType.MECCAN, 14),
        Surah(17, "الإِسْرَاء", "Al-Isra", "The Night Journey", 111, RevelationType.MECCAN, 15),
        Surah(18, "الكَهْف", "Al-Kahf", "The Cave", 110, RevelationType.MECCAN, 15),
        Surah(19, "مَرْيَم", "Maryam", "Mary", 98, RevelationType.MECCAN, 16),
        Surah(20, "طه", "Taha", "Ta-Ha", 135, RevelationType.MECCAN, 16),
        Surah(21, "الأَنْبِيَاء", "Al-Anbiya", "The Prophets", 112, RevelationType.MECCAN, 17),
        Surah(22, "الحَجّ", "Al-Hajj", "The Pilgrimage", 78, RevelationType.MEDINAN, 17),
        Surah(23, "المُؤْمِنُون", "Al-Mu'minun", "The Believers", 118, RevelationType.MECCAN, 18),
        Surah(24, "النُّور", "An-Nur", "The Light", 64, RevelationType.MEDINAN, 18),
        Surah(25, "الفُرْقَان", "Al-Furqan", "The Criterion", 77, RevelationType.MECCAN, 18),
        Surah(26, "الشُّعَرَاء", "Ash-Shu'ara", "The Poets", 227, RevelationType.MECCAN, 19),
        Surah(27, "النَّمْل", "An-Naml", "The Ant", 93, RevelationType.MECCAN, 19),
        Surah(28, "القَصَص", "Al-Qasas", "The Stories", 88, RevelationType.MECCAN, 20),
        Surah(29, "العَنْكَبُوت", "Al-'Ankabut", "The Spider", 69, RevelationType.MECCAN, 20),
        Surah(30, "الرُّوم", "Ar-Rum", "The Romans", 60, RevelationType.MECCAN, 21),
        Surah(31, "لُقْمَان", "Luqman", "Luqman", 34, RevelationType.MECCAN, 21),
        Surah(32, "السَّجْدَة", "As-Sajdah", "The Prostration", 30, RevelationType.MECCAN, 21),
        Surah(33, "الأَحْزَاب", "Al-Ahzab", "The Combined Forces", 73, RevelationType.MEDINAN, 21),
        Surah(34, "سَبَأ", "Saba", "Sheba", 54, RevelationType.MECCAN, 22),
        Surah(35, "فَاطِر", "Fatir", "Originator", 45, RevelationType.MECCAN, 22),
        Surah(36, "يس", "Ya-Sin", "Ya-Sin", 83, RevelationType.MECCAN, 22),
        Surah(37, "الصَّافَّات", "As-Saffat", "Those who set the Ranks", 182, RevelationType.MECCAN, 23),
        Surah(38, "ص", "Sad", "The Letter Sad", 88, RevelationType.MECCAN, 23),
        Surah(39, "الزُّمَر", "Az-Zumar", "The Troops", 75, RevelationType.MECCAN, 23),
        Surah(40, "غَافِر", "Ghafir", "The Forgiver", 85, RevelationType.MECCAN, 24),
        Surah(41, "فُصِّلَت", "Fussilat", "Explained in Detail", 54, RevelationType.MECCAN, 24),
        Surah(42, "الشُّورَى", "Ash-Shura", "The Consultation", 53, RevelationType.MECCAN, 25),
        Surah(43, "الزُّخْرُف", "Az-Zukhruf", "The Ornaments of Gold", 89, RevelationType.MECCAN, 25),
        Surah(44, "الدُّخَان", "Ad-Dukhan", "The Smoke", 59, RevelationType.MECCAN, 25),
        Surah(45, "الجَاثِيَة", "Al-Jathiyah", "The Crouching", 37, RevelationType.MECCAN, 25),
        Surah(46, "الأَحْقَاف", "Al-Ahqaf", "The Wind-Curved Sandhills", 35, RevelationType.MECCAN, 26),
        Surah(47, "مُحَمَّد", "Muhammad", "Muhammad", 38, RevelationType.MEDINAN, 26),
        Surah(48, "الفَتْح", "Al-Fath", "The Victory", 29, RevelationType.MEDINAN, 26),
        Surah(49, "الحُجُرَات", "Al-Hujurat", "The Rooms", 18, RevelationType.MEDINAN, 26),
        Surah(50, "ق", "Qaf", "The Letter Qaf", 45, RevelationType.MECCAN, 26),
        Surah(51, "الذَّارِيَات", "Adh-Dhariyat", "The Winnowing Winds", 60, RevelationType.MECCAN, 26),
        Surah(52, "الطُّور", "At-Tur", "The Mount", 49, RevelationType.MECCAN, 27),
        Surah(53, "النَّجْم", "An-Najm", "The Star", 62, RevelationType.MECCAN, 27),
        Surah(54, "القَمَر", "Al-Qamar", "The Moon", 55, RevelationType.MECCAN, 27),
        Surah(55, "الرَّحْمَٰن", "Ar-Rahman", "The Beneficent", 78, RevelationType.MEDINAN, 27),
        Surah(56, "الوَاقِعَة", "Al-Waqi'ah", "The Inevitable", 96, RevelationType.MECCAN, 27),
        Surah(57, "الحَدِيد", "Al-Hadid", "The Iron", 29, RevelationType.MEDINAN, 27),
        Surah(58, "المُجَادِلَة", "Al-Mujadila", "The Pleading Woman", 22, RevelationType.MEDINAN, 28),
        Surah(59, "الحَشْر", "Al-Hashr", "The Exile", 24, RevelationType.MEDINAN, 28),
        Surah(60, "المُمْتَحَنَة", "Al-Mumtahanah", "She that is to be examined", 13, RevelationType.MEDINAN, 28),
        Surah(61, "الصَّفّ", "As-Saff", "The Ranks", 14, RevelationType.MEDINAN, 28),
        Surah(62, "الجُمُعَة", "Al-Jumu'ah", "The Congregation, Friday", 11, RevelationType.MEDINAN, 28),
        Surah(63, "المُنَافِقُون", "Al-Munafiqun", "The Hypocrites", 11, RevelationType.MEDINAN, 28),
        Surah(64, "التَّغَابُن", "At-Taghabun", "The Mutual Disillusion", 18, RevelationType.MEDINAN, 28),
        Surah(65, "الطَّلَاق", "At-Talaq", "The Divorce", 12, RevelationType.MEDINAN, 28),
        Surah(66, "التَّحْرِيم", "At-Tahrim", "The Prohibition", 12, RevelationType.MEDINAN, 28),
        Surah(67, "المُلْك", "Al-Mulk", "The Sovereignty", 30, RevelationType.MECCAN, 29),
        Surah(68, "القَلَم", "Al-Qalam", "The Pen", 52, RevelationType.MECCAN, 29),
        Surah(69, "الحَاقَّة", "Al-Haqqah", "The Reality", 52, RevelationType.MECCAN, 29),
        Surah(70, "المَعَارِج", "Al-Ma'arij", "The Ascending Stairways", 44, RevelationType.MECCAN, 29),
        Surah(71, "نُوح", "Nuh", "Noah", 28, RevelationType.MECCAN, 29),
        Surah(72, "الجِنّ", "Al-Jinn", "The Jinn", 28, RevelationType.MECCAN, 29),
        Surah(73, "المُزَّمِّل", "Al-Muzzammil", "The Enshrouded One", 20, RevelationType.MECCAN, 29),
        Surah(74, "المُدَّثِّر", "Al-Muddaththir", "The Cloaked One", 56, RevelationType.MECCAN, 29),
        Surah(75, "القِيَامَة", "Al-Qiyamah", "The Resurrection", 40, RevelationType.MECCAN, 29),
        Surah(76, "الإِنْسَان", "Al-Insan", "Man", 31, RevelationType.MEDINAN, 29),
        Surah(77, "المُرْسَلَات", "Al-Mursalat", "The Emissaries", 50, RevelationType.MECCAN, 29),
        Surah(78, "النَّبَأ", "An-Naba", "The Tidings", 40, RevelationType.MECCAN, 30),
        Surah(79, "النَّازِعَات", "An-Nazi'at", "Those who drag forth", 46, RevelationType.MECCAN, 30),
        Surah(80, "عَبَسَ", "'Abasa", "He Frowned", 42, RevelationType.MECCAN, 30),
        Surah(81, "التَّكْوِير", "At-Takwir", "The Overthrowing", 29, RevelationType.MECCAN, 30),
        Surah(82, "الانْفِطَار", "Al-Infitar", "The Cleaving", 19, RevelationType.MECCAN, 30),
        Surah(83, "المُطَفِّفِين", "Al-Mutaffifin", "The Defrauding", 36, RevelationType.MECCAN, 30),
        Surah(84, "الانْشِقَاق", "Al-Inshiqaq", "The Splitting Open", 25, RevelationType.MECCAN, 30),
        Surah(85, "default_api:Buruj", "Al-Buruj", "The Mansions of the Stars", 22, RevelationType.MECCAN, 30),
        Surah(86, "الطَّارِق", "At-Tariq", "The Morning Star", 17, RevelationType.MECCAN, 30),
        Surah(87, "الأَعْلَى", "Al-A'la", "The Most High", 19, RevelationType.MECCAN, 30),
        Surah(88, "الغَاشِيَة", "Al-Ghashiyah", "The Overwhelming", 26, RevelationType.MECCAN, 30),
        Surah(89, "الفَجْر", "Al-Fajr", "The Dawn", 30, RevelationType.MECCAN, 30),
        Surah(90, "البَلَد", "Al-Balad", "The City", 20, RevelationType.MECCAN, 30),
        Surah(91, "الشَّمْس", "Ash-Shams", "The Sun", 15, RevelationType.MECCAN, 30),
        Surah(92, "اللَّيْل", "Al-Layl", "The Night", 21, RevelationType.MECCAN, 30),
        Surah(93, "الضُّحَى", "Ad-Duha", "The Morning Hours", 11, RevelationType.MECCAN, 30),
        Surah(94, "الشَّرْح", "Ash-Sharh", "The Relief", 8, RevelationType.MECCAN, 30),
        Surah(95, "التِّين", "At-Tin", "The Fig", 8, RevelationType.MECCAN, 30),
        Surah(96, "العَلَق", "Al-'Alaq", "The Clot", 19, RevelationType.MECCAN, 30),
        Surah(97, "القَدْر", "Al-Qadr", "The Power", 5, RevelationType.MECCAN, 30),
        Surah(98, "البَيِّنَة", "Al-Bayyinah", "The Clear Proof", 8, RevelationType.MEDINAN, 30),
        Surah(99, "الزَّلْزَلَة", "Az-Zalzalah", "The Earthquake", 8, RevelationType.MEDINAN, 30),
        Surah(100, "العَادِيَات", "Al-'Adiyat", "The Courser", 11, RevelationType.MECCAN, 30),
        Surah(101, "القَارِعَة", "Al-Qari'ah", "The Calamity", 11, RevelationType.MECCAN, 30),
        Surah(102, "التَّكَاثُر", "At-Takathur", "The Rivalry in World Increase", 8, RevelationType.MECCAN, 30),
        Surah(103, "العَصْر", "Al-'Asr", "The Declining Day", 3, RevelationType.MECCAN, 30),
        Surah(104, "الهُمَزَة", "Al-Humazah", "The Traducer", 9, RevelationType.MECCAN, 30),
        Surah(105, "الفِيل", "Al-Fil", "The Elephant", 5, RevelationType.MECCAN, 30),
        Surah(106, "قُرَيْش", "Quraysh", "Quraysh", 4, RevelationType.MECCAN, 30),
        Surah(107, "المَاعُون", "Al-Ma'un", "The Small Kindnesses", 7, RevelationType.MECCAN, 30),
        Surah(108, "الكَوْثَر", "Al-Kawthar", "The Abundance", 3, RevelationType.MECCAN, 30),
        Surah(109, "الكَافِرُون", "Al-Kafirun", "The Disbelievers", 6, RevelationType.MECCAN, 30),
        Surah(110, "النَّصْر", "An-Nasr", "The Divine Support", 3, RevelationType.MEDINAN, 30),
        Surah(111, "المَسَد", "Al-Masad", "The Palm Fiber", 5, RevelationType.MECCAN, 30),
        Surah(112, "الإِخْلَاص", "Al-Ikhlas", "The Sincerity", 4, RevelationType.MECCAN, 30),
        Surah(113, "الفَلَق", "Al-Falaq", "The Daybreak", 5, RevelationType.MECCAN, 30),
        Surah(114, "النَّاس", "An-Nas", "Mankind", 6, RevelationType.MECCAN, 30)
    )

    private val cachedAyahsMap = mutableMapOf<Int, List<Ayah>>()

    private val client = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(6, TimeUnit.SECONDS)
        .build()

    init {
        // Preload essential authentic Surahs for instant zero-latency offline reading
        cachedAyahsMap[1] = listOf(
            Ayah(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah, the Entirely Merciful, the Especially Merciful."),
            Ayah(1, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "[All] praise is [due] to Allah, Lord of the worlds -"),
            Ayah(1, 3, "الرَّحْمَٰنِ الرَّحِيمِ", "The Entirely Merciful, the Especially Merciful,"),
            Ayah(1, 4, "مَالِكِ يَوْمِ الدِّينِ", "Sovereign of the Day of Recompense."),
            Ayah(1, 5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "It is You we worship and You we ask for help."),
            Ayah(1, 6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "Guide us to the straight path -"),
            Ayah(1, 7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.")
        )

        cachedAyahsMap[112] = listOf(
            Ayah(112, 1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "Say, \"He is Allah, [who is] One,"),
            Ayah(112, 2, "اللَّهُ الصَّمَدُ", "Allah, the Eternal Refuge."),
            Ayah(112, 3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "He neither begets nor is born,"),
            Ayah(112, 4, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "Nor is there to Him any equivalent.\"")
        )

        cachedAyahsMap[113] = listOf(
            Ayah(113, 1, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "Say, \"I seek refuge in the Lord of daybreak"),
            Ayah(113, 2, "مِن شَرِّ مَا خَلَقَ", "From the evil of that which He created"),
            Ayah(113, 3, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "And from the evil of darkness when it settles"),
            Ayah(113, 4, "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "And from the evil of the blowers in knots"),
            Ayah(113, 5, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "And from the evil of an envier when he envies.\"")
        )

        cachedAyahsMap[114] = listOf(
            Ayah(114, 1, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "Say, \"I seek refuge in the Lord of mankind,"),
            Ayah(114, 2, "مَلِكِ النَّاسِ", "The Sovereign of mankind,"),
            Ayah(114, 3, "إِلَٰهِ النَّاسِ", "The God of mankind,"),
            Ayah(114, 4, "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "From the evil of the retreating whisperer -"),
            Ayah(114, 5, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "Who whispers into the breasts of mankind -"),
            Ayah(114, 6, "مِنَ الْجِنَّةِ وَالنَّاسِ", "From among the jinn and mankind.\"")
        )

        cachedAyahsMap[108] = listOf(
            Ayah(108, 1, "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ", "Indeed, We have granted you, [O Muhammad], al-Kawthar."),
            Ayah(108, 2, "فَصَلِّ لِرَبِّكَ وَانْحَرْ", "So pray to your Lord and sacrifice [to Him alone]."),
            Ayah(108, 3, "إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ", "Indeed, your enemy is the one cut off.")
        )

        cachedAyahsMap[67] = listOf(
            Ayah(67, 1, "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", "Blessed is He in whose hand is dominion, and He is over all things competent -"),
            Ayah(67, 2, "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا ۚ وَهُوَ الْعَزِيزُ الْغَفُورُ", "[He] who created death and life to test you [as to] which of you is best in deed - and He is the Exalted in Might, the Forgiving -"),
            Ayah(67, 3, "الَّذِي خَلَقَ سَبْعَ سَمَاوَاتٍ طِبَاقًا ۖ مَّا تَرَىٰ فِي خَلْقِ الرَّحْمَٰنِ مِن تَفَاوُتٍ", "[And] who created seven heavens in layers. You do not see in the creation of the Most Merciful any inconsistency."),
            Ayah(67, 4, "ثُمَّ ارْجِعِ الْبَصَرَ كَرَّتَيْنِ يَنقَلِبْ إِلَيْكَ الْبَصَرُ خَاسِئًا وَهُوَ حَسِيرٌ", "Then return [your] vision twice again. [Your] vision will return to you humbled while it is fatigued."),
            Ayah(67, 5, "وَلَقَدْ زَيَّنَّا السَّمَاءَ الدُّنْيَا بِمَصَابِيحَ وَجَعَلْنَاهَا رُجُومًا لِّلشَّيَاطِينِ", "And We have certainly beautified the nearest heaven with stars and have made [from] them missiles for the devils.")
        )
    }

    suspend fun getAyahsForSurah(surahNumber: Int): List<Ayah> = withContext(Dispatchers.IO) {
        cachedAyahsMap[surahNumber]?.let { return@withContext it }

        // Fetch from Quran API (api.alquran.cloud)
        try {
            val request = Request.Builder()
                .url("https://api.alquran.cloud/v1/surah/$surahNumber/editions/quran-uthmani,en.sahih")
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string()
                if (!body.isNullOrBlank()) {
                    val json = JSONObject(body)
                    val data = json.getJSONArray("data")
                    val arabicEd = data.getJSONObject(0).getJSONArray("ayahs")
                    val englishEd = data.getJSONObject(1).getJSONArray("ayahs")

                    val result = mutableListOf<Ayah>()
                    for (i in 0 until arabicEd.length()) {
                        val arObj = arabicEd.getJSONObject(i)
                        val enObj = englishEd.getJSONObject(i)

                        var arText = arObj.getString("text")
                        // Remove Bismillah prefix from first ayah if not Surah 1 & 9
                        if (surahNumber != 1 && surahNumber != 9 && i == 0 && arText.startsWith("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ")) {
                            arText = arText.replace("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "").trim()
                        }

                        result.add(
                            Ayah(
                                surahNumber = surahNumber,
                                numberInSurah = arObj.getInt("numberInSurah"),
                                textArabic = arText,
                                translationEnglish = enObj.getString("text")
                            )
                        )
                    }
                    cachedAyahsMap[surahNumber] = result
                    return@withContext result
                }
            }
        } catch (e: Exception) {
            Log.e("QuranRepository", "Error fetching Surah $surahNumber", e)
        }

        // Fallback placeholder verses if offline
        val surah = surahs.firstOrNull { it.number == surahNumber } ?: return@withContext emptyList()
        val fallbackList = (1..surah.numberOfAyahs).map { index ->
            Ayah(
                surahNumber = surahNumber,
                numberInSurah = index,
                textArabic = "آية $index من سورة ${surah.nameArabic} - قراءة وتلاوة مباركة",
                translationEnglish = "Verse $index of Surah ${surah.nameEnglish}"
            )
        }
        fallbackList
    }

    fun getAudioUrlForAyah(qari: Qari, surah: Int, ayah: Int): String {
        val formatted = String.format("%03d%03d.mp3", surah, ayah)
        return "https://everyayah.com/data/${qari.subfolder}/$formatted"
    }

    fun getFullSurahAudioUrl(qari: Qari, surah: Int): String {
        val formatted = String.format("%03d.mp3", surah)
        return "${qari.surahBaseUrl}$formatted"
    }
}
