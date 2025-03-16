package com.muapp.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet

import kotlin.math.roundToInt
import androidx.compose.foundation.lazy.LazyRow

// Data models for Results
enum class ExamType {
    MIDTERM, FINAL, ASSIGNMENT, QUIZ, PROJECT
}

enum class Semester {
    FALL_2022, SPRING_2023, FALL_2023, SPRING_2024
}

data class ExamResult(
    val id: String,
    val subjectId: String,
    val subjectName: String,
    val examType: ExamType,
    val semester: Semester,
    val totalMarks: Float,
    val obtainedMarks: Float,
    val examDate: String,
    val color: Color,
    val icon: ImageVector
)

// Calculate grade and percentage
fun calculateGrade(percentage: Float): String {
    return when {
        percentage >= 90 -> "A+"
        percentage >= 85 -> "A"
        percentage >= 80 -> "A-"
        percentage >= 75 -> "B+"
        percentage >= 70 -> "B"
        percentage >= 65 -> "B-"
        percentage >= 60 -> "C+"
        percentage >= 55 -> "C"
        percentage >= 50 -> "C-"
        percentage >= 45 -> "D+"
        percentage >= 40 -> "D"
        else -> "F"
    }
}

// Mock data for testing
val mockResults = listOf(
    ExamResult(
        id = "EX001",
        subjectId = "CS101",
        subjectName = "Introduction to Computer Science",
        examType = ExamType.MIDTERM,
        semester = Semester.FALL_2023,
        totalMarks = 100f,
        obtainedMarks = 87f,
        examDate = "2023-10-15",
        color = Color(0xFF5D8CAE),
        icon = Icons.Default.Computer
    ),
    ExamResult(
        id = "EX002",
        subjectId = "CS101",
        subjectName = "Introduction to Computer Science",
        examType = ExamType.FINAL,
        semester = Semester.FALL_2023,
        totalMarks = 100f,
        obtainedMarks = 92f,
        examDate = "2023-12-20",
        color = Color(0xFF5D8CAE),
        icon = Icons.Default.Computer
    ),
    ExamResult(
        id = "EX003",
        subjectId = "MATH202",
        subjectName = "Calculus II",
        examType = ExamType.MIDTERM,
        semester = Semester.FALL_2023,
        totalMarks = 100f,
        obtainedMarks = 76f,
        examDate = "2023-10-18",
        color = Color(0xFF7A9A7E),
        icon = Icons.Default.Calculate
    ),
    ExamResult(
        id = "EX004",
        subjectId = "MATH202",
        subjectName = "Calculus II",
        examType = ExamType.FINAL,
        semester = Semester.FALL_2023,
        totalMarks = 100f,
        obtainedMarks = 81f,
        examDate = "2023-12-22",
        color = Color(0xFF7A9A7E),
        icon = Icons.Default.Calculate
    ),
    ExamResult(
        id = "EX005",
        subjectId = "PHYS101",
        subjectName = "Physics I",
        examType = ExamType.MIDTERM,
        semester = Semester.FALL_2023,
        totalMarks = 100f,
        obtainedMarks = 79f,
        examDate = "2023-10-20",
        color = Color(0xFFB87D4B),
        icon = Icons.Default.Science
    ),
    ExamResult(
        id = "EX006",
        subjectId = "PHYS101",
        subjectName = "Physics I",
        examType = ExamType.FINAL,
        semester = Semester.FALL_2023,
        totalMarks = 100f,
        obtainedMarks = 84f,
        examDate = "2023-12-23",
        color = Color(0xFFB87D4B),
        icon = Icons.Default.Science
    ),
    ExamResult(
        id = "EX007",
        subjectId = "ENG205",
        subjectName = "Technical Writing",
        examType = ExamType.ASSIGNMENT,
        semester = Semester.SPRING_2024,
        totalMarks = 50f,
        obtainedMarks = 45f,
        examDate = "2024-02-10",
        color = Color(0xFF9D7AB8),
        icon = Icons.Default.Book
    ),
    ExamResult(
        id = "EX008",
        subjectId = "ENG205",
        subjectName = "Technical Writing",
        examType = ExamType.MIDTERM,
        semester = Semester.SPRING_2024,
        totalMarks = 100f,
        obtainedMarks = 88f,
        examDate = "2024-03-15",
        color = Color(0xFF9D7AB8),
        icon = Icons.Default.Book
    ),
    ExamResult(
        id = "EX009",
        subjectId = "CS205",
        subjectName = "Data Structures",
        examType = ExamType.QUIZ,
        semester = Semester.SPRING_2024,
        totalMarks = 30f,
        obtainedMarks = 27f,
        examDate = "2024-02-05",
        color = Color(0xFFB85A5A),
        icon = Icons.Default.DataArray
    ),
    ExamResult(
        id = "EX010",
        subjectId = "CS205",
        subjectName = "Data Structures",
        examType = ExamType.MIDTERM,
        semester = Semester.SPRING_2024,
        totalMarks = 100f,
        obtainedMarks = 91f,
        examDate = "2024-03-10",
        color = Color(0xFFB85A5A),
        icon = Icons.Default.DataArray
    ),
    ExamResult(
        id = "EX011",
        subjectId = "MATH303",
        subjectName = "Linear Algebra",
        examType = ExamType.MIDTERM,
        semester = Semester.SPRING_2024,
        totalMarks = 100f,
        obtainedMarks = 82f,
        examDate = "2024-03-18",
        color = Color(0xFF5D8CAE),
        icon = Icons.Default.Functions
    )
)

// Main Results Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen() {
    val coroutineScope = rememberCoroutineScope()

    // State variables
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedSemester by remember { mutableStateOf<Semester?>(null) }
    var selectedSubject by remember { mutableStateOf<String?>(null) }
    var selectedExamType by remember { mutableStateOf<ExamType?>(null) }
    var showDetailsForResult by remember { mutableStateOf<ExamResult?>(null) }
    var showPerformanceGraph by remember { mutableStateOf(false) }

    // Theme colors
    val isDarkTheme = true
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFF5F5F5)
    val surfaceColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color(0xFFFFFFFF)
    val cardColor = if (isDarkTheme) Color(0xFF222222) else Color(0xFFFAFAFA)
    val textColor = if (isDarkTheme) Color(0xFFE0E0E0) else Color(0xFF212121)
    val primaryColor = Color(0xFF9b87f5)
    val secondaryColor = Color(0xFF7E69AB)
    val accentColor = Color(0xFF33C3F0)
    val gradientColors = listOf(primaryColor, secondaryColor)

    // Pre-compute filtered results using derivedStateOf for performance
    val filteredResults by remember {
        derivedStateOf {
            mockResults.filter { result ->
                (selectedSemester == null || result.semester == selectedSemester) &&
                        (selectedSubject == null || result.subjectId == selectedSubject) &&
                        (selectedExamType == null || result.examType == selectedExamType)
            }
        }
    }

    // Calculate overall statistics
    val overallStatistics by remember {
        derivedStateOf {
            if (filteredResults.isNotEmpty()) {
                val totalObtained = filteredResults.sumOf { it.obtainedMarks.toDouble() }
                val totalPossible = filteredResults.sumOf { it.totalMarks.toDouble() }
                val averagePercentage = if (totalPossible > 0) (totalObtained / totalPossible) * 100 else 0.0
                val averageGrade = calculateGrade(averagePercentage.toFloat())
                Triple(averagePercentage, averageGrade, filteredResults.size)
            } else {
                Triple(0.0, "N/A", 0)
            }
        }
    }

    // UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Results", color = textColor) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = surfaceColor
                ),
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(
                            Icons.Outlined.FilterAlt,
                            contentDescription = "Filter Results",
                            tint = textColor
                        )
                    }
                    IconButton(onClick = { showPerformanceGraph = true }) {
                        Icon(
                            Icons.Default.BarChart,
                            contentDescription = "Performance Graph",
                            tint = textColor
                        )
                    }
                    IconButton(
                        onClick = {
                            // Future enhancement - PDF download
                            // For now, show a toast or snackbar
                            coroutineScope.launch {
                                // Show toast message for future feature
                            }
                        }
                    ) {
                        Icon(
                            Icons.Outlined.FileDownload,
                            contentDescription = "Download Results",
                            tint = textColor
                        )
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(backgroundColor)
            ) {
                // Summary Card
                SummaryCard(
                    totalExams = overallStatistics.third,
                    averagePercentage = overallStatistics.first,
                    averageGrade = overallStatistics.second,
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                    textColor = textColor,
                    gradientColors = gradientColors,
                    cardColor = cardColor
                )

                // Active filters
                if (selectedSemester != null || selectedSubject != null || selectedExamType != null) {
                    ActiveFiltersSection(
                        selectedSemester = selectedSemester,
                        selectedSubject = selectedSubject,
                        selectedExamType = selectedExamType,
                        onClearSemester = { selectedSemester = null },
                        onClearSubject = { selectedSubject = null },
                        onClearExamType = { selectedExamType = null },
                        textColor = textColor,
                        surfaceColor = surfaceColor
                    )
                }

                // Results List
                if (filteredResults.isEmpty()) {
                    EmptyResultsView(textColor)
                } else {
                    ResultsList(
                        results = filteredResults,
                        textColor = textColor,
                        surfaceColor = cardColor,
                        onResultClick = { showDetailsForResult = it }
                    )
                }
            }

            // Filter Dialog
            if (showFilterDialog) {
                FilterDialog(
                    currentSemester = selectedSemester,
                    currentSubject = selectedSubject,
                    currentExamType = selectedExamType,
                    onSemesterSelected = { selectedSemester = it },
                    onSubjectSelected = { selectedSubject = it },
                    onExamTypeSelected = { selectedExamType = it },
                    onDismiss = { showFilterDialog = false },
                    onClear = {
                        selectedSemester = null
                        selectedSubject = null
                        selectedExamType = null
                    },
                    allResults = mockResults,
                    textColor = textColor,
                    backgroundColor = surfaceColor,
                    primaryColor = primaryColor
                )
            }

            // Result Details Dialog
            if (showDetailsForResult != null) {
                ResultDetailsDialog(
                    result = showDetailsForResult!!,
                    onDismiss = { showDetailsForResult = null },
                    textColor = textColor,
                    surfaceColor = surfaceColor,
                    primaryColor = primaryColor
                )
            }

            // Performance Graph Dialog
            if (showPerformanceGraph) {
                PerformanceGraphDialog(
                    results = filteredResults,
                    onDismiss = { showPerformanceGraph = false },
                    textColor = textColor,
                    surfaceColor = surfaceColor,
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor
                )
            }
        }
    )
}

@Composable
fun SummaryCard(
    totalExams: Int,
    averagePercentage: Double,
    averageGrade: String,
    primaryColor: Color,
    secondaryColor: Color,
    textColor: Color,
    gradientColors: List<Color>,
    cardColor: Color
) {
    val animatedPercentage by animateFloatAsState(
        targetValue = averagePercentage.toFloat(),
        label = "PercentageAnimation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = gradientColors,
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, 0f)
                    )
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Performance Summary",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    value = totalExams.toString(),
                    label = "Exams",
                    icon = Icons.AutoMirrored.Filled.Assignment,
                    textColor = textColor,
                    iconColor = primaryColor
                )

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                    progress = { animatedPercentage / 100f },
                    modifier = Modifier.size(100.dp),
                    color = primaryColor,
                    strokeWidth = 8.dp,
                    trackColor = primaryColor.copy(alpha = 0.2f),
                    strokeCap = StrokeCap.Round,
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${animatedPercentage.roundToInt()}%",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = textColor
                        )
                        Text(
                            text = "Average",
                            fontSize = 12.sp,
                            color = textColor.copy(alpha = 0.7f)
                        )
                    }
                }

                StatisticItem(
                    value = averageGrade,
                    label = "Grade",
                    icon = Icons.Default.Star,
                    textColor = textColor,
                    iconColor = primaryColor
                )
            }
        }
    }
}

@Composable
fun StatisticItem(
    value: String,
    label: String,
    icon: ImageVector,
    textColor: Color,
    iconColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = textColor
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = textColor.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ActiveFiltersSection(
    selectedSemester: Semester?,
    selectedSubject: String?,
    selectedExamType: ExamType?,
    onClearSemester: () -> Unit,
    onClearSubject: () -> Unit,
    onClearExamType: () -> Unit,
    textColor: Color,
    surfaceColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Filters:",
            color = textColor.copy(alpha = 0.7f),
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        selectedSemester?.let {
            FilterChip(
                label = it.name.replace("_", " "),
                onClear = onClearSemester,
                textColor = textColor,
                surfaceColor = surfaceColor
            )
        }

        selectedSubject?.let {
            val subjectName = mockResults.find { result -> result.subjectId == selectedSubject }?.subjectName ?: it
            FilterChip(
                label = subjectName,
                onClear = onClearSubject,
                textColor = textColor,
                surfaceColor = surfaceColor
            )
        }

        selectedExamType?.let {
            FilterChip(
                label = it.name,
                onClear = onClearExamType,
                textColor = textColor,
                surfaceColor = surfaceColor
            )
        }
    }
}

@Composable
fun FilterChip(
    label: String,
    onClear: () -> Unit,
    textColor: Color,
    surfaceColor: Color
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = surfaceColor.copy(alpha = 0.8f),
        modifier = Modifier
            .height(32.dp)
            .border(
                width = 1.dp,
                color = textColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = textColor,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.Clear,
                contentDescription = "Clear filter",
                modifier = Modifier
                    .size(16.dp)
                    .clickable(onClick = onClear),
                tint = textColor.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ResultsList(
    results: List<ExamResult>,
    textColor: Color,
    surfaceColor: Color,
    onResultClick: (ExamResult) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(results) { result ->
            ResultCard(
                result = result,
                textColor = textColor,
                surfaceColor = surfaceColor,
                onClick = { onResultClick(result) }
            )
        }
    }
}

@Composable
fun ResultCard(
    result: ExamResult,
    textColor: Color,
    surfaceColor: Color,
    onClick: () -> Unit
) {
    val percentage = (result.obtainedMarks / result.totalMarks) * 100
    val grade = calculateGrade(percentage)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = surfaceColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Subject icon with color
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(result.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    result.icon,
                    contentDescription = "Subject Icon",
                    tint = result.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = result.subjectName,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${result.examType.name} | ${result.semester.name.replace("_", " ")}",
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { percentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = when {
                        percentage >= 80 -> Color(0xFF4CAF50) // Green
                        percentage >= 60 -> Color(0xFFFFC107) // Yellow
                        else -> Color(0xFFF44336) // Red
                    },
                    trackColor = Color.Gray.copy(alpha = 0.2f)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Grade
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                percentage >= 80 -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                percentage >= 60 -> Color(0xFFFFC107).copy(alpha = 0.2f)
                                else -> Color(0xFFF44336).copy(alpha = 0.2f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = grade,
                        color = when {
                            percentage >= 80 -> Color(0xFF4CAF50)
                            percentage >= 60 -> Color(0xFFFFC107)
                            else -> Color(0xFFF44336)
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${percentage.roundToInt()}%",
                    color = textColor,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun EmptyResultsView(textColor: Color) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                Icons.Default.SearchOff,
                contentDescription = "No Results",
                tint = textColor.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No results found",
                color = textColor.copy(alpha = 0.7f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Try adjusting your filters to see more results",
                color = textColor.copy(alpha = 0.5f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    currentSemester: Semester?,
    currentSubject: String?,
    currentExamType: ExamType?,
    onSemesterSelected: (Semester?) -> Unit,
    onSubjectSelected: (String?) -> Unit,
    onExamTypeSelected: (ExamType?) -> Unit,
    onDismiss: () -> Unit,
    onClear: () -> Unit,
    allResults: List<ExamResult>,
    textColor: Color,
    backgroundColor: Color,
    primaryColor: Color
) {
    var localSemester by remember { mutableStateOf(currentSemester) }
    var localSubject by remember { mutableStateOf(currentSubject) }
    var localExamType by remember { mutableStateOf(currentExamType) }

    // Get unique subjects from results
    val uniqueSubjects = remember(allResults) {
        allResults.distinctBy { it.subjectId }.map { it.subjectId to it.subjectName }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = backgroundColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Filter Results",
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Semester Filter
                Text(
                    text = "Semester",
                    color = textColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(Semester.entries.toTypedArray()) { semester ->
                        val isSelected = semester == localSemester
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                localSemester = if (isSelected) null else semester
                            },
                            label = {
                                Text(semester.name.replace("_", " "))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = primaryColor,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Subject Filter
                Text(
                    text = "Subject",
                    color = textColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uniqueSubjects) { subject ->
                        val isSelected = subject.first == localSubject
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                localSubject = if (isSelected) null else subject.first
                            },
                            label = {
                                Text(
                                    text = subject.second,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = primaryColor,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Exam Type Filter
                Text(
                    text = "Exam Type",
                    color = textColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ExamType.entries.toTypedArray()) { examType ->
                        val isSelected = examType == localExamType
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                localExamType = if (isSelected) null else examType
                            },
                            label = {
                                Text(examType.name)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = primaryColor,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        localSemester = null
                        localSubject = null
                        localExamType = null
                        onClear()
                    }) {
                        Text("Clear All", color = textColor)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSemesterSelected(localSemester)
                            onSubjectSelected(localSubject)
                            onExamTypeSelected(localExamType)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                    ) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}

@Composable
fun ResultDetailsDialog(
    result: ExamResult,
    onDismiss: () -> Unit,
    textColor: Color,
    surfaceColor: Color,
    primaryColor: Color
) {
    val percentage = (result.obtainedMarks / result.totalMarks) * 100
    val grade = calculateGrade(percentage)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = surfaceColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(result.color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            result.icon,
                            contentDescription = "Subject Icon",
                            tint = result.color,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = result.subjectName,
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "${result.examType.name} Exam",
                            color = textColor.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(
                    thickness = 1.dp,  // Set thickness of the divider
                    modifier = Modifier.fillMaxWidth(),  // Ensure it stretches across the screen
                    color = textColor.copy(alpha = 0.1f)  // Apply transparency
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Result details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ResultDetailItem(
                        value = "${result.obtainedMarks.toInt()} / ${result.totalMarks.toInt()}",
                        label = "Marks",
                        textColor = textColor
                    )
                    ResultDetailItem(
                        value = "${percentage.roundToInt()}%",
                        label = "Percentage",
                        textColor = textColor
                    )
                    ResultDetailItem(
                        value = grade,
                        label = "Grade",
                        textColor = textColor
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Progress bar
                LinearProgressIndicator(
                    progress = { percentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = when {
                        percentage >= 80 -> Color(0xFF4CAF50) // Green
                        percentage >= 60 -> Color(0xFFFFC107) // Yellow
                        else -> Color(0xFFF44336) // Red
                    },
                    trackColor = Color.Gray.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Additional information
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DetailRow(
                        label = "Semester",
                        value = result.semester.name.replace("_", " "),
                        textColor = textColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(
                        label = "Exam Date",
                        value = result.examDate,
                        textColor = textColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(
                        label = "Subject ID",
                        value = result.subjectId,
                        textColor = textColor
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Close button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun ResultDetailItem(
    value: String,
    label: String,
    textColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = textColor.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    textColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = textColor.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = textColor,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Composable
fun PerformanceGraphDialog(
    results: List<ExamResult>,
    onDismiss: () -> Unit,
    textColor: Color,
    surfaceColor: Color,
    primaryColor: Color,
    secondaryColor: Color
) {
    // Group results by subject
    val resultsBySubject = results.groupBy { it.subjectId }

    // For a real implementation, you would use a chart library like MPAndroidChart
    // Here we'll create a simplified visual representation

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp),
            shape = RoundedCornerShape(16.dp),
            color = surfaceColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Performance Trends",
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // In a real implementation, this would be a proper chart
                // For now, we'll display a simplified visual representation
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Performance by Subject",
                        color = textColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Simplified bar chart
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(resultsBySubject.toList()) { (subjectId, subjectResults) ->
                            val subjectName = subjectResults.firstOrNull()?.subjectName ?: "Unknown Subject"
                            val avgPercentage = subjectResults.map { (it.obtainedMarks / it.totalMarks) * 100 }.average()

                            Column {
                                Text(
                                    text = subjectName,
                                    color = textColor,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(avgPercentage.toFloat() / 100f)
                                            .height(24.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    colors = listOf(primaryColor, secondaryColor)
                                                )
                                            )
                                    )

                                    Box(
                                        modifier = Modifier
                                            .weight(1f - avgPercentage.toFloat() / 100f)
                                            .height(24.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color.Gray.copy(alpha = 0.2f))
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = "${avgPercentage.roundToInt()}%",
                                        color = textColor,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text("Close")
                }
            }
        }
    }
}
