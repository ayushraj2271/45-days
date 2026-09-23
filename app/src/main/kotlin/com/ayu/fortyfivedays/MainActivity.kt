package com.ayu.fortyfivedays

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

private const val CHANNEL_ID = "study_reminders"
private const val PREFS = "45days_v2"

data class Topic(val id: String, val subject: String, val chapter: String, val title: String, val minutes: Int, val guide: String)
data class StudyTask(val topic: Topic, val day: Int)

data class SubjectChoice(val name: String, val enabled: Boolean)

private val allTopics = listOf(
    Topic("m1","Mathematics","Real Numbers","Fundamental Theorem of Arithmetic",30,"Prime factorisation, uniqueness of factorisation, HCF and LCM connections."),
    Topic("m2","Mathematics","Real Numbers","Irrationality proofs",30,"Learn proof patterns for √2, √3 and √5 and write each step clearly."),
    Topic("m3","Mathematics","Polynomials","Zeros of a polynomial",30,"Meaning of zero/root and graphical interpretation."),
    Topic("m4","Mathematics","Polynomials","Relationship between zeros and coefficients",35,"For ax²+bx+c, use sum = −b/a and product = c/a."),
    Topic("m5","Mathematics","Pair of Linear Equations","Graphical solution and consistency",35,"Understand intersecting, parallel and coincident lines."),
    Topic("m6","Mathematics","Pair of Linear Equations","Substitution and elimination",35,"Solve pairs systematically and verify the solution."),
    Topic("m7","Mathematics","Pair of Linear Equations","Situational problems",35,"Translate word problems into two linear equations."),
    Topic("m8","Mathematics","Quadratic Equations","Standard form and factorisation",35,"Convert to ax²+bx+c=0 and factor where possible."),
    Topic("m9","Mathematics","Quadratic Equations","Quadratic formula",30,"Use x = (−b ± √(b²−4ac))/2a carefully."),
    Topic("m10","Mathematics","Quadratic Equations","Discriminant and nature of roots",30,"D=b²−4ac; compare D with zero."),
    Topic("m11","Mathematics","Arithmetic Progressions","General term",30,"Use aₙ=a+(n−1)d and identify a,d,n."),
    Topic("m12","Mathematics","Arithmetic Progressions","Sum of n terms",35,"Use Sₙ=n/2[2a+(n−1)d]."),
    Topic("m13","Mathematics","Coordinate Geometry","Distance formula",30,"Apply √((x₂−x₁)²+(y₂−y₁)²)."),
    Topic("m14","Mathematics","Coordinate Geometry","Section formula",30,"Find internal division coordinates."),
    Topic("m15","Mathematics","Triangles","Similarity criteria",35,"AA, SSS and SAS similarity; identify corresponding sides."),
    Topic("m16","Mathematics","Triangles","Basic Proportionality Theorem",30,"Use proportional segments created by a line parallel to one side."),
    Topic("m17","Mathematics","Circles","Tangent theorem",30,"Radius is perpendicular to tangent at point of contact."),
    Topic("m18","Mathematics","Circles","Alternate segment theorem",30,"Relate tangent-chord angle to angle in alternate segment."),
    Topic("m19","Mathematics","Introduction to Trigonometry","Trigonometric ratios",35,"sin, cos and tan using right triangles."),
    Topic("m20","Mathematics","Trigonometry","Identities",35,"Learn and transform standard identities."),
    Topic("m21","Mathematics","Heights and Distances","Angle of elevation and depression",35,"Draw a labelled diagram before forming the ratio."),
    Topic("m22","Mathematics","Areas Related to Circles","Sector and segment",30,"Use arc, sector and segment formulae with correct units."),
    Topic("m23","Mathematics","Surface Areas and Volumes","Cylinder, cone and sphere",40,"Choose the correct surface-area or volume formula."),
    Topic("m24","Mathematics","Statistics","Mean, median and mode",40,"Calculate and interpret grouped-data measures."),
    Topic("m25","Mathematics","Probability","Classical probability",30,"Probability = favourable outcomes / total equally likely outcomes."),

    Topic("s1","Science","Chemical Reactions and Equations","Writing and balancing equations",35,"Identify reactants/products and balance atoms on both sides."),
    Topic("s2","Science","Chemical Reactions and Equations","Types of reactions",30,"Combination, decomposition, displacement, double displacement and redox."),
    Topic("s3","Science","Chemical Reactions and Equations","Oxidation and reduction",25,"Track gain/loss of oxygen, hydrogen or electrons conceptually."),
    Topic("s4","Science","Acids, Bases and Salts","Indicators and pH",30,"Use indicators and understand the pH scale."),
    Topic("s5","Science","Acids, Bases and Salts","Common salts",30,"Baking soda, washing soda, bleaching powder and plaster of Paris."),
    Topic("s6","Science","Metals and Non-metals","Physical and chemical properties",35,"Compare properties and learn important exceptions."),
    Topic("s7","Science","Metals and Non-metals","Reactivity series",35,"Arrange metals and predict displacement reactions."),
    Topic("s8","Science","Carbon and its Compounds","Covalent bonding",35,"Carbon valency, tetravalency and catenation."),
    Topic("s9","Science","Carbon and its Compounds","Homologous series and nomenclature",40,"Recognise functional groups and simple IUPAC naming."),
    Topic("s10","Science","Life Processes","Nutrition",35,"Autotrophic and heterotrophic nutrition; human digestion."),
    Topic("s11","Science","Life Processes","Respiration",35,"Aerobic/anaerobic respiration and human respiratory system."),
    Topic("s12","Science","Life Processes","Transportation and excretion",40,"Heart, blood vessels, plants' transport and kidney function."),
    Topic("s13","Science","Control and Coordination","Nervous system",35,"Neuron, reflex arc, brain and coordination."),
    Topic("s14","Science","Control and Coordination","Plant hormones and tropisms",30,"Tropisms and basic plant growth responses."),
    Topic("s15","Science","How do Organisms Reproduce?","Asexual and sexual reproduction",40,"Compare reproduction methods and biological advantages."),
    Topic("s16","Science","Heredity","Mendel and inheritance",40,"Traits, genes, dominant/recessive characters and crosses."),
    Topic("s17","Science","Heredity","Sex determination",25,"Understand XX/XY inheritance and avoid common misconceptions."),
    Topic("s18","Science","Light – Reflection and Refraction","Mirror formula",40,"Use sign convention, mirror formula and magnification."),
    Topic("s19","Science","Light – Reflection and Refraction","Refraction and lens formula",40,"Use lens formula, power and ray diagrams."),
    Topic("s20","Science","The Human Eye and the Colourful World","Defects of vision",35,"Myopia, hypermetropia and correction with lenses."),
    Topic("s21","Science","Electricity","Ohm's law and resistance",40,"V=IR; resistance depends on material, length and area."),
    Topic("s22","Science","Electricity","Series and parallel circuits",35,"Calculate equivalent resistance and current/voltage relationships."),
    Topic("s23","Science","Magnetic Effects of Electric Current","Magnetic field and Fleming rules",35,"Field lines, current-carrying conductors and force direction."),
    Topic("s24","Science","Our Environment","Food chains and energy flow",30,"Understand trophic levels and the ten-percent energy transfer idea."),

    Topic("ss1","Social Science","The Rise of Nationalism in Europe","French Revolution and nationalism",35,"Trace how revolutionary ideas contributed to nationalism."),
    Topic("ss2","Social Science","The Rise of Nationalism in Europe","Unification of Germany and Italy",40,"Sequence major events and leaders without mixing dates."),
    Topic("ss3","Social Science","Nationalism in India","Non-Cooperation Movement",35,"Causes, programme, participation and withdrawal."),
    Topic("ss4","Social Science","Nationalism in India","Civil Disobedience Movement",40,"Salt March, spread, participation and limitations."),
    Topic("ss5","Social Science","The Making of a Global World","Pre-modern trade and migration",30,"Understand long-distance links before industrialisation."),
    Topic("ss6","Social Science","The Age of Industrialisation","Industrialisation and workers",35,"Factories, hand labour, machines and working conditions."),
    Topic("ss7","Social Science","Print Culture and the Modern World","Print revolution",35,"Gutenberg, print expansion and social effects."),
    Topic("ss8","Social Science","Resources and Development","Resource planning",30,"Classification, planning and sustainable development."),
    Topic("ss9","Social Science","Forest and Wildlife Resources","Conservation",30,"Biodiversity, protected areas and community conservation."),
    Topic("ss10","Social Science","Water Resources","Multipurpose projects",35,"Dams, benefits, conflicts and rainwater harvesting."),
    Topic("ss11","Social Science","Agriculture","Types of farming and crops",40,"Major crops, conditions and cropping patterns."),
    Topic("ss12","Social Science","Minerals and Energy Resources","Minerals and energy",40,"Ferrous/non-ferrous minerals and conventional/non-conventional energy."),
    Topic("ss13","Social Science","Manufacturing Industries","Industrial location and pollution",40,"Factors of location and environmental impacts."),
    Topic("ss14","Social Science","Lifelines of National Economy","Transport and communication",35,"Roadways, railways, ports, communication and trade."),
    Topic("ss15","Social Science","Power Sharing","Forms and case studies",35,"Belgium and Sri Lanka; prudential and moral reasons."),
    Topic("ss16","Social Science","Federalism","Features and Indian federalism",40,"Levels of government, language policy and decentralisation."),
    Topic("ss17","Social Science","Gender, Religion and Caste","Social differences",35,"Gender division, communalism and caste inequalities."),
    Topic("ss18","Social Science","Political Parties","Functions and challenges",35,"Roles of parties, major challenges and reform measures."),
    Topic("ss19","Social Science","Outcomes of Democracy","Accountable government",30,"Assess democracy through accountability, responsiveness and legitimacy."),
    Topic("ss20","Social Science","Development","Income and other criteria",30,"Compare development using income, health, education and sustainability."),
    Topic("ss21","Social Science","Sectors of the Indian Economy","Organised and unorganised sectors",35,"Primary/secondary/tertiary and employment patterns."),
    Topic("ss22","Social Science","Money and Credit","Formal and informal credit",35,"Money functions, banks, credit situations and SHGs."),
    Topic("ss23","Social Science","Globalisation and the Indian Economy","MNCs and globalisation",35,"Production across countries, trade and impacts on producers."),
    Topic("ss24","Social Science","Consumer Rights","Consumer protection",30,"Consumer rights, responsibilities and redressal."),

    Topic("e1","English","Reading Skills","Discursive passage",30,"Read for main idea, inference, vocabulary and evidence."),
    Topic("e2","English","Grammar","Tenses and subject-verb agreement",30,"Identify tense and match subject with verb."),
    Topic("e3","English","Grammar","Modals, reported speech and determiners",40,"Revise rules through short transformation questions."),
    Topic("e4","English","Writing","Formal letter",35,"Follow format, purpose, tone, organisation and word limit."),
    Topic("e5","English","Writing","Analytical paragraph",35,"Select data, compare key features and avoid unsupported opinions."),
    Topic("e6","English","First Flight","A Letter to God",30,"Plot, faith, irony, characters and important textual details."),
    Topic("e7","English","First Flight","Nelson Mandela: Long Walk to Freedom",30,"Apartheid, freedom, courage and inauguration context."),
    Topic("e8","English","First Flight","Two Stories about Flying",35,"Fear, courage, mystery and narrative structure."),
    Topic("e9","English","First Flight","From the Diary of Anne Frank",30,"Diary form, school life and Anne's self-reflection."),
    Topic("e10","English","First Flight","Glimpses of India",35,"Goa, Coorg and Assam; culture, geography and livelihoods."),
    Topic("e11","English","First Flight","Mijbil the Otter",30,"Narrative voice, relationship and humour."),
    Topic("e12","English","First Flight","Madam Rides the Bus",30,"Valli's curiosity, independence and changing perspective."),
    Topic("e13","English","First Flight","The Sermon at Benares",30,"Kisa Gotami, suffering and acceptance."),
    Topic("e14","English","First Flight","The Proposal",35,"Comedy, conflict, characters and dramatic irony."),
    Topic("e15","English","Footprints Without Feet","A Triumph of Surgery",25,"Characterisation, humour and transformation."),
    Topic("e16","English","Footprints Without Feet","The Thief's Story",25,"Trust, education and moral change."),
    Topic("e17","English","Footprints Without Feet","The Midnight Visitor",25,"Intelligence, deception and suspense."),
    Topic("e18","English","Footprints Without Feet","A Question of Trust",25,"Trust, deception and irony."),
    Topic("e19","English","Footprints Without Feet","Footprints without Feet",30,"Scientific misuse, invisibility and consequences."),
    Topic("e20","English","Footprints Without Feet","The Making of a Scientist",30,"Curiosity, discipline and scientific development."),
    Topic("e21","English","Footprints Without Feet","The Necklace",30,"Appearance, ambition, irony and consequences."),
    Topic("e22","English","Footprints Without Feet","Bholi",30,"Education, dignity and self-confidence."),
    Topic("e23","English","Footprints Without Feet","The Book That Saved the Earth",30,"Drama, humour and misunderstanding."),

    Topic("h1","Hindi B","स्पर्श","बड़े भाई साहब",30,"भाईचारा, शिक्षा और जीवन-अनुभव के आधार पर पाठ की समझ।"),
    Topic("h2","Hindi B","स्पर्श","डायरी का एक पन्ना",30,"डायरी शैली, स्वतंत्रता आंदोलन और भावात्मक विवरण।"),
    Topic("h3","Hindi B","स्पर्श","तताँरा-वामीरो कथा",30,"लोककथा, प्रेम, परंपरा और सामाजिक मान्यताएँ।"),
    Topic("h4","Hindi B","स्पर्श","तीसरी कसम के शिल्पकार शैलेंद्र",30,"व्यक्तित्व, रचनात्मकता और संवेदनशीलता।"),
    Topic("h5","Hindi B","स्पर्श","अब कहाँ दूसरे के दुख से दुखी होने वाले",30,"मानवीय संवेदना और बदलते सामाजिक व्यवहार।"),
    Topic("h6","Hindi B","स्पर्श","कारतूस",30,"वीरता, स्वाभिमान और औपनिवेशिक संदर्भ।"),
    Topic("h7","Hindi B","संचयन","हरिहर काका",35,"संपत्ति, परिवार, धर्म और सामाजिक दबाव।"),
    Topic("h8","Hindi B","संचयन","टोपी शुक्ला",35,"मित्रता, सामाजिक पूर्वाग्रह और मानवीय संबंध।"),
    Topic("h9","Hindi B","संचयन","सपनों के-से दिन",30,"बचपन, विद्यालय और स्मृतियों का चित्रण।"),
    Topic("h10","Hindi B","व्याकरण","वाक्य रूपांतरण",30,"सरल, संयुक्त और मिश्र वाक्यों का अभ्यास।"),
    Topic("h11","Hindi B","व्याकरण","समास",30,"समास की पहचान और विग्रह का अभ्यास।"),
    Topic("h12","Hindi B","व्याकरण","मुहावरे और लोकोक्तियाँ",25,"अर्थ, प्रयोग और संदर्भ सहित याद करें।"),
    Topic("h13","Hindi B","लेखन","अनुच्छेद लेखन",30,"विषय, क्रम, भाषा और शब्द-सीमा पर ध्यान।"),
    Topic("h14","Hindi B","लेखन","औपचारिक पत्र",30,"प्रारूप, विषय, संबोधन और विनम्र भाषा।"),
    Topic("h15","Hindi B","कविता","कबीर की साखियाँ",30,"कथ्य, भावार्थ, भाषा और संदेश।"),
    Topic("h16","Hindi B","कविता","मनुष्यता",30,"मानवता, परोपकार और काव्य-संदेश।"),
    Topic("h17","Hindi B","कविता","कर चले हम फ़िदा",25,"देशभक्ति, बलिदान और भाव-व्यंजना।"),
    Topic("h18","Hindi B","कविता","आत्मत्राण",25,"आत्मबल, प्रार्थना और संघर्ष।")
)

private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

class MainActivity : ComponentActivity() {
    private val notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createChannel(this)
        if (android.os.Build.VERSION.SDK_INT >= 33 && checkSelfPermission("android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
            notificationPermission.launch("android.permission.POST_NOTIFICATIONS")
        }
        setContent { FortyFiveDaysApp() }
    }
}

@Composable
fun FortyFiveDaysApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val p = remember { prefs(context) }
    var setupDone by remember { mutableStateOf(p.getBoolean("setup", false)) }
    if (!setupDone) {
        SetupScreen { name, start, hours, end, exam, reminder ->
            p.edit().putBoolean("setup", true).putString("name", name).putString("start", start).putString("end", end).putInt("hours", hours).putString("exam", exam).putString("reminder", String.format(Locale.US,"%02d:%02d", reminder.hour, reminder.minute)).apply()
            scheduleReminder(context, reminder.hour, reminder.minute)
            setupDone = true
        }
    } else {
        MainShell()
    }
}

data class ReminderTime(val hour: Int, val minute: Int)

@Composable
private fun SetupScreen(onDone: (String,String,Int,String,String,ReminderTime)->Unit) {
    var name by remember { mutableStateOf("") }
    var start by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())) }
    var hours by remember { mutableStateOf("3") }
    var end by remember { mutableStateOf("22:00") }
    var exam by remember { mutableStateOf("") }
    var reminder by remember { mutableStateOf("18:00") }
    var error by remember { mutableStateOf("") }
    Surface(modifier=Modifier.fillMaxSize(), color=Color(0xFFF9F5FF)) {
        LazyColumn(contentPadding=PaddingValues(24.dp), verticalArrangement=Arrangement.spacedBy(14.dp)) {
            item {
                Text("45 DAYS", fontSize=40.sp, fontWeight=FontWeight.Black)
                Text("Class 10 CBSE • Offline Study Planner", fontSize=18.sp)
                Spacer(Modifier.height(8.dp))
                Text("Tell the app how you study. It will build a 45-day workload around your available time.")
            }
            item { OutlinedTextField(name,{name=it},label={Text("Your name")},modifier=Modifier.fillMaxWidth()) }
            item { OutlinedTextField(start,{start=it},label={Text("Start date (YYYY-MM-DD)")},modifier=Modifier.fillMaxWidth()) }
            item { OutlinedTextField(exam,{exam=it},label={Text("Board exam date (optional)")},modifier=Modifier.fillMaxWidth()) }
            item { OutlinedTextField(hours,{hours=it.filter(Char::isDigit)},label={Text("Study hours per day")},modifier=Modifier.fillMaxWidth()) }
            item { OutlinedTextField(end,{end=it},label={Text("Latest study time (HH:MM)")},modifier=Modifier.fillMaxWidth()) }
            item { OutlinedTextField(reminder,{reminder=it},label={Text("Daily reminder (HH:MM)")},modifier=Modifier.fillMaxWidth()) }
            item { Text("Subjects included: Mathematics, Science, Social Science, English and Hindi B. You can adjust subjects later.") }
            item {
                if(error.isNotEmpty()) Text(error,color=MaterialTheme.colorScheme.error)
                Button(onClick={
                    val h=hours.toIntOrNull() ?: 0
                    val parts=reminder.split(":")
                    val rh=parts.getOrNull(0)?.toIntOrNull(); val rm=parts.getOrNull(1)?.toIntOrNull()
                    if(name.isBlank() || h<=0 || rh==null || rm==null || rh !in 0..23 || rm !in 0..59) error="Enter a name, study hours and a valid reminder time."
                    else onDone(name,start,h,end,exam,ReminderTime(rh,rm))
                },modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(18.dp)) { Text("Create my 45-day plan") }
            }
        }
    }
}

@Composable
private fun MainShell() {
    var tab by remember { mutableIntStateOf(0) }
    val titles=listOf("Today","Plan","Syllabus","Progress")
    Scaffold(bottomBar={ NavigationBar { titles.forEachIndexed { i,t -> NavigationBarItem(selected=tab==i,onClick={tab=i},icon={Text(listOf("⌂","▦","▤","⌁")[i],fontSize=20.sp)},label={Text(t)}) } } }) { pad ->
        Box(Modifier.padding(pad).fillMaxSize()) {
            when(tab){0->TodayScreen();1->PlanScreen();2->SyllabusScreen();3->ProgressScreen()}
        }
    }
}

private fun currentDay(context: Context): Int {
    val start=prefs(context).getString("start","") ?: ""
    return try { val d=SimpleDateFormat("yyyy-MM-dd",Locale.US); val s=d.parse(start)!!; val n=Date(); ((n.time-s.time)/(24L*60*60*1000)).toInt()+1 } catch(_:Exception){1 }.coerceIn(1,45)
}

private fun tasksForDay(day:Int):List<StudyTask> {
    val count=allTopics.size
    val perDay=maxOf(3, kotlin.math.ceil(count/45.0).toInt())
    val start=((day-1)*perDay).coerceAtMost(count)
    val end=minOf(count,start+perDay)
    return allTopics.subList(start,end).map{StudyTask(it,day)}
}

@Composable
private fun TodayScreen() {
    val context=androidx.compose.ui.platform.LocalContext.current
    var refresh by remember { mutableIntStateOf(0) }
    val p=prefs(context); val day=currentDay(context); val name=p.getString("name","Student") ?: "Student"; val tasks=tasksForDay(day)
    val completed=tasks.count{p.getBoolean("done_${it.topic.id}",false)}
    LazyColumn(contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
        item {
            Text("Good ${if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)<12) "morning" else "evening"}, $name",fontSize=24.sp,fontWeight=FontWeight.Bold)
            Text("DAY $day / 45",fontSize=38.sp,fontWeight=FontWeight.Black)
            Text("Today's target • ${tasks.sumOf{it.topic.minutes}} minutes",fontSize=18.sp)
            LinearProgressIndicator(progress={if(tasks.isEmpty())0f else completed.toFloat()/tasks.size},modifier=Modifier.fillMaxWidth().height(10.dp))
        }
        item { Card(shape=RoundedCornerShape(24.dp),colors=CardDefaults.cardColors(containerColor=Color(0xFFEDE2FF))) { Column(Modifier.padding(18.dp)){Text("Today's thought",fontWeight=FontWeight.Bold,fontSize=18.sp);Text("Don't wait to feel ready. Start today's target, and let progress build confidence.",fontSize=17.sp)}}}
        item { Text("Today's study",fontSize=24.sp,fontWeight=FontWeight.Bold) }
        items(tasks){ task ->
            val done=p.getBoolean("done_${task.topic.id}",false)
            Card(shape=RoundedCornerShape(20.dp),modifier=Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp)){
                Text(task.topic.subject,fontWeight=FontWeight.Bold,color=Color(0xFF5F3AAE)); Text(task.topic.chapter,fontSize=19.sp,fontWeight=FontWeight.SemiBold); Text(task.topic.title,fontSize=18.sp); Text("${task.topic.minutes} min • ${task.topic.guide}",fontSize=14.sp)
                Spacer(Modifier.height(10.dp)); Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){ Button(onClick={p.edit().putBoolean("done_${task.topic.id}",!done).apply();refresh++}){Text(if(done)"Undo" else "Complete")}; OutlinedButton(onClick={}){Text("Guide")}}
            }}
        }
    }
}

@Composable
private fun PlanScreen() {
    val context=androidx.compose.ui.platform.LocalContext.current
    LazyColumn(contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){item{Text("45-Day Plan",fontSize=34.sp,fontWeight=FontWeight.Black);Text("Tap any day to see its exact topics and workload.")};items((1..45).toList()){d->val t=tasksForDay(d);val done=t.count{prefs(context).getBoolean("done_${it.topic.id}",false)};Card(modifier=Modifier.fillMaxWidth().clickable{},shape=RoundedCornerShape(18.dp)){Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){Text("Day $d",fontWeight=FontWeight.Bold,fontSize=20.sp,modifier=Modifier.width(80.dp));Column(Modifier.weight(1f)){Text("${t.size} topics • ${t.sumOf{it.topic.minutes}} min");Text(if(t.isEmpty())"Review / buffer day" else "$done/${t.size} completed")};Text(if(done==t.size && t.isNotEmpty())"✓" else "→",fontSize=24.sp)}}}}
}

@Composable
private fun SyllabusScreen() {
    var selected by remember { mutableStateOf<String?>(null) }
    val subjects=allTopics.groupBy{it.subject}
    LazyColumn(contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){item{Text("Offline Syllabus",fontSize=34.sp,fontWeight=FontWeight.Black);Text("Core syllabus and topic guides are stored locally. No internet is required after installation.")};items(subjects.entries.toList()){entry->Card(shape=RoundedCornerShape(20.dp)){Column(Modifier.padding(18.dp)){Text(entry.key,fontSize=22.sp,fontWeight=FontWeight.Bold);Text("${entry.value.size} study topics");entry.value.groupBy{it.chapter}.forEach{(chapter,ts)->Text("• $chapter — ${ts.size} topics",fontWeight=FontWeight.SemiBold);ts.take(4).forEach{t->Row(Modifier.fillMaxWidth().clickable{selected=t.id}.padding(vertical=3.dp)){Text("  ${t.title}",modifier=Modifier.weight(1f));Text("${t.minutes}m")}}}}}}
        selected?.let{id->allTopics.find{it.id==id}?.let{t->item{AlertDialog(onDismissRequest={selected=null},confirmButton={TextButton(onClick={selected=null}){Text("Close")}},title={Text(t.title)},text={Text("${t.subject}\n${t.chapter}\n\n${t.guide}\n\nSuggested method: Learn → write key points → solve 3–5 questions → mark for revision.")})}}}
    }
}

@Composable
private fun ProgressScreen(){val context=androidx.compose.ui.platform.LocalContext.current;val done=allTopics.count{prefs(context).getBoolean("done_${it.id}",false)};val pct=done.toFloat()/allTopics.size;LazyColumn(contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){item{Text("Progress",fontSize=36.sp,fontWeight=FontWeight.Black);Text("${(pct*100).toInt()}% of loaded topics completed",fontSize=25.sp);LinearProgressIndicator(progress={pct},modifier=Modifier.fillMaxWidth().height(12.dp));Text("$done topics completed");Text("${allTopics.size} topics in the local syllabus dataset")};allTopics.groupBy{it.subject}.forEach{(s,ts)->item{val d=ts.count{prefs(context).getBoolean("done_${it.id}",false)};Card{Column(Modifier.padding(16.dp)){Text(s,fontSize=21.sp,fontWeight=FontWeight.Bold);Text("$d / ${ts.size} completed");LinearProgressIndicator(progress={d.toFloat()/ts.size},modifier=Modifier.fillMaxWidth())}}}}}}

private fun createChannel(context: Context){val nm=context.getSystemService(NotificationManager::class.java);nm.createNotificationChannel(NotificationChannel(CHANNEL_ID,"Study reminders",NotificationManager.IMPORTANCE_HIGH).apply{description="Daily study reminders"})}
private fun scheduleReminder(context: Context,hour:Int,minute:Int){val am=context.getSystemService(AlarmManager::class.java);val intent=Intent(context,ReminderReceiver::class.java);val pi=PendingIntent.getBroadcast(context,45,intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE);val cal=Calendar.getInstance().apply{set(Calendar.HOUR_OF_DAY,hour);set(Calendar.MINUTE,minute);set(Calendar.SECOND,0);if(timeInMillis<=System.currentTimeMillis())add(Calendar.DAY_OF_YEAR,1)};if(android.os.Build.VERSION.SDK_INT>=31 && am.canScheduleExactAlarms())am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,cal.timeInMillis,pi) else am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,cal.timeInMillis,pi)}

class ReminderReceiver:BroadcastReceiver(){override fun onReceive(context:Context,intent:Intent?){val nm=context.getSystemService(NotificationManager::class.java);val pi=PendingIntent.getActivity(context,46,Intent(context,MainActivity::class.java),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE);val n=androidx.core.app.NotificationCompat.Builder(context,CHANNEL_ID).setSmallIcon(android.R.drawable.ic_popup_reminder).setContentTitle("45 DAYS • Study time").setContentText("Open today's plan and complete your next topic.").setContentIntent(pi).setAutoCancel(true).build();nm.notify(45,n);val p=prefs(context);val time=p.getString("reminder","18:00")?:"18:00";val a=time.split(":");if(a.size==2)scheduleReminder(context,a[0].toIntOrNull()?:18,a[1].toIntOrNull()?:0)}}
