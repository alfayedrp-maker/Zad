package com.example.data.azkar

data class ZikrItem(
    val id: String,
    val arabicText: String,
    val translation: String,
    val benefit: String,
    val targetCount: Int
)

data class AzkarCategory(
    val id: String,
    val titleArabic: String,
    val titleEnglish: String,
    val items: List<ZikrItem>
)

object AzkarRepository {

    val categories: List<AzkarCategory> = listOf(
        AzkarCategory(
            id = "morning",
            titleArabic = "أذكار الصباح",
            titleEnglish = "Morning Azkar",
            items = listOf(
                ZikrItem(
                    id = "m_1",
                    arabicText = "أَصْبَحْنَا وَأَصْبَحَ المُلْكُ لِلَّهِ، وَالحَمْدُ لِلَّهِ، لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ المُلْكُ وَلَهُ الحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ.",
                    translation = "We have entered the morning and the kingdom belongs to Allah; praise be to Allah. None has the right to be worshipped except Allah alone, without partner.",
                    benefit = "سؤال خير هذا اليوم والتعوذ من الشر",
                    targetCount = 1
                ),
                ZikrItem(
                    id = "m_2",
                    arabicText = "اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ وَإِلَيْكَ النُّشُورُ.",
                    translation = "O Allah, by You we enter the morning and by You we enter the evening, by You we live and by You we die, and to You is the resurrection.",
                    benefit = "التوكل وتجديد الإيمان",
                    targetCount = 1
                ),
                ZikrItem(
                    id = "m_3",
                    arabicText = "اللَّهُمَّ أَنْتَ رَبِّي لاَ إِلَهَ إِلاَّ أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لاَ يَغْفِرُ الذُّنُوبَ إِلاَّ أَنْتَ.",
                    translation = "O Allah, You are my Lord, none has the right to be worshipped except You. You created me and I am Your servant...",
                    benefit = "سيد الاستغفار: من قالها موقناً بها فمات من يومه دخل الجنة",
                    targetCount = 1
                ),
                ZikrItem(
                    id = "m_4",
                    arabicText = "بِسْمِ اللَّهِ الَّذِي لاَ يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الأَرْضِ وَلاَ فِي السَّمَاءِ وَهُوَ السَّمِيعُ العَلِيمُ.",
                    translation = "In the Name of Allah with Whose Name nothing can do harm on earth nor in the heavens, and He is the All-Hearing, All-Knowing.",
                    benefit = "حماية من كل مكروه وضر",
                    targetCount = 3
                ),
                ZikrItem(
                    id = "m_5",
                    arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، عَدَدَ خَلْقِهِ، وَرِضَا نَفْسِهِ، وَزِنَةَ عَرْشِهِ، وَمِدَادَ كَلِمَاتِهِ.",
                    translation = "Glory is to Allah and praise is to Him, by the number of His creation and His pleasure, and by the weight of His Throne, and the ink of His words.",
                    benefit = "مضاعفة الأجر والميزان",
                    targetCount = 3
                )
            )
        ),
        AzkarCategory(
            id = "evening",
            titleArabic = "أذكار المساء",
            titleEnglish = "Evening Azkar",
            items = listOf(
                ZikrItem(
                    id = "e_1",
                    arabicText = "أَمْسَيْنَا وَأَمْسَى المُلْكُ لِلَّهِ، وَالحَمْدُ لِلَّهِ، لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ المُلْكُ وَلَهُ الحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ.",
                    translation = "We have reached the evening and the kingdom belongs to Allah, and all praise is for Allah.",
                    benefit = "حفظ وبركة المساء",
                    targetCount = 1
                ),
                ZikrItem(
                    id = "e_2",
                    arabicText = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ.",
                    translation = "I seek refuge in the Perfect Words of Allah from the evil of what He has created.",
                    benefit = "وقاية وحصن من كل شر وهوام الأرض",
                    targetCount = 3
                ),
                ZikrItem(
                    id = "e_3",
                    arabicText = "اللَّهُمَّ عَافِنِي فِي بَدَنِي، اللَّهُمَّ عَافِنِي فِي سَمْعِي، اللَّهُمَّ عَافِنِي فِي بَصَرِي، لاَ إِلَهَ إِلاَّ أَنْتَ.",
                    translation = "O Allah, grant me health in my body. O Allah, grant me health in my hearing. O Allah, grant me health in my sight.",
                    benefit = "العافية والستر في الدنيا والآخرة",
                    targetCount = 3
                ),
                ZikrItem(
                    id = "e_4",
                    arabicText = "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ، أَصْلِحْ لِي شَأْنِي كُلَّهُ، وَلاَ تَكِلْنِي إِلَى نَفْسِي طَرْفَةَ عَيْنٍ.",
                    translation = "O Ever Living, O Sustainer, by Your mercy I seek assistance; rectify for me all of my affairs and do not leave me to myself, even for the blink of an eye.",
                    benefit = "تفريج الكروب والاعتماد على الله",
                    targetCount = 1
                )
            )
        ),
        AzkarCategory(
            id = "after_prayer",
            titleArabic = "أذكار بعد الصلاة المفروضة",
            titleEnglish = "Post-Prayer Azkar",
            items = listOf(
                ZikrItem(
                    id = "ap_1",
                    arabicText = "أَسْتَغْفِرُ اللَّهَ، أَسْتَغْفِرُ اللَّهَ، أَسْتَغْفِرُ اللَّهَ. اللَّهُمَّ أَنْتَ السَّلاَمُ وَمِنْكَ السَّلاَمُ، تَبَارَكْتَ يَا ذَا الجَلاَلِ وَالإِكْرَامِ.",
                    translation = "I ask Allah for forgiveness (3x). O Allah, You are Peace and from You comes peace. Blessed are You, O Owner of majesty and honor.",
                    benefit = "سنة المصطفى ﷺ عقب التسليم من الصلاة",
                    targetCount = 1
                ),
                ZikrItem(
                    id = "ap_2",
                    arabicText = "سُبْحَانَ اللَّهِ (33) ، الحَمْدُ لِلَّهِ (33) ، اللَّهُ أَكْبَرُ (33) ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ المُلْكُ وَلَهُ الحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ (1).",
                    translation = "Glory be to Allah (33), Praise be to Allah (33), Allah is the Greatest (33)...",
                    benefit = "تغفر الخطايا وإن كانت مثل زبد البحر",
                    targetCount = 33
                ),
                ZikrItem(
                    id = "ap_3",
                    arabicText = "اللَّهُ لاَ إِلَهَ إِلاَّ هُوَ الحَيُّ القَيُّومُ لاَ تَأْخُذُهُ سِنَةٌ وَلاَ نَوْمٌ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الأَرْضِ... (آية الكرسي)",
                    translation = "Allah - there is no deity except Him, the Ever-Living, the Sustainer of existence...",
                    benefit = "من قرأها دبر كل صلاة مكتوبة لم يمنعه من دخول الجنة إلا الموت",
                    targetCount = 1
                )
            )
        ),
        AzkarCategory(
            id = "sleep",
            titleArabic = "أذكار النوم والاستيقاظ",
            titleEnglish = "Sleep & Awakening",
            items = listOf(
                ZikrItem(
                    id = "sl_1",
                    arabicText = "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي، وَبِكَ أَرْفَعُهُ، فَإِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ.",
                    translation = "In Your name my Lord, I lie down and in Your name I rise up...",
                    benefit = "حفظ النفس والروح أثناء النوم",
                    targetCount = 1
                ),
                ZikrItem(
                    id = "sl_2",
                    arabicText = "الحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ.",
                    translation = "Praise is to Allah Who gave us life after having given us death, and to Him is the resurrection.",
                    benefit = "ذكر الاستيقاظ من النوم",
                    targetCount = 1
                )
            )
        )
    )

    val defaultTasbeehItems = listOf(
        ZikrItem("t_1", "سُبْحَانَ اللَّهِ", "SubhanAllah (Glory be to Allah)", "تسبيح", 33),
        ZikrItem("t_2", "الْحَمْدُ لِلَّهِ", "Alhamdulillah (Praise be to Allah)", "تحميد", 33),
        ZikrItem("t_3", "اللَّهُ أَكْبَرُ", "Allahu Akbar (Allah is the Greatest)", "تكبير", 33),
        ZikrItem("t_4", "لَا إِلَٰهَ إِلَّا اللَّهُ", "La Ilaha Illa Allah", "تهليل", 100),
        ZikrItem("t_5", "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ", "Astaghfirullah wa Atubu Ilayh", "استغفار", 100),
        ZikrItem("t_6", "اللَّهُمَّ صَلِّ عَلَى سَيِّدِنَا مُحَمَّدٍ", "Allahumma Salli Ala Sayyidina Muhammad", "صلاة على النبي", 100),
        ZikrItem("t_7", "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ", "La Hawla Wala Quwwata Illa Billah", "كنز من كنوز الجنة", 100)
    )
}
