package com.mardillu.malami.ui.courses.course_modules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mardillu.malami.R
import com.mardillu.malami.data.model.course.Module
import com.mardillu.malami.ui.common.ui.NowPlayingBottomBar
import com.mardillu.malami.ui.courses.list.CourseListViewModel
import com.mardillu.malami.ui.courses.player.AudioPlayerScreen
import com.mardillu.malami.ui.courses.player.AudioPlayerViewModel
import com.mardillu.malami.ui.courses.player.CustomDragHandle
import com.mardillu.malami.ui.navigation.AppNavigation
import com.mardillu.malami.utils.AppAlertDialog
import kotlinx.coroutines.launch

/**
 * Created on 20/05/2024 at 12:33 pm
 * @author mardillu
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleListScreen(
    navigation: AppNavigation,
    courseId: String,
    startService: () -> Unit,
    viewModel: CourseListViewModel,
    modulesViewModel: ModulesViewModel,
    playerViewModel: AudioPlayerViewModel
) {
    val courseList by viewModel.courseListState.collectAsState()
    val course = courseList.firstOrNull { it.id == courseId }
    var showMessageDialog by rememberSaveable { mutableStateOf(false) }
    var sectionIdState by rememberSaveable { mutableStateOf("") }
    var moduleIdState by rememberSaveable { mutableStateOf("") }
    val bottomSheetState = rememberStandardBottomSheetState(skipHiddenState = false, initialValue = SheetValue.Hidden)
    val sheetState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        BottomSheetScaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = course?.title ?: "",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigation.back() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            sheetContent = {
                AudioPlayerScreen(
                    courseId = courseId,
                    sectionId = sectionIdState,
                    moduleId = moduleIdState,
                    startService = { startService() },
                    viewModel = playerViewModel,
                )
            },
            sheetDragHandle = {
                CustomDragHandle {
                    scope.launch { sheetState.bottomSheetState.hide() }
                }
            },
            sheetPeekHeight = 0.dp,
            scaffoldState = sheetState,
            content = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    if (course != null) {
                        itemsIndexed(course.sections) { sectionIndex, section ->
                            ExpandableListItem(
                                section.title,
                                section.modules,
                                if (sectionIndex > 0) course.sections[sectionIndex - 1].id else "", //prev section id
                                courseId,
                                section.id,
                                sectionIndex,
                                navigation,
                                viewModel,
                                modulesViewModel,
                            ) { courseId, sectionId, moduleId ->
                                if (modulesViewModel.moduleHasAudio(moduleId)) {
                                    sectionIdState = sectionId
                                    moduleIdState = moduleId
                                    scope.launch {
                                        sheetState.bottomSheetState.expand()
                                    }
                                    //navigation.gotoAudioPlayer(courseId, sectionId, moduleId,)
                                } else {
                                    showMessageDialog = true
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(60.dp))
                        }
                    }
                }
            }
        )

        AnimatedVisibility(
            visible = sheetState.bottomSheetState.currentValue != SheetValue.Expanded,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            NowPlayingBottomBar(
                viewModel = playerViewModel,
            ) {
                scope.launch { sheetState.bottomSheetState.expand() }
            }
        }

        if (showMessageDialog) {
            AppAlertDialog(
                dialogText = "Audio for this module is being synthesised. It can take up to 10 minutes to synthesise high quality audios for all modules. Please check back later.",
                onDismissRequest = {
                    showMessageDialog = false
                },
                onConfirmation = {
                    showMessageDialog = false
                },
                icon = Icons.Default.Info
            )
        }
    }
}

@Composable
fun ExpandableListItem(
    title: String,
    modules: List<Module>,
    prevSectionId: String,
    courseId: String,
    sectionId: String,
    sectionIndex: Int,
    navigation: AppNavigation,
    viewModel: CourseListViewModel,
    modulesViewModel: ModulesViewModel,
    onPlay: (courseId: String, sectionId: String, moduleId: String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val quizAttemptsUiState by modulesViewModel.quizAttemptsUiState.collectAsState()

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        //border = BorderStroke(0.5.dp, color = Purple40)
    ) {
        Column {
            Column(
                modifier = Modifier
                    .animateContentSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.width(8.dp)) // Add some space between the title and action text
                    TextButton(onClick = {  expanded = !expanded }) {
                        Text(
                            text = if (expanded) stringResource(R.string.hide) else stringResource(
                                R.string.show
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = if (expanded) "Collapse" else "Expand",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(24.dp)
                                .then(
                                    if (expanded) Modifier.rotate(
                                        180f
                                    ) else Modifier
                                ),
                        )
                    }
                }

                if (expanded) {
                    modules.forEachIndexed {i, module ->
                        val moduleActive = viewModel.isModuleActive(sectionIndex, i, courseId, quizAttemptsUiState, prevSectionId)
                        ModuleListItem(module, moduleActive, onClick = {
                            if (moduleActive) {
                                navigation.goToModuleContent(courseId, module.id, sectionId)
                            }
                        },{
                            onPlay(courseId, sectionId, module.id,)
                        }, modulesViewModel)
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 0.dp, horizontal = 16.dp),
                            thickness = 0.5.dp,
                        )
                    }
                    //add the quiz module
                    ModuleListItem(
                        Module(
                            title = "Section Quiz",
                            shortDescription = "Take the quiz to complete this section and move on to the next",
                            id = "",
                            content = "",
                            completed = modulesViewModel.isQuizTaken(sectionId),
                        ), (modulesViewModel.isQuizTaken(sectionId) || modules[modules.size-1].completed), onClick = {
                            navigation.gotoQuiz(courseId, sectionId)
                        },
                        viewModel = modulesViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun ModuleListItem(module: Module, isModuleActive: Boolean, onClick: () -> Unit, onPlay: () -> Unit = {}, viewModel: ModulesViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = module.title,
                style = if (isModuleActive) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
                fontWeight = if (isModuleActive) FontWeight.Bold else FontWeight.Normal,
            )
            Text(
                text = module.timeToRead,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(onClick = { onPlay() }) {

            Icon(
                imageVector = if(viewModel.moduleHasAudio(module.id)) Icons.Filled.PlayArrow else Icons.Filled.Timelapse,
                contentDescription = if(viewModel.moduleHasAudio(module.id)) "Listen" else "Synthesizing Audio",
                tint = Color.Gray
            )
        }
        IconButton(onClick = { /* Handle module completion */ }) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        if (module.completed) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (module.completed) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    }
}