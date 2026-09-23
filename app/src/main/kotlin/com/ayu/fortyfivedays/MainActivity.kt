package com.ayu.fortyfivedays

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import java.text.SimpleDateFormat
import java.util.*

data class Topic(val title: String, val minutes: Int, val guide: String = "")
data class Chapter(val title: String, val topics: List<Topic>, val difficulty: Int = 2)
data class Subject(val name: String, val chapters: List<Chapter>)

private val syllabus = listOf(
    Subject("Mathematics", listOf(
        Chapter("Real Numbers", listOf(
            Topic("Fundamental Theorem of Arithmetic", 25, "Review prime factorisation and state/apply the Fundamental Theorem of Arithmetic."),
            Topic("Irrationality proofs", 30, "Practise proofs of irrationality for √2, √3 and √5.")
        )),
        Chapter("Polynomials", listOf(
            Topic("Zeros of a polynomial", 25, "Understand zeros graphically and algebraically."),
            Topic("Relationship between zeros and coefficients", 30, "For quadratic polynomials, connect zeros with coefficients.")
        )),
        Chapter("Pair of Linear Equations in Two Variables", listOf(
            Topic("Graphical solution and consistency", 30),
            Topic("Substitution and elimination", 35),
            Topic("Situational problems", 30)
        )),
        Chapter("Quadratic Equations", listOf(
            Topic("Standard form and factorisation", 30),
            Topic("Quadratic formula", 30),
            Topic("Discriminant and nature of roots", 25),
            Topic("Situational problems", 30)
        )),
        Chapter("Arithmetic Progressions", listOf(
            Topic("nth term of an AP", 30),
            Topic("Sum of first n terms", 35),
            Topic("Applications", 30)
        )),
        Chapter("Coordinate Geometry", listOf(
            Topic("Distance formula", 30),
            Topic("Section formula", 30)
        )),
        Chapter("Triangles", listOf(
            Topic("Similar triangles", 30),
            Topic("Basic Proportionality Theorem", 35),
            Topic("Similarity criteria", 35)
        )),
        Chapter("Circles", listOf(
            Topic("Tangent and radius theorem", 30),
            Topic("Tangents from an external point", 30),
            Topic("Applications", 25)
        )),
        Chapter("Introduction to Trigonometry", listOf(
            Topic("Trigonometric ratios", 30),
            Topic("Standard angles", 25),
            Topic("Relationships between ratios", 25)
        )),
        Chapter("Trigonometric Identities", listOf(
            Topic("sin²A + cos²A = 1", 30),
            Topic("Simple identities", 30)
        )),
        Chapter("Heights and Distances", listOf(
            Topic("Angle of elevation", 30),
            Topic("Angle of depression", 30),
            Topic("Applications", 30)
        )),
        Chapter("Areas Related to Circles", listOf(
            Topic("Sectors", 30),
            Topic("Segments", 30),
            Topic("Area and perimeter problems", 30)
        )),
        Chapter("Surface Areas and Volumes", listOf(
            Topic("Surface area and volume", 35),
            Topic("Combination of solids", 40)
        )),
        Chapter("Statistics", listOf(
            Topic("Mean of grouped data", 35),
            Topic("Median and mode", 35)
        )),
        Chapter("Probability", listOf(
            Topic("Classical probability", 25),
            Topic("Simple probability problems", 30)
        ))
    )),
    Subject("Science", listOf(
        Chapter("Chemical Reactions and Equations", listOf(
            Topic("Chemical equations and balancing", 30),
            Topic("Types of reactions", 35),
            Topic("Oxidation and reduction", 30)
        )),
        Chapter("Acids, Bases and Salts", listOf(
            Topic("Properties and indicators", 30),
            Topic("pH and neutralisation", 30),
            Topic("Important salts and uses", 35)
        )),
        Chapter("Metals and Non-metals", listOf(
            Topic("Properties and reactivity series", 30),
            Topic("Ionic compounds and metallurgy", 35),
            Topic("Corrosion and prevention", 25)
        )),
        Chapter("Carbon and its Compounds", listOf(
            Topic("Covalent bonding", 30),
            Topic("Hydrocarbons and nomenclature", 35),
            Topic("Chemical properties", 35),
            Topic("Ethanol, ethanoic acid, soaps and detergents", 35)
        )),
        Chapter("Life Processes", listOf(
            Topic("Nutrition", 30),
            Topic("Respiration", 30),
            Topic("Transportation", 30),
            Topic("Excretion", 30)
        )),
        Chapter("Control and Coordination", listOf(
            Topic("Nervous system", 30),
            Topic("Hormonal coordination", 30),
            Topic("Plant coordination", 25)
        )),
        Chapter("How do Organisms Reproduce?", listOf(
            Topic("Asexual reproduction", 25),
            Topic("Sexual reproduction", 35),
            Topic("Reproductive health", 25)
        )),
        Chapter("Heredity and Evolution", listOf(
            Topic("Mendel's laws", 35),
            Topic("Inheritance", 30),
            Topic("Evolution", 30)
        )),
        Chapter("Light – Reflection and Refraction", listOf(
            Topic("Reflection", 30),
            Topic("Refraction", 35),
            Topic("Lenses and ray diagrams", 35)
        )),
        Chapter("Human Eye and the Colourful World", listOf(
            Topic("Human eye", 30),
            Topic("Defects and correction", 30),
            Topic("Dispersion and atmospheric effects", 25)
        )),
        Chapter("Electricity", listOf(
            Topic("Current, potential difference and resistance", 30),
            Topic("Ohm's law", 25),
            Topic("Series and parallel circuits", 35),
            Topic("Power and energy", 30)
        )),
        Chapter("Magnetic Effects of Electric Current", listOf(
            Topic("Magnetic field", 30),
            Topic("Force on current-carrying conductor", 30),
            Topic("Electromagnetic induction", 30)
        )),
        Chapter("Our Environment", listOf(
            Topic("Ecosystem", 25),
            Topic("Food chains and webs", 25),
            Topic("Environmental management", 25)
        ))
    )),
    Subject("Social Science", listOf(
        Chapter("The Rise of Nationalism in Europe", listOf(Topic("French Revolution and nationalism", 30), Topic("Nation states and movements", 35), Topic("First World War context", 30))),
        Chapter("Nationalism in India", listOf(Topic("First World War and Khilafat", 30), Topic("Non-Cooperation and Civil Disobedience", 35), Topic("Collective belonging", 30))),
        Chapter("The Making of a Global World", listOf(Topic("Pre-modern world", 30), Topic("19th century", 30), Topic("Inter-war economy", 30), Topic("Post-war rebuilding", 30))),
        Chapter("The Age of Industrialisation", listOf(Topic("Pre-industrialisation", 30), Topic("Industrialisation and markets", 35), Topic("Industrialisation in India", 35))),
        Chapter("Print Culture and the Modern World", listOf(Topic("Print in East Asia", 25), Topic("Print revolution in Europe", 30), Topic("Print and India", 30))),
        Chapter("Resources and Development", listOf(Topic("Resource planning", 30), Topic("Land resources and soils", 30))),
        Chapter("Forest and Wildlife Resources", listOf(Topic("Biodiversity", 30), Topic("Conservation", 30))),
        Chapter("Water Resources", listOf(Topic("Water scarcity", 30), Topic("Multipurpose projects", 30), Topic("Rainwater harvesting", 25))),
        Chapter("Agriculture", listOf(Topic("Types of farming", 30), Topic("Major crops", 35), Topic("Technological and institutional reforms", 30))),
        Chapter("Minerals and Energy Resources", listOf(Topic("Mineral resources", 30), Topic("Energy resources", 30), Topic("Conservation", 25))),
        Chapter("Manufacturing Industries", listOf(Topic("Industrial locations", 30), Topic("Major industries", 35), Topic("Industrial pollution", 25))),
        Chapter("Lifelines of National Economy", listOf(Topic("Transport", 30), Topic("Communication and trade", 30), Topic("Map work", 30))),
        Chapter("Power-sharing", listOf(Topic("Forms of power sharing", 30), Topic("Belgium and Sri Lanka", 30))),
        Chapter("Federalism", listOf(Topic("Features", 30), Topic("Indian federalism", 35))),
        Chapter("Gender, Religion and Caste", listOf(Topic("Gender and politics", 25), Topic("Religion and politics", 25), Topic("Caste and politics", 25))),
        Chapter("Political Parties", listOf(Topic("Functions", 30), Topic("Challenges and reforms", 30))),
        Chapter("Outcomes of Democracy", listOf(Topic("Accountable government", 30), Topic("Economic growth and inequality", 30))),
        Chapter("Development", listOf(Topic("Different goals", 25), Topic("Income and other criteria", 30), Topic("Sustainable development", 25))),
        Chapter("Sectors of the Indian Economy", listOf(Topic("Primary, secondary and tertiary sectors", 30), Topic("Organised and unorganised sectors", 30), Topic("Public and private sectors", 25))),
        Chapter("Money and Credit", listOf(Topic("Money as a medium of exchange", 25), Topic("Formal and informal credit", 30), Topic("Self-help groups", 25))),
        Chapter("Globalisation and the Indian Economy", listOf(Topic("Meaning and drivers", 30), Topic("Production across countries", 30), Topic("WTO and fair globalisation", 30))),
        Chapter("Consumer Rights", listOf(Topic("Consumer awareness and protection", 30)))
    )),
    Subject("English", listOf(
        Chapter("First Flight – Prose", listOf(
            Topic("A Letter to God", 30), Topic("Nelson Mandela – Long Walk to Freedom", 30),
            Topic("Stories About Flying", 30), Topic("From the Diary of Anne Frank", 30),
            Topic("Glimpses of India", 35), Topic("Mijbil the Otter", 30),
            Topic("Madam Rides the Bus", 30), Topic("The Sermon at Benares", 30), Topic("The Proposal", 35)
        )),
        Chapter("First Flight – Poems", listOf(
            Topic("Dust of Snow", 20), Topic("Fire and Ice", 20), Topic("A Tiger in the Zoo", 20),
            Topic("How to Tell Wild Animals", 20), Topic("The Ball Poem", 20), Topic("Amanda!", 20),
            Topic("The Trees", 20), Topic("Fog", 20), Topic("The Tale of Custard the Dragon", 20), Topic("For Anne Gregory", 20)
        )),
        Chapter("Footprints Without Feet", listOf(
            Topic("A Triumph of Surgery", 25), Topic("The Thief's Story", 25), Topic("The Midnight Visitor", 25),
            Topic("A Question of Trust", 25), Topic("Footprints Without Feet", 25), Topic("The Making of a Scientist", 25),
            Topic("The Necklace", 25), Topic("Bholi", 25), Topic("The Book that Saved the Earth", 25)
        )),
        Chapter("Grammar and Writing", listOf(
            Topic("Determiners, Tenses and Modals", 30), Topic("Subject–verb concord", 25),
            Topic("Reported speech", 30), Topic("Formal letter", 30), Topic("Analytical paragraph", 30)
        ))
    )),
    Subject("Hindi", listOf(
        Chapter("Hindi Course – A", listOf(
            Topic("अपठित बोध", 30), Topic("व्यावहारिक व्याकरण", 35),
            Topic("पाठ्यपुस्तक एवं पूरक पाठ्यपुस्तक", 60), Topic("रचनात्मक लेखन", 40)
        )),
        Chapter("Hindi Course – B", listOf(
            Topic("अपठित बोध", 30), Topic("व्यावहारिक व्याकरण", 35),
            Topic("पाठ्यपुस्तक एवं पूरक पाठ्यपुस्तक", 60), Topic("रचनात्मक लेखन", 40)
        ))
    ))
)

data class PlanItem(val subject: String, val chapter: String, val topic: String, val minutes: Int)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)
        setContent { App() }
    }
}

@Composable
fun App() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("45days", Context.MODE_PRIVATE) }
    var setupDone by remember { mutableStateOf(prefs.getBoolean("setup", false)) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var day by remember { mutableIntStateOf(prefs.getInt("day", 1)) }
    var completed by remember { mutableStateOf(prefs.getStringSet("done", emptySet()) ?: emptySet()) }

    MaterialTheme {
        if (!setupDone) {
            SetupScreen { duration, hours ->
                prefs.edit().putBoolean("setup", true).putInt("duration", duration).putFloat("hours", hours).apply()
                setupDone = true
            }
        } else {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        listOf(
                            "Today" to Icons.Default.Home,
                            "Plan" to Icons.Default.CalendarMonth,
                            "Syllabus" to Icons.Default.MenuBook,
                            "Progress" to Icons.Default.Insights
                        ).forEachIndexed { i, pair ->
                            NavigationBarItem(
                                selected = selectedTab == i,
                                onClick = { selectedTab = i },
                                icon = { Icon(pair.second, null) },
                                label = { Text(pair.first) }
                            )
                        }
                    }
                }
            ) { padding ->
                when (selectedTab) {
                    0 -> TodayScreen(day, completed) { id ->
                        val new = completed + id
                        completed = new
                        prefs.edit().putStringSet("done", new).apply()
                    }
                    1 -> PlanScreen()
                    2 -> SyllabusScreen()
                    else -> ProgressScreen(completed)
                }
            }
        }
    }
}

@Composable
fun SetupScreen(onDone: (Int, Float) -> Unit) {
    var duration by remember { mutableStateOf("45") }
    var hours by remember { mutableStateOf("4") }
    var examDate by remember { mutableStateOf("") }
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("45 DAYS", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
        Text("Class 10 CBSE preparation planner", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(duration, { duration = it }, label = { Text("Preparation days") }, singleLine = true)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(hours, { hours = it }, label = { Text("Available study hours/day") }, singleLine = true)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(examDate, { examDate = it }, label = { Text("Exam date (DD/MM/YYYY)") }, singleLine = true)
        Spacer(Modifier.height(20.dp))
        Text("Your plan will use your available time, include revision, and recover missed tasks.")
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                onDone(duration.toIntOrNull()?.coerceIn(1, 365) ?: 45, hours.toFloatOrNull()?.coerceIn(0.5f, 16f) ?: 4f)
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("CREATE MY PLAN") }
    }
}

@Composable
fun TodayScreen(day: Int, completed: Set<String>, onComplete: (String) -> Unit) {
    val items = remember(day) { makePlan(day) }
    val doneCount = items.count { completed.contains(keyOf(it)) }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("DAY $day / 45", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("${items.sumOf { it.minutes }} minutes planned")
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { if (items.isEmpty()) 0f else doneCount.toFloat() / items.size },
                Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))
            Text("Today's thought", fontWeight = FontWeight.Bold)
            Text("Don't wait to feel ready. Start today's target, and let progress build confidence.")
            Spacer(Modifier.height(20.dp))
        }
        items(items) { item ->
            val id = keyOf(item)
            Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(item.subject, fontWeight = FontWeight.Bold)
                    Text(item.chapter)
                    Text(item.topic)
                    Text("${item.minutes} min")
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { onComplete(id) },
                        enabled = !completed.contains(id)
                    ) { Text(if (completed.contains(id)) "COMPLETED" else "MARK COMPLETE") }
                }
            }
        }
    }
}

@Composable
fun PlanScreen() {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("45-Day Calendar", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Tap a day in the final version to edit and recover its workload.")
            Spacer(Modifier.height(12.dp))
        }
        items((1..45).toList()) { d ->
            ListItem(
                headlineContent = { Text("Day $d") },
                supportingContent = { Text("Study + revision + recovery-aware plan") },
                leadingContent = { Icon(Icons.Default.CalendarMonth, null) }
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun SyllabusScreen() {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item { Text("Offline Syllabus", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        syllabus.forEach { subject ->
            item {
                Spacer(Modifier.height(14.dp))
                Text(subject.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            items(subject.chapters) { chapter ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(14.dp)) {
                        Text(chapter.title, fontWeight = FontWeight.Bold)
                        chapter.topics.forEach { Text("• ${it.title}") }
                    }
                }
            }
        }
    }
}

@Composable
fun ProgressScreen(completed: Set<String>) {
    val total = syllabus.sumOf { s -> s.chapters.sumOf { it.topics.size } }
    val pct = if (total == 0) 0 else (completed.size * 100 / total)
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Progress", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))
        Text("$pct% of loaded topics completed", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        LinearProgressIndicator(progress = { pct / 100f }, Modifier.fillMaxWidth())
        Spacer(Modifier.height(20.dp))
        Text("${completed.size} topics completed")
        Text("$total topics in the local syllabus dataset")
    }
}

private fun makePlan(day: Int): List<PlanItem> {
    val flat = syllabus.flatMap { s -> s.chapters.flatMap { c -> c.topics.map { t -> PlanItem(s.name, c.title, t.title, t.minutes) } } }
    if (flat.isEmpty()) return emptyList()
    val start = ((day - 1) * 4) % flat.size
    val chosen = (0 until minOf(5, flat.size)).map { flat[(start + it) % flat.size] }
    return chosen
}

private fun keyOf(x: PlanItem) = "${x.subject}|${x.chapter}|${x.topic}"


private fun createNotificationChannel(context: Context) {
    val channel = NotificationChannel("study", "Study reminders", NotificationManager.IMPORTANCE_HIGH)
    context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
}
